# Mazing

Mazing est une bibliothèque Java/Swing pour afficher des scènes isométriques (projection dimétrique 2:1).

Ce dépôt contient les sources, les tests et les exemples de Mazing.

## Prérequis

- JDK 25 pour la construction avec Gradle (toolchain téléchargeable).
- Gradle Wrapper fourni avec le projet.
- Un environnement graphique pour les exemples Swing.

La bibliothèque produite cible **Java 21 et supérieur**.

## Compiler et tester

macOS / Linux :

```sh
./gradlew clean build
```

Windows :

```bat
.\gradlew.bat clean build
```

Le JAR est généré dans `build/libs/`.

## Lancer les exemples

```sh
./gradlew runMain
./gradlew runTest
./gradlew runGallery
```

Les sources des exemples se trouvent dans `src/examples/java/`.

## Construire la distribution

```sh
./gradlew apiDist
```

Le fichier `build/distributions/Mazing-API-2026.zip` contient le JAR, les ressources, la documentation et les licences.

Le fichier [dist/README.md](dist/README.md) explique comment utiliser cette distribution dans un autre projet Java.

## Documentation et licences

- [Guide de l'API](doc/GUIDE_API.md)
- Javadoc : `build/docs/javadoc/index.html`
- [Licence MIT](LICENSE)
- [Licences tierces](THIRD_PARTY_LICENSES.md)

Copyright (c) 2026 Lionel Clément and Nicolas Hubner.
