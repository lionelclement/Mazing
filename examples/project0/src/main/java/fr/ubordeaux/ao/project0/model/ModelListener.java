package fr.ubordeaux.ao.project0.model;

import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.Direction;

public interface ModelListener {
    void onModeChanged(Mode mode);
    void onDirectionChanged(Direction direction);
}
