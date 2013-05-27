package redsun;

import redsun.events.Event;

/*
 * Generic interface for all Objects that subscribe to the event dispatching system.
 * 
 * EventListeners register with EventManager to receive events of the specified type,
 * then handle the events when they are received.
 */

public interface EventListener {
  
  public void handleEvent(Event e);

}
