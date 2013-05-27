package redsun.events;

import redsun.entities.Tile;

public class CombatEvent extends Event {
  private Tile target;
  private Tile source;
  
  public CombatEvent(EventType type, Tile target) {
    super(type);
    this.target = target;
  }
  
  public CombatEvent(EventType type, Tile target, Tile source) {
    this(type, target);
    this.source = source;
  }
  
  public Tile getTarget() {
    return target;
  }
  
  public Tile getSource() {
    return source;
  }
}
