plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.147" apply false
    id("net.minecraftforge.gradle") version "7.0.40" apply false
    id("com.gradleup.shadow") version "9.6.1" apply false
}

val minecraftVersion by extra { "26.3" }
val fabricVersion by extra { "0.19.5" }
val fabricApiVersion by extra { "0.161.0+26.3" }
val modMenuVersion by extra { "21.0.0" }
val voxelConfigVersion by extra { "1.0.2" }

val forgeVersion by extra { "66.0.3" }
val neoForgeVersion by extra { "26.3.0.16-beta" }

val durabilityViewerVersion by extra { "1.13.1" }
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
        maven {
            name = "Brokkonaut"
            url = uri("https://www.iani.de/nexus/content/groups/public/")
        }
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
}
