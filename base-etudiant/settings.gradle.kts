plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "ao-project"

include("project00")
project(":project00").projectDir = file("examples/project00")
include("project01")
project(":project01").projectDir = file("examples/project01")
include("project02")
project(":project02").projectDir = file("examples/project02")