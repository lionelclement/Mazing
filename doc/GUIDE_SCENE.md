# Mazing — construire une scène

Ce guide montre comment ajouter des **sols, trous, murs, portes, objets et personnages**.

Ouvrez `src/main/java/fr/ubordeaux/ao/mazing/Main.java`. Les exemples ci-dessous utilisent les classes déjà importées dans ce fichier.

Pour afficher un élément, on utilise toujours :

```java
scene.addRenderable(...);
```

Les coordonnées `(x, y, z)` indiquent la position sur la grille. `x` et `y` désignent une case ; `z = 0` correspond au niveau du sol. Dans la démonstration actuelle, la grille mesure **5 × 5 cases**, numérotées de `0` à `4`.

## 1. Sols et trous

Utilisez **`FloorTile`** pour le sol. Par exemple, dans `addFloor(ViewPanel scene)` :

```java
for (int x = 0; x < GRID_SIZE; x++) {
    for (int y = 0; y < GRID_SIZE; y++) {
        scene.addRenderable(new FloorTile(
                IsometricSprites.STONE, Orientation.NORTH, x, y, 0));
    }
}
```

Pour changer une tuile, choisissez un autre sprite :

```java
scene.addRenderable(new FloorTile(
        IsometricSprites.DIRT, Orientation.NORTH, 1, 2, 0));
```

Quelques sols disponibles : `STONE`, `DIRT`, `PLANKS`, `STONE_TILE`, `STONE_MISSING_TILES`, `PLANKS_HOLE`, `BRIDGE`.

**Dessiner un trou** : remplacez la tuile par une texture de trou, au lieu de dessiner deux sols au même endroit :

```java
var floor = (x == 2 && y == 2)
        ? IsometricSprites.PLANKS_HOLE
        : IsometricSprites.STONE;

scene.addRenderable(new FloorTile(
        floor, Orientation.NORTH, x, y, 0));
```

Ce dernier exemple remplace la création d'une tuile **à l'intérieur de la double boucle** de `addFloor`.

**Laisser une case vide** : dans cette même boucle, ne créez pas de `FloorTile` pour la case concernée :

```java
if (x == 2 && y == 2) {
    continue;
}
```

Une texture de trou est **uniquement visuelle** : elle n'empêche pas un personnage de passer dessus.

## 2. Murs et portes

Utilisez **`Entity`**, comme dans `addWalls(ViewPanel scene)` :

```java
scene.addRenderable(new Entity(
        IsometricSprites.STONE_WALL,
        Orientation.NORTH, 0, 2, 0));
```

Pour une porte fermée :

```java
scene.addRenderable(new Entity(
        IsometricSprites.STONE_WALL_DOOR_CLOSED,
        Orientation.NORTH, 0, 2, 0));
```

Pour une porte ouverte, utilisez `STONE_WALL_DOOR_OPEN`. Il existe aussi `STONE_WALL_GATE_CLOSED`, `STONE_WALL_GATE_OPEN` et `STONE_WALL_WINDOW`.

**Important :** à un emplacement donné, remplacez le mur par la porte ; n'ajoutez pas les deux au même endroit. Par exemple, dans une boucle qui crée un mur :

```java
var sprite = (position == 2)
        ? IsometricSprites.STONE_WALL_DOOR_CLOSED
        : IsometricSprites.STONE_WALL;

scene.addRenderable(new Entity(
        sprite, Orientation.EAST, position, 0, 0));
```

Pour les coins, utilisez `STONE_WALL_CORNER` avec une orientation diagonale, par exemple `Orientation.NORTH_EAST`.

Les portes ouvertes ou fermées sont pour l'instant **des images** : leur affichage ne gère pas automatiquement le passage des personnages.

## 3. Objets

Utilisez **`Entity`**, comme dans `addObjects(ViewPanel scene)` :

```java
scene.addRenderable(new Entity(
        IsometricSprites.CHAIR,
        Orientation.WEST, 2, 2, 0));
```

Pour choisir la taille, ajoutez un dernier argument (`scale`) :

```java
scene.addRenderable(new Entity(
        IsometricSprites.CHAIR,
        Orientation.WEST, 2, 2, 0, 1.5));
```

`1.0` = taille normale ; `0.5` = moitié ; `2.0` = double. La taille affichée change, **pas la case occupée**.

Autres objets disponibles : `BARREL`, `WOODEN_CRATE`, `CHEST_CLOSED`, `CHEST_OPEN`, `TABLE_ROUND`, `TABLE_SHORT`.

## 4. Personnages

Utilisez **`AnimatedEntity`**. Dans `createAndShowScene()`, vous pouvez ajouter un personnage à la scène :

```java
AnimatedEntity crusader = new AnimatedEntity(
        AnimatedSprites.CRUSADER_WALK,
        Orientation.NORTH,
        1, 1, 0, 2, WITH_SHADOW);

scene.addRenderable(crusader);
```

`WITH_SHADOW` active l'ombre (l'import existe déjà dans `Main.java`). Pour un personnage sans ombre, utilisez `ShadowMode.WITHOUT_SHADOW`.

Changer son animation :

```java
crusader.setSprite(AnimatedSprites.CRUSADER_ATTACK);
```

Changer sa vitesse **d'animation** :

```java
crusader.setAnimationSpeed(1.5); // 50% faster
```

Déplacer un personnage par petites fractions de case :

```java
crusader.moveBy(0.05, 0);
```

Autres animations disponibles : `CRUSADER_IDLE`, `CRUSADER_RUN`, `CRUSADER_JUMP`, `CRUSADER_BLOCK`, `CRUSADER_HIT`, `CRUSADER_DEATH` et `SPIDER_WALK` (pour une araignée).

Dans la démonstration existante, **`CrusaderDemoSequence` contrôle déjà le croisé** : ses déplacements et ses changements de sprite automatiques peuvent remplacer les changements faits à la main. La boucle d'animation et les sons sont également déjà configurés dans `Main.java`.

## 5. Voir le résultat

Dans `createScene()`, vérifiez que les méthodes souhaitées sont appelées :

```java
addFloor(scene);
addObjects(scene);
addWalls(scene);
```

Puis, à la racine du projet :

```sh
./gradlew run
```

**À retenir :** `FloorTile` = sol ; `Entity` = mur, porte ou objet ; `AnimatedEntity` = personnage. Les sprites disponibles sont déclarés dans `IsometricSprites.java` et `AnimatedSprites.java`.
