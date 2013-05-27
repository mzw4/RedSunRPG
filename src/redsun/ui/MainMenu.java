package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import redsun.EventManager;
import redsun.GameState;
import redsun.RedSunGame;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.events.TransitionEvent;
import redsun.resources.ImageLoader;

public class MainMenu extends Menu {

  public MainMenu(RedSunGame game, UIHandler ui) {
    super(game, ui);
    menuW = gameW/4;
    menuH = gameH/2;
    screenX = 0;
    screenY = 0;
    metrics = game.getFontMetrics(ui.getBigFont());
    
    id = "SystemMenu";
    String[] str = {"New Game", "Load Game", "Options", "Exit Game"};
    options = str;
  }

  @Override
  public void doSelection() {
    switch((String)options[curSelection]) {
    case "New Game":
      ui.exitAllMenus();
      EventManager eventMgr = ui.getEventManager();
      eventMgr.addEvent(
	  new TransitionEvent(EventType.Event_UI_FadeTransition, GameState.IN_GAME));
      eventMgr.addEvent(new Event(EventType.Event_Game_EnterInGame));
      break;
    case "Exit Game":
      ui.getEventManager().addEvent(new Event(EventType.Event_System_GameClosed));
      break;
    }
  }

  @Override
  public void draw(Graphics2D g, ImageLoader images) {
    //draw menu box
    g.setColor(color);
    g.setFont(ui.getBigFont());
    g.drawImage(images.getImg("RedSunTitle"), screenX, screenY, gameW, gameH, null);
    
    //draw/highlight selection and draw cursor
    for(int i = 0; i < options.length; i++) {
      g.setColor(new Color(255, 255, 255, 150));

      if(options[curSelection] == (options[i])) {
	g.setColor(Color.white);
	g.fillRect(gameW / 6,
	    gameH * 5 / 11 + (i + 1) * metrics.getHeight() - metrics.getHeight()/2, 5, 5);
      }
      g.drawString((String)options[i], gameW / 4, gameH * 5 / 11 + (i + 1) * metrics.getHeight());
    }
  }  
}
