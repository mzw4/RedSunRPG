package redsun;

import java.util.ArrayList;

import redsun.entities.Map;
import redsun.entities.Tile;

public class Path {
  private ArrayList<PathNode> path;
  private PathNode end;
  private int current;
  
  public Path() {
    path = new ArrayList<PathNode>();
  }
  
  public Path(ArrayList<PathNode> path, PathNode end) {
    this.path = path;
    this.end = end;
    current = 0;
  }
  
  public ArrayList<PathNode> getPath() {
    return path;
  }
  
  public boolean hasNext() {
    return current < path.size();
  }
  
//  public boolean hasPrev() {
//    return current > 0;
//  }
  
  public PathNode getNext() {
    PathNode next = path.get(current);
    current++;
    return next;
  }
  
  public PathNode getPrev() {
    PathNode prev = null;
    if(current > 0)
      prev = path.get(current - 1);
    return prev;
  }
  
  public void startOver() {
    current = 0;
  }
  
  public int getCurrent() {
    return current;
  }
  
  public PathNode getEnd() {
    return end;
  }
  
  public int length() {
    return path.size();
  }
}
