package com.tungsten.hmclpe.sweetrice.auth.authlibinjector;

import static com.tungsten.hmclpe.sweetrice.utils.Lang.tryCast;
import static com.tungsten.hmclpe.sweetrice.utils.Logging.LOG;
import static com.tungsten.hmclpe.sweetrice.utils.io.IOUtils.readFullyAsByteArray;
import static com.tungsten.hmclpe.sweetrice.utils.io.IOUtils.readFullyWithoutClosing;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.JsonAdapter;
import com.tungsten.hmclpe.sweetrice.auth.yggdrasil.YggdrasilService;

@JsonAdapter(AuthlibInjectorServer.Deserializer.class)
public class AuthlibInjectorServer {

    private static final Gson GSON = new GsonBuilder().create();

    public static AuthlibInjectorServer locateServer(String url) throws IOException {
        try {
            url = addHttpsIfMissing(url);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            String ali = conn.getHeaderField("x-authlib-injector-api-location");
            if (ali != null) {
                URL absoluteAli = new URL(conn.getURL(), ali);
                if (!urlEqualsIgnoreSlash(url, absoluteAli.toString())) {
                    conn.disconnect();
                    url = absoluteAli.toString();
                    conn = (HttpURLConnection) absoluteAli.openConnection();
                }
            }

            if (!url.endsWith("/"))
                url += "/";

            try {
                AuthlibInjectorServer server = new AuthlibInjectorServer(url);
                server.refreshMetadata(readFullyWithoutClosing(conn.getInputStream()));
                return server;
            } finally {
                conn.disconnect();
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }

    private static String addHttpsIfMissing(String url) {
        String lowercased = url.toLowerCase();
        if (!lowercased.startsWith("http://") && !lowercased.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }

    private static boolean urlEqualsIgnoreSlash(String a, String b) {
        if (!a.endsWith("/"))
            a += "/";
        if (!b.endsWith("/"))
            b += "/";
        return a.equals(b);
    }

    private String url;
    @Nullable
    private String metadataResponse;
    private long metadataTimestamp;

    @Nullable
    private transient String name;
    private transient Map<String, String> links = emptyMap();
    private transient boolean nonEmailLogin;

    private transient boolean metadataRefreshed;
    private final transient YggdrasilService yggdrasilService;

    public AuthlibInjectorServer(String url) {
        this.url = url;
        this.yggdrasilService = new YggdrasilService(new AuthlibInjectorProvider(url));
    }

    public String getUrl() {
        return url;
    }

    public YggdrasilService getYggdrasilService() {
        return yggdrasilService;
    }

    public Optional<String> getMetadataResponse() {
        return Optional.ofNullable(metadataResponse);
    }

    public long getMetadataTimestamp() {
        return metadataTimestamp;
    }

    public String getName() {
        return Optional.ofNullable(name)
                .orElse(url);
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public boolean isNonEmailLogin() {
        return nonEmailLogin;
    }

    public String fetchMetadataResponse() throws IOException {
        if (metadataResponse == null || !metadataRefreshed) {
            refreshMetadata();
        }
        return getMetadataResponse().get();
    }

    public void refreshMetadata() throws IOException {
        refreshMetadata(readFullyAsByteArray(new URL(url).openStream()));
    }

    private void refreshMetadata(byte[] rawResponse) throws IOException {
        long timestamp = System.currentTimeMillis();
        String text = new String(rawResponse, UTF_8);
        try {
            setMetadataResponse(text, timestamp);
        } catch (JsonParseException e) {
            throw new IOException("Malformed response\n" + text, e);
        }

        metadataRefreshed = true;
        LOG.info("authlib-injector server metadata refreshed: " + url);
    }

    private void setMetadataResponse(String metadataResponse, long metadataTimestamp) throws JsonParseException {
        JsonObject response = GSON.fromJson(metadataResponse, JsonObject.class);
        if (response == null) {
            throw new JsonParseException("Metadata response is empty");
        }

        synchronized (this) {
            this.metadataResponse = metadataResponse;
            this.metadataTimestamp = metadataTimestamp;

            Optional<JsonObject> metaObject = tryCast(response.get("meta"), JsonObject.class);

            this.name = metaObject.flatMap(meta -> tryCast(meta.get("serverName"), JsonPrimitive.class).map(JsonPrimitive::getAsString))
                    .orElse(null);
            this.links = metaObject.flatMap(meta -> tryCast(meta.get("links"), JsonObject.class))
                    .map(linksObject -> {
                        Map<String, String> converted = new LinkedHashMap<>();
                        linksObject.entrySet().forEach(
                                entry -> tryCast(entry.getValue(), JsonPrimitive.class).ifPresent(element -> {
                                    converted.put(entry.getKey(), element.getAsString());
                                }));
                        return converted;
                    })
                    .orElse(emptyMap());
            this.nonEmailLogin = metaObject.flatMap(meta -> tryCast(meta.get("feature.non_email_login"), JsonPrimitive.class))
                    .map(it -> it.getAsBoolean())
                    .orElse(false);
        }
    }

    public void invalidateMetadataCache() {
        metadataRefreshed = false;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof AuthlibInjectorServer))
            return false;
        AuthlibInjectorServer another = (AuthlibInjectorServer) obj;
        return this.url.equals(another.url);
    }

    @Override
    public String toString() {
        return name == null ? url : url + " (" + name + ")";
    }

    public static class Deserializer implements JsonDeserializer<AuthlibInjectorServer> {
        @Override
        public AuthlibInjectorServer deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            AuthlibInjectorServer instance = new AuthlibInjectorServer(jsonObj.get("url").getAsString());

            if (jsonObj.has("name")) {
                instance.name = jsonObj.get("name").getAsString();
            }

            if (jsonObj.has("metadataResponse")) {
                try {
                    instance.setMetadataResponse(jsonObj.get("metadataResponse").getAsString(), jsonObj.get("metadataTimestamp").getAsLong());
                } catch (JsonParseException e) {
                    LOG.log(Level.WARNING, "Ignoring malformed metadata response cache: " + jsonObj.get("metadataResponse"), e);
                }
            }
            return instance;
        }

    }
}