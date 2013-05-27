package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import redsun.EventManager;
import redsun.GameData;
import redsun.RedSunGame;
import redsun.entities.Inventory;
import redsun.entities.Player;
import redsun.entities.Character;
import redsun.events.EventType;
import redsun.events.MenuEvent;
import redsun.resources.ImageLoader;

public class GameMenu extends Menu {
	private GameData data;

	public GameMenu(RedSunGame game, UIHandler ui) {
		super(game, ui);
		id = "GameMenu";

		menuW = gameW;
		menuH = gameH;

		String[] str = { "Player Stats", "Inventory", "Skills", "Objectives" };
		options = str;
//		addChild("Inventory", "InventoryMenu");
//		addChild("Skills", "SkillsMenu");
	}

	@Override
	public void update() {
		this.data = game.getData();
		display();
	}

	@Override
	public void draw(Graphics2D g, ImageLoader images) {
		if (hidden)
			return;

		// draw menu box
		g.setColor(color);
		g.fillRect(screenX, screenY, menuW, menuH);

		// colors menu UI
		for (int i = 0; i < options.length; i++) {
			g.setColor(new Color(255, 255, 255, 50));
			if (curSelection == i)
				g.setColor(new Color(150, 200, 150, 120));
			g.fillRect(menuW / options.length * i, 0, menuW / options.length,
					metrics.getHeight() + yInset * 2);

			g.setColor(new Color(255, 255, 255, 150));
			if (curSelection == i)
				g.setColor(Color.white);
			g.drawString((String) options[i],
					menuW / options.length * i +
					(menuW / options.length - metrics.stringWidth((String) options[i])) / 2,
					metrics.getHeight() + yInset);
		}

		// populates in menu contents depending on the currently selected menu
		switch ((String) options[curSelection]) {
		case "Player Stats":
			drawStats(g);
			break;
		case "Objectives":
			break;
		}
	}

	// displays submenu when an option is selected
	public void display() {
		closeChildren();
		openChild(options[curSelection], false);
	}

	// transfers control over to the submenu
	@Override
	public void doSelection() {
		closeChildren();
		openChild(options[curSelection], true);
	}

	// the selections are left to right instead of up and down
	@Override
	public void processKeys(KeyInputHandler keyboard) {
		if (keyboard.keyPressedOnce(KeyEvent.VK_ESCAPE)) {
//			closeChildren();
//			ui.exitMenu(id);
			ui.exitAllMenus();
			game.getSoundLoader().play("click.wav");
		}
		if (keyboard.keyPressedOnce(KeyEvent.VK_LEFT)) {
			selectionUp();
			game.getSoundLoader().play("click.wav");
			display();
		}
		if (keyboard.keyPressedOnce(KeyEvent.VK_RIGHT)) {
			selectionDown();
			game.getSoundLoader().play("click.wav");
			display();
		}
		if (keyboard.keyPressedOnce(KeyEvent.VK_DOWN)) {
			game.getSoundLoader().play("click.wav");
			doSelection();
		}
	}

	private void drawStats(Graphics2D g) {
		// change - draw player portrait
		g.setColor(Color.black);
		g.fillRect(xInset, metrics.getHeight() + yInset * 3, menuW * 4 / 9, menuW * 4 / 9);

		// displays stats text
		g.setColor(Color.white);
		int i = 0;
		for (Character.Stat s : Character.Stat.values()) {
			g.drawString(s + ": " + game.getPlayer().getStat(s), menuW / 2, metrics.getHeight() * (i + 2)
					+ yInset * 3);
			i++;
		}
	}
}
