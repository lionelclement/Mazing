package fr.ubordeaux.ao.project0.model;

import java.awt.LayoutManager;

import fr.ubordeaux.ao.mazing.api.model.entity.Entity;
import fr.ubordeaux.ao.mazing.impl.model.entity.Crusader;

public class MazingModel {
	
	private Crusader crusader;
	
	public MazingModel()
	{
		crusader = new Crusader();
		crusader.changeMode("WALK");
	}

	public Entity getCrusader() {
		return crusader;
	}

	public LayoutManager getBoardGame() {
		return this.getBoardGame();;
	}
	
}
