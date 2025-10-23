# Makefile robuste pour Mazing

# Outils
JAVA := java
JAVAC := javac
JAR := jar
MVN := mvn

# Répertoires
MAZING_SRC := mazing/src/main/java
MAZING_BIN := mazing/bin
MAZING_LIB := mazing/lib/mazing.jar
RESOURCES := mazing/src/main/resources
EXAMPLES := examples
PROJECTS := project00 project01 project02 project03 project04 project05
PROJECTS := project02
#PROJECTS := project0 project1

# Tous les fichiers .java
MAZING_SRCS := $(shell find $(MAZING_SRC) -name "*.java")

# Fichiers images à merger
IMAGES_GROUNDS := $(RESOURCES)/Dungeon-Pack/Isometric/grounds/png/*
IMAGES_WALLS := $(RESOURCES)/Dungeon-Pack/Isometric/walls/png/*
IMAGES_STAIRS := $(RESOURCES)/Dungeon-Pack/Isometric/stairs/png/*
IMAGES_CONSTRUCTIONS := $(RESOURCES)/Dungeon-Pack/Isometric/constructions/png/*
IMAGES_OBJECTS := $(RESOURCES)/Dungeon-Pack/Isometric/objects/png/*
IMAGES_PREVIEWS := $(RESOURCES)/kenney_prototype-kit/Previews/*

ALL_MERGED := $(RESOURCES)/isometric-tiles/all_000.png \
              $(RESOURCES)/isometric-tiles/all_100.png \
              $(RESOURCES)/isometric-tiles/all_200.png \
              $(RESOURCES)/isometric-tiles/all_300.png \
              $(RESOURCES)/isometric-tiles/all_400.png \
              $(RESOURCES)/isometric-tiles/all_500.png \
              $(RESOURCES)/isometric-tiles/all_600.png \
              $(RESOURCES)/isometric-tiles/all_700.png

# Targets
.PHONY: all clean compile-mazing merge-images jar install-maven compile-examples run-examples

all: $(MAZING_LIB) compile-examples run-examples

clean:
	rm -rf $(MAZING_BIN) $(MAZING_LIB) $(ALL_MERGED)
	$(foreach proj,$(PROJECTS), rm -rf $(EXAMPLES)/$(proj)/bin;)

compile-mazing: $(MAZING_BIN)/.compiled

merge-images: $(ALL_MERGED)

jar: $(MAZING_LIB)


# Compilation Mazing si sources changent
$(MAZING_BIN)/.compiled: $(MAZING_SRCS)
	@echo "=== Compilation Mazing ==="
	mkdir -p $(MAZING_BIN)
	$(JAVAC) -d $(MAZING_BIN) $(MAZING_SRCS)
	touch $(MAZING_BIN)/.compiled


# Merge images si les sources changent
$(ALL_MERGED): $(IMAGES_GROUNDS) $(IMAGES_WALLS) $(IMAGES_STAIRS) \
               $(IMAGES_CONSTRUCTIONS) $(IMAGES_OBJECTS) $(IMAGES_PREVIEWS) | $(MAZING_BIN)/.compiled
	@echo "=== Merge images ==="
	mkdir -p $(RESOURCES)/isometric-tiles
	$(JAVA) -cp $(MAZING_BIN) fr.ubordeaux.ao.mazing.api.MergePng $(RESOURCES)/Dungeon-Pack/Isometric/grounds/png 256 512 10 10 000
	$(JAVA) -cp $(MAZING_BIN) fr.ubordeaux.ao.mazing.api.MergePng $(RESOURCES)/Dungeon-Pack/Isometric/walls/png 256 512 10 10 100
	$(JAVA) -cp $(MAZING_BIN) fr.ubordeaux.ao.mazing.api.MergePng $(RESOURCES)/Dungeon-Pack/Isometric/stairs/png 256 512 10 10 300
	$(JAVA) -cp $(MAZING_BIN) fr.ubordeaux.ao.mazing.api.MergePng $(RESOURCES)/Dungeon-Pack/Isometric/constructions/png 256 512 10 10 400
	$(JAVA) -cp $(MAZING_BIN) fr.ubordeaux.ao.mazing.api.MergePng $(RESOURCES)/Dungeon-Pack/Isometric/objects/png 256 512 10 10 500
	mv all_*.png $(RESOURCES)/isometric-tiles
	$(JAVA) -cp $(MAZING_BIN) fr.ubordeaux.ao.mazing.api.MergePng $(RESOURCES)/kenney_prototype-kit/Previews 64 128 10 10 600


# Création JAR si nécessaire
$(MAZING_LIB): $(MAZING_BIN)/.compiled $(ALL_MERGED)
	@echo "=== Création JAR Mazing ==="
	$(JAR) cf $(MAZING_LIB) \
	-C $(MAZING_BIN) fr/ubordeaux/ao/mazing/api \
	-C $(RESOURCES) isometric-tiles \
	-C $(RESOURCES) isometric-Mini-Crusader \
	-C $(RESOURCES) bw_spider \
	-C $(RESOURCES) sounds \
	-C $(RESOURCES) kenney_impact-sounds


# Installation Maven
install-maven: $(MAZING_LIB)
	@echo "=== Installation JAR dans Maven local ==="
	$(MVN) install:install-file -Dfile=$(MAZING_LIB) -DgroupId=fr.ubordeaux.ao.mazing.api \
	-DartifactId=mazing -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

# Compilation des exemples uniquement si sources changent
compile-examples: $(MAZING_LIB)
	@echo "=== Compilation des exemples ==="
	@for proj in $(PROJECTS); do \
		mkdir -p $(EXAMPLES)/$$proj/bin; \
		SRC_FILES=$$(find $(EXAMPLES)/$$proj/src/main/java -name '*.java'); \
		$(JAVAC) -cp $(MAZING_LIB) -d $(EXAMPLES)/$$proj/bin $$SRC_FILES; \
	done

# Exécution des exemples
run-examples:
	@echo "=== Exécution des exemples ==="
	@$(foreach proj,$(PROJECTS), \
		$(JAVA) -cp $(MAZING_LIB):$(EXAMPLES)/$(proj)/bin fr.ubordeaux.ao.$(proj).Main;)
