plugins {
    id("java")
    id("fabric-loom") version ("1.14-SNAPSHOT") apply (false)
    id("net.minecraftforge.gradle") version ("6.0.47") apply (false)
    id("net.neoforged.moddev") version ("2.0.137") apply (false)
    id("org.spongepowered.mixin") version ("0.7-SNAPSHOT") apply (false)
}

val MINECRAFT_VERSION by extra { "1.21.11" }
val FORGE_VERSION by extra { "61.0.1" }
val NEOFORGE_VERSION by extra { "21.11.21-beta" }
val FABRIC_LOADER_VERSION by extra { "0.18.2" }
val FABRIC_API_VERSION by extra { "0.140.0+1.21.11" }
val DURABILITY_VIEWER_VERSION by extra { "1.11.2" }

val MODMENU_VERSION by extra { "17.0.0-alpha.1" }
val MALILIB_VERSION by extra { "1.21.11-0.27.0" }
val TRINKETS_VERSION by extra { "3.7.1" }
val CARDINAL_VERSION by extra { "6.1.1" }

val MOD_VERSION by extra { "$MINECRAFT_VERSION-$DURABILITY_VIEWER_VERSION" }

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    enabled = false
}

subprojects {
    apply(plugin = "maven-publish")

    repositories {
        maven { url = uri("https://api.modrinth.com/maven") }
    }

    java.toolchain.languageVersion = JavaLanguageVersion.of(21)

    tasks.processResources {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to MOD_VERSION))
        }
    }

    version = MOD_VERSION
    group = "de.guntram.mcmod"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    tasks.withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }
}
