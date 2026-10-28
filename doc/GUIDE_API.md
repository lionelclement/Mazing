# Mazing — Guide d'utilisation de l'API

Mazing permet de construire et de modifier la vue isométrique d'un jeu vidéo.

Le programme utilisateur décide des éléments présents dans la scène, de leurs positions, de leurs orientations et de leurs animations. Mazing assure leur affichage.

Les exemples supposent qu'une scène `ViewPanel scene` est déjà disponible. Les classes `Main` et `Demo` fournissent des exemples d'intégration.

## 1. Coordonnées et orientations

Les positions sont exprimées en coordonnées `(x, y, z)` :

- `x`, `y` : position sur la grille ;
- `z` : niveau (`0` pour le sol, `1` pour le premier étage, etc.).

Un objet placé sur une tuile utilise les mêmes coordonnées que celle-ci.

Les orientations disponibles dans `Orientation` sont :

`NORTH`, `NORTH_EAST`, `EAST`, `SOUTH_EAST`, `SOUTH`, `SOUTH_WEST`, `WEST`, `NORTH_WEST`.

Chaque sprite dispose de ses propres variantes graphiques.

## 2. Poser une tuile

**Classe :** `FloorTile`

```java
new FloorTile(Sprite sprite, Orientation orientation,
              int x, int y, int z)
```

Ajout à la scène :

```java
scene.addRenderable(new FloorTile(
        IsometricSprites.STONE, Orientation.NORTH, x, y, z));
```

Une tuile peut être placée à n'importe quel niveau `z`.

`PLANKS_HOLE` désigne une tuile percée. Pour laisser une case entièrement vide, il suffit de ne pas y placer de tuile.

### Catalogue des tuiles

Toutes les constantes suivantes appartiennent à `IsometricSprites`.

| Catégorie | Sprites |
|---|---|
| Ponts | `BRIDGE`, `BRIDGE_BROKEN` |
| Terre | `DIRT`, `DIRT_TILES` |
| Bois | `PLANKS`, `PLANKS_BROKEN`, `PLANKS_HOLE` |
| Pierre | `STONE`, `STONE_TILE`, `STONE_UNEVEN`, `STONE_INSET`, `STONE_CORNER`, `STONE_LEFT`, `STONE_RIGHT`, `STONE_SIDE`, `STONE_SIDE_UNEVEN`, `STONE_MISSING_TILES`, `STONE_STEP`, `STONE_STEPS` |

## 3. Poser un objet ou une structure

**Classe :** `Entity`

```java
new Entity(Sprite sprite, Orientation orientation,
           int x, int y, int z)

new Entity(Sprite sprite, Orientation orientation,
           int x, int y, int z, double scale)
```

La classe `Entity` permet de placer le mobilier, les objets et les éléments architecturaux.

```java
Entity object = new Entity(
        IsometricSprites.CHAIR, Orientation.WEST, x, y, z);

scene.addRenderable(object);
```

### Modifier un objet

| Commande | Effet |
|---|---|
| `setX(int)`, `setY(int)`, `setZ(int)` | Définir sa position |
| `move(int dx, int dy, int dz)` | Déplacer l'objet |
| `setOrientation(Orientation)` | Changer son orientation |
| `setSprite(Sprite)` | Changer son apparence |
| `setScale(double)` | Changer sa taille apparente |

### Mobilier et objets (15)

`BARREL`, `BARRELS_STACKED`, `BARRELS`, `CHAIR`, `CHEST_CLOSED`, `CHEST_OPEN`, `TABLE_CHAIRS_BROKEN`, `TABLE_ROUND_CHAIRS`, `TABLE_ROUND_ITEMS_CHAIRS`, `TABLE_ROUND`, `TABLE_SHORT_CHAIRS`, `TABLE_SHORT`, `WOODEN_CRATE`, `WOODEN_CRATES`, `WOODEN_PILE`.

### Colonnes et constructions (6)

`STONE_COLUMN_WOOD`, `STONE_COLUMN`, `WOODEN_SUPPORT_BEAMS`, `WOODEN_SUPPORTS_BEAM`, `WOODEN_SUPPORTS_BLOCK`, `WOODEN_SUPPORTS`.

### Escaliers (4)

`STAIRS_AGED`, `STAIRS_CORNER`, `STAIRS_SPIRAL`, `STAIRS`.

### Murs, portes et grilles (28)

`STONE_WALL_AGED_LEFT`, `STONE_WALL_AGED_RIGHT`, `STONE_WALL_AGED`, `STONE_WALL_ARCHWAYS`, `STONE_WALL_BROKEN_LEFT`, `STONE_WALL_BROKEN_RIGHT`, `STONE_WALL_BROKEN`, `STONE_WALL_COLUMN_IN`, `STONE_WALL_COLUMN`, `STONE_WALL_CORNER`, `STONE_WALL_DOOR_BARS`, `STONE_WALL_DOOR_CLOSED`, `STONE_WALL_DOOR_OPEN`, `STONE_WALL_DOOR`, `STONE_WALL_GATE_BARS`, `STONE_WALL_GATE_CLOSED`, `STONE_WALL_GATE_OPEN`, `STONE_WALL_GATE`, `STONE_WALL_HALF`, `STONE_WALL_HOLE`, `STONE_WALL_ROUND_BROKEN`, `STONE_WALL_ROUND_WINDOW`, `STONE_WALL_ROUND`, `STONE_WALL_STRUCTURE`, `STONE_WALL_TOP`, `STONE_WALL_WINDOW_BARS`, `STONE_WALL_WINDOW`, `STONE_WALL`.

Pour placer un coin de mur, utilisez `STONE_WALL_CORNER` et choisissez son apparence parmi les quatre orientations possibles : `NORTH_EAST`, `NORTH_WEST`, `SOUTH_EAST` ou `SOUTH_WEST`.

### Palissades (4)

Les palissades sont disponibles dans `PalisadeSprite` :

`X_MIN`, `X_MAX`, `Y_MIN`, `Y_MAX`.

Ces constantes correspondent aux quatre bords possibles d'une tuile.

```java
scene.addRenderable(new Entity(
        PalisadeSprite.X_MIN, Orientation.NORTH, x, y, z));
```

Les portes, grilles et escaliers sont des éléments visuels. Le jeu conserve la responsabilité de ses règles de déplacement, de collision et de passage.

## 4. Poser un personnage

**Classe :** `AnimatedEntity`

```java
new AnimatedEntity(AnimatedOrientedSprite sprite,
                   Orientation orientation,
                   int x, int y, int z)
```

```java
AnimatedEntity character = new AnimatedEntity(
        AnimatedSprites.CRUSADER_IDLE,
        Orientation.SOUTH, x, y, z);

scene.addRenderable(character);
```

### Choisir une animation

La méthode `setSprite(...)` permet de changer l'animation affichée.

```java
character.setSprite(AnimatedSprites.CRUSADER_WALK);
```

### Catalogue des animations

Toutes les constantes suivantes appartiennent à `AnimatedSprites`.

| Personnage | Animations |
|---|---|
| Croisé (8) | `CRUSADER_ATTACK`, `CRUSADER_BLOCK`, `CRUSADER_DEATH`, `CRUSADER_HIT`, `CRUSADER_IDLE`, `CRUSADER_JUMP`, `CRUSADER_RUN`, `CRUSADER_WALK` |
| Araignée (3) | `SPIDER_ATTACK`, `SPIDER_DEATH`, `SPIDER_WALK` |

### Positionner et déplacer un personnage

| Commande | Effet |
|---|---|
| `setOrientation(Orientation)` | Choisir son orientation |
| `moveBy(double dx, double dy)` | Le déplacer avec une précision fractionnaire |
| `moveInDirection(double dx, double dy)` | Le déplacer et adapter son orientation |
| `setPreciseZ(double)` | Définir sa hauteur, y compris entre deux étages |
| `setSprite(...)` | Choisir son animation |
| `setAnimationSpeed(double)` | Modifier la vitesse de son animation |
| `setAnimationPaused(boolean)` | Suspendre ou reprendre son animation |

Exemple de commandes envoyées par le jeu :

```java
character.setSprite(AnimatedSprites.CRUSADER_WALK);
character.setOrientation(Orientation.NORTH);
character.moveBy(0.0, -distance);
```

Pour un déplacement entre deux étages, le jeu peut modifier progressivement la hauteur du personnage avec `setPreciseZ(double)`.

## 5. Gérer les éléments présents dans la scène

Les commandes de `ViewPanel` sont :

| Commande | Effet |
|---|---|
| `addRenderable(element)` | Ajouter un élément |
| `removeRenderable(element)` | Retirer un élément |
| `clearRenderables()` | Vider la scène |

Le jeu peut conserver ses propres références aux entités pour les déplacer, changer leur apparence ou les retirer ultérieurement.

## 6. Référence rapide

| Besoin | Classe ou catalogue |
|---|---|
| Ajouter une tuile | `FloorTile`, `IsometricSprites` |
| Ajouter du mobilier | `Entity`, `IsometricSprites` |
| Ajouter un mur ou un escalier | `Entity`, `IsometricSprites` |
| Ajouter une palissade | `Entity`, `PalisadeSprite` |
| Ajouter un personnage | `AnimatedEntity`, `AnimatedSprites` |
| Définir une orientation | `Orientation` |
| Ajouter ou retirer un élément | `ViewPanel` |

Les exemples d'utilisation figurent dans `Main.java`, `Demo.java` et `ShadowGallery.java`.

Les licences des ressources graphiques sont indiquées dans [ASSET_LICENSES.md](ASSET_LICENSES.md).