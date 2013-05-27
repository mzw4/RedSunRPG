package redsun.entities;


public class MapObject {
  
  private int x;
  private int y;
  
  private boolean walkable;
  private Item item;
  
  public MapObject(int x, int y, boolean walkable) {
    this.x = x;
    this.y = y;
    this.walkable = walkable;
  }
  
  // ------------------------------ Getters and setters ------------------------------------
  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }
  
  
}
