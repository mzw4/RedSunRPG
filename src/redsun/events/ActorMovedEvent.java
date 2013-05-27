package redsun.events;

import redsun.MapEntity;
import redsun.entities.Map;
import redsun.entities.Tile;
import redsun.entities.Character;;

/*
 * Event dispatched whenever an actor is moved on the Map
 * 
 * Either describes the destination Tile or the direction of movement
 * depending on the situation.
 */
public class ActorMovedEvent extends ActorEvent {
  
  private Tile dest;
  private Map.Dir dir;
  
  public ActorMovedEvent(EventType type, MapEntity obj, Tile dest) {
    super(type, obj);
    this.dest = dest;
  }
  
  public ActorMovedEvent(EventType type, MapEntity obj, Map.Dir dir) {
    super(type, obj);
    this.dir = dir;
  }

  public Tile getDest() {
    return dest;
  }

  public Map.Dir getDir() {
    return dir;
  }
}
