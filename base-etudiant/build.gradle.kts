plugins {
    application
    idea
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    flatDir {
        dirs("lib")
    }
}

dependencies {
    implementation(":mazing:1.0.1")

    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

application {
    mainClass = "fr.ubordeaux.ao.project.Main"
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
