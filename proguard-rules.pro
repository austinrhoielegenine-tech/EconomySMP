# EconomySMP ProGuard rules.
# ProGuard runs on the plugin's own classes before Maven Shade adds libraries.

# Keep the Bukkit entry point because plugin.yml refers to its exact name.
-keep class com.archiveaustxn.EconomySMP {
    *;
}

# Keep public integration classes: other plugins may link to these by name.
-keep class com.archiveaustxn.api.** {
    *;
}

# Bukkit discovers event handlers through runtime annotations. Keep the
# annotated methods, while still allowing their names to be obfuscated.
-keepclassmembers,allowobfuscation class * {
    @org.bukkit.event.EventHandler <methods>;
}

# PlaceholderAPI discovers expansion implementations and invokes their
# overridden methods reflectively.
-keep class * extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    *;
}

# Preserve runtime metadata used by Bukkit, PlaceholderAPI, reflection,
# generics, records, and useful stack traces.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Record,Exceptions,SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Adapt class-name string constants when ProGuard renames matching classes.
-adaptclassstrings

# Keep the build conservative: rename classes/members without deleting code
# or changing control flow. This reduces risk with Bukkit and optional hooks.
-dontshrink
-dontoptimize

# The JDK 25 runner image does not expose a readable java.base.jmod path.
# Keep unresolved library references as warnings so obfuscation can proceed.
-ignorewarnings

# SkinsRestorer is an optional server-provided API and is excluded from ProGuard's
# library inputs because its current build targets Java 25.
-dontwarn net.skinsrestorer.**

# Keep an obfuscation map in target/ for diagnosing production stack traces.
-printmapping target/proguard-mapping.txt
