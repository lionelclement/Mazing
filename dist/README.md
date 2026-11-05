# Mazing — Distribution de l'API

Cette distribution permet d'utiliser Mazing dans un projet Java sans Gradle.

## Contenu

- `lib/mazing.jar` : bibliothèque compilée ;
- `resources/` : textures et sons ;
- `doc/` : guide API, Javadoc et licences.

## Prérequis

**JDK 21 ou supérieur** pour compiler et exécuter les programmes utilisant Mazing.

Vérifier :

```sh
java -version
javac -version
```

Un environnement graphique est nécessaire pour Swing.

## Compiler un programme

Placer `MonProgramme.java` dans ce dossier, sans déclaration de package.

macOS / Linux :

```sh
mkdir -p out
javac -cp "lib/mazing.jar" -d out MonProgramme.java
```

Windows :

```bat
mkdir out
javac -cp "lib\mazing.jar" -d out MonProgramme.java
```

## Exécuter le programme

macOS / Linux :

```sh
java -cp "out:resources:lib/mazing.jar" MonProgramme
```

Windows :

```bat
java -cp "out;resources;lib\mazing.jar" MonProgramme
```

**Important :** le dossier `resources/` doit être présent dans le classpath pour charger les textures. Si le programme déclare un package Java, utiliser le nom complet de sa classe principale.

Le JAR est une bibliothèque et ne se lance pas directement avec `java -jar`.

## Documentation et licences

- [Guide de l'API](doc/GUIDE_API.md)
- [Javadoc](doc/javadoc/index.html)
- [Licence MIT](LICENSE)
- [Licences tierces](doc/THIRD_PARTY_LICENSES.md)

Copyright (c) 2026 Lionel Clément and Nicolas Hubner.
