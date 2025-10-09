package fr.ubordeaux.ao.project0.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.model.ModelListener;

public class MazingView {

	private IWindowGame window;
	private Crusader crusader;
	private MazingModel model;

	public MazingView(IWindowGame window, MazingModel model) {
		this.window = window;
		this.model = model;
		init();
	}
	
	private void init() {
		window.setTitle("Mon jeu à moi");
		window.setResizable(true);
		window.setBackground(Color.GRAY);
		window.setVisible(true);
		
		window.fillTilesBackground(72, 1);
		int[][][] walls = {
				{
			{138, 110, 110, 110, 139},
			{108, -1, -1, -1, 111},
			{108, -1, -1, -1, 111},
			{108, -1, -1, -1, 111},
			{136, 109, 113, 109, 137},
			},
				};
		
		window.add(walls);
		
		int[][][] grounds = {{
			{24, 24, 24, 24, 24},
			{24, 24, 24, 24, 24},
			{24, 24, 24, 24, 24},
			{24, 24, 24, 24, 24},
			{24, 24, 24, 24, 24},
			}};
		
		window.add(grounds);
		
		int[][][] items = {{
			{-1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1},
			{-1, -1, 520, -1, -1},
			{-1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1},
			}};
		
		window.add(items);
		
		
		
    	crusader = new Crusader();
    	crusader.setTickAnimationTrigger(e -> onCrusaderTick((Crusader) e));
    	crusader.setBeginAnimationTrigger(e -> onCrusaderBegin((Crusader) e));
    	crusader.setEndAnimationTrigger(e -> onCrusaderEnd((Crusader) e));
    	crusader.setMidAnimationTrigger(e -> onCrusaderMid((Crusader) e));
		window.add(crusader);
	}

	private void onCrusaderTick(Crusader crusader) {
	    model.updateCrusaderPosition();
	    crusader.setPosition(model.getX(), model.getY(), model.getZ());
	}
	
	private void onCrusaderBegin(Crusader crusader) {
		model.updateCrusaderBegin();
	}
	
	private void onCrusaderEnd(Crusader crusader) {
		model.updateCrusaderEnd();
	}
	
	private void onCrusaderMid(Crusader crusader) {
		model.updateCrusaderMid();
	}
	
    public void bindToModel(MazingModel model) {
        model.setListener(new ModelListener() {
            @Override
            public void onModeChanged(Mode mode) {
                if (crusader != null) crusader.setMode(mode);
            }

            @Override
            public void onDirectionChanged(Direction direction) {
                if (crusader != null) {
                	crusader.setDirection(direction);
                }
            }
        });
    }

	

}
