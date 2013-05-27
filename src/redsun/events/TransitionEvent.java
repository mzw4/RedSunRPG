package redsun.events;

import redsun.GameState;

public class TransitionEvent extends Event {
  
  private GameState nextState;
  
  public TransitionEvent(EventType type, GameState nextState) {
    super(type);
    
    this.nextState = nextState;
  }
  
  public GameState getNextState() {
    return nextState;
  }
}
