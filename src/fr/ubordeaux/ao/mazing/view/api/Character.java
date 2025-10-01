package fr.ubordeaux.ao.mazing.view.api;

public interface Character<S extends CharacterMode> extends InternCharacter<S> {

    void setPosition(float x, float y, float z);

    void setSpeed(float xSpeed, float ySpeed, float zSpeed);

    void walk();

    void run();

    void stop();

    Direction getCurrentDirection();

    void setDirection(Direction direction);

    S getCurrentMode();

    void changeDirection(Direction targetDirection);

    void changeDirection(String targetDirection);

    void changeMode(S mode);

    void changeMode(String mode);

    void setLoopTrigger(Runnable callback);

    void setBeginLoopTrigger(Runnable callback);

    void setMidLoopTrigger(Runnable callback);

    void setEndLoopTrigger(Runnable callback);

    void setOnTileTrigger(Runnable callback);

    void update();

}
