## Avec Gradle

# Pour exécuter :
```shell
./gradlew project1:run
```
ou
```shell
./gradlew project2:run
```

## Avec la commande javac

# Compiler la bibliothèque mazing.jar
```shell
javac -d build/classes $(find mazing/src/main/java/fr/ubordeaux/ao/mazing/api -name "*.java")
cp -r mazing/src/main/resources/* build/classes/
jar cf lib/mazing.jar -C build/classes .
```
 
# Compiler et exécuter le projet project0 (faire pareil avec project1, project2, etc.)
javac -cp lib/mazing.jar -d build/classes $(find examples/project0/src -name "*.java")
java -cp lib/mazing.jar:build/classes/ fr.ubordeaux.ao.project0.Main
```


