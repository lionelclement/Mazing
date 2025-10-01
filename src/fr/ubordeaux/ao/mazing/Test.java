package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.view.*;
import fr.ubordeaux.ao.mazing.view.api.Crusader;
import fr.ubordeaux.ao.mazing.view.api.Direction;
import fr.ubordeaux.ao.mazing.view.api.IBoardGame;
import fr.ubordeaux.ao.mazing.view.api.IWindow;
import fr.ubordeaux.ao.mazing.view.api.Spider;
import fr.ubordeaux.ao.mazing.view.api.Window;

public final class Test {
        public static void main(String[] args) {

                IWindow window = new Window();
                IBoardGame boardGame = window.getBoardGame();
                // boardGame.fillTilesBackground(72);
                boardGame.fillCheckerboardBackground(3);

                if (true) {
                        Crusader crusader = new Crusader();
                        crusader.setPosition(0, 0, 0);
                        crusader.changeDirection(Direction.SOUTH);
                        crusader.changeMode(Crusader.Mode.WALK);
                        crusader.stop();
                        boardGame.addToSceneMap(crusader);

                        // boardGame.setPlayer(crusader);

                        Spider spider = new Spider();
                        spider.setPosition(1, 0, 0);
                        // spider.setSpeed(0.033f, 0, 0);
                        spider.setDirection(Direction.NORTHWEST);
                        boardGame.addToSceneMap(spider);

                        int[][][] grounds = new int[][][] {
                                        {
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        },

                        };

                        int[][][] walls = new int[][][] {
                                        {
                                                        { 180, 102, 102, 102, 102, 102, 102, 102, 182 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 203 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 303 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 403 },
                                                        { 181, 101, 101, 101, 101, 101, 101, 101, 183 }
                                        },
                                        {
                                                        { 180, 102, 102, 102, 102, 102, 102, 102, 182 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 203 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 303 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 403 },
                                                        { 181, 101, 101, 101, 101, 101, 101, 101, 183 }
                                        },
                                        {
                                                        { 180, 102, 102, 102, 102, 102, 102, 102, 182 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 203 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 303 },
                                                        { 108, -1, -1, -1, -1, -1, -1, -1, 403 },
                                                        { 181, 101, 101, 101, 101, 101, 101, 101, 183 }
                                        }
                        };

                        boardGame.addToSceneMap(72, 1, 1, 0);
                        int k = 500;
                        int lenght = 7;
                        for (int i = 1; i <= lenght; i++) {
                                for (int j = 1; j <= lenght; j++) {
                                        if ((Math.abs(i) + Math.abs(j)) % 2 == 1) {
                                                boardGame.addToSceneMap(k++, i, j, 0);
                                        }
                                }
                        }

                        boardGame.addToSceneMap(grounds);
                        boardGame.addToSceneMap(walls);
                        // boardGame.addToSceneMap(506, 3, 3, 0);
                        // boardGame.addToSceneMap(72, 0, 0, 0);
                        // boardGame.addToSceneMap(72, 1, 0, 0);
                        // boardGame.addToSceneMap(103, 1, 0, 0);
                }

        }

}
