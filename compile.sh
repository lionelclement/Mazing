javac -d mazing/bin mazing/src/main/java/**/*.java

#java -cp mazing/bin fr.ubordeaux.ao.mazing.api.MergePng /Users/clement/Workspace/mazing-20250929/mazing/src/main/resources/Dungeon-Pack/Isometric/grounds/png 256 512 10 10 000

#java -cp mazing/bin fr.ubordeaux.ao.mazing.api.MergePng /Users/clement/Workspace/mazing-20250929/mazing/src/main/resources/Dungeon-Pack/Isometric/walls/png 256 512 10 10 100

#java -cp mazing/bin fr.ubordeaux.ao.mazing.api.MergePng /Users/clement/Workspace/mazing-20250929/mazing/src/main/resources/Dungeon-Pack/Isometric/stairs/png 256 512 10 10 300

#java -cp mazing/bin fr.ubordeaux.ao.mazing.api.MergePng /Users/clement/Workspace/mazing-20250929/mazing/src/main/resources/Dungeon-Pack/Isometric/constructions/png 256 512 10 10 400

#java -cp mazing/bin fr.ubordeaux.ao.mazing.api.MergePng /Users/clement/Workspace/mazing-20250929/mazing/src/main/resources/Dungeon-Pack/Isometric/objects/png 256 512 10 10 500

#mv all_*.png mazing/src/main/resources/isometric-tiles

java -cp mazing/bin fr.ubordeaux.ao.mazing.api.MergePng mazing/src/main/resources/kenney_prototype-kit/Previews 64 128 10 10 600

jar cf mazing/lib/mazing.jar -C mazing/bin fr/ubordeaux/ao/mazing/api -C mazing/src/main/resources isometric-tiles -C mazing/src/main/resources isometric-Mini-Crusader -C mazing/src/main/resources bw_spider -C mazing/src/main/resources sounds -C mazing/src/main/resources kenney_impact-sounds

#mvn install:install-file -Dfile=mazing/lib/mazing.jar -DgroupId=fr.ubordeaux.ao.mazing.api -DartifactId=mazing -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

#javac -cp mazing/lib/mazing.jar -d examples/project00/bin examples/project00/src/main/java/**/*.java
#java -cp mazing/lib/mazing.jar:examples/project00/bin fr.ubordeaux.ao.project00.Main

#javac -cp mazing/lib/mazing.jar -d examples/project01/bin examples/project01/src/main/java/**/*.java
#java -cp mazing/lib/mazing.jar:examples/project01/bin fr.ubordeaux.ao.project01.Main

#javac -cp mazing/lib/mazing.jar -d examples/project02/bin examples/project02/src/main/java/**/*.java
#java -cp mazing/lib/mazing.jar:examples/project02/bin fr.ubordeaux.ao.project02.Main

#javac -cp mazing/lib/mazing.jar -d examples/project03/bin examples/project03/src/main/java/**/*.java
#java -cp mazing/lib/mazing.jar:examples/project03/bin fr.ubordeaux.ao.project03.Main

#javac -cp mazing/lib/mazing.jar -d examples/project04/bin examples/project04/src/main/java/**/*.java
#java -cp mazing/lib/mazing.jar:examples/project04/bin fr.ubordeaux.ao.project04.Main

javac -cp mazing/lib/mazing.jar -d examples/project0/bin examples/project0/src/main/java/**/*.java
java -cp .:mazing/lib/mazing.jar:examples/project0/bin fr.ubordeaux.ao.project0.Main

#javac -cp mazing/lib/mazing.jar -d examples/project1/bin examples/project1/src/main/java/**/*.java
#java -cp mazing/lib/mazing.jar:examples/project1/bin fr.ubordeaux.ao.project1.Main
