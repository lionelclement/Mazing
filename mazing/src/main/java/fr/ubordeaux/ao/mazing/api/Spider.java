package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Représente une araignée avec ses animations et comportements spécifiques.
 * Étend {@link AbstractCharacter} et utilise {@link CharacterMode} pour gérer
 * les modes.
 */
public class Spider extends AbstractCharacter<CharacterMode> {

    private static final float DEFAULT_SCALE = 2.5f;
    private static final float OFFSETX = -0.1f;
    private static final float OFFSETY = 0.1f;

    public enum Mode implements CharacterMode {
        WALK("walk", 25, true),
        IDLE("walk", 1, true),
        ATTACK("attack", 8, false),
        FALL("death", 8, false),
        DEATH("death", 8, false),
        TURN_LEFT("walk", 25, true),
        TURN_RIGHT("walk", 25, true);

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
        super(id, Mode.values().length, OFFSETX, OFFSETY);
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
        List<Direction> selectedDirections = List.of(
                Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST, Direction.NORTH, Direction.NORTHWEST,
                Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH

        );

        for (Mode mode : modes) {
            switch (mode) {
                case ATTACK:
                case DEATH:
                case FALL:
                case IDLE:
                case WALK: {

                    int numFrames = mode.getNumFrames();
                    for (Direction direction : selectedDirections) {
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
                                mode.isLoop(), DEFAULT_FPS, this);

                    }
                }
                    break;
                case TURN_LEFT:
                case TURN_RIGHT: {
                    int numFrames = mode.getNumFrames();
                    for (Direction direction : selectedDirections) {
                        images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadTurnAnimations(numFrames,
                                String.format("/bw_spider/%s/%s-BW_Spider_",
                                        mode.getName(),
                                        mode.getName()),
                                scale, mode == Mode.TURN_LEFT);

                        shadow_images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadTurnAnimations(numFrames,
                                String.format("/bw_spider/%s/_shadow/%s-BW_Spider-shadow_",
                                        mode.getName(),
                                        mode.getName()),
                                scale, mode == Mode.TURN_LEFT);

                        animations[mode.ordinal()][direction.ordinal()] = new Animation(
                                images[mode.ordinal()][direction.ordinal()],
                                shadow_images[mode.ordinal()][direction.ordinal()],
                                mode.isLoop(), DEFAULT_FPS, this);

                    }
                }
                    break;
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
        previousMode = currentMode;
        currentMode = s;
        animations[currentMode.ordinal()][currentDirection.ordinal()].reset();
    }

    @Override
    public void setMode(String modeName) {
        setMode(Mode.valueOf(modeName));
    }

    RenderType getRenderType() {
        return RenderType.CONSTRUCTION;
    }

    void render(Graphics g) {
        Animation<CharacterMode> anim = getCurrentAnimation(currentMode.ordinal());
        BufferedImage image = anim.getCurrentFrame();
        BufferedImage shadow = anim.getCurrentShadow();
        TileMap.drawImage(x, y, z, shadow, g, RenderType.CONSTRUCTION, 1f);
        TileMap.drawImage(x, y, z, image, g, RenderType.CONSTRUCTION, 1f);

        super.render(g, currentMode.ordinal());
    }

    int getRank() {
        return 1;
    }

    @Override
    public boolean callBeginTrigger() {
        return super.callEndTrigger();
    }

    @Override
    public boolean callEndTrigger() {
        if (super.callEndTrigger()) {
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
        return true;
    }
    return false;
    }

    @Override
    protected boolean callTickTrigger(float frameIndex) {
        return super.callTickTrigger(frameIndex);
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
        setPosition(x, y, z, offsetX, offsetY);
    }

}
