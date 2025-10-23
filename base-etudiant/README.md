# Base de projet

Vous pouvez exécuter le projet avec :
```bash
./gradlew run
```

Des examples sont disponibles dans le dossier `examples/` et il est possible de les lancer avec :
```shell
./gradlew :projectX:run
```
En remplaçant `X` par le numéro de l'exemple, c'est-à-dire `00`, `01`, ou `02`.

# Mettre à jour Mazing

Les jars de la bibliothèque mazing doivent être mis dans le dossier `lib/` avec le nom `mazing-v.jar` (en remplaçant `v` par le numéro de version).
Il faut ensuite modifier la ligne suivante dans les fichiers `build.gradle.kts` pour prendre en compte la version utilisée :
```kotlin
implementation(":mazing:1.0.1")  // ici, là version de mazing utilisée est 1.0.1
```

# Version de Java

Le projet utilise Java 23, mais il est possible de changer la version utilisée en modifiant les fichiers `build.gradle.kts`:
```kotlin
languageVersion = JavaLanguageVersion.of(23)  // changer 23 par la version voulue
```