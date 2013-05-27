package redsun.events;

import redsun.ui.Menu;


public class MenuEvent extends Event {
  private Menu menu;
  private boolean active;
  
  public MenuEvent(EventType type, Menu menu, boolean active) {
    super(type);
    this.menu = menu;
    this.active = active;
  }
  
  public Menu getMenu() {
    return menu;
  }
  
  public boolean setActive() {
  	return active;
  }
}
