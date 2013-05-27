package redsun.ui;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Stack;

import redsun.EventListener;
import redsun.EventManager;
import redsun.RedSunGame;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.events.MenuEvent;

public class MenuHandler implements EventListener {
  
  private EventManager eventMgr;
  private RedSunGame game;

  //contains all menus that are currently showing
  //stack preserves order of appearance for drawing
  private Stack<Menu> showingMenus;
  private HashMap<String, Menu> menus;
  
  public MenuHandler(EventManager e, RedSunGame game) {
    eventMgr = e;
    eventMgr.register(this, EventType.Event_UI_MenuOpened);
    this.game = game;
    
    showingMenus = new Stack<>();
    menus = new HashMap<>();
    
//    //should all be in a config file of some sort
//    SystemMenu sysMenu = new SystemMenu(game, this);
//    menus.put("SystemMenu", sysMenu);
//    GameMenu gameMenu = new GameMenu(game, this);
//    menus.put("GameMenu", gameMenu);
//    MoveMenu moveMenu = new MoveMenu(game, this);
//    menus.put("MoveMenu", moveMenu);
  }
  
  public void handleEvent(Event e) {
    if(e instanceof MenuEvent) {
      MenuEvent me = (MenuEvent)e;
      //when a menu is opened
      if(me.getType() == EventType.Event_UI_MenuOpened) {
	Menu menu = me.getMenu();
	if(menu != null && !showingMenus.contains(menu)) {
	  showingMenus.push(menu);
	  game.pauseGame();
	}
      }
    }
  }
  
  public void exitAllMenus() {
    showingMenus.clear();
    game.resumeGame();
  }
  
  public void exitTopMenu() {
    showingMenus.pop();
    if(showingMenus.isEmpty())
      game.resumeGame();
  }
  
  public void openMenu(Menu menu) {
    eventMgr.sendEvent(new MenuEvent(EventType.Event_UI_MenuOpened, menu, true));
  }
  
  public boolean isOpen(Menu menu) {
    return showingMenus.contains(menu);
  }
  
  public void drawMenus(Graphics2D g) {
//    if(!showingMenus.isEmpty())
//      for(Menu m: showingMenus)
//        m.draw(g);
  }

  public Stack<Menu> getShowingMenus() {
    return showingMenus;
  }
  
  public Menu getMenu(String s) {
    return menus.get(s);
  }
  
  public EventManager getEventManager() {
    return eventMgr;
  }
}
