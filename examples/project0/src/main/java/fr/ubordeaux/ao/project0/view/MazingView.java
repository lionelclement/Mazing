package fr.ubordeaux.ao.project0.view;

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
	private int originX;
    private int originY;
    private static int SCROLL_SPEED = 1;
    

	public MazingView(IWindowGame window, MazingModel model) {
		this.window = window;
		this.model = model;
		init();
	}
	
	private void init() {
		window.setTitle("Mon jeu à moi");
		window.setSize(1800, 1169);
		window.setVisible(true);
		window.setResizable(true);
		//window.setBackground(Color.WHITE);
		//window.setTileSize(100, 100);
		
		window.fillTilesBackground(12, -24, -24, 48, 48);
		//window.fillCheckerboardBackground(-12, -12, 24, 24);
		//window.setFPS(50);
		
		int[][][] walls = {
				{
					{-1, -1, -1, -1, -1, -1, -1},
			{-1, 138, 110, 110, 110, 139, -1},
			{-1, 108, -1, -1, -1, 111, -1},
			{-1, 108, -1, -1, -1, 111, -1},
			{-1, 108, -1, -1, -1, 111, -1},
			{-1, 136, 109, 113, 109, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			},
				};
		
		window.add(walls);
		
		int[][][] grounds = {{
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, 72, 72, 72, 72, 72, -1},
			{-1, 72, 72, 72, 72, 72, -1},
			{-1, 72, 72, 72, 72, 72, -1},
			{-1, 72, 72, 72, 72, 72, -1},
			{-1, 72, 72, 72, 72, 72, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			}};
		
		window.add(grounds);
		
		int[][][] items = {{
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, 520, -1, -1, 540},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			}};
		
		window.add(items);
		
    	crusader = new Crusader();
    	crusader.setTickAnimationTrigger(e -> onCrusaderTick((Crusader) e));
    	crusader.setBeginAnimationTrigger(e -> onCrusaderBegin((Crusader) e));
    	crusader.setEndAnimationTrigger(e -> onCrusaderEnd((Crusader) e));
    	crusader.setMidAnimationTrigger(e -> onCrusaderMid((Crusader) e));
    	crusader.setScale(3.0f);
    	crusader.setOffset(0, 12);
    	window.add(crusader);
    	
    	Spider spider = new Spider();
    	spider.setPosition(6, 6, 0);
    	spider.setScale(1.5f);
    	window.add(spider);
		
		
	}
	
	// 🔸 Déplacements
    public void scrollRight() {
        originX += SCROLL_SPEED;
		window.scroll(originX, originY);
    }

    public void scrollLeft() {
        originX -= SCROLL_SPEED;
		window.scroll(originX, originY);
    }

    public void scrollUp() {
        originY -= SCROLL_SPEED;
		window.scroll(originX, originY);
    }

    public void scrollDown() {
        originY += SCROLL_SPEED;
		window.scroll(originX, originY);
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
