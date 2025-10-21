package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.UUID;

/**
 * Représente le personnage jouable "Crusader" avec ses animations,
 * sons et comportements spécifiques (marcher, courir, sauter, attaquer, subir
 * des dégâts, etc.).
 * Étend {@link AbstractCharacter} et utilise {@link CharacterMode} pour gérer
 * les différents états du personnage.
 */
public class Crusader extends AbstractCharacter<CharacterMode> {

    private static final float DEFAULT_SCALE = 3.0f;
    private static final float OFFSETX = 0.0f;
    private static final float OFFSETY = 0.0f;

    public enum Mode implements CharacterMode {
        WALK("walk", 15, true),
        RUN("run", 17, true),
        JUMP("jump", 16, false),
        GOTHIT("gothit", 13, false),
        ATTACK("attack", 18, false),
        FALL("death", 9, false),
        DEATH("death", 9, false),
        IDLE("idle", 16, true),
        TURN_LEFT("idle", 16, true),
        TURN_RIGHT("idle", 16, true),
        WALK_LEFT("walk", 15, true),
        WALK_RIGHT("walk", 15, true);

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
    private Sound currentSound;
    private Mode savedMode;
    private Sound savedSound;

    public Crusader(UUID id) {
        super(id, Mode.values().length, OFFSETX, OFFSETY);
        init();
    }

    public Crusader() {
        this(UUID.randomUUID());
        init();
    }

    private void init() {
        savedMode = currentMode = Mode.IDLE;
        currentSound = Sound.CARPET_SOFT;
        currentDirection = Direction.EAST;
        initSounds();
        this.scale = DEFAULT_SCALE;
        initAnimations(DEFAULT_SCALE);
    }

    private void initSounds() {
        String name;
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
    protected void initAnimations(float scale) {
        Mode[] modes = Mode.values();
        for (Mode mode : modes) {
            switch (mode) {
                case ATTACK:
                case DEATH:
                case FALL:
                case GOTHIT:
                case IDLE:
                case JUMP:
                case RUN:
                case WALK: {
                    int numFrames = mode.getNumFrames();
                    for (Direction direction : Direction.values()) {
                        images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadAnimations(numFrames,
                                direction.ordinal(),
                                String.format("/isometric-Mini-Crusader/%s/crusader_%s_",
                                        mode.getName(),
                                        mode.getName()),
                                scale);

                        shadow_images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadAnimations(numFrames,
                                direction.ordinal(),
                                String.format("/isometric-Mini-Crusader/%s/_shadows/shadow-crusader_%s_",
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
                case TURN_RIGHT:
                case WALK_LEFT:
                case WALK_RIGHT: {
                    boolean left = switch (mode) {
                        case TURN_LEFT -> true;
                        case WALK_LEFT -> true;
                        default -> false;
                    };
                    int numFrames = mode.getNumFrames();
                    for (Direction direction : Direction.values()) {
                        images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadTurnAnimations(numFrames,
                                String.format("/isometric-Mini-Crusader/%s/crusader_%s_",
                                        mode.getName(),
                                        mode.getName()),
                                scale, left);

                        shadow_images[mode.ordinal()][direction.ordinal()] = ImageLoader.loadTurnAnimations(numFrames,
                                String.format("/isometric-Mini-Crusader/%s/_shadows/shadow-crusader_%s_",
                                        mode.getName(),
                                        mode.getName()),
                                scale, left);

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
    public void setMode(CharacterMode mode) {
        savedMode = currentMode;
        Mode newMode = (Mode) mode;
        currentMode = newMode;
        savedSound = currentSound;
        switch (mode) {
            case Mode.WALK -> currentSound = Sound.CARPET_SOFT;
            case Mode.RUN -> currentSound = Sound.CARPET;
            case Mode.ATTACK -> currentSound = Sound.ATTACK;
            case Mode.JUMP -> currentSound = Sound.WOOD_SOFT;
            case Mode.GOTHIT -> currentSound = Sound.GOTHIT;
            case Mode.DEATH -> currentSound = Sound.DEATH;
            default -> {
            }
        }
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
    protected boolean callTickTrigger(float frameIndex) {
        if (super.callTickTrigger(frameIndex)) {

            switch (currentMode) {
                case RUN, WALK -> {
                    if ((int) (frameIndex % 8) == 3) {
                        SoundManager.getInstance().play(currentSound.name());
                    }
                }
                default -> {
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean callBeginTrigger() {
        if (super.callBeginTrigger()) {
            switch (currentMode) {
                case GOTHIT:
                case JUMP:
                    SoundManager.getInstance().play(currentSound.name());
                default:
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean callMidTrigger() {
        return super.callMidTrigger();
    }

    @Override
    public boolean callEndTrigger() {
        if (super.callEndTrigger()) {
            switch (currentMode) {
                case ATTACK:
                case GOTHIT:
                    SoundManager.getInstance().play(currentSound.name());
                    currentMode = savedMode;
                    currentSound = savedSound;
                    break;
                case FALL:
                    currentMode = Mode.DEATH;
                    SoundManager.getInstance().play(currentSound.name());
                    animations[currentMode.ordinal()][currentDirection.ordinal()].lastFrame();
                    break;
                case JUMP:
                    currentMode = savedMode;
                    SoundManager.getInstance().play(currentSound.name());
                    currentSound = savedSound;
                default:
                    break;
            }
            return true;
        }
        return false;
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
        Crusader other = (Crusader) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        setPosition(x, y, z, offsetX, offsetY);
    }

    @Override
    public String toString() {
        return "Crusader [z=" + z + ", getIsoDepth()=" + getIsoDepth() + "]";
    }

}