package com.tungsten.hmclpe.sweetrice.launcher.mod;

import com.tungsten.hmclpe.sweetrice.utils.Logging;
import com.tungsten.hmclpe.sweetrice.utils.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author huangyuhui
 */
public final class LocalModFile implements Comparable<LocalModFile> {

    private Path file;
    private final ModManager modManager;
    private final LocalMod mod;
    private final String name;
    private final Description description;
    private final String authors;
    private final String version;
    private final String gameVersion;
    private final String url;
    private final String fileName;
    private final String logoPath;
    private boolean active;

    public LocalModFile(ModManager modManager, LocalMod mod, Path file, String name, Description description) {
        this(modManager, mod, file, name, description, "", "", "", "", "");
    }

    public LocalModFile(ModManager modManager, LocalMod mod, Path file, String name, Description description, String authors, String version, String gameVersion, String url, String logoPath) {
        this.modManager = modManager;
        this.mod = mod;
        this.file = file;
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.gameVersion = gameVersion;
        this.url = url;
        this.logoPath = logoPath;

        this.active = !modManager.isDisabled(file);

        fileName = FileUtils.getNameWithoutExtension(ModManager.getModName(file));

        if (isOld()) {
            mod.getOldFiles().add(this);
        } else {
            mod.getFiles().add(this);
        }
    }

    public ModManager getModManager() {
        return modManager;
    }

    public LocalMod getMod() {
        return mod;
    }

    public Path getFile() {
        return file;
    }

    public ModLoaderType getModLoaderType() {
        return mod.getModLoaderType();
    }

    public String getId() {
        return mod.getId();
    }

    public String getName() {
        return name;
    }

    public Description getDescription() {
        return description;
    }

    public String getAuthors() {
        return authors;
    }

    public String getVersion() {
        return version;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getUrl() {
        return url;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) throws IOException {
        this.active = active;
        Path path = LocalModFile.this.file.toAbsolutePath();
        try {
            if (active)
                LocalModFile.this.file = modManager.enableMod(path);
            else
                LocalModFile.this.file = modManager.disableMod(path);
        } catch (IOException e) {
            Logging.LOG.log(Level.SEVERE, "Unable to invert state of mod file " + path, e);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isOld() {
        return modManager.isOld(file);
    }

    public void setOld(boolean old) throws IOException {
        file = modManager.setOld(this, old);

        if (old) {
            mod.getFiles().remove(this);
            mod.getOldFiles().add(this);
        } else {
            mod.getOldFiles().remove(this);
            mod.getFiles().add(this);
        }
    }

    public ModUpdate checkUpdates(String gameVersion, RemoteModRepository repository) throws IOException {
        Optional<RemoteMod.Version> currentVersion = repository.getRemoteVersionByLocalFile(this, file);
        if (!currentVersion.isPresent()) return null;
        List<RemoteMod.Version> remoteVersions = repository.getRemoteVersionsById(currentVersion.get().getModid())
                .filter(version -> version.getGameVersions().contains(gameVersion))
                .filter(version -> version.getLoaders().contains(getModLoaderType()))
                .filter(version -> version.getDatePublished().compareTo(currentVersion.get().getDatePublished()) > 0)
                .sorted(Comparator.comparing(RemoteMod.Version::getDatePublished).reversed())
                .collect(Collectors.toList());
        if (remoteVersions.isEmpty()) return null;
        return new ModUpdate(this, currentVersion.get(), remoteVersions);
    }

    @Override
    public int compareTo(LocalModFile o) {
        return getFileName().compareTo(o.getFileName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LocalModFile && Objects.equals(getFileName(), ((LocalModFile) obj).getFileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFileName());
    }

    public static class ModUpdate {
        private final LocalModFile localModFile;
        private final RemoteMod.Version currentVersion;
        private final List<RemoteMod.Version> candidates;

        public ModUpdate(LocalModFile localModFile, RemoteMod.Version currentVersion, List<RemoteMod.Version> candidates) {
            this.localModFile = localModFile;
            this.currentVersion = currentVersion;
            this.candidates = candidates;
        }

        public LocalModFile getLocalMod() {
            return localModFile;
        }

        public RemoteMod.Version getCurrentVersion() {
            return currentVersion;
        }

        public List<RemoteMod.Version> getCandidates() {
            return candidates;
        }
    }

    public static class Description {
        private final List<Part> parts;

        public Description(String text) {
            this.parts = new ArrayList<>();
            this.parts.add(new Part(text, "black"));
        }

        public Description(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Part part : parts) {
                builder.append(part.text);
            }
            return builder.toString();
        }

        public static class Part {
            private final String text;
            private final String color;

            public Part(String text) {
                this(text, "");
            }

            public Part(String text, String color) {
                this.text = Objects.requireNonNull(text);
                this.color = Objects.requireNonNull(color);
            }

            public String getText() {
                return text;
            }

            public String getColor() {
                return color;
            }
        }
    }
}