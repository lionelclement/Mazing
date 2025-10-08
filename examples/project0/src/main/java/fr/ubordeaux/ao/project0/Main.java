package fr.ubordeaux.ao.project0;

import java.awt.Color;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.IWindowGame;

import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {
    public static void main(String[] args) {
    	
    	IWindowGame window = new WindowGame();
    	
    	window.setTitle("Mon jeu à moi");
    	window.setResizable(true);
    	window.setBackground(Color.GRAY);
    	window.setVisible(true);

    	Crusader crusader = new Crusader();
    	window.add(crusader);
    	crusader.setDirection(Direction.SOUTH);
    	crusader.setDirection("SOUTH");
    	crusader.setPosition(0, 0, 0);
    	crusader.setX(0);
    	//crusader.startWalking();
    	crusader.startRunning();
    	crusader.setDirection(Direction.WEST);
    	
    	crusader.setTickAnimationTrigger(() -> {System.err.println(crusader.getX());});
    	//crusader.setMode(Crusader.Mode.WALK);
    	//crusader.startRunning();
    	//crusader.performJump();
    	//crusader.performAttack();
    	//crusader.die();
    	
    	
    	Spider spider = new Spider();
    	window.add(spider);
    	spider.startWalking();
    	
    	
    	//window.fillCheckerboardBackground(12);
    	
    	
    	
    }

}
