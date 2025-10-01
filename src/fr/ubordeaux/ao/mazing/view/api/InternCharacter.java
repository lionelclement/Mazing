package fr.ubordeaux.ao.mazing.view.api;

public interface InternCharacter<S extends CharacterMode> extends IRenderable3D {

    Animation<S> getCurrentAnimation(int state);

}
