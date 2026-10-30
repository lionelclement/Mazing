# Mazing

Mazing est une bibliothèque Java/Swing de rendu de scènes **isométriques (dimétriques 2:1)**.
Le moteur et ses ressources se trouvent dans `src/main/`. Trois démonstrations
graphiques sont isolées dans `src/examples/java/` : `Main`, `Test` et `Gallery`.
Elles ne sont pas faites pour montrer un code modèle, mais pour montrer des exemples.

## Prérequis

- **JDK 25**, utilisé pour compiler le code source. Vérifiez avec `java -version`.
- Un environnement graphique pour exécuter les exemples Swing.
- Une connexion Internet lors du premier lancement de Gradle, pour obtenir
  sa distribution et les éventuelles dépendances.

Le **Gradle Wrapper** est fourni : inutile d’installer Gradle séparément.
Le projet configure une toolchain Java 25 : un JDK 25 doit être disponible
localement ou via un mécanisme de téléchargement de toolchain configuré
dans Gradle.

## Compiler et exécuter

Depuis la racine du dépôt (le dossier contenant `gradlew` et `build.gradle.kts`) :

**macOS / Linux**

```sh
chmod +x gradlew   # seulement si le fichier n'est pas exécutable
./gradlew build
./gradlew run
```

**Windows (PowerShell)**

```powershell
.\\gradlew.bat build
.\\gradlew.bat run
```

**Windows (CMD)**

```bat
gradlew.bat build
gradlew.bat run
```

`build` compile la bibliothèque et vérifie la compilation des exemples
via la tâche `check` ; les tests JUnit sont exécutés s’il y en a. `run` démarre la scène minimale (`fr.ubordeaux.ao.mazing.Main`).
Les autres démonstrations sont disponibles avec les tâches suivantes :

| Tâche | Classe exécutée | Contenu |
|---|---|---|
| `run` / `runMain` / `runDemo` | `Main` | Sol 5 × 5, personnage animé et cubes de débogage |
| `runTest` | `Test` | Décor à plusieurs niveaux, personnages, déplacements, caméra et sons |
| `runGallery` | `Gallery` | Galerie des sprites fixes et animés |

Sous Windows, lancez ces tâches via `gradlew.bat`. Voir aussi
[EXAMPLES.md](EXAMPLES.md).

## Bibliothèque et documentation

Le JAR de bibliothèque est produit dans `build/libs/` par `./gradlew jar`
(ou `./gradlew build`). **Il ne contient pas les classes des exemples** et
ne doit pas être lancé avec `java -jar`. Les ressources situées dans `src/main/resources/` sont incluses dans ce JAR.

Pour générer la Javadoc :

```sh
./gradlew javadoc
./gradlew javadocJar
```

La documentation HTML est placée dans `build/docs/javadoc/index.html` et
l'archive Javadoc dans `build/libs/`.

La tâche optionnelle `./gradlew generateShadowMasks` extrait des silhouettes
depuis les images de `src/main/resources/Dungeon-Pack/Isometric` et les écrit
sous `build/generated/shadow-masks/`. Elle n'est pas exécutée par `build`.

## En cas de problème

- **JDK incorrect / erreur de compilation** : utilisez JDK 25 et vérifiez
  `java -version` et `JAVA_HOME`. La compilation utilise une toolchain Java 25.
- **`Permission denied`** : exécutez `chmod +x gradlew` sur macOS/Linux.
- **Pas de fenêtre** : les démonstrations Swing exigent une session graphique.
- **Téléchargement impossible** : vérifiez la connexion au réseau ou la
  configuration des dépôts et des toolchains Gradle.

## Références

- [Guide de l'API](doc/GUIDE_API.md)
- [Exemples](EXAMPLES.md)

Le code de Mazing est distribué sous licence MIT (`LICENSE`). Les ressources
tierces conservent leurs licences respectives (voir les fichiers de licence
présents sous `src/main/resources/`).
