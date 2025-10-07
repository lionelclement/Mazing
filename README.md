# Avec Gradle

## Pour exécuter :
```shell
./gradlew project1:run
```
ou
```shell
./gradlew project2:run
```

# Avec la commande javac

## Compiler la bibliothèque mazing.jar
```shell
javac -d mazing/build/classes $(find mazing/src/main/java/fr/ubordeaux/ao/mazing/api -name "*.java") 
cp -rf mazing/src/main/resources mazing/build/ressources 
jar cf mazing/lib/mazing.jar -C mazing/build/classes . -C mazing/build/resources
```
 
## Compiler et exécuter le projet project0 (faire pareil avec project1, project2, etc.)
```
javac -cp mazing/lib/mazing.jar -d examples/project0/build $(find examples/project0/src -name "*.java")
java -cp mazing/lib/mazing.jar:examples/project0/build fr.ubordeaux.ao.project0.Main                   
```







