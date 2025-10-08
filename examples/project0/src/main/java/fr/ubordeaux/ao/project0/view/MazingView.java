package fr.ubordeaux.ao.project0.view;

import java.awt.Color;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.project0.model.MazingModel;

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
    	getWindow().setTitle("Mon jeu à moi");
    	getWindow().setResizable(true);
    	getWindow().setBackground(Color.GRAY);
    	getWindow().setVisible(true);
    	crusader = new Crusader();
    	crusader.setTickAnimationTrigger(e -> onCrusaderTick((Crusader) e));
		window.add(crusader);
	}

	public IWindowGame getWindow() {
		return window;
	}

	public Crusader getCrusader() {
		return crusader;
	}
	
	private void onCrusaderTick(Crusader c) {
	    model.updateCrusaderPosition();
	    c.setPosition(model.getX(), model.getY(), model.getZ());
	}
	
	

}
