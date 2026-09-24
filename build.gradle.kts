plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.147" apply false
    id("net.minecraftforge.gradle") version "7.0.17" apply false
}

val minecraftVersion by extra { "26.3" }
val fabricVersion by extra { "0.19.5" }
val fabricApiVersion by extra { "0.160.5+26.3" }
val modMenuVersion by extra { "21.0.0-beta.1" }
val voxelConfigVersion by extra { "1.0.2" }

// Forge has not released for 26.3 yet, so :forge stays out of
// settings.gradle.kts - this follows their version scheme but is a guess.
val forgeVersion by extra { "66.0.0" }
val neoForgeVersion by extra { "26.3.0.0-beta" }

val durabilityViewerVersion by extra { "1.13.0" }
val fullVersion by extra { "${minecraftVersion}-${durabilityViewerVersion}" }

tasks.jar {
    enabled = false
}

allprojects {
    apply(plugin = "java")

    group = "de.guntram.mcmod"
    version = fullVersion

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://maven.terraformersmc.com/releases") { name = "TerraformersMC" }
        maven(url = "https://maven.ladysnake.org/releases") { name = "Ladysnake Libs" }
        maven(url = "https://api.modrinth.com/maven") {
            name = "Modrinth"
            content { includeGroup("maven.modrinth") }
        }
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
}
