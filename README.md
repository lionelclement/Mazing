# Avec Gradle

Pour exécuter :
```shell
./gradlew project1:run
```
ou
```shell
./gradlew project2:run
```

# Avec la commande javac
```shell
javac -d bin $(find mazing/src/main/java -name "*.java") 
javac -d bin $(find examples/projectXXX/src -name "*.java")
java -cp bin:mazing/src/main/resources fr.ubordeaux.ao.projectXXX.Main
```
