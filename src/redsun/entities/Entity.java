package redsun.entities;

import redsun.Sprite;


public class Entity extends Sprite {
  
  public Entity(String id) {
    super(id);
  }

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  
}
