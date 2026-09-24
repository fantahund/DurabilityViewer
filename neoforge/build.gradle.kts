plugins {
    id("net.neoforged.moddev")
}

val neoForgeVersion = rootProject.extra["neoForgeVersion"] as String
val voxelConfigVersion = rootProject.extra["voxelConfigVersion"] as String
val fullVersion = rootProject.extra["fullVersion"] as String

base {
    archivesName.set("durabilityviewer-neoforge")
}

// FML only puts located mods on the game classloader; everything else on the
// classpath goes to the parent, where Minecraft is a separate copy. VoxelConfig
// extends Screen, so leaving it on `implementation` makes runClient die with
// "ConfigScreen is not assignable to Screen". Unpacking it into an output
// directory of the mod's source set puts it in the mod's own module instead -
// the same route :common already takes.
val shade: Configuration by configurations.creating
configurations.named("compileOnly") { extendsFrom(shade) }

dependencies {
    compileOnly(project.project(":common").sourceSets.getByName("main").output)
    shade("de.voxelmap:voxelconfig:${voxelConfigVersion}")
}

val unpackShadedLibs by tasks.registering(Sync::class) {
    from({ shade.map { if (it.isDirectory) it else zipTree(it) } })
    into(layout.buildDirectory.dir("shaded-libs"))
    exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
}

sourceSets.main.get().output.dir(
    mapOf("builtBy" to unpackShadedLibs),
    layout.buildDirectory.dir("shaded-libs"))

neoForge {
    version = neoForgeVersion

    runs {
        create("client") {
            client()
        }
    }

    mods {
        create("durabilityviewer") {
            sourceSet(sourceSets.main.get())
            sourceSet(project.project(":common").sourceSets.getByName("main"))
        }
    }
}

tasks.named<JavaCompile>("compileJava") {
    source(project(":common").sourceSets.main.get().java.srcDirs)
}

tasks.processResources {
    from(project.project(":common").sourceSets.getByName("main").resources.srcDirs) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    inputs.property("version", fullVersion)
    filesMatching("META-INF/neoforge.mods.toml") {
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
    exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
}

tasks.withType<Test> {
    enabled = false
}

// NFRT defaults to a Java 21 launcher. moddev overrides that for
// createMinecraftArtifacts (26.3 needs Java 25) but not for downloadAssets, so
// an IntelliJ import fails on "Cannot find a Java installation ... 21" unless a
// JDK 21 happens to be installed. downloadAssets only downloads files, so the
// project toolchain works just as well - drop this once moddev fixes it.
val javaToolchains = extensions.getByType<JavaToolchainService>()
val nfrtLauncher = javaToolchains.launcherFor(java.toolchain)
    .map { it.executablePath.asFile.absolutePath }

tasks.withType<net.neoforged.nfrtgradle.NeoFormRuntimeTask>().configureEach {
    javaExecutable.set(nfrtLauncher)
}
