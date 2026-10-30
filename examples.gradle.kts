import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.JavaExec

// The library JAR contains only src/main; the Swing demos live in src/examples.
val sets = extensions.getByType<SourceSetContainer>()
val library = sets.getByName("main")
val examples = sets.create("examples") {
    java.srcDir("src/examples/java")
    compileClasspath += library.output + library.compileClasspath
    runtimeClasspath += library.output + library.runtimeClasspath
}

// A build checks that the examples still compile, but does not bundle them.
tasks.named("check") {
    dependsOn(examples.classesTaskName)
}

// The application plugin creates run; point it at the examples classpath.
tasks.named<JavaExec>("run") {
    dependsOn(examples.classesTaskName)
    classpath = examples.runtimeClasspath
}

fun registerDemo(taskName: String, entryPoint: String, details: String) {
    tasks.register<JavaExec>(taskName) {
        group = "application"
        description = details
        dependsOn(examples.classesTaskName)
        classpath = examples.runtimeClasspath
        mainClass.set(entryPoint)
    }
}

registerDemo("runMain", "fr.ubordeaux.ao.mazing.Main", "Runs the minimal isometric scene")
registerDemo("runDemo", "fr.ubordeaux.ao.mazing.Main", "Alias for the minimal example")
registerDemo("runTest", "fr.ubordeaux.ao.mazing.Test", "Runs the interactive demonstration")
registerDemo("runGallery", "fr.ubordeaux.ao.mazing.Gallery", "Runs the sprite gallery")
