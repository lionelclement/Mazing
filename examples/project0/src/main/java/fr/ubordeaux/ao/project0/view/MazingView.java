package fr.ubordeaux.ao.project0.view;

import javax.swing.JFrame;

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
    				//System.out.println("MODEL ==> VIEW\n");
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
		((JFrame)window).setTitle("Mon jeu à moi");
		((JFrame)window).setSize(1800, 1169);
		((JFrame)window).setResizable(true);
		
		window.setTileSize(130);
		
		window.fillArea(12, -240, -240, 0, 480, 480);
		//window.fillCheckerboardBackground(-12, -12, 24, 24);
		window.setFPS(40);
		
		int[][][] walls = {
				{
					{-1, -1, -1, -1, -1, -1, -1},
					{-1, 138, 110, 110, 110, 139, -1},
					{-1, 108, -1, -1, -1, -1, -1},
					{-1, 108, -1, -1, -1, -1, -1},
					{-1, 108, -1, -1, -1, -1, -1},
					{-1, 136, -1, -1, -1, -1, -1},
					{-1, -1, -1, -1, -1, -1, -1},
				},
			};
		
		window.add(walls);
		
		int[][][] transparentWalls = {
				{
					{-1, -1, -1, -1, -1, -1, -1},
					{-1, -1, -1, -1, -1, -1, -1},
					{-1, -1, -1, -1, -1, 111, -1},
					{-1, -1, -1, -1, -1, 111, -1},
					{-1, -1, -1, -1, -1, 111, -1},
					{-1, -1, 109, 113, 109, 137, -1},
					{-1, -1, -1, -1, -1, -1, -1},
				},
			};
		
		window.add(transparentWalls, 0.7f);
		
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
			{-1, 518, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, 520, -1, -1, 540},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1},
			}};
		
		window.add(items);
	
		window.initImagesSheet("all_600.png", 64, 128, 0.6f, 0, 30, 600);
		window.add(72, 8, 8, 0);
		window.add(665, 8, 8, 0);

    	crusader = new Crusader();
		crusader.setPosition(5, 5, 0);
    	crusader.setTickAnimationTrigger(e -> onTick(e));
    	crusader.setBeginAnimationTrigger(e -> onBegin(e));
    	crusader.setEndAnimationTrigger(e -> onEnd(e));
    	crusader.setMidAnimationTrigger(e -> onMid(e));
    	window.add(crusader);
    	
    	Spider spider = new Spider();
    	spider.setPosition(7, 7, 0);
    	spider.setTickAnimationTrigger(e -> onTick(e));
    	window.add(spider);
		
		window.setVisible(true);
		
	}
	
	private boolean onMid(ICharacter<?> e) {
		return true;
	}

	private boolean onEnd(ICharacter<?> e) {
		return true;
	}

	private boolean onBegin(ICharacter<?> e) {
		return true;
	}

    // La vue lit la position dans le modèle et se met à jour 
    private boolean onTick(ICharacter<?> character) {
    	if (animationListener != null) {
            animationListener.onTick(character);
        }
        if (character.equals(crusader))
			character.setPosition(model.getX(), model.getY(), model.getZ());
		return true;
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
