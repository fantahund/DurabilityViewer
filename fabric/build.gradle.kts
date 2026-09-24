plugins {
    id("net.fabricmc.fabric-loom")
}

val minecraftVersion = rootProject.extra["minecraftVersion"] as String
val fabricVersion = rootProject.extra["fabricVersion"] as String
val fabricApiVersion = rootProject.extra["fabricApiVersion"] as String
val modMenuVersion = rootProject.extra["modMenuVersion"] as String
val voxelConfigVersion = rootProject.extra["voxelConfigVersion"] as String
val fullVersion = rootProject.extra["fullVersion"] as String

base {
    archivesName.set("durabilityviewer-fabric")
}

val shade: Configuration by configurations.creating
configurations.named("implementation") { extendsFrom(shade) }

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")

    implementation("net.fabricmc:fabric-loader:${fabricVersion}")
    implementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
    compileOnly("com.terraformersmc:modmenu:${modMenuVersion}")
    // So the config screen can actually be opened in runClient.
    localRuntime("com.terraformersmc:modmenu:${modMenuVersion}")

    shade("de.voxelmap:voxelconfig:${voxelConfigVersion}")

    implementation(project.project(":common").sourceSets.getByName("main").output)
}

loom {
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("../run")
        }
    }
}

tasks.processResources {
    from(project.project(":common").sourceSets.getByName("main").resources)
    inputs.property("version", fullVersion)

    filesMatching("fabric.mod.json") {
        // Groovy's template engine eats backslashes, which turns the \n escapes
        // in "description" into real newlines and makes the JSON invalid for
        // every strict parser (Fabric Loader is lenient, Modrinth is not).
        expand(mapOf("version" to fullVersion)) {
            escapeBackslash.set(true)
        }
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(rootDir.resolve("build").resolve("libs"))

    from(rootDir.resolve("LICENSE")) {
        rename { "${it}_durabilityviewer" }
    }
    from(project.project(":common").sourceSets.getByName("main").output.classesDirs)
    // Loom 1.17 on Mojang mappings has no remapJar task, so the shaded classes
    // have to be merged in here - verify with `unzip -l` after changing this.
    from({ shade.map { if (it.isDirectory) it else zipTree(it) } })
    exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
}
