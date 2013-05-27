package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import redsun.EventManager;
import redsun.GameState;
import redsun.RedSunGame;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.events.MenuEvent;
import redsun.events.TransitionEvent;
import redsun.resources.ImageLoader;

public class SystemMenu extends Menu {

	public SystemMenu(RedSunGame game, UIHandler ui) {
		super(game, ui);
		menuW = gameW / 4;
		menuH = gameH / 2;
		screenX = (gameW - menuW) / 2;
		screenY = (gameH - menuH) / 2;

		id = "SystemMenu";
		String[] str = { "Resume Game", "Save", "Load", "Options", "Exit to menu", "Exit game" };
		options = str;
		addChild("Save", "SaveMenu");
		addChild("Load", "LoadMenu");
		addChild("Options", "OptionsMenu");
	}

	public void draw(Graphics2D g, ImageLoader images) {
		if (hidden)
			return;

		// draw menu box
		g.setColor(color);
		g.fillRect(screenX, screenY, menuW, menuH);

		// draw/highlight selection and draw cursor
		for (int i = 0; i < options.length; i++) {
			g.setColor(new Color(255, 255, 255, 150));

			if (options[curSelection] == (options[i])) {
				g.setColor(Color.white);
				g.fillRect(
						screenX + xInset,
						screenY + yInset + (i + 1) * metrics.getHeight()
								- metrics.getHeight() / 2, 5, 5);
			}
			g.drawString((String) options[i],
					(gameW - metrics.stringWidth((String) options[i])) / 2, screenY
							+ yInset + (i + 1) * metrics.getHeight());
		}
	}

	public void doSelection() {
		EventManager eventMgr = ui.getEventManager();

		switch ((String) options[curSelection]) {
		case "Resume Game":
			ui.exitAllMenus();
			break;
		case "Save":
		  ui.openMenu("SaveMenu", true);
			break;
		case "Load":
			break;
		case "Options":
			break;
		case "Exit to menu":
			ui.exitAllMenus();
			eventMgr.addEvent(new TransitionEvent(EventType.Event_UI_FadeTransition,
					GameState.TITLE));
			eventMgr.addEvent(new Event(EventType.Event_Game_EnterTitle));
			break;
		case "Exit game":
			ui.getEventManager().addEvent(
					new Event(EventType.Event_System_GameClosed));
			break;
		}
	}
}
