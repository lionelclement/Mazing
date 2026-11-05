# Mazing — Guide de l'API

Mazing est une bibliothèque pour afficher les éléments d'un jeu isométrique.
L'API s'utilise à partir de `fr.ubordeaux.ao.mazing.Scene` et d'objets
visuels fournis par `fr.ubordeaux.ao.mazing.view.entity.*`.

## 1. Créer et afficher une scène

Une scène représente une carte en vue isométrique, composée de tuiles. Elle peut être affichée dans une fenêtre autonome ou intégrée dans une interface Swing.

Exemple
```java
import fr.ubordeaux.ao.mazing.Scene;

Scene scene = new Scene(960, 640, 10, 10);
scene.show();
scene.startAnimation(24);
```

`Scene(int width, int height, int mapWidth, int mapHeight)` prend :

- `width`, `height` : dimensions initiales de l'affichage, en pixels ;
- `mapWidth`, `mapHeight` : dimensions de la carte, en nombre de tuiles.

La carte est centrée dans la zone d'affichage, y compris après un redimensionnement.

`show()` ouvre une fenêtre autonome. `close()` la ferme et arrête les
animations. `startAnimation(fps)` fait progresser les animations à la cadence
demandée et rafraîchit leur affichage ; `stopAnimation()` arrête cette horloge.

## 2. Intégrer la scène dans un `JPanel`

La méthode `scene.getPanel()` renvoie le `JPanel` contenant l'affichage de la scène. Il peut être intégré dans une fenêtre Swing avec d'autres composants (`JButton`, `JLabel`, etc.).

```java
JFrame fenetre = new JFrame("Mazing");
JPanel contenu = new JPanel(new BorderLayout());

contenu.add(scene.getPanel(), BorderLayout.CENTER);
contenu.add(new JButton("Nouvelle partie"), BorderLayout.SOUTH);

fenetre.setContentPane(contenu);
fenetre.pack();
fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);fenetre.setVisible(true);

scene.startAnimation(24);
```

Deux modes d'affichage sont possibles :
- `scene.show()` : affiche la scène dans sa propre fenêtre.
- `scene.getPanel()` : permet de l'intégrer dans une interface Swing existante.

Ces deux modes sont alternatifs.

**Important** : les composants Swing doivent être manipulés sur l'EDT (*Event Dispatch Thread*), le thread qui gère l'interface graphique. `SwingUtilities.invokeLater()` permet d'y exécuter le code en toute sécurité.

## 3. Méthodes de `Scene`

| Méthode | Utilisation |
|---|---|
| `addRenderable(Renderable)` | Ajoute un élément graphique ; renvoie `boolean` |
| `addRenderables(List<Renderable>)` | Ajoute plusieurs éléments ; renvoie `boolean` |
| `removeRenderable(Renderable)` | Retire un élément ; renvoie `boolean` |
| `clearRenderables()` | Retire tous les éléments |
| `moveScene(int dx, int dy)` | Déplace la caméra en pixels écran |
| `centerMap()` | Recentre la carte |
| `setZoom(int zoom)` | Modifie le zoom (entier strictement positif) |
| `startAnimation(int fps)` | Démarre ou redémarre la progression automatique des animations et leur rafraîchissement ; `fps` doit être positif |
| `stopAnimation()` | Arrête la progression automatique |
| `animationTick()` | Avance les animations d'un tick et déclenche les éventuels rappels de frame, sans redessiner |
| `repaint()` | Demande un nouveau rendu sans faire progresser les animations |
| `getPanel()` | Renvoie le composant Swing de la scène |
| `show()` | Ouvre la fenêtre autonome |
| `close()` | Ferme cette fenêtre et arrête l'animation |

`Renderable` est l'interface commune aux objets graphiques que l'on peut
ajouter à une scène. Le zoom est un **diviseur** : `setZoom(2)` affiche les
sprites deux fois plus petits que `setZoom(1)`.

### Avancement des animations

Deux modes d'utilisation sont possibles :

- **Automatique :** `scene.startAnimation(24)` fait progresser les animations
  à une cadence d'environ 24 Hz et demande le rafraîchissement de la scène.
  `scene.stopAnimation()` arrête cette progression.
- **Manuel :** l'application appelle `scene.animationTick()` à la cadence
  souhaitée, puis `scene.repaint()` pour afficher les nouvelles frames.

`animationTick()` actualise les animations des objets présents dans la scène,
y compris ceux qui se trouvent hors de la zone visible. Les rappels enregistrés
avec `setAnimationFrameListener(...)` sont exécutés lors des changements de
frame. `repaint()` ne fait pas avancer les animations et ne déclenche pas ces
rappels.

Ne pas appeler manuellement `animationTick()` pendant que
`startAnimation(...)` pilote la même scène.

## 4. Éléments graphiques

Classes du package `fr.ubordeaux.ao.mazing.view.entity` :

| Classe | Rôle | Constructeur courant |
|---|---|---|
| `FloorTile` | Tuile de sol | `FloorTile(Sprite, Orientation, int x, int y, int z)` |
| `Entity` | Objet fixe | `Entity(Sprite, Orientation, int x, int y, int z)` |
| `FreeEntity` | Objet à position fractionnaire | `FreeEntity(Sprite, Orientation, double x, double y, double z, double scale)` |
| `OccludingEntity` | Mur ou structure pouvant masquer un personnage | `OccludingEntity(Sprite, Orientation, int x, int y, int z)` |
| `AnimatedEntity` | Personnage ou objet animé | `AnimatedEntity(AnimatedOrientedSprite, Orientation, int x, int y, int z)` |

Exemple d'ajout d'un sol et d'un mur :

```java
scene.addRenderable(new FloorTile(
    IsometricSprites.STONE, Orientation.NORTH, 2, 4, 0));
scene.addRenderable(new OccludingEntity(
    IsometricSprites.STONE_WALL, Orientation.NORTH, 2, 3, 0));
```

Pour `Entity` et ses variantes, les méthodes utiles sont :

| Méthode | Utilisation |
|---|---|
| `setPrecisePosition(double x, double y)` | Change la position dans le plan |
| `setPreciseZ(double z)` | Change l'altitude |
| `moveBy(double dx, double dy)` | Déplace l'objet relativement |
| `setOrientation(Orientation)` | Change son orientation graphique |
| `setScale(double scale)` | Modifie sa taille à l'écran |
| `setSprite(Sprite)` | Change son apparence |
| `setRenderOffset(int x, int y)` | Décale visuellement le sprite en pixels source |

`FreeEntity` accepte des coordonnées fractionnaires. La valeur `scale = 1.0`
conserve la taille d'origine ; elle doit être strictement positive. Les
positions sont exprimées en unités de tuile, et `z` en niveaux d'altitude.

## 5. `AnimatedEntity`

```java
AnimatedEntity personnage = new AnimatedEntity(
    AnimatedSprites.CRUSADER_IDLE, Orientation.SOUTH, 2, 3, 0);
scene.addRenderable(personnage);
```

Constructeurs disponibles :

```java
new AnimatedEntity(sprite);
new AnimatedEntity(sprite, orientation);
new AnimatedEntity(sprite, x, y, z);
new AnimatedEntity(sprite, orientation, x, y, z);
```

`AnimatedEntity` affiche automatiquement l'ombre du personnage si son
sprite en possède une.

| Méthode | Utilisation |
|---|---|
| `setPosition(double x, double y, double z)` | Place le personnage à une position précise |
| `moveBy(double dx, double dy)` | Le déplace relativement |
| `moveInDirection(double dx, double dy)` | Le déplace et adapte son orientation sur les axes principaux |
| `setOrientation(Orientation)` | Change l'orientation |
| `setScale(double scale)` | Modifie la taille affichée |
| `setSprite(Sprite)` | Change l'animation et la redémarre ; attend un `AnimatedOrientedSprite` |
| `getSprite()` | Renvoie le sprite utilisé |
| `setAnimationSpeed(double speed)` | Change la vitesse de lecture |
| `setAnimationPaused(boolean paused)` | Met en pause ou reprend l'animation |
| `setAnimationFrameListener(IntConsumer listener)` | Reçoit l'indice des nouvelles frames lors des ticks d'animation |

```java
if (personnage.getSprite() != AnimatedSprites.CRUSADER_WALK) {
    personnage.setSprite(AnimatedSprites.CRUSADER_WALK);
}
personnage.moveBy(0.1, 0.0);
```

Ne pas appeler `setSprite()` à chaque rafraîchissement si l'animation ne
change pas : l'appel réinitialise sa lecture. Avec
`setAnimationSpeed(0.5)`, elle joue deux fois moins vite ; avec `1.5`, une
fois et demie plus vite. La vitesse d'animation ne modifie pas le déplacement.

## 6. Sprites et orientations

Dans `fr.ubordeaux.ao.mazing.view.sprite` :

- `IsometricSprites` fournit notamment `STONE`, `PLANKS`, `CHAIR`,
  `STONE_WALL`, `STONE_WALL_CORNER`, `STAIRS` et `STAIRS_SPIRAL` ;
- `AnimatedSprites` fournit `CRUSADER_IDLE`, `CRUSADER_WALK`,
  `CRUSADER_RUN`, `CRUSADER_ATTACK`, `CRUSADER_BLOCK`, `CRUSADER_HIT`,
  `CRUSADER_JUMP`, `CRUSADER_DEATH`, `SPIDER_WALK`, `SPIDER_ATTACK`
  et `SPIDER_DEATH`.

`fr.ubordeaux.ao.mazing.view.Orientation` définit `NORTH`, `SOUTH`,
`EAST`, `WEST`, `NORTH_EAST`, `NORTH_WEST`, `SOUTH_EAST` et `SOUTH_WEST`.
