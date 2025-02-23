package com.tungsten.hmclpe.sweetrice.utils.string;

import static com.tungsten.hmclpe.sweetrice.utils.Logging.LOG;
import static com.tungsten.hmclpe.sweetrice.utils.Pair.pair;

import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.tungsten.hmclpe.sweetrice.launcher.mod.RemoteModRepository;
import com.tungsten.hmclpe.sweetrice.utils.Pair;
import com.tungsten.hmclpe.sweetrice.utils.io.IOUtils;

/**
 * Parser for mod_data.txt
 *
 * @see <a href="https://www.mcmod.cn">mcmod.cn</a>
 */
public final class ModTranslations {
    public static ModTranslations MOD = new ModTranslations("/assets/mod_data.txt");
    public static ModTranslations MODPACK = new ModTranslations("/assets/modpack_data.txt");
    public static ModTranslations EMPTY = new ModTranslations("");

    public static ModTranslations getTranslationsByRepositoryType(RemoteModRepository.Type type) {
        switch (type) {
            case MOD:
                return MOD;
            case MODPACK:
                return MODPACK;
            default:
                return EMPTY;
        }
    }

    private final String resourceName;
    private List<Mod> mods;
    private Map<String, Mod> modIdMap; // mod id -> mod
    private Map<String, Mod> curseForgeMap; // curseforge id -> mod
    private List<Pair<String, Mod>> keywords;
    private int maxKeywordLength = -1;

    private ModTranslations(String resourceName) {
        this.resourceName = resourceName;
    }

    @Nullable
    public Mod getModByCurseForgeId(String id) {
        if (StringUtils.isBlank(id) || !loadCurseForgeMap()) return null;

        return curseForgeMap.get(id);
    }

    @Nullable
    public Mod getModById(String id) {
        if (StringUtils.isBlank(id) || !loadModIdMap()) return null;

        return modIdMap.get(id);
    }

    public List<Mod> searchMod(String query) {
        if (!loadKeywords()) return Collections.emptyList();

        StringBuilder newQuery = query.chars()
                .filter(ch -> !Character.isSpaceChar(ch))
                .collect(StringBuilder::new, (sb, value) -> sb.append((char)value), StringBuilder::append);
        query = newQuery.toString();

        StringUtils.LongestCommonSubsequence lcs = new StringUtils.LongestCommonSubsequence(query.length(), maxKeywordLength);
        List<Pair<Integer, Mod>> modList = new ArrayList<>();
        for (Pair<String, Mod> keyword : keywords) {
            int value = lcs.calc(query, keyword.getKey());
            if (value >= Math.max(1, query.length() - 3)) {
                modList.add(pair(value, keyword.getValue()));
            }
        }
        return modList.stream()
                .sorted((a, b) -> -a.getKey().compareTo(b.getKey()))
                .map(Pair::getValue)
                .collect(Collectors.toList());
    }

    private boolean loadFromResource() {
        if (mods != null) return true;
        if (StringUtils.isBlank(resourceName)) {
            mods = Collections.emptyList();
            return true;
        }
        try {
            String modData = IOUtils.readFullyAsString(ModTranslations.class.getResourceAsStream(resourceName), StandardCharsets.UTF_8);
            mods = Arrays.stream(modData.split("\n")).filter(line -> !line.startsWith("#")).map(Mod::new).collect(Collectors.toList());
            return true;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to load " + resourceName, e);
            return false;
        }
    }

    private boolean loadCurseForgeMap() {
        if (curseForgeMap != null) {
            return true;
        }

        if (mods == null) {
            if (!loadFromResource()) return false;
        }

        curseForgeMap = new HashMap<>();
        for (Mod mod : mods) {
            if (StringUtils.isNotBlank(mod.getCurseforge())) {
                curseForgeMap.put(mod.getCurseforge(), mod);
            }
        }
        return true;
    }

    private boolean loadModIdMap() {
        if (modIdMap != null) {
            return true;
        }

        if (mods == null) {
            if (!loadFromResource()) return false;
        }

        modIdMap = new HashMap<>();
        for (Mod mod : mods) {
            for (String id : mod.getModIds()) {
                if (StringUtils.isNotBlank(id) && !"examplemod".equals(id)) {
                    modIdMap.put(id, mod);
                }
            }
        }
        return true;
    }

    private boolean loadKeywords() {
        if (keywords != null) {
            return true;
        }

        if (mods == null) {
            if (!loadFromResource()) return false;
        }

        keywords = new ArrayList<>();
        maxKeywordLength = -1;
        for (Mod mod : mods) {
            if (StringUtils.isNotBlank(mod.getName())) {
                keywords.add(pair(mod.getName(), mod));
                maxKeywordLength = Math.max(maxKeywordLength, mod.getName().length());
            }
            if (StringUtils.isNotBlank(mod.getSubname())) {
                keywords.add(pair(mod.getSubname(), mod));
                maxKeywordLength = Math.max(maxKeywordLength, mod.getSubname().length());
            }
            if (StringUtils.isNotBlank(mod.getAbbr())) {
                keywords.add(pair(mod.getAbbr(), mod));
                maxKeywordLength = Math.max(maxKeywordLength, mod.getAbbr().length());
            }
        }
        return true;
    }

    public static class Mod {
        private final String curseforge;
        private final String mcmod;
        private final String mcbbs;
        private final List<String> modIds;
        private final String name;
        private final String subname;
        private final String abbr;

        public Mod(String line) {
            String[] items = line.split(";", -1);
            if (items.length != 7) {
                throw new IllegalArgumentException("Illegal mod data line, 7 items expected " + line);
            }

            curseforge = items[0];
            mcmod = items[1];
            mcbbs = items[2];
            modIds = Collections.unmodifiableList(Arrays.asList(items[3].split(",")));
            name = items[4];
            subname = items[5];
            abbr = items[6];
        }

        public Mod(String curseforge, String mcmod, String mcbbs, List<String> modIds, String name, String subname, String abbr) {
            this.curseforge = curseforge;
            this.mcmod = mcmod;
            this.mcbbs = mcbbs;
            this.modIds = modIds;
            this.name = name;
            this.subname = subname;
            this.abbr = abbr;
        }

        public String getDisplayName() {
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(abbr)) {
                builder.append("[").append(abbr.trim()).append("] ");
            }
            builder.append(name);
            if (StringUtils.isNotBlank(subname)) {
                builder.append(" (").append(subname).append(")");
            }
            return builder.toString();
        }

        public String getCurseforge() {
            return curseforge;
        }

        public String getMcmod() {
            return mcmod;
        }

        public String getMcbbs() {
            return mcbbs;
        }

        public List<String> getModIds() {
            return modIds;
        }

        public String getName() {
            return name;
        }

        public String getSubname() {
            return subname;
        }

        public String getAbbr() {
            return abbr;
        }
    }
}
