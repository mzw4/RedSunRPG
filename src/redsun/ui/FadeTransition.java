package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import redsun.GameState;
import redsun.RedSunGame;
import redsun.entities.Transition;

public class FadeTransition extends Transition {
  
  private int alpha = 0;
  
  //change - if game specifics arent actually needed just make it take a jpanel or component
  public FadeTransition(RedSunGame game, GameState nextState) {
    super(game, nextState);
  }
  
  public FadeTransition(RedSunGame game, GameState nextState, int speed) {
    super(game, nextState, speed);
  }
  
  public void draw(Graphics2D g) {
    g.setColor(new Color(0, 0, 0, alpha));
    g.fillRect(0, 0, game.getPanelWidth(), game.getPanelHeight());
  }
  
  public void update() {
    if(alpha < 255) {
      alpha += speed;
      if(alpha > 255)
	alpha = 255;
      if(alpha <= 0 && speed <= 0)
	game.setTransition(null);
    }
    else {
      game.setGameState(nextState);
      speed = -speed;
      alpha += speed;
    }
  }  
}
