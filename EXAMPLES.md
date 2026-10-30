# Exemples exécutables

Les trois points d’entrée Swing se trouvent dans
`src/examples/java/fr/ubordeaux/ao/mazing/`. Ils sont compilés dans le
source set Gradle `examples`, distinct de la bibliothèque (`src/main`).
Le JAR produit par `jar` ne contient donc aucune de ces trois classes.

## Lancer une démonstration

Depuis la racine du projet, avec un JDK 25 disponible et une session graphique :

```sh
./gradlew build       # bibliothèque, compilation des exemples et tests éventuels
./gradlew run         # lancement de Main (application par défaut)
./gradlew runMain     # même démonstration
./gradlew runDemo     # alias historique de runMain : pas de classe Demo
./gradlew runTest     # démonstration complète Test
./gradlew runGallery  # galerie Gallery
```

Sous Windows, utilisez `gradlew.bat` (CMD) ou `.\gradlew.bat` (PowerShell).
Le Gradle Wrapper fourni évite d’installer Gradle séparément.

## Contenu des exemples

| Classe | Rôle | Tâches Gradle |
|---|---|---|
| `Main` | Scène 5 × 5 avec sol, cubes de débogage et personnage animé | `run`, `runMain`, `runDemo` |
| `Test` | Démonstration plus complète : plateformes, escaliers, caméra, animations, sons et ombres statiques | `runTest` |
| `Gallery` | Inspection visuelle des sprites et animations sur une grande scène | `runGallery` |

Les fenêtres sont construites avec Swing : ces tâches nécessitent un affichage
et ne conviennent pas à un environnement sans interface graphique.

## Autres tâches

`./gradlew javadoc` génère la documentation de la bibliothèque dans
`build/docs/javadoc/`. `./gradlew generateShadowMasks` produit des masques
sous `build/generated/shadow-masks/` : cette tâche est indépendante des
exemples et ne fait pas partie de `build`.

Pour les classes et méthodes de l’API, voir [le guide API](doc/GUIDE_API.md).
