rootProject.name = "durabilityviewer"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://maven.fabricmc.net/") { name = "Fabric" }
        maven(url = "https://maven.neoforged.net/releases") { name = "NeoForge" }
        maven(url = "https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
include("neoforge")
// Forge has not released for 26.3 yet. :forge only consumes :common, so
// uncommenting this line is all it takes once it does - check forgeVersion in
// build.gradle.kts first.
//include("forge")
