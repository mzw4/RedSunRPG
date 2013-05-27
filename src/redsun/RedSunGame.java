package redsun;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import redsun.entities.Character;
import redsun.entities.Dialogue;
import redsun.entities.Map;
import redsun.entities.NonPlayer;
import redsun.entities.Player;
import redsun.entities.SaveState;
import redsun.entities.Tile;
import redsun.entities.Transition;
import redsun.entities.Weapon;
import redsun.entities.Character.Stat;
import redsun.events.ActorEvent;
import redsun.events.ActorMovedEvent;
import redsun.events.CombatEvent;
import redsun.events.DialogueEvent;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.events.MenuEvent;
import redsun.events.TransitionEvent;
import redsun.resources.CharacterLoader;
import redsun.resources.DialogueLoader;
import redsun.resources.ImageLoader;
import redsun.resources.ItemLoader;
import redsun.resources.SoundLoader;
import redsun.ui.Camera;
import redsun.ui.KeyInputHandler;
import redsun.ui.Menu;
import redsun.ui.MainMenu;
import redsun.ui.MouseInputHandler;
import redsun.ui.UIHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class RedSunGame extends JPanel implements Runnable, EventListener
{
  // ------------------------------ Fields ------------------------------------
  private static final int pWIDTH = 756;
  private static final int pHEIGHT = 540;
  private static final int MAX_FRAME_SKIPS = 5;
  private static final int DELAYS_PER_YIELD = 16;
  private static int DEFAULT_FPS = 60;
  private long period = 1000000000L/DEFAULT_FPS;

  private ScreenManager s;
  private XStream xs;
  private Camera camera;
  private StopWatch gameplayTimer;
  
  public static EventManager eventMgr;
  private KeyInputHandler keyboard;
  private MouseInputHandler mouse;
  
  //only the camera needs this maybe? change
  private ImageLoader images;
  private SoundLoader sounds;
  private CharacterLoader characters;
  private ItemLoader items;
  private DialogueLoader dialogues;
  private MapLoader maps;
  
  private ActorEventHandler actorHdlr;
//  private MenuHandler menuHdlr;
  private UIHandler ui;
  private TransitionHandler transitionHdlr;
   
  private Thread gameThread;
  
  //total play time in ns
  private long playTime;
  //current FPS calculated based on frame times
  private double FPS;

  //change - message fonts
  private Font font;
  private FontMetrics metrics;
  
  //current game state
  private GameState gameState;
  private Transition transition;
  
  //contain all game data
  private GameData data;
  
  //game life cycle indicators
  private boolean running;
  private boolean paused;
  private boolean gameOver;
  
  //contains the ID of the object showing dialogue, empty if no dialogue is showing
  private String showingDialogue = "";
      
  private MapEntity cursor;
  private Map curMap;
  private Character player;
  //change
  private Character guy;
  private Character guy2;
  private Dialogue dialogue;
  
  // ------------------------------ Game Constructor ------------------------------------
  //core game constructor, initializes game components and loads content
  public RedSunGame() {
    setPreferredSize(new Dimension(pWIDTH, pHEIGHT));
    setFocusable(true);
    requestFocus(); //to receive input events
    
    loadFont();
    font = new Font("Calibri", Font.BOLD, 14);
    metrics = this.getFontMetrics(font);
    gameplayTimer = new StopWatch();
    
    loadResources();
    startGame();
  }
  
  private void loadResources() {
    eventMgr = new EventManager();
    eventMgr.register(this, EventType.Event_UI_DialogueOpened);
    eventMgr.register(this, EventType.Event_UI_DialogueClosed);
    eventMgr.register(this, EventType.Event_UI_DialogueCont);
    eventMgr.register(this, EventType.Event_UI_MenuOpened); 
    eventMgr.register(this, EventType.Event_Game_EnterCombat);
    eventMgr.register(this, EventType.Event_Game_ExitCombat);
    eventMgr.register(this, EventType.Event_Game_EnterInGame);
    eventMgr.register(this, EventType.Event_Game_EnterTitle);
    eventMgr.register(this, EventType.Event_System_GameClosed);
   
    keyboard = new KeyInputHandler();
    addKeyListener(keyboard);
    mouse = new MouseInputHandler();
    addMouseListener(mouse);
    
    images = new ImageLoader();
    sounds = new SoundLoader();
    characters = new CharacterLoader();
    items = new ItemLoader();
    dialogues = new DialogueLoader();
    maps = new MapLoader();
    curMap = maps.getMap("castle.xml");
    
    //change later - will be extracted from save file
    data = new GameData();

    actorHdlr = new ActorEventHandler(eventMgr);
//    menuHdlr = new MenuHandler(eventMgr, this);
    ui = new UIHandler(eventMgr, images, this, curMap);
    transitionHdlr = new TransitionHandler(eventMgr, this);
    dialogue = new Dialogue(this, eventMgr);
    
    xs = new XStream();
  }
  
  // ------------------------------ Core Game methods ------------------------------------
  
  //loads content, initializes game loop thread and runs it
  private void startGame() {
    if(gameThread == null)
      gameThread = new Thread(this);
    running = true;
    
    //------------temp--------------
    cursor = ui.getCursor();
    gameState = GameState.TITLE;
    
    //xStream tester
    Weapon w = new Weapon("Spear", Weapon.WepClass.SPEAR);
    String xmlTest = xs.toXML(w);
    try {
      String fname = "spear.xml";
      BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
      bw.write(xmlTest);
      bw.close();
      System.out.println("File \"" + fname + "\" successfully created.");
    } catch (IOException e) {e.printStackTrace();}
    
    player = characters.getCharacter("Playa");
    player.setLoc(curMap.getTileAt(1, 1));
    curMap.getTileAt(1, 1).setEntity(player);
    
    guy = characters.getCharacter("Man");
    guy.setLoc(curMap.getTileAt(5, 5));
    curMap.getTileAt(5, 5).setEntity(guy);
    guy2 = characters.getCharacter("Man1");
    guy2.setLoc(curMap.getTileAt(4, 6));
    curMap.getTileAt(4, 6).setEntity(guy);

    //------------/temp--------------
    
    camera = new Camera(eventMgr, this, images, curMap, ui);
    camera.follow(player);
    ui.openMenu("MainMenu", true);

    gameThread.start();
  }
  
  //core game loop: update, render
  public void run() {
    long gameStartTime = System.nanoTime();
    long beforeFrame = gameStartTime, afterFrame, sleepTime, lagTime = 0L;
    int frameSkips = 0, renderDelays = 0;
    gameplayTimer.start();
    
    while(running) {
      gameUpdate();
      repaint();

      //calculate sleepTime based on the time it took to finish the frame operations
      afterFrame = System.nanoTime();
      sleepTime = period - (afterFrame - beforeFrame);
      
      //thread sleeps for the specified time
      if(sleepTime >= 0) {
        while(System.nanoTime() - afterFrame < sleepTime) {
          Thread.yield();
        }
      }
      else {
        //if sleepTime < 0, game is rendering too slowly, add to lagTime
        lagTime -= sleepTime;
        
        //updates game without rendering to make up for lag
        while(lagTime > period && frameSkips < MAX_FRAME_SKIPS) {
          gameUpdate();
          lagTime -= period;
          frameSkips++;
        }
        
        //if rendering is taking too long, allow other threads to run
        renderDelays++;
        if(renderDelays > DELAYS_PER_YIELD) {
          Thread.yield();
          renderDelays = 0;
        }
      }
      
      playTime = gameplayTimer.getElapsed() + data.getPlayTime();
      FPS = getFPS(beforeFrame);
      beforeFrame = System.nanoTime();
    }
    System.exit(0);
  }
  
  private void gameUpdate() {
    //process all input commands
    keyboard.update();
    mouse.update();
    
    Menu activeMenu = ui.getActiveMenu();
    if(activeMenu != null)
      activeMenu.processKeys(keyboard);
    else if(!showingDialogue.isEmpty())
      dialogue.processKeys(keyboard);
    else
      processKeys();
    processMouse();
    
    //process all events
    eventMgr.update();
    if(transition != null)
      transition.update();
        
    data.updatePlayer(player);

    switch(gameState) {
    case INTRO:
      break;
    case TITLE:
      if(!sounds.isRunning("poketheme.wav"))
	sounds.fadeIn("poketheme.wav", 0.2f, true);
      break;
    case MENUS:
      break;
    case CUTSCENE:
      break;
    case IN_GAME:
      if(!paused) {
//        camera.follow(player);
        player.updateSprite();
        player.updateLogic();
        guy.updateSprite();
        guy.updateLogic();
        guy2.updateSprite();
        guy2.updateLogic();
      }
      break;
    case COMBAT:
      player.updateSprite();
      player.updateLogic();
      guy.updateSprite();
      guy.updateLogic();
      guy2.updateSprite();
      guy2.updateLogic();
      cursor.updateLogic();
      //temp ________________________________________________
      if(player.reachedDest())
	eventMgr.addEvent(new Event(EventType.Event_Game_ActorStopped));
      if(guy.getStat(Stat.HEALTH) == 0) {
	sounds.play("snorlax.wav");
	guy.setStat(Stat.HEALTH, 1);
      }
      if(guy2.getStat(Stat.HEALTH) == 0) {
	sounds.play("snorlax.wav");
	guy2.setStat(Stat.HEALTH, 1);
      }
      break;
    }
    camera.update();
    sounds.update();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    
    BufferedImage image = new BufferedImage(pWIDTH, pHEIGHT, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
  
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setColor(Color.black);
    g2d.fillRect(0, 0, pWIDTH, pHEIGHT);
    g2d.setFont(font);

    switch(gameState) {
    case INTRO:
      break;
    case TITLE:
      ui.drawMenus(g2d);
      break;
    case MENUS:
      break;
    case CUTSCENE:
      break;
    case IN_GAME:
      camera.drawMap(g2d);
      camera.drawSprites(g2d);

      if(!showingDialogue.isEmpty())
	dialogue.drawText(g2d);
      g2d.setFont(font);
      g2d.setColor(Color.white);
      g2d.drawString("FPS: " + FPS, 200, pHEIGHT-5);
      g2d.drawString("Total game time: " + playTime, 10, pHEIGHT - 5);
      g2d.drawString("P = pause", 370, pHEIGHT-5);
      g2d.drawString("C = change screen", 440, pHEIGHT-5);
      g2d.drawString("Esc = title screen", 560, pHEIGHT-5);
      
      //draw any showing menus
      ui.drawMenus(g2d);
      
      break;
    case COMBAT:
      camera.drawMap(g2d);
      ui.drawUI(g2d, images);
      camera.drawSprites(g2d);
      
      if(!showingDialogue.isEmpty())
      	dialogue.drawText(g2d);
      ui.drawMenus(g2d);
      break;
    }
    if(paused)
      g2d.drawString("PAUSED", 0, metrics.getHeight());
    //if screen is transitioning, paint the transition mask
    if(transition != null)
      transition.draw(g2d);
    
    //night lol
//    g2d.setColor(new Color(0, 0, 0, 150));
//    g2d.fillRect(0, 0, pWIDTH, pHEIGHT);
    
    g2.drawImage(image, 0, 0, pWIDTH, pHEIGHT, null);
    g2d.dispose();
    g2.dispose();
  }
  
  private void gameOver() {
    gameOver = true;
  }
  
  private double getFPS(long beforeFrame) {
    long frameTime = System.nanoTime() - beforeFrame;
    return (double)1000000000L/frameTime;
  }
  
  private void loadFont() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./src/Fonts/Elronmonospace.ttf")));
    } catch (FontFormatException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void setTransition(Transition t) {
    transition = t;
  }
  
  // ------------------------------ Input handlers ------------------------------------
  private void processKeys() {
    //change - right now dialogue pauses the game but you can unpause it with the pause button
    if(keyboard.keyPressedOnce(KeyEvent.VK_P)) {
	if(!paused)
	  pauseGame();
	else if(showingDialogue.isEmpty())
	  resumeGame();
    }
    
    if(keyboard.keyPressedOnce(KeyEvent.VK_S)) {
      sounds.stopAll();
    }
    
    //put into a menu event handler?
    if(keyboard.keyPressedOnce(KeyEvent.VK_ESCAPE)) {
    	ui.openMenu("SystemMenu", true);
    }
    if(keyboard.keyPressedOnce(KeyEvent.VK_T)) {
    	ui.openMenu("GameMenu", true);
    }
    
    switch(gameState) {
    case INTRO:
      break;
    case TITLE:
      break;
    case MENUS:
      break;
    case CUTSCENE:
      break;
    case IN_GAME:
			// change - create events for these? modifying data directly
			if (!paused) {
				if (keyboard.keyPressed(KeyEvent.VK_LEFT))
					player.move(Map.Dir.WEST);
				if (keyboard.keyPressed(KeyEvent.VK_UP))
					player.move(Map.Dir.NORTH);
				if (keyboard.keyPressed(KeyEvent.VK_RIGHT))
					player.move(Map.Dir.EAST);
				if (keyboard.keyPressed(KeyEvent.VK_DOWN))
					player.move(Map.Dir.SOUTH);
				if (keyboard.keyPressed(KeyEvent.VK_SPACE))
					player.setMoveSpeed(4);
				if (keyboard.keyReleased(KeyEvent.VK_SPACE))
					player.setMoveSpeed(2);
	
        if(keyboard.keyPressedOnce(KeyEvent.VK_E)) {
          Tile tile = player.getFacingTile();
          if(tile != null && !tile.getEntityName().isEmpty()) {
            eventMgr.addEvent(new DialogueEvent(EventType.Event_UI_DialogueOpened, tile.getEntityName()));
          }
        }          
        //temp
        if(keyboard.keyPressedOnce(KeyEvent.VK_C)) {
          eventMgr.addEvent(new CombatEvent(EventType.Event_Game_EnterCombat,
              curMap.getTileAt(curMap.getWidth()/2, curMap.getHeight()/2)));
        }

        //temp
        if(keyboard.keyPressedOnce(KeyEvent.VK_I)) {
          player.receiveItem(items.getItem("Sword"));
        }
        if(keyboard.keyPressedOnce(KeyEvent.VK_O)) {
          player.receiveItem(items.getItem("Axe"));
        }
      }
      break;
    case COMBAT:
      if(paused) break;
      if(!ui.showUI()) break;
            
      if (keyboard.keyPressedOnce(KeyEvent.VK_ENTER))
      	eventMgr.addEvent(new Event(EventType.Event_UI_CursorSelect));
      if (keyboard.keyPressedOnce(KeyEvent.VK_BACK_SPACE))
      	eventMgr.addEvent(new Event(EventType.Event_UI_CursorDeSelect));
      if (keyboard.keyPressed(KeyEvent.VK_LEFT))
      	eventMgr.addEvent(new ActorMovedEvent(EventType.Event_Game_ActorMoved, cursor, Map.Dir.WEST));
      if (keyboard.keyPressed(KeyEvent.VK_UP))
      	eventMgr.addEvent(new ActorMovedEvent(EventType.Event_Game_ActorMoved, cursor, Map.Dir.NORTH));
      if (keyboard.keyPressed(KeyEvent.VK_RIGHT))
      	eventMgr.addEvent(new ActorMovedEvent(EventType.Event_Game_ActorMoved, cursor, Map.Dir.EAST));
      if (keyboard.keyPressed(KeyEvent.VK_DOWN))
      	eventMgr.addEvent(new ActorMovedEvent(EventType.Event_Game_ActorMoved, cursor, Map.Dir.SOUTH));
      if (keyboard.keyPressed(KeyEvent.VK_SPACE))
      	eventMgr.addEvent(new Event(EventType.Event_UI_CursorSpeedFast));
      if (keyboard.keyReleased(KeyEvent.VK_SPACE))
      	eventMgr.addEvent(new Event(EventType.Event_UI_CursorSpeedSlow));
      
      if(keyboard.keyPressedOnce(KeyEvent.VK_C)) {
        eventMgr.addEvent(new Event(EventType.Event_Game_ExitCombat));
      }
      
      //change to event
      Map.Dir dir = ui.cursorOnEdge();
      if(dir != null) {
      	camera.setCenterTile(camera.getCenterTile().getAdjacent(dir), true);
      }
      
      break;
    }
  }

	private void processMouse() {
    
  }
  
  private void handleMousePress(MouseEvent e) {

  }
  
  private void handleMouse(MouseEvent e) {
    
  }
  
  // ------------------------------ Event Handling method ------------------------------------

  public void handleEvent(Event e) {
    EventType type = e.getType();
    
    if(e instanceof DialogueEvent) {
      DialogueEvent de = (DialogueEvent)e;
      if(type == EventType.Event_UI_DialogueOpened) {
        showingDialogue = dialogues.getDialogue(de.getId());
        sounds.play("click.wav");
      }
      if(type == EventType.Event_UI_DialogueClosed) {
        showingDialogue = "";
        sounds.play("click.wav");
      }
      if(type == EventType.Event_UI_DialogueCont)
	sounds.play("click.wav");
    }
    else if(e instanceof MenuEvent) {
      MenuEvent me = (MenuEvent)e;
      if(!(me.getMenu() instanceof MainMenu))
	sounds.play("click.wav");
    }
    else if(e instanceof CombatEvent) {
      CombatEvent ce = (CombatEvent)e;
      if(type == EventType.Event_Game_EnterCombat) {
        gameState = GameState.COMBAT;
        camera.setCenterTile(ce.getTarget(), true);
//        sounds.pause("piano.wav");
//        sounds.play("battle.wav");
      }	
    }
    else {
      if(type == EventType.Event_System_GameClosed)
	System.exit(0);
      
      if(type == EventType.Event_Game_StartNewGame);
      
      if(type == EventType.Event_Game_EnterInGame) {
	sounds.fadeOut("poketheme.wav", 0.4f);
	sounds.fadeIn("fe.wav", 0.2f, true);
      }
      if(type == EventType.Event_Game_EnterTitle) {
      	ui.openMenu("MainMenu", true);
	sounds.fadeOut("fe.wav", 0.4f);
	sounds.fadeIn("poketheme.wav", 0.2f, true);
      }
      if(type == EventType.Event_Game_ExitCombat) {
	gameState = GameState.IN_GAME;
      }
    }
  }
  
  // ------------------------------ Game Lifecycle methods ------------------------------------
  public void pauseGame() {
    paused = true;
    gameplayTimer.stop();
  }
  
  public void resumeGame() {
    paused = false;
    gameplayTimer.start();
  }
  
  public void stopGame() {
    running = false;
  }
  
  // ------------------------------ Getters and Setters------------------------------------
  public int getPanelWidth() {
    return pWIDTH;
  }

  public int getPanelHeight() {
    return pHEIGHT;
  }
  
  public ImageLoader getILoader() {
    return images;
  }
  
  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }
  
//  public Player getPlayer() {
//    return player;
//  }
  public Character getPlayer() {
    return player;
  }
  
  public Font getFont() {
    return font;
  }
  
  public SoundLoader getSoundLoader() {
    return sounds;
  }
  
  public ItemLoader getItems() {
		return items;
	}
  
  public GameData getData() {
    return data;
  } 
  
}
