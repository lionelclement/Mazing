package fr.ubordeaux.ao.project02;

import java.util.function.Predicate;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.ICharacter;

// Implémentation d'une Functional Interface qui fait tourner le
// personnage quand il est en mode RUN
public class RoundCrusader implements Predicate<ICharacter<?>> {

    private float t;
    private float centerX = 4;
    private float centerY = 4;
    private float Radius = 3;
    private float x, y;
    private float targetX;
    private float targetY;
    private Direction lastDirection;
    private Crusader.Mode mode;
    private float angle;

    @Override
    public boolean test(ICharacter<?> character) {

        if (((MyCrusader) character).getCurrentMode() == Crusader.Mode.RUN) {
            t += 0.005f;
            if (t > 1f)
                t -= 1f; // boucle autour du cercle

            // Angle en radians
            angle = t * 2 * (float) Math.PI;

            if (angle > Math.PI / 8 && angle < Math.PI / 8 + Math.PI / 24) {
                if (mode != Crusader.Mode.ATTACK) {
                    mode = Crusader.Mode.ATTACK;
                    ((MyCrusader) character).setCurrentMode(mode);
                }
            } else {
                mode = Crusader.Mode.RUN;
            }

            // Position circulaire
            x = targetX;
            y = targetY;
            targetX = centerX + Radius * (float) Math.cos(angle);
            targetY = centerY + Radius * (float) Math.sin(angle);

            float dx = targetX - x;
            float dy = targetY - y;
            lastDirection = null;

            float angle2 = (float) Math.atan2(dy, dx); // -pi à pi
            Direction dir;

            if (angle2 >= -Math.PI / 8 && angle2 < Math.PI / 8)
                dir = Direction.EAST;
            else if (angle2 >= Math.PI / 8 && angle2 < 3 * Math.PI / 8)
                dir = Direction.SOUTHEAST;
            else if (angle2 >= 3 * Math.PI / 8 && angle2 < 5 * Math.PI / 8)
                dir = Direction.SOUTH;
            else if (angle2 >= 5 * Math.PI / 8 && angle2 < 7 * Math.PI / 8)
                dir = Direction.SOUTHWEST;
            else if (angle2 >= 7 * Math.PI / 8 || angle2 < -7 * Math.PI / 8)
                dir = Direction.WEST;
            else if (angle2 >= -7 * Math.PI / 8 && angle2 < -5 * Math.PI / 8)
                dir = Direction.NORTHWEST;
            else if (angle2 >= -5 * Math.PI / 8 && angle2 < -3 * Math.PI / 8)
                dir = Direction.NORTH;
            else
                dir = Direction.NORTHEAST;

            if (dir != lastDirection) {
                lastDirection = dir;
                character.setDirection(dir);
            }

            
            character.setPosition(targetX, targetY, 0);
            return false;
        }
					
        return true;
    }

}
