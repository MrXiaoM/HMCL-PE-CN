package com.tungsten.hmclpe.sweetrice.launcher.mod.modrinth;

import com.google.gson.JsonParseException;
import com.tungsten.hmclpe.sweetrice.launcher.mod.ModpackManifest;
import com.tungsten.hmclpe.sweetrice.launcher.mod.ModpackProvider;
import com.tungsten.hmclpe.sweetrice.utils.gson.tools.TolerableValidationException;
import com.tungsten.hmclpe.sweetrice.utils.gson.tools.Validation;

import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModrinthManifest implements ModpackManifest, Validation {

    private final String game;
    private final int formatVersion;
    private final String versionId;
    private final String name;
    @Nullable
    private final String summary;
    private final List<File> files;
    private final Map<String, String> dependencies;

    public ModrinthManifest(String game, int formatVersion, String versionId, String name, @Nullable String summary, List<File> files, Map<String, String> dependencies) {
        this.game = game;
        this.formatVersion = formatVersion;
        this.versionId = versionId;
        this.name = name;
        this.summary = summary;
        this.files = files;
        this.dependencies = dependencies;
    }

    public String getGame() {
        return game;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public String getVersionId() {
        return versionId;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary == null ? "" : summary;
    }

    public List<File> getFiles() {
        return files;
    }

    public Map<String, String> getDependencies() {
        return dependencies;
    }

    public String getGameVersion() {
        return dependencies.get("minecraft");
    }

    @Override
    public ModpackProvider getProvider() {
        return ModrinthModpackProvider.INSTANCE;
    }

    @Override
    public void validate() throws JsonParseException, TolerableValidationException {
        if (dependencies == null || dependencies.get("minecraft") == null) {
            throw new JsonParseException("missing Modrinth.dependencies.minecraft");
        }
    }

    public static class File {
        private final String path;
        private final Map<String, String> hashes;
        private final Map<String, String> env;
        private final List<URL> downloads;
        private final int fileSize;

        public File(String path, Map<String, String> hashes, Map<String, String> env, List<URL> downloads, int fileSize) {
            this.path = path;
            this.hashes = hashes;
            this.env = env;
            this.downloads = downloads;
            this.fileSize = fileSize;
        }

        public String getPath() {
            return path;
        }

        public Map<String, String> getHashes() {
            return hashes;
        }

        public Map<String, String> getEnv() {
            return env;
        }

        public List<URL> getDownloads() {
            return downloads;
        }

        public int getFileSize() {
            return fileSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            File file = (File) o;
            return fileSize == file.fileSize && path.equals(file.path) && hashes.equals(file.hashes) && env.equals(file.env) && downloads.equals(file.downloads);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, hashes, env, downloads, fileSize);
        }
    }

}