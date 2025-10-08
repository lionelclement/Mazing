package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Représente le personnage jouable "Crusader" avec ses animations,
 * sons et comportements spécifiques (marcher, courir, sauter, attaquer, subir
 * des dégâts, etc.).
 * Étend {@link AbstractCharacter} et utilise {@link CharacterMode} pour gérer
 * les différents états du personnage.
 */
public class Crusader extends AbstractCharacter<CharacterMode> {

    private static final int DELAY = 60;
    private static final float WALK_SPEED = 0.03f;
    private static final float RUN_SPEED = 0.08f;
    private static final float JUMP_SPEED = 1.5f;
    private static final float SCALE = 2.5f;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 8;

    public enum Mode implements CharacterMode {
        WALK("walk", 15, true),
        RUN("run", 17, true),
        JUMP("jump", 16, false),
        GOTHIT("gothit", 13, false),
        ATTACK("attack", 18, false),
        FALL("death", 9, false),
        DEATH("death", 9, false),
        IDLE("idle", 16, true);

        private final String name;
        private final int numFrames;
        private final boolean loop;

        // Nouveau constructeur
        Mode(String name, int numFrames, boolean loop) {
            this.name = name;
            this.numFrames = numFrames;
            this.loop = loop;
        }

        public String getName() {
            return name;
        }

        @Override
        public int getNumFrames() {
            return numFrames;
        }

        @Override
        public boolean isLoop() {
            return loop;
        }
    }

    public enum Sound {
        CARPET, CARPET_SOFT,
        WOOD, WOOD_SOFT, JUMP, GOTHIT, ATTACK, DEATH;

    }

    private Mode currentMode;
    private final Sound currentSound;
    private Mode previousMode;
    private float previousSpeedX, previousSpeedY, previousSpeedZ;
    private float previousZ;

    public Crusader() {
        super(Mode.values().length, OFFSET_X, OFFSET_Y);
        previousMode = currentMode = Mode.IDLE;
        currentSound = Sound.CARPET_SOFT;
        currentDirection = Direction.EAST;
        initSounds();
        initAnimations();
    }

    private void initSounds() {
        String name;
        // String key = null;
        for (Sound footstepSound : Sound.values()) {
            switch (footstepSound) {
                case Sound.CARPET -> {
                    name = String.format("/kenney_impact-sounds/Audio/footstep_carpet_000.wav");
                    SoundManager.getInstance().loadSound(Sound.CARPET.name(), name);
                    SoundManager.getInstance().setVolume(Sound.CARPET.name(), 0.8f);
                }
                case Sound.CARPET_SOFT -> {
                    name = String.format("/kenney_impact-sounds/Audio/footstep_carpet_000.wav");
                    SoundManager.getInstance().loadSound(Sound.CARPET_SOFT.name(), name);
                    SoundManager.getInstance().setVolume(Sound.CARPET_SOFT.name(), 0.2f);
                }
                case Sound.WOOD -> {
                    name = String.format("/kenney_impact-sounds/Audio/footstep_wood_000.wav");
                    SoundManager.getInstance().loadSound(Sound.WOOD.name(), name);
                    SoundManager.getInstance().setVolume(Sound.WOOD.name(), 0.8f);
                }
                case Sound.WOOD_SOFT -> {
                    name = String.format("/kenney_impact-sounds/Audio/footstep_wood_000.wav");
                    SoundManager.getInstance().loadSound(Sound.WOOD_SOFT.name(), name);
                    SoundManager.getInstance().setVolume(Sound.WOOD_SOFT.name(), 0.2f);
                }
                case Sound.JUMP -> {
                    name = String.format("/kenney_impact-sounds/Audio/footstep_carpet_000.wav");
                    SoundManager.getInstance().loadSound(Sound.JUMP.name(), name);
                    SoundManager.getInstance().setVolume(Sound.JUMP.name(), 0.8f);
                }

                case Sound.GOTHIT -> {
                    name = String.format("/kenney_impact-sounds/Audio/impactTin_medium_000.wav");
                    SoundManager.getInstance().loadSound(Sound.GOTHIT.name(), name);
                    SoundManager.getInstance().setVolume(Sound.GOTHIT.name(), 0.8f);
                }

                case Sound.ATTACK -> {
                    name = String.format("/kenney_impact-sounds/Audio/impactPlate_heavy_000.wav");
                    SoundManager.getInstance().loadSound(Sound.ATTACK.name(), name);
                    SoundManager.getInstance().setVolume(Sound.ATTACK.name(), 0.8f);
                }

                case Sound.DEATH -> {
                    name = String.format("/kenney_impact-sounds/Audio/impactWood_heavy_000.wav");
                    SoundManager.getInstance().loadSound(Sound.DEATH.name(), name);
                    SoundManager.getInstance().setVolume(Sound.DEATH.name(), 0.8f);
                }

                default -> {
                }
            }

        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initAnimations() {
        Mode[] modes = Mode.values();
        Direction[] directions = Direction.values();

        for (Mode mode : modes) {
            int numFrames = mode.getNumFrames();
            for (Direction direction : directions) {
                images[mode.ordinal()][direction.ordinal()] = Loader.loadAnimations(numFrames,
                        direction.ordinal(),
                        String.format("/isometric-Mini-Crusader/%s/crusader_%s_",
                                mode.getName(),
                                mode.getName()),
                        SCALE);

                shadow_images[mode.ordinal()][direction.ordinal()] = Loader.loadAnimations(numFrames,
                        direction.ordinal(),
                        String.format("/isometric-Mini-Crusader/%s/_shadows/shadow-crusader_%s_",
                                mode.getName(),
                                mode.getName()),
                        SCALE);

                animations[mode.ordinal()][direction.ordinal()] = new Animation(
                        images[mode.ordinal()][direction.ordinal()],
                        shadow_images[mode.ordinal()][direction.ordinal()],
                        DELAY, mode.isLoop(), this);

            }
        }
    }

    @Override
    public Mode getCurrentMode() {
        return currentMode;
    }

    @Override
    public void setMode(CharacterMode mode) {
        previousMode = currentMode;
        Mode newMode = (Mode) mode;
        currentMode = newMode;
        animations[currentMode.ordinal()][currentDirection.ordinal()].reset();
    }

    RenderType getRenderType() {
        return RenderType.OBJECT;
    }

    void render(Graphics g) {
        Animation<CharacterMode> anim = getCurrentAnimation(currentMode.ordinal());
        BufferedImage image = anim.getCurrentFrame();
        BufferedImage shadow = anim.getCurrentShadow();
        TileMap.getInstance().drawImage(x, y, z, shadow, g, offsetX, offsetY, RenderType.OBJECT, 1.0f);
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, RenderType.OBJECT, 1.0f);

        super.render(g, currentMode.ordinal());
    }

    int getRank() {
        return 1;
    }

    @Override
    protected void callTickTrigger(int frameIndex) {
        switch (currentMode) {
            case RUN, WALK -> {
                if (frameIndex % 8 == 3) {
                    SoundManager.getInstance().stop(currentSound.name());
                    SoundManager.getInstance().play(currentSound.name());
                }
            }
            default -> {
            }
        }
        super.callTickTrigger(frameIndex);
    }

    @Override
    public void callBeginTrigger() {
        switch (currentMode) {
            case ATTACK:
                previousSpeedX = speedX;
                previousSpeedY = speedY;
                previousSpeedZ = speedZ;
                break;
            case GOTHIT:
                previousSpeedX = speedX;
                previousSpeedY = speedY;
                previousSpeedZ = speedZ;
                SoundManager.getInstance().play(currentMode.name());
                break;
            case JUMP:
                previousZ = z;
                previousSpeedZ = speedZ;
                speedZ = (float) Math.sqrt(speedX * speedX + speedY * speedY) * JUMP_SPEED;
            default:
                break;
        }
        super.callBeginTrigger();
    }

    @Override
    public void callMidTrigger() {
        switch (currentMode) {
            case JUMP:
                speedZ = -speedZ;
            default:
                break;
        }
        super.callMidTrigger();
    }

    @Override
    public void callEndTrigger() {
        switch (currentMode) {
            case ATTACK:
            case GOTHIT:
                SoundManager.getInstance().play(currentMode.name());
                currentMode = previousMode;
                speedX = previousSpeedX;
                speedY = previousSpeedY;
                speedZ = previousSpeedZ;
                break;
            case FALL:
                currentMode = Mode.IDLE;
                currentMode = Mode.DEATH;
                SoundManager.getInstance().play(currentMode.name());
                animations[currentMode.ordinal()][currentDirection.ordinal()].lastFrame();
                break;
            case JUMP:
                z = previousZ;
                currentMode = previousMode;
                speedZ = previousSpeedZ;
            default:
                break;
        }
        super.callEndTrigger();
    }

    private void moveForward(float speed) {
        float vx = 0, vy = 0, vz = 0;

        // Ajuste la vitesse selon la direction cardinale
        switch (currentDirection) {
            case NORTH -> vy = -speed;
            case SOUTH -> vy = speed;
            case EAST -> vx = speed;
            case WEST -> vx = -speed;
            case NORTHEAST -> {
                vx = speed;
                vy = -speed;
            }
            case NORTHWEST -> {
                vx = -speed;
                vy = -speed;
            }
            case SOUTHEAST -> {
                vx = speed;
                vy = speed;
            }
            case SOUTHWEST -> {
                vx = -speed;
                vy = speed;
            }
        }
        setSpeed(vx, vy, vz);
    }

    @Override
    public void startWalking() {
        setMode(Mode.WALK);
        moveForward(WALK_SPEED);
    }

    @Override
    public void startRunning() {
        setMode(Mode.RUN);
        moveForward(RUN_SPEED);
    }

    @Override
    public void stopMoving() {
        super.setSpeed(0, 0, 0);
        setMode(Mode.IDLE);
    }

    @Override
    public void performAttack() {
        setMode(Mode.ATTACK);
    }

    @Override
    public void die() {
        super.setSpeed(0, 0, 0);
        setMode(Mode.DEATH);
    }


    @Override
    public void setDirection(Direction newDirection) {
        // On met à zéro les vitesses pendant la rotation
        speedX = speedY = speedZ = 0;

        turnPlayer(newDirection, 125, () -> {
            currentDirection = newDirection;

            // Calculer la direction unitaire pour X et Y selon la direction
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

            // Normaliser si diagonale
            if (dirX != 0 && dirY != 0) {
                float invSqrt2 = 1 / (float) Math.sqrt(2);
                dirX *= invSqrt2;
                dirY *= invSqrt2;
            }

            // Obtenir la vitesse scalaire selon l'état actuel
            float speed = switch (currentMode) {
                case WALK -> WALK_SPEED;
                case RUN -> RUN_SPEED;
                default -> 0;
            };

            // Appliquer la vitesse dans la direction choisie
            speedX = dirX * speed;
            speedY = dirY * speed;
            speedZ = 0; // ou conserver speedZ si besoin
        });
    }

    @Override
    public float getIsoDepth() {
        return getX() + getY() + 0.9f;
    }

    @Override
    public void setMode(String mode) {
        // System.err.println("Crusader@changeMode");
        try {
            setMode(Mode.valueOf(mode));
        } catch (IllegalArgumentException e) {
    		throw new IllegalStateException("Unexpected mode");
        }
    }

    @Override
    public void setDirection(String newDirection) {
        try {
            setDirection(Direction.valueOf(newDirection));
        } catch (IllegalArgumentException e) {
    		throw new IllegalStateException("Unexpected direction");
        }
    }

    @Override
    public String toString() {
        return "Crusader [x=" + x + ", y=" + y + ", z=" + z + ", speedX=" + speedX + ", speedY=" + speedY + ", speedZ="
                + speedZ + ", currentState=" + currentMode + "]";
    }

}