plugins {
    id("net.minecraftforge.gradle")
    id("com.gradleup.shadow")
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

// Forge loads every mod as a JPMS module, and two modules may not contain the
// same package. VoxelMap also bundles VoxelConfig, so a plain copy crashes with
// "Modules voxelmap and durabilityviewer export package de.voxelmap.voxelconfig".
// Relocating our copy into our own package avoids the split package.
tasks.jar {
    archiveClassifier.set("slim")
}

tasks.shadowJar {
    archiveClassifier.set("")
    destinationDirectory.set(rootDir.resolve("build").resolve("libs"))
    configurations.set(listOf(shade))
    relocate("de.voxelmap.voxelconfig", "de.guntram.mcmod.durabilityviewer.shadow.voxelconfig")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["MixinConfigs"] = "mixins.durabilityviewer.json"
    }

    from(rootDir.resolve("LICENSE")) {
        rename { "${it}_durabilityviewer" }
    }
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA", "module-info.class")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.withType<Test> {
    enabled = false
}
