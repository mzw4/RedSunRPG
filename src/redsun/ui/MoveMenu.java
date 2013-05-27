package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import redsun.RedSunGame;
import redsun.resources.ImageLoader;
import redsun.entities.Character;

public class MoveMenu extends Menu {
	private Character selected;

	public MoveMenu(RedSunGame game, UIHandler ui) {
		super(game, ui);
		id = "MoveMenu";

		menuW = gameW / 4;
		menuH = gameH / 2;
		screenX = (gameW - menuW) / 2;
		screenY = (gameH - menuH) / 2;

		String[] str = { "Attack", "Item", "Skill", "Wait" };
		options = str;
		addChild("Attack", "WeaponSelect");
		addChild("Item", "ItemSelect");
		addChild("Skill", "SkilSelect");
	}

	@Override
	public void update() {
		selected = ui.getSelected();
	}

	@Override
	public void doSelection() {
		switch ((String) options[curSelection]) {
		case "Attack":
			// if(ui.getSelected().getFacingTile().getEntity() == null)
			// break;
			// ui.getSelected().attack();
			// game.getSoundLoader().play("clash.wav");
			if (selected != null && selected.hasTargets()) {
				ui.hideTop();
				ui.openMenu("AttackMenu", true);
			}
			break;
		case "Item":
			break;
		case "Skill":
			break;
		case "Wait":
			ui.exitAllMenus();
			ui.clearSelected();
			break;
		}
	}

	@Override
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
}
