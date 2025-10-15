# Avec Maven

Compiler et packager en jar avec:
```shell
mvn clean package
```

Executer directement le jar avec java:
```shell
java -jar example/project0/target/project0-1.0-jar-with-dependencies.jar
```

# Avec Gradle

Pour exécuter :
```shell
./gradlew project1:run
```
ou
```shell
./gradlew project2:run
```

# En ligne de commande

## Compiler l'API
```
javac -d mazing/build/classes mazing/src/main/java/**/*.java
jar cf mazing/lib/mazing.jar -C mazing/build/classes . -C mazing/src/main/resources .
```

## Compiler et exécuter le projet project0
```
javac -cp mazing/lib/mazing.jar -d examples/project0/build/classes examples/project0/src/**/*.java
java -cp mazing/lib/mazing.jar:examples/project0/build/classes fr.ubordeaux.ao.project0.Main
```

