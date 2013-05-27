package redsun;

import java.util.Stack;

import redsun.events.ActorEvent;
import redsun.events.ActorMovedEvent;
import redsun.entities.Character;
import redsun.entities.Map;
import redsun.entities.Tile;
import redsun.events.Event;
import redsun.events.EventType;


public class ActorEventHandler implements EventListener {

  private EventManager eventMgr;
  
  private Stack<ActorMovedEvent> moveCmdStack;
  
  public ActorEventHandler(EventManager eMgr) {
    this.eventMgr = eMgr;
    moveCmdStack = new Stack<ActorMovedEvent>();

    eventMgr.register(this, EventType.Event_Game_ActorCreated);
    eventMgr.register(this, EventType.Event_Game_ActorDestroyed);
    eventMgr.register(this, EventType.Event_Game_ActorMoved);
    eventMgr.register(this, EventType.Event_Game_ActorStopped);
  }
  
  //change - actually use this
  public void handleEvent(Event e) {
    //handles events concerning actor movement
    if(e instanceof ActorEvent) {
      ActorEvent ae = (ActorEvent)e;
      if(ae instanceof ActorMovedEvent) {
        ActorMovedEvent ame = (ActorMovedEvent)ae;
        moveCmdStack.push(ame);
	if (ame.getActor() != null) {
	  if(ame.getType() == EventType.Event_Game_ActorMoved) {
	    MapEntity obj = ame.getActor();
	    Map.Dir dir = ame.getDir();
	    Tile dest = ame.getDest();
	    
	    if (dir != null)
	      obj.move(dir);
	    else if (dest != null)
	      obj.moveTo(dest);
	    else
	      System.out.println("Invalid GActorMovedEvent dispatched for actor: " + obj);
	  }
	}
      }
//      else if(e.getType().equals(EventType.Event_Game_ActorStopped)) {
//	if(!moveCmdStack.isEmpty()) {
//	ActorMovedEvent ame = moveCmdStack.pop();
//	  ame.getActor().move(ame.getDir());
//	}
//      }
    }
  }
}
