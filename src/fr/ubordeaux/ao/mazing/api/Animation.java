package fr.ubordeaux.ao.mazing.api;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Animation<S extends CharacterMode> implements IAnimation<S> {

	private final BufferedImage[] images;
	private final BufferedImage[] shadows;
	private final int frameNumber;
	private final int frameNumberMid;
	private final boolean loop;
	private boolean frozen;

	private int frameIndex;

	public Animation(BufferedImage[] frames, BufferedImage[] shadows, int frameDelayMs, boolean loop,
			AbstractCharacter<CharacterMode> owner) {
		this.images = frames;
		this.shadows = shadows;
		this.frameNumber = frames.length;
		this.frameNumberMid = frames.length / 2;
		this.loop = loop;
		this.frozen = false;
		Random rand = new Random();
		this.frameIndex = rand.nextInt(frames.length);
	}

	public void tick(AbstractCharacter<? extends CharacterMode> owner) {
		owner.callTickTrigger(frameIndex);
		if (!frozen) {
			if (frameIndex == 0) {
				owner.callBeginTrigger();
			}
			if (frameIndex == frameNumberMid) {
				owner.callMidTrigger();
			}
			frameIndex++;
		}
		if (frameIndex >= frameNumber) {
			if (loop) {
				frameIndex = 0;
				owner.callLoopTrigger();
			} else {
				frameIndex--;
				frozen = true;
				owner.callEndTrigger();
			}
		}
	}

	public BufferedImage getCurrentFrame() {
		return images[frameIndex];
	}

	public BufferedImage getCurrentShadow() {
		return shadows[frameIndex];
	}

	public void reset() {
		frameIndex = 0;
		frozen = false;
	}

	public void lastFrame() {
		frameIndex = images.length - 1;
	}

}
