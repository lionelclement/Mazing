# Mazing — prise en main

Mazing est une petite bibliothèque Java pour afficher des scènes **2D isométriques** avec Swing.

Ce guide permet de **télécharger**, **installer** et **essayer** le projet sur Windows, macOS ou Linux.

## 1. Télécharger

1. Ouvrez la page du dépôt GitHub de Mazing.
2. Cliquez sur **Code → Download ZIP**.
3. Décompressez l'archive, puis ouvrez le dossier `Mazing-main` dans un terminal.

Vous pouvez aussi cloner le dépôt avec Git si vous connaissez son adresse.

## 2. Installer Java

Mazing nécessite **Java JDK 25**.

- **Windows** : installez [Temurin JDK 25](https://adoptium.net/temurin/releases/?version=25) (installateur Windows).
- **macOS** : installez le même JDK, ou utilisez Homebrew : `brew install openjdk@25`.
- **Linux** : installez le JDK 25 proposé pour votre distribution sur [Adoptium](https://adoptium.net/temurin/releases/?version=25).

Ouvrez un **nouveau terminal**, puis vérifiez :

```sh
java -version
```

La version affichée doit commencer par `25`. Si une autre version apparaît, configurez votre `JAVA_HOME` et votre `PATH` pour sélectionner le JDK 25.

**Gradle n'a pas besoin d'être installé :** les scripts nécessaires sont inclus dans le projet. Une connexion Internet est nécessaire lors de la première compilation.

## 3. Compiler et essayer

Depuis le dossier `Mazing-main` (où se trouve `build.gradle.kts`) :

**macOS / Linux**

```sh
chmod +x gradlew
./gradlew build
java -cp "build/classes/java/main:build/resources/main" fr.ubordeaux.ao.mazing.Main
```

**Windows (PowerShell)**

```powershell
.\gradlew.bat build
java -cp "build/classes/java/main;build/resources/main" fr.ubordeaux.ao.mazing.Main
```

**Windows (Invite de commandes / CMD)**

```bat
gradlew.bat build
java -cp "build\classes\java\main;build\resources\main" fr.ubordeaux.ao.mazing.Main
```

La compilation réussie affiche `BUILD SUCCESSFUL`. L'exécution ouvre une **fenêtre Swing** montrant un exemple de scène isométrique. Vous pouvez ensuite modifier `src/main/java/fr/ubordeaux/ao/mazing/Main.java` et relancer la compilation.

## 4. Documentation de l’API (Javadoc)

Pour générer la documentation HTML des classes et méthodes :

**macOS / Linux**

```sh
./gradlew javadoc
```

**Windows**

```powershell
.\gradlew.bat javadoc
```

Ouvrez ensuite `build/docs/javadoc/index.html` dans un navigateur.

Pour produire l’archive Javadoc associée à la bibliothèque :

```sh
./gradlew javadocJar
```

Le fichier est généré dans `build/libs/`, avec le suffixe `-javadoc.jar`.
La Javadoc décrit l’API Java ; le fichier `GUIDE_SCENE.md` fournit des exemples pratiques pour composer une scène.

## En cas de problème

- **`UnsupportedClassVersionError`** : vous lancez probablement le programme avec une version de Java antérieure à 25. Vérifiez `java -version`.
- **`Permission denied`** (macOS/Linux) : lancez `chmod +x gradlew`.
- **`java: command not found`** : vérifiez l'installation du JDK et la variable `PATH`.
- **Aucune fenêtre** : le programme nécessite un environnement graphique (Swing).

> Le fichier JAR généré dans `build/libs/` est une bibliothèque, pas une application autonome à lancer avec `java -jar`. Pour l'essai, utilisez les commandes ci-dessus.

## Documentation

- [Créer une scène](doc/GUIDE_SCENE.md)
- [Guide de l'API](doc/GUIDE_API.md)
- [Licences des ressources](doc/ASSET_LICENSES.md)
- [Audit des ressources](doc/RESOURCE_AUDIT.md)

Le code original de Mazing est proposé sous licence MIT.
Les ressources de tiers conservent leurs propres licences.
