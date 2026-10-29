# Examples and demos (not part of the library JAR)

The Java rendering library lives under `src/main/java`. The runnable examples
(`Demo`, `Gallery`, `Main`, `ShadowGallery`) live under `src/examples/java`.
Their package names have not changed, so existing Gradle main class strings remain valid.

## Enable the separate examples source set

The root `build.gradle.kts` already applies the configuration:

```kotlin
apply(from = "examples.gradle.kts")
```

The script creates the `examples` source set, gives `run`, `runDemo`,
`runMain`, `runGallery` and `runShadowGallery` access to its classes, and compiles
examples on `./gradlew build` without putting them in the main JAR. It deliberately
does not redefine your tasks or their `mainClass`.

## Build / launch

```bash
./gradlew clean build
./gradlew runDemo
./gradlew runGallery
```

The compatibility `ShadowGallery` entry point is retained because some existing
Gradle tasks still use it.

## Undo

Reverse the examples-isolation Git patch if you want to restore all four Java
sources to `src/main/java`. Do not simply remove the Gradle script line: that
would leave the example sources outside the compiled source sets.
