package com.tungsten.hmclpe.sweetrice.launcher.mod;

import com.google.gson.JsonParseException;
import com.tungsten.hmclpe.sweetrice.utils.gson.JsonUtils;
import com.tungsten.hmclpe.sweetrice.utils.io.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author huangyuhui
 */
public final class LiteModMetadata {
    private final String name;
    private final String version;
    private final String mcversion;
    private final String revision;
    private final String author;
    private final String[] classTransformerClasses;
    private final String description;
    private final String modpackName;
    private final String modpackVersion;
    private final String checkUpdateUrl;
    private final String updateURI;

    public LiteModMetadata() {
        this("", "", "", "", "", new String[]{""}, "", "", "", "", "");
    }

    public LiteModMetadata(String name, String version, String mcversion, String revision, String author, String[] classTransformerClasses, String description, String modpackName, String modpackVersion, String checkUpdateUrl, String updateURI) {
        this.name = name;
        this.version = version;
        this.mcversion = mcversion;
        this.revision = revision;
        this.author = author;
        this.classTransformerClasses = classTransformerClasses;
        this.description = description;
        this.modpackName = modpackName;
        this.modpackVersion = modpackVersion;
        this.checkUpdateUrl = checkUpdateUrl;
        this.updateURI = updateURI;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getGameVersion() {
        return mcversion;
    }

    public String getRevision() {
        return revision;
    }

    public String getAuthor() {
        return author;
    }

    public String[] getClassTransformerClasses() {
        return classTransformerClasses;
    }

    public String getDescription() {
        return description;
    }

    public String getModpackName() {
        return modpackName;
    }

    public String getModpackVersion() {
        return modpackVersion;
    }

    public String getCheckUpdateUrl() {
        return checkUpdateUrl;
    }

    public String getUpdateURI() {
        return updateURI;
    }
    
    public static LocalModFile fromFile(ModManager modManager, Path modFile) throws IOException, JsonParseException {
        try (ZipFile zipFile = new ZipFile(modFile.toFile())) {
            ZipEntry entry = zipFile.getEntry("litemod.json");
            if (entry == null)
                throw new IOException("File " + modFile + "is not a LiteLoader mod.");
            LiteModMetadata metadata = JsonUtils.GSON.fromJson(IOUtils.readFullyAsString(zipFile.getInputStream(entry)), LiteModMetadata.class);
            if (metadata == null)
                throw new IOException("Mod " + modFile + " `litemod.json` is malformed.");
            return new LocalModFile(modManager, modManager.getLocalMod(metadata.getName(), ModLoaderType.LITE_LOADER), modFile, metadata.getName(), new LocalModFile.Description(metadata.getDescription()), metadata.getAuthor(),
                    metadata.getVersion(), metadata.getGameVersion(), metadata.getUpdateURI(), "");
        }
    }
    
}