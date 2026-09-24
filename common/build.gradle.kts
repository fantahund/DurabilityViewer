plugins {
    id("net.fabricmc.fabric-loom")
}

val minecraftVersion = rootProject.extra["minecraftVersion"] as String
val fabricVersion = rootProject.extra["fabricVersion"] as String
val voxelConfigVersion = rootProject.extra["voxelConfigVersion"] as String

// VoxelConfig is published in named (Mojang) mappings and is shaded into each
// loader jar, so it has to be visible to every module that compiles common.
val shade: Configuration by configurations.creating
configurations.named("implementation") { extendsFrom(shade) }
configurations.named("apiElements") { extendsFrom(shade) }

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    compileOnly("net.fabricmc:fabric-loader:${fabricVersion}")
    compileOnly("net.fabricmc:sponge-mixin:0.17.4+mixin.0.8.7")

    shade("de.voxelmap:voxelconfig:${voxelConfigVersion}")
}

loom {
    mods {
        create("main") {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Common is compiled into each loader jar, never shipped on its own.
tasks.jar {
    enabled = false
}
