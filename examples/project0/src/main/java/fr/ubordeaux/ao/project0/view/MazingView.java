package fr.ubordeaux.ao.project0.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.ICharacter;
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
    private AnimationListener animationListener;
    private static int SCROLL_SPEED = 1;
    

	public MazingView(IWindowGame window, MazingModel model) {
		this.window = window;
		this.model = model;
		init();
	}
	
	// Le game loop s'abonne ici
    public void attachAnimationListener(AnimationListener listener) {
        this.animationListener = listener;
    }

    // La vue écoute le modèle
    public void attachModelListener(MazingModel model) {
    	model.attachModelListener(new ModelListener() {
            @Override
            public void onModeChanged(Mode mode) {
                if (crusader != null) {
    				System.out.println("MODEL ==> VIEW\n");
                	crusader.setMode(mode);
                }
            }

            @Override
            public void onDirectionChanged(Direction direction) {
                if (crusader != null) {
                	crusader.setDirection(direction);
                }
            }

        });
    }

    
	private void init() {
		window.setTitle("Mon jeu à moi");
		//window.setSize(1800, 1169);
		window.setResizable(true);
		//window.setBackground(Color.WHITE);
		window.setTileSize(120, 120);
		
		window.fillTilesBackground(12, -24, -24, 48, 48);
		//window.fillCheckerboardBackground(-12, -12, 24, 24);
		window.setFPS(40);
		
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
    	crusader.setTickAnimationTrigger(e -> onTick(e));
    	crusader.setBeginAnimationTrigger(e -> onBegin(e));
    	crusader.setEndAnimationTrigger(e -> onEnd(e));
    	crusader.setMidAnimationTrigger(e -> onMid(e));
    	crusader.setScale(3.0f);
    	crusader.setOffset(0, 6);
    	window.add(crusader);
    	
    	Spider spider = new Spider();
    	spider.setPosition(6, 6, 0);
    	spider.setScale(2.5f);
    	spider.setTickAnimationTrigger(e -> onTick(e));
    	window.add(spider);
		
		window.setVisible(true);
		
	}
	
	private void onMid(ICharacter<?> e) {
		// TODO Auto-generated method stub
	}

	private void onEnd(ICharacter<?> e) {
		// TODO Auto-generated method stub
	}

	private void onBegin(ICharacter<?> e) {
		// TODO Auto-generated method stub
	}

    // La vue lit la position dans le modèle et se met à jour 
    private void onTick(ICharacter<?> character) {
    	if (animationListener != null) {
            animationListener.onTick(character);
        }
        if (character.equals(crusader))
			character.setPosition(model.getX(), model.getY(), model.getZ());
	}
	
	// Déplacements du plateau
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

	

}
