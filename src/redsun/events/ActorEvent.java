package redsun.events;

import redsun.MapEntity;
import redsun.entities.Character;

public class ActorEvent extends Event {

  private MapEntity obj;

  public ActorEvent(EventType type, MapEntity c) {
    super(type);
    this.obj = c;
  }
  
  public MapEntity getActor() {
    return obj;
  }

}
