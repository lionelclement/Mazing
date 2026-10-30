# Mazing — Guide API

Moteur de rendu dimétrique 2:1 pour Java/Swing.

**Packages principaux :**

| Package | Contenu |
|---|---|
| `fr.ubordeaux.ao.mazing.view` | `Orientation`, `Renderable` |
| `fr.ubordeaux.ao.mazing.view.entity` | Entités affichables |
| `fr.ubordeaux.ao.mazing.view.sprite` | Sprites et animations |
| `fr.ubordeaux.ao.mazing.view.renderer` | Projection et caméra |
| `fr.ubordeaux.ao.mazing.view.swing` | `ViewPanel` |
| `fr.ubordeaux.ao.mazing.view.shadow` | Géométries d'ombres |

## 1. `ViewPanel`

`fr.ubordeaux.ao.mazing.view.swing.ViewPanel`

Composant Swing contenant la scène. Gère les objets affichables, leur animation et leur rendu. Le constructeur avec trois arguments accepte un `Renderer` (ici un `IsometricRenderer`).

**Construction**

```java
IsometricRenderer renderer = new IsometricRenderer(480, 320);
ViewPanel scene = new ViewPanel(960, 640, renderer);
```

**Méthodes utilisées**

| Méthode | Comportement |
|---|---|
| `addRenderable(renderable)` | Ajoute un élément à la scène |
| `animationTick()` | Fait progresser les animations d'un tick |
| `repaint()` | Demande un nouveau rendu Swing |
| `setStaticShadowsEnabled(boolean)` | Active ou désactive les ombres statiques |
| `prepareStaticShadows()` | Prépare le cache des ombres statiques |
| `invalidateStaticShadows()` | Invalide le cache des ombres statiques |

`animationTick()` et `repaint()` sont deux opérations distinctes. Un rafraîchissement écran ne doit pas être considéré comme un tick d'animation.

```java
scene.setStaticShadowsEnabled(true);

// Ajout des sols, murs et objets fixes...
scene.prepareStaticShadows();

// Horloge d'animation, par exemple à 24 Hz
scene.animationTick();

// Rafraîchissement écran
scene.repaint();
```

Si la géométrie permanente de la scène change, invalider le cache d'ombres statiques.

## 2. `IsometricRenderer`

`fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer`

Effectue la transformation des coordonnées du monde en coordonnées écran.

**Constructeur disponible**

```java
new IsometricRenderer(originX, originY);
```

Les dimensions de tuile utilisées par ce renderer sont définies par les
constantes de la classe ; il n’existe pas de constructeur public recevant
`tileDiagonalX`, `tileDiagonalY` et `tileHeight`.

**Méthodes**

| Méthode | Comportement |
|---|---|
| `setOrigin(int x, int y)` | Définit l'origine de projection en pixels |
| `pan(int dx, int dy)` | Déplace l'origine d'un décalage relatif en pixels |
| `setZoom(int zoom)` | Définit le diviseur d'échelle du rendu |
| `setDebug(boolean debug)` | Active ou désactive les informations de débogage |

Exemple :

```java
IsometricRenderer renderer = new IsometricRenderer(150, 320);

renderer.setZoom(2);
renderer.pan(32, -16);
```

`zoom = 2` produit un affichage deux fois plus petit que `zoom = 1`.

Les dimensions des tuiles sont exprimées en pixels source, avant application du zoom.

**Repère du moteur**

- `x`, `y` : coordonnées du plan en unités de tuile.
- `z` : altitude en unités de niveau.
- La projection est dimétrique 2:1 avec les dimensions standards 256 × 128.
- Le déplacement de caméra agit sur la projection, pas sur les coordonnées des entités.

## 3. Entités fixes

Package : `fr.ubordeaux.ao.mazing.view.entity`

### `FloorTile`

Représente une cellule de sol à une altitude donnée.

```java
scene.addRenderable(new FloorTile(
    IsometricSprites.STONE,
    Orientation.NORTH,
    x, y, z
));
```

Les coordonnées `x` et `y` identifient la cellule. `z` permet de construire des niveaux superposés.

### `Entity`

Entité fixe associant un sprite, une orientation et une position.

```java
Entity stairs = new Entity(
    IsometricSprites.STAIRS,
    Orientation.EAST,
    2, 4, 0
);

scene.addRenderable(stairs);
```

### `FreeEntity`

Entité à position fractionnaire et échelle individuelle.

```java
FreeEntity chair = new FreeEntity(
    IsometricSprites.CHAIR,
    Orientation.WEST,
    2.25, 2.40, 0, 1.75
);

scene.addRenderable(chair);
```

Le dernier argument est un facteur d'échelle : `1.0` correspond à la taille d'origine.

### `OccludingEntity`

Entité dont la géométrie intervient dans l'occultation et, selon sa configuration, dans la génération des ombres.

```java
OccludingEntity wall = new OccludingEntity(
    IsometricSprites.STONE_WALL,
    Orientation.NORTH,
    2, 3, 0
);

scene.addRenderable(wall);
```

Une surcharge permet de fournir une échelle :

```java
OccludingEntity column = new OccludingEntity(
    IsometricSprites.STONE_COLUMN_WOOD,
    Orientation.NORTH,
    2, 3, 0, 0.5
);
```

**Méthodes complémentaires utilisées**

| Méthode | Comportement |
|---|---|
| `setRenderOffset(int x, int y)` | Décalage visuel du sprite en pixels source |
| `setShadowGeometry(ShadowGeometry geometry)` | Géométrie projetée utilisée pour les ombres |

```java
column.setRenderOffset(0, -13);
column.setShadowGeometry(
    ShadowGeometry.box(0.35, 0.35, 1.0)
);
```

La géométrie d'ombre est distincte de l'image PNG. Elle doit correspondre au volume que l'objet est censé représenter.

## 4. `AnimatedEntity`

`fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity`

Entité orientable, positionnable avec précision et associée à un `AnimatedOrientedSprite`.

**Constructeurs**

```java
new AnimatedEntity(
    AnimatedSprites.CRUSADER_IDLE
);

new AnimatedEntity(
    AnimatedSprites.CRUSADER_IDLE,
    Orientation.SOUTH
);
```

Constructeur permettant de configurer explicitement la position, le zoom de sprite et l'ombre :

```java
AnimatedEntity hero = new AnimatedEntity(
    AnimatedSprites.CRUSADER_WALK,
    Orientation.EAST,
    2, 3, 0,
    2,
    ShadowMode.WITH_SHADOW
);
```

**Position et orientation**

| Méthode | Comportement |
|---|---|
| `setPosition(double x, double y, double z)` | Fixe les trois coordonnées précises |
| `getPreciseX()` | Coordonnée X précise |
| `getPreciseY()` | Coordonnée Y précise |
| `getPreciseZ()` | Altitude précise |
| `setPreciseZ(double z)` | Modifie uniquement l'altitude |
| `moveBy(double dx, double dy)` | Déplacement relatif sur le plan |
| `setOrientation(Orientation orientation)` | Change l'orientation |
| `getOrientation()` | Retourne l'orientation actuelle |
| `setScale(double scale)` | Modifie l'échelle individuelle |

```java
hero.setPosition(2.25, 3.75, 0.65);
hero.moveBy(0.10, -0.05);
hero.setOrientation(Orientation.NORTH_EAST);
hero.setScale(0.90);
```

Utiliser les coordonnées précises pour les déplacements continus et les altitudes fractionnaires.

**Animations**

| Méthode | Comportement |
|---|---|
| `setSprite(Sprite sprite)` | Change le sprite ; le changement d'animation redémarre sa lecture |
| `getSprite()` | Retourne le sprite courant |
| `setAnimationSpeed(double speed)` | Ajuste la vitesse de lecture |
| `setAnimationPaused(boolean paused)` | Suspend ou reprend la lecture |
| `isAnimationPaused()` | Indique si la lecture est suspendue |
| `setAnimationFrameListener(listener)` | Enregistre un rappel lors du changement de frame |

Exemple de transition :

```java
if (hero.getSprite() != AnimatedSprites.CRUSADER_ATTACK) {
    hero.setSprite(AnimatedSprites.CRUSADER_ATTACK);
    hero.setAnimationPaused(false);
}
```

Éviter d'appeler `setSprite()` à chaque frame si le sprite n'a pas changé : la transition redémarre l'animation.

**Vitesse**

```java
hero.setAnimationSpeed(0.5); // Lecture ralentie
hero.setAnimationSpeed(1.0); // Vitesse nominale
hero.setAnimationSpeed(1.5); // Lecture accélérée
```

La vitesse d'animation est indépendante de la vitesse de déplacement dans le monde.

**Événements de frame**

```java
hero.setAnimationFrameListener(frame -> {
    if (frame == 1) {
        footstepSound.play();
    }

    if (frame == hero.getSprite().animationSize() - 1) {
        hero.setAnimationPaused(true);
    }
});
```

`animationSize()` fournit le nombre de frames du sprite. Le dernier index est donc `animationSize() - 1`.

Ce mécanisme peut servir à synchroniser des sons, des effets ou des transitions avec les animations.

## 5. Sprites

Package : `fr.ubordeaux.ao.mazing.view.sprite`

### `IsometricSprites`

Catalogue des sprites fixes fournis avec Mazing.

Exemples :

```java
IsometricSprites.STONE
IsometricSprites.PLANKS
IsometricSprites.CHAIR
IsometricSprites.STONE_WALL
IsometricSprites.STONE_WALL_CORNER
IsometricSprites.STAIRS
IsometricSprites.STAIRS_SPIRAL
```

Les sprites sont réutilisables par plusieurs entités. La position et l'orientation appartiennent à l'entité.

### `AnimatedSprites`

Catalogue des animations orientées.

| Crusader | Spider |
|---|---|
| `CRUSADER_IDLE` | `SPIDER_WALK` |
| `CRUSADER_WALK` | `SPIDER_ATTACK` |
| `CRUSADER_RUN` | `SPIDER_DEATH` |
| `CRUSADER_ATTACK` | |
| `CRUSADER_BLOCK` | |
| `CRUSADER_HIT` | |
| `CRUSADER_JUMP` | |
| `CRUSADER_DEATH` | |

Chaque constante est un `AnimatedOrientedSprite`.

`AnimatedOrientedSprite.animationSize()` retourne la longueur de l'animation en frames.

### `Orientation`

Les orientations disponibles comprennent :

```java
Orientation.NORTH
Orientation.SOUTH
Orientation.EAST
Orientation.WEST

Orientation.NORTH_EAST
Orientation.NORTH_WEST
Orientation.SOUTH_EAST
Orientation.SOUTH_WEST
```

La représentation effective dépend des orientations définies dans le sprite utilisé.

## 6. Avancement des animations

`ViewPanel` n'impose pas la fréquence d'actualisation des objets métier.

Exemple : rendu à 60 Hz et animations à 24 Hz.

```java
private static final int RENDER_FPS = 60;
private static final int ANIMATION_FPS = 24;
```

À chaque cycle :

```java
updateEntities(elapsedSeconds);  // Fonction du programme appelant

// À chaque intervalle de 1 / ANIMATION_FPS :
scene.animationTick();

scene.repaint();
```

Utiliser un accumulateur temporel lorsque la fréquence d'affichage diffère de celle des animations. `Test` contient une implémentation utilisant `System.nanoTime()` et un accumulateur de temps.

Les opérations sur les composants Swing s'effectuent sur l'EDT.

## 7. Ombres et géométrie

**Initialisation**

```java
scene.setStaticShadowsEnabled(true);

// Construction du décor fixe...
scene.prepareStaticShadows();
```

**Modification du décor**

```java
scene.invalidateStaticShadows();
```

**Géométrie d'un objet**

```java
column.setShadowGeometry(
    ShadowGeometry.box(0.35, 0.35, 1.0)
);
```

`ShadowGeometry` décrit un volume projetable indépendamment du PNG.

Les ombres statiques sont mises en cache pour éviter leur recalcul systématique. Les déplacements des personnages animés ne nécessitent pas d'invalider ce cache à chaque image.

## 8. Points d'attention

| Élément | Contrainte |
|---|---|
| `setSprite()` | Redémarre l'animation lors d'un changement de sprite |
| `animationTick()` | Ne déclenche pas à lui seul un repaint Swing |
| `repaint()` | N'avance pas les animations |
| `setZoom(int)` | Diviseur d'échelle, pas multiplicateur |
| `setScale(double)` | Échelle propre à l'entité |
| `setRenderOffset()` | Décalage visuel en pixels source |
| `setPosition()` | Coordonnées du monde, pas pixels écran |
| `prepareStaticShadows()` | À utiliser après la construction du décor statique |
| `invalidateStaticShadows()` | Nécessaire après modification d'un décor statique affectant ses ombres |
| Ressources graphiques | PNG fournis dans `src/main/resources/` et chargés via le classpath |

## 9. Exemples

`Demo` et `Gallery` sont isolés des sources principales.

- `Demo` : exemple d'assemblage, de déplacement, d'animation, de caméra et d'ombres.
- `Gallery` : inspection des sprites et animations disponibles.

```bash
./gradlew runDemo
./gradlew runGallery
```