package redsun;

import redsun.events.Event;
import redsun.events.EventType;
import redsun.events.TransitionEvent;
import redsun.ui.FadeTransition;

public class TransitionHandler implements EventListener {
  private EventManager eventMgr;
  private RedSunGame game;
  
  public TransitionHandler(EventManager eventMgr, RedSunGame game) {
    this.eventMgr = eventMgr;
    this.game = game;
    
    eventMgr.register(this, EventType.Event_UI_FadeTransition);
  }
  
  public void handleEvent(Event e) {
    if(e instanceof TransitionEvent) {
      TransitionEvent te = (TransitionEvent)e;
      if(te.getType() == EventType.Event_UI_FadeTransition) {
	game.setTransition(new FadeTransition(game, te.getNextState(), 30));
      }
    }
  }
  
}
