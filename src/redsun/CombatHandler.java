package redsun;

import redsun.events.Event;
import redsun.events.EventType;

public class CombatHandler implements EventListener {

  private EventManager eventMgr;
  
  public CombatHandler(EventManager eventMgr) {
    this.eventMgr = eventMgr;
    
    eventMgr.register(this, EventType.Event_Game_EnterCombat);
    eventMgr.register(this, EventType.Event_Game_ExitCombat);
  }


  @Override
  public void handleEvent(Event e) {

    	
  }
}
