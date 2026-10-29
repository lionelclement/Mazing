// Examples are deliberately not compiled into the distributable main JAR.
// Applied from build.gradle.kts with: apply(from = "examples.gradle.kts")
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.JavaExec

val exampleSourceSets = extensions.getByType(SourceSetContainer::class.java)
val mainSourceSet = exampleSourceSets.getByName("main")
val examplesSourceSet = exampleSourceSets.create("examples") {
    java.setSrcDirs(listOf("src/examples/java"))
    compileClasspath += mainSourceSet.output + mainSourceSet.compileClasspath
    runtimeClasspath += mainSourceSet.runtimeClasspath
}

// These existing JavaExec tasks must execute the *examples* output, not just
// the main library JAR. Explicitly depend on compilation so invoking runDemo
// directly works even if `build` has not been executed beforehand.
// The standard application `run` task also launches Main from this source set.
tasks.withType(JavaExec::class.java).configureEach {
    if (name in setOf("run", "runDemo", "runGallery", "runMain", "runShadowGallery")) {
        dependsOn(examplesSourceSet.classesTaskName)
        classpath = examplesSourceSet.runtimeClasspath
    }
}

// Also compile the examples during `./gradlew build` without packaging them.
tasks.named("check") {
    dependsOn(examplesSourceSet.classesTaskName)
}
