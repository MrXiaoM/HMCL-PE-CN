package com.tungsten.hmclpe.sweetrice.launcher.mod;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.tungsten.hmclpe.sweetrice.utils.gson.JsonUtils;
import com.tungsten.hmclpe.sweetrice.utils.gson.tools.Validation;
import com.tungsten.hmclpe.sweetrice.utils.io.FileUtils;
import com.tungsten.hmclpe.sweetrice.utils.io.ZipTools;
import com.tungsten.hmclpe.sweetrice.utils.string.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackMcMeta implements Validation {

    @SerializedName("pack")
    private final PackInfo pack;

    public PackMcMeta() {
        this(new PackInfo());
    }

    public PackMcMeta(PackInfo packInfo) {
        this.pack = packInfo;
    }

    public PackInfo getPackInfo() {
        return pack;
    }

    @Override
    public void validate() throws JsonParseException {
        if (pack == null)
            throw new JsonParseException("pack cannot be null");
    }

    @JsonAdapter(PackInfoDeserializer.class)
    public static class PackInfo {
        @SerializedName("pack_format")
        private final int packFormat;

        @SerializedName("description")
        private final LocalModFile.Description description;

        public PackInfo() {
            this(0, new LocalModFile.Description(Collections.emptyList()));
        }

        public PackInfo(int packFormat, LocalModFile.Description description) {
            this.packFormat = packFormat;
            this.description = description;
        }

        public int getPackFormat() {
            return packFormat;
        }

        public LocalModFile.Description getDescription() {
            return description;
        }
    }

    public static class PackInfoDeserializer implements JsonDeserializer<PackInfo> {

        private String parseText(JsonElement json) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                JsonPrimitive primitive = json.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    return Boolean.toString(primitive.getAsBoolean());
                } else if (primitive.isNumber()) {
                    return primitive.getAsNumber().toString();
                } else if (primitive.isString()) {
                    return primitive.getAsString();
                } else {
                    throw new JsonParseException("pack.mcmeta text not boolean nor number nor string???");
                }
            } else if (json.isJsonArray()) {
                JsonArray arr = json.getAsJsonArray();
                if (arr.size() == 0) {
                    return "";
                } else {
                    return parseText(arr.get(0));
                }
            } else {
                throw new JsonParseException("pack.mcmeta text should be a string, a boolean, a number or a list of raw JSON text components");
            }
        }

        public LocalModFile.Description.Part deserialize(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return new LocalModFile.Description.Part(parseText(json));
            } else if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                String text = parseText(obj.get("text"));
                return new LocalModFile.Description.Part(text);
            } else {
                throw new JsonParseException("pack.mcmeta Raw JSON text should be string or an object");
            }
        }

        @Override
        public PackInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<LocalModFile.Description.Part> parts = new ArrayList<>();
            JsonObject packInfo = json.getAsJsonObject();
            int packFormat = packInfo.get("pack_format").getAsInt();
            JsonElement description = packInfo.get("description");
            if (description.isJsonPrimitive()) {
                parts.add(new LocalModFile.Description.Part(parseText(description)));
            } else if (description.isJsonArray()) {
                for (JsonElement element : description.getAsJsonArray()) {
                    JsonObject descriptionPart = element.getAsJsonObject();
                    parts.add(new LocalModFile.Description.Part(descriptionPart.get("text").getAsString(), descriptionPart.get("color").getAsString()));
                }
            } else {
                throw new JsonParseException("pack.mcmeta::pack::description should be String or array of text objects with text and color fields");
            }
            return new PackInfo(packFormat, new LocalModFile.Description(parts));
        }
    }

    public static LocalModFile fromFile(ModManager modManager, Path modFile) throws IOException, JsonParseException {
        String s = ZipTools.readNormalMeta(modFile.toString(),"pack.mcmeta");
        if (StringUtils.isBlank(s)) {
            throw new IOException("File " + modFile + " is not a resource pack.");
        }
        PackMcMeta metadata = JsonUtils.fromNonNullJson(s, PackMcMeta.class);
        return new LocalModFile(
                modManager,
                modManager.getLocalMod(FileUtils.getNameWithoutExtension(modFile), ModLoaderType.PACK),
                modFile,
                FileUtils.getNameWithoutExtension(modFile),
                metadata.pack.description,
                "", "", "", "", "");
    }
}