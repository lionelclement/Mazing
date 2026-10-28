# Guide de l'API Mazing

Ce guide présente les classes à utiliser pour créer une scène isométrique.

Pour construire une scène, voir aussi [GUIDE_SCENE.md](GUIDE_SCENE.md).
Pour la référence complète des méthodes, générer la Javadoc.

## 1. Démarrer

Mazing utilise Java 25, Swing et Gradle.

```bash
./gradlew clean build run
```

Pour générer la Javadoc :

```bash
./gradlew javadoc
```

Ouvrir ensuite `build/docs/javadoc/index.html`.

## 2. Créer un moteur de rendu

```java
IsometricRenderer renderer =
        new IsometricRenderer(150, 320, 256, 128, 60);

ViewPanel scene = new ViewPanel(800, 600, renderer);
```

Les arguments du moteur définissent :

| Argument | Signification |
| --- | --- |
| `150` | Position horizontale de l'origine |
| `320` | Position verticale de l'origine |
| `256` | Largeur diagonale d'une tuile |
| `128` | Hauteur diagonale d'une tuile |
| `60` | Hauteur d'un niveau |

Les coordonnées `(x, y, z)` des entités sont exprimées dans
le repère de la grille, et non directement en pixels.

Pour afficher les informations de débogage :

```java
renderer.setDebug(true);
```

## 3. Ajouter des éléments à la scène

Tous les éléments visibles sont ajoutés à `ViewPanel` :

```java
scene.addRenderable(element);
```

Les éléments implémentent l'interface `Renderable`.

Le moteur gère leur position isométrique et leur ordre d'affichage.

## 4. Sols

Un sol utilise `FloorTile` et un sprite d'`IsometricSprites`.

```java
scene.addRenderable(new FloorTile(
        IsometricSprites.STONE,
        Orientation.NORTH,
        2, 3, 0));
```

Pour créer un sol de 5 × 5 cases :

```java
for (int x = 0; x < 5; x++) {
    for (int y = 0; y < 5; y++) {
        scene.addRenderable(new FloorTile(
                IsometricSprites.STONE,
                Orientation.NORTH,
                x, y, 0));
    }
}
```

Pour laisser une case vide, ne pas créer de tuile à cette position.

Une texture représentant un trou est différente d'une case
réellement absente : dans le premier cas, le trou est graphique.

## 5. Murs et portes

Les murs et les portes sont des entités statiques :

```java
scene.addRenderable(new Entity(
        IsometricSprites.STONE_WALL,
        Orientation.EAST,
        2, 0, 0));
```

L'orientation choisit le côté ou la variante du sprite.

Les portes sont pour l'instant des éléments graphiques :
leur ouverture et les collisions nécessitent une logique supplémentaire.

Consulter `IsometricSprites` pour les noms exacts des sprites disponibles.

## 6. Objets

Une chaise est une `Entity` :

```java
Entity chair = new Entity(
        IsometricSprites.CHAIR,
        Orientation.WEST,
        2, 2, 0);

scene.addRenderable(chair);
```

Pour changer sa taille :

```java
chair.setScale(1.75);
```

Ou directement dans le constructeur :

```java
scene.addRenderable(new Entity(
        IsometricSprites.CHAIR,
        Orientation.WEST,
        2, 2, 0,
        1.75));
```

`1.0` est la taille normale. Une valeur inférieure réduit l'objet.

Le scale change l'apparence, pas les coordonnées de grille.

## 7. Personnages animés

Les personnages utilisent `AnimatedEntity`.

```java
AnimatedEntity crusader = new AnimatedEntity(
        AnimatedSprites.CRUSADER_WALK,
        Orientation.NORTH,
        1, 1, 0, 2,
        WITH_SHADOW);

scene.addRenderable(crusader);
```

`WITH_SHADOW` active l'ombre intégrée au sprite animé.

Sans ce mode, le personnage peut être créé sans ombre.

## 8. Animations disponibles

La classe `AnimatedSprites` expose notamment :

| Personnage | Animations |
| --- | --- |
| Crusader | `WALK`, `RUN`, `IDLE`, `JUMP` |
| Crusader | `BLOCK`, `ATTACK`, `HIT`, `DEATH` |
| Spider | `WALK`, `ATTACK`, `DEATH` |

Exemple :

```java
crusader.setSprite(AnimatedSprites.CRUSADER_ATTACK);
```

Les atlas du personnage et de son ombre sont synchronisés
par le moteur d'animation.

## 9. Vitesse des animations

```java
crusader.setAnimationSpeed(1.0);
crusader.setAnimationSpeed(1.5);
crusader.setAnimationSpeed(2.0);
```

`1.0` représente la vitesse normale.

La vitesse de lecture des images est indépendante de la
vitesse de déplacement du personnage.

Une animation immobile doit garder une vitesse strictement positive.

## 10. Déplacement continu

Un personnage animé possède des coordonnées précises :

```java
double x = crusader.getPreciseX();
double y = crusader.getPreciseY();
```

Pour avancer d'une fraction de case :

```java
crusader.moveBy(0.05, 0.0);
```

Pour un mouvement régulier, calculer la distance selon
le temps écoulé :

```java
double speed = 0.75; // Tiles per second.
double distance = speed * elapsedSeconds;
crusader.moveBy(distance, 0.0);
```

Les méthodes `getX()` et `getY()` fournissent les coordonnées
entières de compatibilité.

## 11. Orientations

`Orientation` propose huit directions :

`NORTH`, `NORTH_EAST`, `EAST`, `SOUTH_EAST`,
`SOUTH`, `SOUTH_WEST`, `WEST`, `NORTH_WEST`.

Exemple :

```java
crusader.setOrientation(Orientation.NORTH_EAST);
```

Attention : l'orientation affichée dépend de la convention
de l'atlas. Dans la démonstration actuelle, les orientations
des sprites du croisé sont décalées par rapport aux axes
de déplacement sur la grille.

Voir `Main.java` et sa méthode `spriteOrientation()`.

## 12. Sons liés aux animations

Un personnage peut notifier chaque changement d'image :

```java
crusader.setAnimationFrameListener(frame -> {
    if (frame == 1) {
        // Play a sound here.
    }
});
```

Cela permet de synchroniser les pas, impacts ou grognements
avec les images, sans temporisateur audio supplémentaire.

Le projet contient des effets sonores synthétisés dans
le package `view.sound`.

## 13. Rafraîchissement et animation

Deux rythmes sont utilisés dans la démonstration :

- Le rendu Swing, par exemple 60 FPS.
- L'avancement des sprites, par exemple 24 FPS.

Le déplacement est calculé selon le temps réel écoulé.

L'animation d'un personnage n'a donc pas besoin d'avancer
à chaque rafraîchissement de l'écran.

## 14. Ordre de rendu

`FloorTile` est dessiné sous les objets et les personnages.

Les objets et personnages utilisent ensuite leurs positions
et leur ordre de rendu pour gérer leur superposition.

Pour un nouveau type d'élément, consulter :

- `Renderable`
- `getRenderLayer()`
- `getBlitOrder()`
- `compareTo()`

Ne pas confondre ordre graphique et collisions :
Mazing ne fournit pas automatiquement un système de physique.

## 15. Classes principales

| Classe | Utilité |
| --- | --- |
| `ViewPanel` | Conteneur Swing de la scène |
| `IsometricRenderer` | Projection grille → écran |
| `Renderable` | Contrat des éléments visibles |
| `FloorTile` | Sol |
| `Entity` | Objet statique, mur, porte |
| `AnimatedEntity` | Personnage animé |
| `Orientation` | Huit directions |
| `IsometricSprites` | Catalogue des sprites statiques |
| `AnimatedSprites` | Catalogue des animations |
| `TextureAtlas` | Chargement des textures |
| `TextureAtlases` | Catalogue des atlas |

Pour découvrir toutes les signatures publiques avec leurs
paramètres, consulter la Javadoc générée par Gradle.

## 16. Limites actuelles

L'API fournit principalement un moteur graphique :

- Pas de collisions automatiques.
- Pas de recherche de chemin intégrée.
- Pas d'ouverture automatique des portes.
- Pas de simulation physique.
- Pas de déclenchement automatique des animations selon
  le déplacement.

Ces comportements peuvent être ajoutés dans le programme
utilisant Mazing.

## 17. Licences

Le code de Mazing est proposé sous licence MIT.

Les ressources graphiques ont leurs propres licences.
Voir [ASSET_LICENSES.md](ASSET_LICENSES.md).

Ne pas supposer qu'une image est couverte par la licence MIT
simplement parce qu'elle se trouve dans le même dépôt.
