plugins {
    id("java")
    id("idea")
    id("fabric-loom") version ("1.14-SNAPSHOT")
}

val MINECRAFT_VERSION: String by rootProject.extra
val FABRIC_LOADER_VERSION: String by rootProject.extra
val FABRIC_API_VERSION: String by rootProject.extra

val MODMENU_VERSION: String by rootProject.extra
val MALILIB_VERSION: String by rootProject.extra
val TRINKETS_VERSION: String by rootProject.extra
val CARDINAL_VERSION: String by rootProject.extra

repositories {
    maven { url = uri("https://api.modrinth.com/maven") }
    maven { url = uri("https://maven.terraformersmc.com/releases") }
    maven { url = uri("https://maven.terraformersmc.com/") }
    maven { url = uri("https://maven.ladysnake.org/releases") }
    maven { url = uri("https://maven.fallenbreath.me/releases") }
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = MINECRAFT_VERSION)
    mappings(loom.layered() {
        officialMojangMappings()
    })
    compileOnly("net.fabricmc:sponge-mixin:0.16.4+mixin.0.8.7")
    modCompileOnly("net.fabricmc:fabric-loader:$FABRIC_LOADER_VERSION")

    compileOnly("io.github.llamalad7:mixinextras-common:0.5.0")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.0")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${FABRIC_API_VERSION}")


    modApi("com.terraformersmc:modmenu:${MODMENU_VERSION}")
    modImplementation("com.github.sakura-ryoko:malilib:${MALILIB_VERSION}")

    modImplementation("dev.emi:trinkets:${TRINKETS_VERSION}")
    modApi("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${CARDINAL_VERSION}")

}

sourceSets {

}

loom {
    accessWidenerPath = file("src/main/resources/durabilityviewer.accesswidener")

    mods {
        val main by creating { // to match the default mod generated for Forge
            sourceSet("main")
        }
    }
}

tasks {
    jar {
        from(rootDir.resolve("LICENSE.md"))
    }
}

// This trick hides common tasks in the IDEA list.
tasks.configureEach {
    group = null
}