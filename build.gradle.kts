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
