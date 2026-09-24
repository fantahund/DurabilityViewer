plugins {
    id("net.minecraftforge.gradle")
}

val minecraftVersion = rootProject.extra["minecraftVersion"] as String
val forgeVersion = rootProject.extra["forgeVersion"] as String
val voxelConfigVersion = rootProject.extra["voxelConfigVersion"] as String
val fullVersion = rootProject.extra["fullVersion"] as String

base {
    archivesName.set("durabilityviewer-forge")
}

sourceSets {
    all {
        val dir = layout.buildDirectory.dir("sourcesSets/${this.name}")
        output.setResourcesDir(dir)
        java.destinationDirectory.set(dir)
    }
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    maven { url = uri("https://maven.minecraftforge.net/") }
}

val shade: Configuration by configurations.creating
configurations.named("implementation") { extendsFrom(shade) }

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}"))
    compileOnly(project.project(":common").sourceSets.getByName("main").output)
    shade("de.voxelmap:voxelconfig:${voxelConfigVersion}")
}

minecraft {
    runs {
        create("client") {
            workingDir.set(rootProject.file("run"))
            args("--mixin.config=mixins.durabilityviewer.json")
            mods {
                create("durabilityviewer") {
                    source(sourceSets.main.get())
                    source(project.project(":common").sourceSets.getByName("main"))
                }
            }
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
    filesMatching("META-INF/mods.toml") {
        expand(mapOf("version" to fullVersion)) {
            escapeBackslash.set(true)
        }
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(rootDir.resolve("build").resolve("libs"))

    manifest {
        attributes["MixinConfigs"] = "mixins.durabilityviewer.json"
    }

    from(rootDir.resolve("LICENSE")) {
        rename { "${it}_durabilityviewer" }
    }
    from({ shade.map { if (it.isDirectory) it else zipTree(it) } })
    exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
}

tasks.withType<Test> {
    enabled = false
}
