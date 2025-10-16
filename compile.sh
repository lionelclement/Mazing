javac -d mazing/bin mazing/src/main/java/**/*.java
jar cf mazing/lib/mazing.jar -C mazing/bin fr/ubordeaux/ao/mazing/api -C mazing/src/main/resources/ .
mvn install:install-file \
  -Dfile=mazing/lib/mazing.jar \
  -DgroupId=fr.ubordeaux.ao.mazing.api \
  -DartifactId=mazing \
  -Dversion=1.0 \
  -Dpackaging=jar \
  -DgeneratePom=true

javac -cp mazing/lib/mazing.jar -d examples/project00/bin examples/project00/src/main/java/**/*.java
java -cp mazing/lib/mazing.jar:examples/project00/bin fr.ubordeaux.ao.project00.Main

javac -cp mazing/lib/mazing.jar -d examples/project01/bin examples/project01/src/main/java/**/*.java
java -cp mazing/lib/mazing.jar:examples/project01/bin fr.ubordeaux.ao.project01.Main

javac -cp mazing/lib/mazing.jar -d examples/project02/bin examples/project02/src/main/java/**/*.java
java -cp mazing/lib/mazing.jar:examples/project02/bin fr.ubordeaux.ao.project02.Main

javac -cp mazing/lib/mazing.jar -d examples/project0/bin examples/project0/src/main/java/**/*.java
java -cp mazing/lib/mazing.jar:examples/project0/bin fr.ubordeaux.ao.project0.Main

javac -cp mazing/lib/mazing.jar -d examples/project1/bin examples/project1/src/main/java/**/*.java
java -cp mazing/lib/mazing.jar:examples/project1/bin fr.ubordeaux.ao.project1.Main
