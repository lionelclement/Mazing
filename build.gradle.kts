plugins {
    application
    id("java-library")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

group = "fr.ubordeaux.ao.mazing"
version = "1.0.1"

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Jar>().configureEach {
    from("LICENSE") { into("") }
}


tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

application {
    mainClass.set("fr.ubordeaux.ao.mazing.Main")
}

// Launch the full interactive demonstration without changing the default entry point.
tasks.register<JavaExec>("runDemo") {
    group = "application"
    description = "Runs the complete Mazing demonstration"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("fr.ubordeaux.ao.mazing.Demo")
}

// Launch the static shadow test gallery without modifying the minimal example.
tasks.register<JavaExec>("runShadowGallery") {
    group = "application"
    description = "Runs the shadow comparison scene"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("fr.ubordeaux.ao.mazing.ShadowGallery")
}

// Analyze the Dungeon Pack PNG alpha to generate reusable shadow silhouettes.
// This is an explicit task: no PNGs are regenerated during a normal build.
tasks.register<JavaExec>("generateShadowMasks") {
    group = "assets"
    description = "Generates alpha shadow silhouettes for isometric sprites"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("fr.ubordeaux.ao.mazing.tools.ShadowMaskGenerator")
}

tasks.register<JavaExec>("runMain") {
    group = "application"
    description = "Lance Main avec le cube de débogage"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("fr.ubordeaux.ao.mazing.Main")
}

// The example applications live in a separate source set and are not included
// in the library JAR. This task is intentionally separate from runShadowGallery.
tasks.register<JavaExec>("runGallery") {
    group = "application"
    description = "Lance la galerie de tous les sprites"
    mainClass.set("fr.ubordeaux.ao.mazing.Gallery")
}

// Enable compilation and execution of Demo, Gallery, Main and ShadowGallery.
apply(from = "examples.gradle.kts")
