package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.rmi.UnexpectedException;

/**
 * Classe abstraite représentant un personnage dans le jeu.
 * 
 * Cette classe gère les informations de position, de vitesse,
 * d'animations et de direction du personnage. Elle fournit
 * également des méthodes de rendu et des hooks pour les triggers
 * (début, fin, milieu, tick) lors de l'animation.
 * 
 *
 * @param <T> le type d'état du personnage, qui doit implémenter
 *            {@link CharacterMode}
 */
 abstract class AbstractCharacter<T extends CharacterMode> extends AbstractRenderable implements ICharacter<T> {

    protected BufferedImage[][][] images; // [state][direction][frame]
    protected BufferedImage[][][] shadow_images; // [state][direction][frame]
    protected Animation<T>[][] animations; // [state][direction]

    protected float x, y, z;
    protected float targetX, targetY, targetZ;
    protected float speedX, speedY, speedZ;
    protected Direction currentDirection;
    protected int offsetX;
    protected int offsetY;

    private Runnable loopAnimationTrigger;
    private Runnable beginLoopAnimationTrigger;
    private Runnable midLoopAnimationTrigger;
    private Runnable endLoopAnimationTrigger;
    private Runnable tickAnimationTrigger;

    @SuppressWarnings("unchecked")
    AbstractCharacter(int numberStates, int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        images = new BufferedImage[numberStates][Direction.values().length][];
        shadow_images = new BufferedImage[numberStates][Direction.values().length][];
        animations = new Animation[numberStates][Direction.values().length];
        currentDirection = Direction.SOUTH;
    }

    Animation<T> getCurrentAnimation(int state) {
        return animations[state][currentDirection.ordinal()];
    }

    void render(Graphics g, int state) {
        Animation<T> anim = getCurrentAnimation(state);
        BufferedImage image = anim.getCurrentFrame();
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, getRenderType(), 1.0f);
    }

    abstract RenderType getRenderType();

    protected void setSpeed(float speedX, float speedY, float speedZ) {
        Direction lastDirection = currentDirection;
        Direction newDirection = currentDirection;
        if (speedX > 0 && speedY == 0)
            newDirection = Direction.EAST;
        else if (speedX < 0 && speedY == 0)
            newDirection = Direction.WEST;
        else if (speedX == 0 && speedY > 0)
            newDirection = Direction.SOUTH;
        else if (speedX == 0 && speedY < 0)
            newDirection = Direction.NORTH;
        else if (speedX > 0 && speedY > 0)
            newDirection = Direction.SOUTHEAST;
        else if (speedX > 0 && speedY < 0)
            newDirection = Direction.NORTHEAST;
        else if (speedX < 0 && speedY > 0)
            newDirection = Direction.SOUTHWEST;
        else if (speedX < 0 && speedY < 0)
            newDirection = Direction.NORTHWEST;

        this.speedX = this.speedY = this.speedZ = 0;

        if (newDirection != lastDirection) {
            // Change la direction et lance le déplacement
            turnPlayer(newDirection, 75, () -> {
                this.speedX = speedX;
                this.speedY = speedY;
                this.speedZ = speedZ;
            });
        } else {
            this.speedX = speedX;
            this.speedY = speedY;
            this.speedZ = speedZ;
        }
    }

    // Déplace le personnage vers une case et ajuste la direction progressivement
    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    void turnPlayer(Direction targetDirection, int delayMs, Runnable onFinished) {

        Direction direction = currentDirection;
        int currentAngle = direction.getAngle();
        int targetAngle = targetDirection.getAngle();

        int delta = ((targetAngle - currentAngle + 540) % 360) - 180;
        int steps = Math.abs(delta) / 45;
        int stepSign = (delta >= 0) ? 1 : -1;

        if (steps == 0) {
            if (null != onFinished)
                onFinished.run();
            return;
        }

        javax.swing.Timer rotationTimer = new javax.swing.Timer(delayMs, null);
        final int[] stepCount = { 0 };

        rotationTimer.addActionListener(_-> {
            int newAngle = (currentAngle + stepSign * 45 * stepCount[0] + 360) % 360;

            // Choisir la direction la plus proche
            Direction nearest = Direction.values()[0];
            int minDiff = 360;
            for (Direction dir : Direction.values()) {
                int diff = Math.abs(((dir.getAngle() - newAngle + 180) % 360) - 180);
                if (diff < minDiff) {
                    minDiff = diff;
                    nearest = dir;
                }
            }
            this.currentDirection = nearest;

            stepCount[0]++;
            if (stepCount[0] > steps) {
                setDirection(targetDirection);
                rotationTimer.stop();
                if (onFinished != null)
                    onFinished.run();
            }
        });

        rotationTimer.setRepeats(true);
        rotationTimer.start();
    }

    @Override
    public void setDirection(Direction newDirection) {
        // Calculer la vitesse scalaire actuelle dans le plan XY
        float currentSpeedXY = (float) Math.sqrt(speedX * speedX + speedY * speedY);
        float currentSpeedZ = speedZ; // on conserve Z

        // On met à zéro les vitesses pendant la rotation visuelle
        speedX = speedY = speedZ = 0;

        turnPlayer(newDirection, 125, () -> {
            currentDirection = newDirection;

            // Vecteur direction brute pour X et Y
            float dirX = 0, dirY = 0;
            switch (newDirection) {
                case NORTH -> {
                    dirX = 0;
                    dirY = -1;
                }
                case SOUTH -> {
                    dirX = 0;
                    dirY = 1;
                }
                case EAST -> {
                    dirX = 1;
                    dirY = 0;
                }
                case WEST -> {
                    dirX = -1;
                    dirY = 0;
                }
                case NORTHEAST -> {
                    dirX = 1;
                    dirY = -1;
                }
                case NORTHWEST -> {
                    dirX = -1;
                    dirY = -1;
                }
                case SOUTHEAST -> {
                    dirX = 1;
                    dirY = 1;
                }
                case SOUTHWEST -> {
                    dirX = -1;
                    dirY = 1;
                }
            }

            // Normaliser automatiquement (aucun if nécessaire)
            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0) {
                dirX /= length;
                dirY /= length;
            }

            // Appliquer la vitesse existante dans la nouvelle direction XY
            speedX = dirX * currentSpeedXY;
            speedY = dirY * currentSpeedXY;
            speedZ = currentSpeedZ; // conserver la vitesse sur Z
        });
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    float getIsoDepth() {
        return getX() + getY() + 0.5f;
    }

    void callTickTrigger(int frameIndex) {
        x += speedX;
        y += speedY;
        z += speedZ;

        if (tickAnimationTrigger != null) {
            tickAnimationTrigger.run();
        }
    }

    void callBeginTrigger() {
        if (beginLoopAnimationTrigger != null) {
            beginLoopAnimationTrigger.run();
        }
    }

    void callMidTrigger() {
        if (midLoopAnimationTrigger != null) {
            midLoopAnimationTrigger.run();
        }
    }

    void callEndTrigger() {
        if (endLoopAnimationTrigger != null) {
            endLoopAnimationTrigger.run();
        }
    }

    void callLoopTrigger() {
        if (loopAnimationTrigger != null) {
            loopAnimationTrigger.run();
        }
    }

    @Override
    public void setTickAnimationTrigger(Runnable callback) {
        tickAnimationTrigger = callback;
    }

    @Override
    public void setBeginAnimationTrigger(Runnable callback) {
        beginLoopAnimationTrigger = callback;
    }

    @Override
    public void setMidAnimationTrigger(Runnable callback) {
        midLoopAnimationTrigger = callback;
    }

    @Override
    public void setEndAnimationTrigger(Runnable callback) {
        endLoopAnimationTrigger = callback;
    }

    @Override
    public void setLoopAnimationTrigger(Runnable callback) {
        loopAnimationTrigger = callback;
    }

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public void startWalking() {
		throw new IllegalStateException("Unexpected action: startWalking");
	}

	@Override
	public void startRunning() {
		throw new IllegalStateException("Unexpected action: startRunning");
	}

	@Override
	public void stopMoving() {
		throw new IllegalStateException("Unexpected action: stopMoving");
	}

	@Override
	public void performAttack() {
		throw new IllegalStateException("Unexpected action: performAttack");
	}

	@Override
	public void performJump() {
		throw new IllegalStateException("Unexpected action: performJump");
	}

	@Override
	public void die() {
		throw new IllegalStateException("Unexpected action: die");
	}

	@Override
	public void takeHit() {
		throw new IllegalStateException("Unexpected action: takeHit");
	}
	
	abstract CharacterMode getCurrentMode();



}