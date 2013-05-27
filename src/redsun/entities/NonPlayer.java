package redsun.entities;

public class NonPlayer extends Character
{
  public enum Affil {
    ALLY, NEUTRAL, ENEMY
  }
  
  private Affil affil;
  private String id;
  
  //determines whether or not the player can control this character
  private boolean control;
  
  public NonPlayer(String name, Affil a, Class c, boolean control) {
    super(name, c);
    affil = a;
    this.control = control;
  }
}
