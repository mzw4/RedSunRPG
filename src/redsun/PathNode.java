package redsun;

import redsun.entities.Map;
import redsun.entities.Tile;
import redsun.events.EventType;

public class PathNode {
  private Tile tile;
  private PathNode parent;
  private int x, y;
  private int g, h;
  
  public PathNode(Tile tile) {
    this.tile = tile;
    x = tile.getX();
    y = tile.getY();
  }
  
  public boolean isBlocked() {
    return tile.isBlocked();
  }
  
  public Map.Dir getDirFrom(PathNode parent) {
    return tile.getDirFrom(parent.getTile());
  }
  
  @Override
  //two PathNodes are equal if they contain the same Tile
  public boolean equals(Object obj) {
    if(!(obj instanceof PathNode)) return false;
    
    PathNode n = (PathNode)obj;
    return tile == n.getTile();
  }
  
  @Override
  public int hashCode() {
    return 43 * tile.hashCode() + 155;
  }
  
  // ------------------------------ Getters and setters ------------------------------------

  public Tile getTile() {
    return tile;
  }

  public void setTile(Tile tile) {
    this.tile = tile;
  }

  public PathNode getParent() {
    return parent;
  }

  public void setParent(PathNode parent) {
    this.parent = parent;
  }
  
  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getG() {
    return g;
  }

  public void setG(int g) {
    this.g = g;
  }

  public int getH() {
    return h;
  }

  public void setH(int h) {
    this.h = h;
  }

  public int getF() {
    return g + h;
  }
}
