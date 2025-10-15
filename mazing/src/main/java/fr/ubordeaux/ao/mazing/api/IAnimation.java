package fr.ubordeaux.ao.mazing.api;

import java.awt.image.BufferedImage;

interface IAnimation<S extends CharacterMode> {

	public void tick(AbstractCharacter<? extends CharacterMode> owner);

	public BufferedImage getCurrentFrame();

	public BufferedImage getCurrentShadow();

	public void reset();

	public void lastFrame();

}
