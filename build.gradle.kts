plugins {
    application
    id("java-library")
}

repositories {
    mavenCentral()
}

group = "fr.ubordeaux.ao.mazing"
version = "1.0.1"

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

tasks.withType<Jar>().configureEach {
    from("LICENSE") { into("") }
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("fr.ubordeaux.ao.mazing.Main")
}

// Keep executable demos separate from the published library classes.
apply(from = "examples.gradle.kts")

// This tool belongs to the library sources, not to the examples source set.
tasks.register<JavaExec>("generateShadowMasks") {
    group = "assets"
    description = "Generates alpha shadow silhouettes for isometric sprites"
    dependsOn(tasks.named("classes"))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("fr.ubordeaux.ao.mazing.tools.ShadowMaskGenerator")
}


// Distribution binaire de l'API.
tasks.register<Zip>("apiDist") {
    group = "distribution"
    description = "Construit le ZIP de l'API Mazing"

    dependsOn("jar", "javadoc", "processResources")

    archiveFileName.set("Mazing-API-2026.zip")
    destinationDirectory.set(
        layout.projectDirectory.dir("dist")
    )

    into("Mazing-API-2026") {
        from("dist/README.md")
        from("LICENSE")

        from(tasks.named<Jar>("jar")) {
            into("lib")
            rename { "mazing.jar" }
        }

        from(layout.buildDirectory.dir("resources/main")) {
            into("resources")
        }

        from(listOf("doc/GUIDE_API.md", "THIRD_PARTY_LICENSES.md")) {
            into("doc")
        }

        from(layout.buildDirectory.dir("docs/javadoc")) {
            into("doc/javadoc")
        }

        from("src/main/resources/sounds/license.txt") {
            into("doc/notices/sounds")
        }

        from("src/main/resources/isometric-Mini-Crusader/License.txt") {
            into("doc/notices/isometric-Mini-Crusader")
        }

        // Exemple Java fourni avec la distribution.
        from("src/examples/java/fr/ubordeaux/ao/mazing/Main.java") {
            into("examples/fr/ubordeaux/ao/mazing")
        }
    }
}
