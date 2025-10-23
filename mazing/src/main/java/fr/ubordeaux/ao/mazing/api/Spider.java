package fr.ubordeaux.ao.mazing.api;

import java.awt.Color;
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
    private static final float OFFSETX = -0.1f;
    private static final float OFFSETY = 0.1f;

    static protected BufferedImage[][][] cachedImages; // [mode][direction][frame]
    static protected BufferedImage[][][] cachedShadow_images; // [mode][direction][frame]

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
    private boolean soundsInitialized;

    public Spider(UUID id, float scale) {
        super(id, Mode.values().length, OFFSETX, OFFSETY);
        init(scale);
    }

    public Spider(UUID id) {
        this(UUID.randomUUID(), DEFAULT_SCALE);
    }

    public Spider(float scale) {
        this(UUID.randomUUID(), scale);
    }

    public Spider() {
        this(UUID.randomUUID(), DEFAULT_SCALE);
    }

    public BufferedImage[][][] getCachedImages() {
        return cachedImages;
    }

    public BufferedImage[][][] getCachedShadow_images() {
        return cachedShadow_images;
    }

    public void setCachedImages(BufferedImage[][][] cachedImages) {
        Spider.cachedImages = cachedImages;
    }

    public void setCachedShadow_images(BufferedImage[][][] cachedShadow_images) {
        Spider.cachedShadow_images = cachedShadow_images;
    }

    private void init(float scale) {
        currentMode = Mode.WALK;
        currentDirection = Direction.EAST;
        initSounds();
        this.scale = scale;
        initAnimations(scale);
    }

private void initSounds() {
        if (!soundsInitialized) {
            // Sons spécifiques à Spider
            SoundCache.loadIfAbsent("ATTACK", "/kenney_impact-sounds/Audio/footstep_snow_000.wav", 0.4f);
            SoundCache.loadIfAbsent("DEATH", "/kenney_impact-sounds/Audio/impactSoft_heavy_000.wav", 0.4f);

            // Tu peux ajouter d’autres sons si besoin
            soundsInitialized = true; // une seule fois pour toutes les instances
        }
    }
    @Override
    protected void loadImages(float scale2) {
        Mode[] modes = Mode.values();
        for (Mode mode : modes) {
            switch (mode) {
                case ATTACK:
                case DEATH:
                case FALL:
                case IDLE:
                case WALK: {

                    int numFrames = mode.getNumFrames();
                    for (Direction direction : Direction.values()) {
                        int correctedDirection = (direction.ordinal() + 6) % 8;
                        images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadAnimations(
                                numFrames,
                                correctedDirection,
                                String.format("/bw_spider/%s/%s-BW_Spider_",
                                        mode.getName(),
                                        mode.getName()),
                                scale);

                        shadow_images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadAnimations(numFrames,
                                correctedDirection,
                                String.format("/bw_spider/%s/_shadows/%s-BW_Spider-shadow_",
                                        mode.getName(),
                                        mode.getName()),
                                scale);

                    }
                }
                    break;
                case TURN_LEFT:
                case TURN_RIGHT: {
                    boolean left = switch (mode) {
                        case TURN_LEFT -> true;
                        default -> false;
                    };
                    int numFrames = mode.getNumFrames();
                    for (Direction direction : Direction.values()) {
                        images[mode.ordinal()][direction.ordinal()] = ImageLoader
                                .loadTurnAnimations(numFrames,
                                        String.format("/bw_spider/%s/%s-BW_Spider_",
                                                mode.getName(),
                                                mode.getName()),
                                        scale, left);

                        shadow_images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadTurnAnimations(
                                numFrames,
                                String.format("/bw_spider/%s/_shadows/%s-BW_Spider-shadow_",
                                        mode.getName(),
                                        mode.getName()),
                                scale, left);

                    }
                }
                    break;
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void loadAnimations() {
        Mode[] modes = Mode.values();
        for (Mode mode : modes) {
            switch (mode) {
                case ATTACK:
                case DEATH:
                case FALL:
                case IDLE:
                case WALK:
                case TURN_LEFT:
                case TURN_RIGHT: {
                    for (Direction direction : Direction.values()) {
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

    @Override
    void render(Graphics g, Color color) {
        Animation<CharacterMode> anim = getCurrentAnimation(currentMode.ordinal());
        BufferedImage image = anim.getCurrentFrame();
        BufferedImage shadow = anim.getCurrentShadow();
        TileMap.drawImage(x, y, z, shadow, g, RenderType.CONSTRUCTION, 1f, brightness, null);
        TileMap.drawImage(x, y, z, image, g, RenderType.CONSTRUCTION, 1f, brightness, color);

        super.render(g, currentMode.ordinal(), color);
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
                    SoundCache.playCached(currentMode.name());
                    currentMode = previousMode;
                }
                case FALL -> {
                    currentMode = Mode.DEATH;
                    SoundCache.playCached(currentMode.name());
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
