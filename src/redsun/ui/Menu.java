package redsun.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import redsun.EventManager;
import redsun.RedSunGame;
import redsun.events.EventType;
import redsun.events.MenuEvent;
import redsun.resources.ImageLoader;

public abstract class Menu {
  //width and height of game panel
  protected int gameW, gameH;
  //width and height of menu
  protected int menuW, menuH;
  //location of the menu on screen
  protected int screenX = 0, screenY = 0;
  //standard inset size
  protected int xInset = 10, yInset = 10;
  //menu color, translucent dark gray by default
  protected Color color = new Color(0, 0, 0, 150);
  //for subclasses, the menu's parent and child menus
  protected String parent;
  protected Hashtable<String, String> children;
  
  protected UIHandler ui;
  protected EventManager eventMgr;
  
  //Menu id
  protected String id;
  //menu options
  protected String[] options;
  //currently selected menu option
  protected int curSelection;
  
  //true if this menu is hidden from view
  protected boolean hidden;
  
  protected RedSunGame game;
  protected FontMetrics metrics;
  
	// ------------------------------- Constructors -------------------------------

  //construct a menu
  public Menu(RedSunGame game, UIHandler ui) { 
    this.game = game;
    this.ui = ui;
    this.eventMgr = ui.getEventManager();
    
    gameW = game.getPanelWidth();
    gameH = game.getPanelHeight();
    metrics = game.getFontMetrics(ui.getFont());
    
    children = new Hashtable<>();
    curSelection = 0;
  }
  
	// ------------------------------- Methods -------------------------------

  //perform the selected action
  public void processKeys(KeyInputHandler keyboard) {
    if(keyboard.keyPressedOnce(KeyEvent.VK_ESCAPE)) {
    	closeChildren();
      ui.exitTopMenu();
      game.getSoundLoader().play("click.wav");
    }
    if (keyboard.keyPressedOnce(KeyEvent.VK_ENTER)) {
      doSelection();
      game.getSoundLoader().play("click.wav");
    }
    if (keyboard.keyPressedOnce(KeyEvent.VK_DOWN)) {
      selectionDown();
      game.getSoundLoader().play("click.wav");
    }
    if (keyboard.keyPressedOnce(KeyEvent.VK_UP)) {
      selectionUp();
      game.getSoundLoader().play("click.wav");
    }
    if(!children.isEmpty() && keyboard.keyPressedOnce(KeyEvent.VK_RIGHT)) {
      openChild(options[curSelection], true);
      game.getSoundLoader().play("click.wav");
    }
  }
  
  //if a menu must be updated each time it is opened, it can override this method
  public void update() {};
  
  //subclasses must override
  public abstract void doSelection();
  public abstract void draw(Graphics2D g, ImageLoader images);
  
  //move the cursor down
  protected void selectionDown() {
    curSelection++;
    if(curSelection >= options.length)
      curSelection = 0;
  }
  
  //move the cursor up
  protected void selectionUp() {
    curSelection--;
    if(curSelection < 0)
      curSelection = options.length - 1;
  }
  
  //make this menu invisible
  protected void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
  
  //opens the submenu of the specified id
  protected void openChild(String id, boolean active) {
  	String s = children.get(id);
  	if(s != null)
  		ui.openMenu(s, active);
  }
  
  //closes all submenus of this menu, including submenus of submenus
  protected void closeChildren() {
  	for(String s: children.values()) {
  		ui.exitMenu(s);
  		Menu m = ui.getMenu(s);
  		if(m != null)
  			m.closeChildren();
  	}
  }
  
	//adds a child submenu associated with index of the corresponding menu option that opens it
  protected void addChild(String option, String id) {
  	if(id != null)
  		children.put(option, id);
  }
  
  protected void addParent(String parent) {
  	if(parent != null)
  		this.parent = parent;
  }
  
	// ------------------------------- Getters and Setters -------------------------------
  
  public String getId() {
    return id;
  }
  
  public Menu getParent() {
    return ui.getMenu(parent);
  }
  
  public Menu getChild(int selection) {
    return ui.getMenu(children.get(selection));
  }
  
  public void setOptions(String[] options) {
  	this.options = options;
  }
}
