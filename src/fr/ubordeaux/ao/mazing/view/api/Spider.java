package fr.ubordeaux.ao.mazing.view.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Spider extends AbstractCharacter<CharacterMode> {

    private static final int DELAY = 60;
    private static final float WALK_SPEED = 0.03f;
    private static final float SCALE = 2.5f;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;

    public enum Mode implements CharacterMode {
        IDLE("idle", 1, true),
        WALK("walk", 25, true),
        ATTACK("attack", 8, false),
        FALL("death", 8, false),
        DEATH("death", 8, false);

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

        public int getNumFrames() {
            return numFrames;
        }

        public boolean isLoop() {
            return loop;
        }
    }

    private Mode currentState;
    SoundManager soundManager = new SoundManager();
    private Mode previousState;
    protected float previousSpeedX, previousSpeedY, previousSpeedZ;

    public Spider() {
        super(Mode.values().length, OFFSET_X, OFFSET_Y);
        currentState = Mode.WALK;
        currentDirection = Direction.EAST;
        soundManager = new SoundManager();
        initSounds();
        initAnimations();
    }

    private void initSounds() {
        String name = null;
        // String key = null;
        for (Mode state : Mode.values()) {
            switch (state) {
                case Mode.ATTACK:
                    name = String.format("/kenney_impact-sounds/Audio/footstep_snow_000.wav");
                    soundManager.loadSound(state.name(), name);
                    soundManager.setVolume(state.name(), 0.4f);
                    break;

                case Mode.DEATH:
                    name = String.format("/kenney_impact-sounds/Audio/impactSoft_heavy_000.wav");
                    soundManager.loadSound(state.name(), name);
                    soundManager.setVolume(state.name(), 0.4f);
                    break;

                default:
                    break;
            }

        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initAnimations() {
        Mode[] states = Mode.values();
        Direction[] directions = Direction.values();

        for (Mode state : states) {
            int numFrames = state.getNumFrames();
            for (Direction direction : directions) {
                images[state.ordinal()][direction.ordinal()] = Loader.loadAnimations(
                        numFrames,
                        (direction.ordinal() + 6) % 8,
                        String.format("/bw_spider/%s/%s-BW_Spider_",
                                state.getName(),
                                state.getName()),
                        SCALE);

                shadow_images[state.ordinal()][direction.ordinal()] = Loader.loadAnimations(
                        numFrames,
                        (direction.ordinal() + 6) % 8,
                        String.format("/bw_spider/%s/_shadow/%s-BW_Spider-shadow_",
                                state.getName(),
                                state.getName()),
                        SCALE);

                animations[state.ordinal()][direction.ordinal()] = new Animation(
                        images[state.ordinal()][direction.ordinal()],
                        shadow_images[state.ordinal()][direction.ordinal()],
                        DELAY, state.isLoop(), this);

            }
        }
    }

    public Mode getCurrentMode() {
        return currentState;
    }

    public void stop() {
        super.setSpeed(0, 0, 0);
        changeMode(Mode.WALK);
    }

    @Override
    public void changeMode(CharacterMode state) {
        Mode s = (Mode) state;
        System.out.println("changeState: " + currentState + " -> " + s);
        previousState = currentState;
        previousSpeedX = speedX;
        previousSpeedY = speedY;
        previousSpeedZ = speedZ;
        speedX = speedY = speedZ = 0;
        currentState = s;
        animations[currentState.ordinal()][currentDirection.ordinal()].reset();
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.OBJECT;
    }

    @Override
    public void render(Graphics g) {
        Animation<CharacterMode> anim = getCurrentAnimation(currentState.ordinal());
        BufferedImage image = anim.getCurrentFrame();
        BufferedImage shadow = anim.getCurrentShadow();
        TileMap.getInstance().drawImage(x, y, z, shadow, g, offsetX, offsetY, RenderType.OBJECT);
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, RenderType.OBJECT);

        super.render(g, currentState.ordinal());
    }

    @Override
    public int getRank() {
        return 1;
    }

    @Override
    public String toString() {
        return "Crusader [x=" + x + ", y=" + y + ", z=" + z + ", getRenderType()=" + getRenderType() + ", getRank()="
                + getRank() + "]";
    }

    @Override
    public void callBeginLoopTrigger() {
        super.callEndLoopTrigger();
    }

    @Override
    public void callEndLoopTrigger() {
        switch (currentState) {
            case ATTACK:
                soundManager.play(currentState.name());
                currentState = previousState;
                speedX = previousSpeedX;
                speedY = previousSpeedY;
                speedZ = previousSpeedZ;
                break;
            case FALL:
                currentState = Mode.DEATH;
                soundManager.play(currentState.name());
                animations[currentState.ordinal()][currentDirection.ordinal()].lastFrame();
                break;
            default:
                break;
        }
        super.callEndLoopTrigger();
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
    public void walk() {
        changeMode(Mode.WALK);
        moveForward(WALK_SPEED);
    }

    @Override
    public void run() {
    }

    @Override
    public void changeDirection(String newDirection) {
        try {
            changeDirection(Direction.valueOf(newDirection));
        } catch (IllegalArgumentException e) {
            // Gérer le cas où la direction est inconnue
            System.err.println("Direction invalide : " + newDirection);
        }
    }

    public void changeDirection(Direction newDirection) {
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
            float speed = switch (currentState) {
                case WALK -> WALK_SPEED;
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
        return getX() + getY() + 0.6f;
    }

    @Override
    public void update() {
        float speed = (float) Math.sqrt((speedX * speedX + speedY * speedY + speedZ * speedZ));
        if (speed < 0.02f)
            changeMode(Spider.Mode.IDLE);
        else
            changeMode(Spider.Mode.WALK);

    }

    @Override
    public void changeMode(String mode) {
        try {
            changeMode(Mode.valueOf(mode));
        } catch (IllegalArgumentException e) {
            // Gérer le cas où la direction est inconnue
            System.err.println("Mode invalide : " + mode);
        }
    }

}
