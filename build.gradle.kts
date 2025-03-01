val minecraftVersion: String by project
val minecraftVersionRange: String by project
val forgeVersion: String by project
val forgeVersionRange: String by project
val loaderVersionRange: String by project
val mappingChannel: String by project
val mappingVersion: String by project

val modId: String by project
val modName: String by project
val modLicense: String by project
val modVersion: String by project
val modGroupId: String by project
val modAuthors: String by project
val modDescription: String by project

val self = this

plugins {
    kotlin("jvm") version "1.9.25"
    id("net.minecraftforge.gradle") version "6.0.26"
}

group = modGroupId
version = modVersion

base {
    archivesName = modId
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    minecraft("net.minecraftforge:forge:${self.minecraftVersion}-${self.forgeVersion}")

    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

minecraft {
    mappings(self.mappingChannel, self.mappingVersion)
    reobf = false

    enableIdeaPrepareRuns = false
    copyIdeResources = true
    generateRunFolders = false

    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
        }

        create("client") {
            property("forge.enabledGameTestNamespaces", self.modId)
        }

        create("server") {
            property("forge.enabledGameTestNamespaces", self.modId)
            args("--nogui")
        }

        create("gameTestServer") {
            property("forge.enabledGameTestNamespaces", self.modId)
        }

        create("data") {
            workingDirectory(project.file("run-data"))
            args("--mod", self.modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
        }
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }

    all {
        val dir = layout.buildDirectory.dir("sourcesSets/${name}")
        output.setResourcesDir(dir)
        kotlin.destinationDirectory.set(dir)
    }
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties = mapOf(
        "minecraft_version" to self.minecraftVersion,
        "minecraft_version_range" to self.minecraftVersionRange,
        "forge_version" to self.forgeVersion,
        "forge_version_range" to self.forgeVersionRange,
        "loader_version_range" to self.loaderVersionRange,
        "mod_id" to self.modId,
        "mod_name" to self.modName,
        "mod_license" to self.modLicense,
        "mod_version" to self.modVersion,
        "mod_authors" to self.modAuthors,
        "mod_description" to self.modDescription,
    )
    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties + mapOf("project" to project))
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Specification-Title" to self.modId,
            "Specification-Vendor" to self.modAuthors,
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.tasks.getByName<Jar>("jar").archiveVersion.get(),
            "Implementation-Vendor" to self.modAuthors
        )
    }
}
