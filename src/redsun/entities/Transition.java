package redsun.entities;

import java.awt.Graphics2D;

import redsun.GameState;
import redsun.RedSunGame;

public abstract class Transition {
  
  protected int speed;
  
  protected RedSunGame game;
  protected GameState nextState;
  
  public Transition(RedSunGame game, GameState nextState) {
    this.game = game;
    this.nextState = nextState;
    
    //default transition speed
    speed = 5;
  }
  
  public Transition(RedSunGame game, GameState nextState, int speed) {
    this(game, nextState);
    this.speed = speed;
  }
  
  public abstract void update();
  public abstract void draw(Graphics2D g);
}
