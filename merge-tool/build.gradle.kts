plugins {
    java
    application
    idea
    id("org.openjfx.javafxplugin")  version "0.1.0"
}

group = "fr.ubordeaux.ao"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "23"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
}

application {
    mainClass = "fr.ubordeaux.ao.MergePng"
}


// IntelliJ IDEA doesn't download source/javadoc by default anymore
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}