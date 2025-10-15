package fr.ubordeaux.ao.mazing.api;

interface CharacterMode extends ICharacterMode {

    int getNumFrames();

    boolean isLoop();

    int ordinal();

    String name();

}
