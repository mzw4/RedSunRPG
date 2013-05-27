package redsun.events;

public class DialogueEvent extends Event {
  
  private String id;
  
  public DialogueEvent(EventType type, String id) {
    super(type);
    this.id = id;
  }
  
  public String getId() {
    return id;
  }

}
