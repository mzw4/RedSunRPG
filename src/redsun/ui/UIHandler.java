package redsun.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Timer;

import redsun.EventListener;
import redsun.EventManager;
import redsun.MapEntity;
import redsun.RedSunGame;
import redsun.entities.Map;
import redsun.entities.Tile;
import redsun.entities.Character;
import redsun.events.ActorMovedEvent;
import redsun.events.CombatEvent;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.events.MenuEvent;
import redsun.resources.ImageLoader;

/*
 * is this class even needed?
 */
public class UIHandler implements ActionListener, EventListener {

	private EventManager eventMgr;
	private RedSunGame game;
	private ImageLoader images;

	private Map map;

	private boolean showUI;
	private boolean inCombat;

	// contains all menus that are currently showing
	// stack preserves order of appearance for drawing
	private Menu active;
	private LinkedList<Menu> showingMenus;
	private HashMap<String, Menu> menus;
	private Font font;
	private Font bigFont;

	// cursor info
	private MapEntity cursor;
	private final int cursorBlinkSpeed = 5;
	private final int cursorSpeed = 8;
	private final int cursorFast = 12;
	private boolean cursorBlink;
	private Timer cursorBlinkTimer;

	// move area info
	private ArrayList<Tile> moveArea;
	// blue-gray
	private Color moveAreaColor = new Color(60, 80, 150, 100);
	private Color moveAreaGridColor = new Color(255, 255, 255, 50);

	// attack area info
	private ArrayList<Tile> attackArea;
	private Color attackAreaColor = new Color(150, 80, 60, 100);

	// currently selected character
	private Character selected;

	public UIHandler(EventManager eventMgr, ImageLoader images, RedSunGame game,
			Map map) {
		this.eventMgr = eventMgr;
		this.game = game;
		this.images = images;
		this.map = map;

		eventMgr.register(this, EventType.Event_Game_EnterCombat);
		eventMgr.register(this, EventType.Event_Game_ExitCombat);
		eventMgr.register(this, EventType.Event_UI_CameraPanStarted);
		eventMgr.register(this, EventType.Event_UI_CameraPanEnded);
		eventMgr.register(this, EventType.Event_Game_ActorMoved);
		eventMgr.register(this, EventType.Event_Game_ActorStopped);
		eventMgr.register(this, EventType.Event_UI_CursorSpeedFast);
		eventMgr.register(this, EventType.Event_UI_CursorSpeedSlow);
		eventMgr.register(this, EventType.Event_UI_CursorSelect);
		eventMgr.register(this, EventType.Event_UI_CursorDeSelect);
		eventMgr.register(this, EventType.Event_UI_MenuOpened);

		showingMenus = new LinkedList<>();
		menus = new HashMap<>();
		font = new Font("Calibri", Font.BOLD, 14);
		bigFont = new Font("Calibri", Font.BOLD, 20);

		// should all be in a config file of some sort
		//inits all menus
		SystemMenu sysMenu = new SystemMenu(game, this);
		menus.put("SystemMenu", sysMenu);
		SaveMenu saveMenu = new SaveMenu(game, this);
		menus.put("SaveMenu", saveMenu);
		GameMenu gameMenu = new GameMenu(game, this);
		menus.put("GameMenu", gameMenu);
		InventoryMenu inventoryMenu = new InventoryMenu(game, this);
		menus.put("InventoryMenu", inventoryMenu);
		MoveMenu moveMenu = new MoveMenu(game, this);
		menus.put("MoveMenu", moveMenu);
		MainMenu mainMenu = new MainMenu(game, this);
		menus.put("MainMenu", mainMenu);
		AttackMenu attackMenu = new AttackMenu(game, this);
		menus.put("AttackMenu", attackMenu);
		
		InventoryMenu invenMenu = new InventoryMenu(game, this);
		menus.put("InventoryMenu", invenMenu);
		InventoryItemMenu weapons = new InventoryItemMenu(game, this, "Weapons");
		menus.put("Weapons", weapons);
		InventoryItemMenu armor = new InventoryItemMenu(game, this, "Armor");
		menus.put("Armor", armor);
		InventoryItemMenu consumables = new InventoryItemMenu(game, this, "Consumables");
		menus.put("Consumables", consumables);
		InventoryItemMenu items = new InventoryItemMenu(game, this, "Items");
		menus.put("Items", items);
		InventoryItemMenu keyItems = new InventoryItemMenu(game, this, "Key Items");
		menus.put("Key Items", keyItems);
		
		addLink(gameMenu, invenMenu, "Inventory");
		addLink(invenMenu, weapons, "Weapons");
		addLink(invenMenu, armor, "Armor");
		addLink(invenMenu, consumables, "Consumables");
		addLink(invenMenu, items, "Items");
		addLink(invenMenu, keyItems, "Key Items");

		// initialize move/attack area
		moveArea = new ArrayList<>();
		attackArea = new ArrayList<>();

		// initialize cursor
		cursor = new MapEntity("cursor");
		cursor.setGhost(true);
		cursor.setMoveSpeed(cursorSpeed);
		cursorBlinkTimer = new Timer(1000 / cursorBlinkSpeed, this);
		cursorBlinkTimer.setInitialDelay(100);
	}

	public void drawUI(Graphics2D g, ImageLoader images) {
		if (!showUI)
			return;
		if (inCombat) {
			// drawcursor
			if (cursor.isMoving()) {
				cursorBlinkTimer.stop();
				cursorBlink = true;
			} else if (!cursorBlinkTimer.isRunning())
				cursorBlinkTimer.start();

			// draw move area
			if (!moveArea.isEmpty()) {
				for (Tile t : moveArea) {
					g.setColor(moveAreaColor);
					g.fillRect(t.getScreenX(), t.getScreenY(), Tile.width, Tile.height);
					g.setColor(moveAreaGridColor);
					g.drawRect(t.getScreenX(), t.getScreenY(), Tile.width, Tile.height);
				}
			}
			if (!attackArea.isEmpty()) {
				for (Tile t : attackArea) {
					g.setColor(attackAreaColor);
					g.fillRect(t.getScreenX(), t.getScreenY(), Tile.width, Tile.height);
					g.setColor(moveAreaGridColor);
					g.drawRect(t.getScreenX(), t.getScreenY(), Tile.width, Tile.height);
				}
			}

			if (cursorBlink) {
				cursor.drawSprite(g, images);
			}
		} else {

		}
	}

	public void drawMenus(Graphics2D g) {
		if (!showingMenus.isEmpty()) {
			Iterator<Menu> iterator = showingMenus.descendingIterator();
			while(iterator.hasNext()) {
				iterator.next().draw(g, images);
			}
		}
	}

	public boolean showUI() {
		return showUI;
	}

	public void exitMenu(String id) {
		Menu menu = getMenu(id);
		if(showingMenus.contains(menu))
			showingMenus.remove(showingMenus.indexOf(menu));
	}
	
	public void exitTopMenu() {
		showingMenus.pop();
		if (showingMenus.isEmpty()) {
			game.resumeGame();
			active = null;
		}
		else
			active = showingMenus.peek();
	}

	public void exitAllMenus() {
		showingMenus.clear();
		game.resumeGame();
		active = null;
	}
	
	public void openMenu(String id, boolean active) {
		eventMgr.sendEvent(new MenuEvent(EventType.Event_UI_MenuOpened, menus.get(id), active));
	}

	public boolean isOpen(Menu menu) {
		return showingMenus.contains(menu);
	}

	public void hideTop() {
		showingMenus.peek().setHidden(true);
	}

	public void addLink(Menu parent, Menu child, String option) {
		parent.addChild(option, child.getId());
		child.addParent(parent.getId());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// blinks cursor on and off
		if (e.getSource() == cursorBlinkTimer)
			cursorBlink = !cursorBlink;
	}

	@Override
	public void handleEvent(Event e) {
		EventType type = e.getType();
		// change so ui doesnt show during a cutscene
		// if(type == EventType.Event_UI_CameraPanStarted);
		// showUI = false;
		// if(type == EventType.Event_UI_CameraPanEnded)
		showUI = true;

		if (e instanceof MenuEvent) {
			MenuEvent me = (MenuEvent) e;
			// open a menu
			if (me.getType() == EventType.Event_UI_MenuOpened) {
				Menu menu = me.getMenu();
				if (menu != null && !showingMenus.contains(menu)) {
					showingMenus.push(menu);
					menu.setHidden(false);
					menu.update();
					game.pauseGame();
				}
				if(me.setActive())
					active = menu;
			}
		}

		if (type == EventType.Event_UI_CursorSelect) {

			Tile tile = cursor.getLoc();
			if (!moveArea.isEmpty()) {
				if (moveArea.contains(tile))
					eventMgr.addEvent(new ActorMovedEvent(
							EventType.Event_Game_ActorMoved, selected, tile));
			}
			// when a character is selected, display its allowed move area
			if (tile.hasCharacter()) {
				// only assign the selected entity if there is none currently selected
				if (selected == null)
					selected = (Character) tile.getEntity();

				if (moveArea.isEmpty()) {
					int moveDist = selected.getMoveDistance();
					// get all tiles within the move distance
					ArrayList<Tile> ma = map.getTilesWithin(tile, moveDist + 1);
					for (Tile t : ma) {
						if (selected.canReach(t) || t.getX() == selected.getX()
								&& t.getY() == selected.getY())
							moveArea.add(t);
						else if (selected.canAttack(t))
							attackArea.add(t);
					}
				}
			}
		}
		if (type == EventType.Event_UI_CursorDeSelect) {
			clearSelected();
		}

		if (type == EventType.Event_Game_ActorStopped)
			if (!moveArea.isEmpty()) {
				openMenu("MoveMenu", true);
			}

		if (type == EventType.Event_UI_CursorSpeedFast)
			cursor.setMoveSpeed(cursorFast);
		if (type == EventType.Event_UI_CursorSpeedSlow)
			cursor.setMoveSpeed(cursorSpeed);

		if (e instanceof CombatEvent) {
			CombatEvent ce = (CombatEvent) e;
			if (type == EventType.Event_Game_EnterCombat) {
				Tile target = ce.getTarget();
				cursor.setLoc(target);
				inCombat = true;
			} else if (type == EventType.Event_Game_ExitCombat) {
				cursorBlinkTimer.stop();
				clearSelected();
				inCombat = false;
			}
		}
	}

	// temp delete this
	private void showMoveArea() {
		Tile t = map.getTileAt(cursor.getX(), cursor.getY());
		if (t.hasCharacter()) {
			Character c = (Character) t.getEntity();
			int move = c.getMoveDistance();

			// get all tiles within the move distance
			moveArea = map.getTilesWithin(t, move);
			moveArea.remove(new Point(t.getX(), t.getY()));
		}
	}

	public void clearSelected() {
		moveArea.clear();
		attackArea.clear();
		System.out.println(moveArea);
		selected = null;
	}

	public Map.Dir cursorOnEdge() {
		if (cursor.getScreenY() <= 0)
			return Map.Dir.NORTH;
		else if (cursor.getScreenX() <= 0)
			return Map.Dir.WEST;
		else if (cursor.getScreenY() + Tile.height >= game.getPanelHeight())
			return Map.Dir.SOUTH;
		else if (cursor.getScreenX() + Tile.width >= game.getPanelWidth())
			return Map.Dir.EAST;
		else
			return null;
	}

	// ------------------------------ Getters and Setters
	// ------------------------------------

	public Menu getActiveMenu() {
		return active;
	}
	
	public void setActiveMenu(String id) {
		Menu menu = getMenu(id);
		if(menu != null)
			this.active = menu;
	}
	
	public MapEntity getCursor() {
		return cursor;
	}

	public LinkedList<Menu> getShowingMenus() {
		return showingMenus;
	}

	public Menu getMenu(String s) {
		return menus.get(s);
	}

	public EventManager getEventManager() {
		return eventMgr;
	}

	public Character getSelected() {
		return selected;
	}

	public Font getFont() {
		return font;
	}

	public Font getBigFont() {
		return bigFont;
	}
}
