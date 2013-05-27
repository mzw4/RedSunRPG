package redsun.ui;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import redsun.EventListener;
import redsun.EventManager;
import redsun.MapEntity;
import redsun.entities.Map;
import redsun.entities.Tile;
import redsun.entities.Map.Dir;
import redsun.events.ActorMovedEvent;
import redsun.events.CombatEvent;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.resources.ImageLoader;
//unused
public class Cursor extends MapEntity implements ActionListener, EventListener {


  private EventManager eventMgr;
  //cursor info
  private final int blinkSpeed = 5;
  private final int cursorSpeed = 8;
  private final int cursorFast = 12;
  private boolean blinkOn;
  private Timer blinkTimer;
  
  public Cursor(EventManager eventMgr) {
    super("Cursor");
    setGhost(true);
    setMoveSpeed(cursorSpeed);
    
    blinkTimer = new Timer(1000/blinkSpeed, this);
    blinkTimer.setInitialDelay(100);
    
    this.eventMgr = eventMgr;
    eventMgr.register(this, EventType.Event_Game_EnterCombat);
    eventMgr.register(this, EventType.Event_Game_ExitCombat);

  }
  
  public void selectCurrent() {

  }
  
  @Override
  /*
   * Draws cursor. Blinks if not moving.
   */
  public void drawSprite(Graphics2D g, ImageLoader images) {
    //drawcursor
    if(isMoving()) {
	blinkTimer.stop();
	blinkOn = true;
    }
    else if(!blinkTimer.isRunning())
	blinkTimer.start();
    
    if(blinkOn) {
	super.drawSprite(g, images);
    }  
  }
  
  @Override
  /*
   * Blinks the cursor on or off
   */
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == blinkTimer)
      blinkOn = !blinkOn;
  }

  @Override
  public void handleEvent(Event e) {
    EventType type = e.getType();
    if(type == EventType.Event_UI_CursorSpeedFast)
      setMoveSpeed(cursorFast);
    if(type == EventType.Event_UI_CursorSpeedSlow)
      setMoveSpeed(cursorSpeed);
    
    if(e instanceof CombatEvent) {
      CombatEvent ce = (CombatEvent)e;
      if (type == EventType.Event_Game_EnterCombat) {
	Tile target = ce.getTarget();
	loc = target;
	      System.out.println(loc);
      }
      else if (type == EventType.Event_Game_ExitCombat) {
	blinkTimer.stop();
      }
    }
    
    if(e instanceof ActorMovedEvent) {
      ActorMovedEvent ame = (ActorMovedEvent)e;
      //cursor movement
      if(type == EventType.Event_Game_ActorMoved && ame.getActor() == this) {
	Tile dest = ame.getDest();
	Map.Dir dir = ame.getDir();
	if(dest != null)
	  moveTo(dest);
	else if(dir != null)
	  move(dir);
      }
    }    
  }
}
