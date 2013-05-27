package redsun.entities;

import java.util.ArrayList;

import redsun.MapEntity;

public class Trigger {

  private String id;
  private int x, y;
  
  private ArrayList<MapEntity> activators;
  
  public Trigger(String id, int x, int y, ArrayList<MapEntity> activators) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.activators = activators;
  }
}
