package fr.ubordeaux.ao.project00.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project00.model.MazingModel;

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
		//window.setSize(1800, 1169);
		window.setVisible(true);
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
    	crusader.setPosition(5, 5, 0);
    	crusader.setScale(3.0f);
    	window.add(crusader);
    	
    	Spider spider = new Spider();
    	spider.setPosition(6, 6, 0);
    	spider.setScale(3.5f);
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

}
