package fr.ubordeaux.ao.mazing.impl.model.entity;

import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.SoundManager;
import fr.ubordeaux.ao.mazing.api.model.entity.EntityMode;

/**
 * Représente une araignée avec ses animations et comportements spécifiques.
 * Étend {@link AbstractEntity} et utilise {@link EntityMode} pour gérer
 * les modes.
 */
public class Spider extends AbstractEntity {

    private static final float WALK_SPEED = 0.03f;

    public enum Mode implements EntityMode {
        WALK("walk", 25, true),
        IDLE("walk", 2, true),
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

        @Override
        public int getNumFrames() {
            return numFrames;
        }

        @Override
        public boolean isLoop() {
            return loop;
        }
    }

    private Mode currentMode;
    SoundManager soundManager;
    private Mode previousMode;
    protected float previousSpeedX, previousSpeedY, previousSpeedZ;

    public Spider() {
        super();
        currentMode = Mode.WALK;
        currentDirection = Direction.EAST;
        soundManager = new SoundManager();
        initSounds();
    }

    private void initSounds() {
        String name;
        // String key = null;
        for (Mode mode : Mode.values()) {
            switch (mode) {
                case Mode.ATTACK -> {
                    name = String.format("/kenney_impact-sounds/Audio/footstep_snow_000.wav");
                    soundManager.loadSound(mode.getName(), name);
                    soundManager.setVolume(mode.getName(), 0.4f);
                }

                case Mode.DEATH -> {
                    name = String.format("/kenney_impact-sounds/Audio/impactSoft_heavy_000.wav");
                    soundManager.loadSound(mode.getName(), name);
                    soundManager.setVolume(mode.getName(), 0.4f);
                }

                default -> {
                }
            }

        }
    }


    @Override
    public Mode getCurrentMode() {
        return currentMode;
    }

    @Override
    public void stop() {
        super.setSpeed(0, 0, 0);
        changeMode(Mode.WALK);
    }

    @Override
    public void changeMode(EntityMode state) {
        Mode s = (Mode) state;
        System.out.println("changeMode: " + currentMode + " -> " + s);
        previousMode = currentMode;
        previousSpeedX = speedX;
        previousSpeedY = speedY;
        previousSpeedZ = speedZ;
        speedX = speedY = speedZ = 0;
        currentMode = s;
    }

    @Override
    public void onBeginAnimation() {
        super.onEndAnimation();
    }

    @Override
    public void onEndAnimation() {
        switch (currentMode) {
            case ATTACK -> {
                soundManager.play(currentMode.name());
                currentMode = previousMode;
                speedX = previousSpeedX;
                speedY = previousSpeedY;
                speedZ = previousSpeedZ;
            }
            case FALL -> {
                currentMode = Mode.DEATH;
                soundManager.play(currentMode.name());
            }
            default -> {
            }
        }
        super.onEndAnimation();
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

    @Override
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
            float speed = switch (currentMode) {
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
    public void changeMode(String mode) {
        try {
            changeMode(Mode.valueOf(mode));
        } catch (IllegalArgumentException e) {
            // Gérer le cas où la direction est inconnue
            System.err.println("Mode invalide : " + mode);
        }
    }

}
