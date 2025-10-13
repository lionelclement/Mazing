package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.UUID;

/**
 * Représente une araignée avec ses animations et comportements spécifiques.
 * Étend {@link AbstractCharacter} et utilise {@link CharacterMode} pour gérer
 * les modes.
 */
public class Spider extends AbstractCharacter<CharacterMode> {

    private static final float DEFAULT_SCALE = 2.5f;
    private static final float OFFSETX = -0.8f;
    private static final float OFFSETY = -0.8f;

    public enum Mode implements CharacterMode {
        WALK("walk", 25, true),
        IDLE("walk", 1, true),
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
    private Mode previousMode;
    protected float previousSpeedX, previousSpeedY, previousSpeedZ;

    public Spider(UUID id) {
        super(id, Mode.values().length);
        init();
    }

    public Spider() {
        this(UUID.randomUUID());
        init();
    }

    private void init() {
        currentMode = Mode.WALK;
        currentDirection = Direction.EAST;
        initSounds();
        this.scale = DEFAULT_SCALE;
        initAnimations(DEFAULT_SCALE);

    }

    private void initSounds() {
        String name;
        // String key = null;
        for (Mode mode : Mode.values()) {
            switch (mode) {
                case Mode.ATTACK -> {
                    name = String.format("/kenney_impact-sounds/Audio/footstep_snow_000.wav");
                    SoundManager.getInstance().loadSound(mode.getName(), name);
                    SoundManager.getInstance().setVolume(mode.getName(), 0.4f);
                }

                case Mode.DEATH -> {
                    name = String.format("/kenney_impact-sounds/Audio/impactSoft_heavy_000.wav");
                    SoundManager.getInstance().loadSound(mode.getName(), name);
                    SoundManager.getInstance().setVolume(mode.getName(), 0.4f);
                }

                default -> {
                }
            }

        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void initAnimations(float scale) {
        Mode[] modes = Mode.values();
        Direction[] directions = Direction.values();

        for (Mode mode : modes) {
            int numFrames = mode.getNumFrames();
            for (Direction direction : directions) {
                images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadAnimations(numFrames,
                        (direction.ordinal() + 6) % 8,
                        String.format("/bw_spider/%s/%s-BW_Spider_",
                                mode.getName(),
                                mode.getName()),
                        scale);

                shadow_images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadAnimations(numFrames,
                        (direction.ordinal() + 6) % 8,
                        String.format("/bw_spider/%s/_shadow/%s-BW_Spider-shadow_",
                                mode.getName(),
                                mode.getName()),
                        scale);

                animations[mode.ordinal()][direction.ordinal()] = new Animation(
                        images[mode.ordinal()][direction.ordinal()],
                        shadow_images[mode.ordinal()][direction.ordinal()],
                        mode.isLoop(), this);

            }
        }
    }

    @Override
    public Mode getCurrentMode() {
        return currentMode;
    }

    @Override
    public void setMode(CharacterMode state) {
        Mode s = (Mode) state;
        System.out.println("changeMode: " + currentMode + " -> " + s);
        previousMode = currentMode;
        currentMode = s;
        animations[currentMode.ordinal()][currentDirection.ordinal()].reset();
    }

    RenderType getRenderType() {
        return RenderType.CONSTRUCTION;
    }

    void render(Graphics g) {
        Animation<CharacterMode> anim = getCurrentAnimation(currentMode.ordinal());
        BufferedImage image = anim.getCurrentFrame();
        BufferedImage shadow = anim.getCurrentShadow();
        TileMap.getInstance().drawImage(x, y, z, shadow, g, RenderType.CONSTRUCTION, 1.0f);
        TileMap.getInstance().drawImage(x, y, z, image, g, RenderType.CONSTRUCTION, 1.0f);

        super.render(g, currentMode.ordinal());
    }

    int getRank() {
        return 1;
    }

    @Override
    public void callBeginTrigger() {
        super.callEndTrigger();
    }

    @Override
    public void callEndTrigger() {
        switch (currentMode) {
            case ATTACK -> {
                SoundManager.getInstance().play(currentMode.name());
                currentMode = previousMode;
            }
            case FALL -> {
                currentMode = Mode.DEATH;
                SoundManager.getInstance().play(currentMode.name());
                animations[currentMode.ordinal()][currentDirection.ordinal()].lastFrame();
            }
            default -> {
            }
        }
        super.callEndTrigger();
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
    public void setDirection(Direction newDirection) {
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

        });
    }

    @Override
    public float getIsoDepth() {
        return x + y + 1.5f;
    }

    @Override
    public void setMode(String mode) {
        try {
            setMode(Mode.valueOf(mode));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unexpected mode");
        }
    }

    @Override
    protected void callTickTrigger(int frameIndex) {
        super.callTickTrigger(frameIndex);
    }

    @Override
    protected float getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Spider other = (Spider) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        setPosition(x, y, z, OFFSETX, OFFSETY);
    }

}
