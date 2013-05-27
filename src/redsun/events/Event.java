package redsun.events;


public class Event {
 
  private EventType type;
//  //a script specifying the effects of this event
//  private String data;
  
  public Event(EventType type) {
    this.type = type;
  }
  
  public EventType getType() {
    return type;
  }  
}
