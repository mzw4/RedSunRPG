package redsun.events;

import redsun.MapEntity;
import redsun.entities.AttackType;

public class AttackEvent extends ActorEvent {

  private Character target;
  private AttackType attack;
  
  public AttackEvent(EventType type, MapEntity c, Character target, AttackType attack) {
    super(type, c);
    this.target = target;
    this.attack = attack;
  }
  
  public Character getTarget() {
    return target;
  }
  
  public AttackType getAttack() {
    return attack;
  }
}
