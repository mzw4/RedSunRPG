package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import redsun.RedSunGame;
import redsun.entities.Inventory;
import redsun.resources.ImageLoader;

public class InventoryMenu extends Menu {

	public InventoryMenu(RedSunGame game, UIHandler ui) {
    super(game, ui);
    id = "InventoryMenu";
    parent = "GameMenu";

    menuW = gameW / 4;
    menuH = gameH - (metrics.getHeight() + yInset*2);

    screenX = 0;
    screenY = gameH - menuH;
    
    String[] str = { "Weapons", "Armor", "Consumables", "Items", "Key Items" };
    options = str;
	}
	
	@Override
	public void update() {
		display();
	}
	
	//displays submenu when an option is selected
	private void display() {
		closeChildren();
		ui.openMenu(options[curSelection], false);		
	}
	
	//transfers control over to the submenu 
	@Override
	public void doSelection() {
		closeChildren();
		ui.openMenu(options[curSelection], true);
	}
	
	@Override
  public void processKeys(KeyInputHandler keyboard) {
    if(keyboard.keyPressedOnce(KeyEvent.VK_ESCAPE)) {
      ui.setActiveMenu(parent);
      game.getSoundLoader().play("click.wav");
    }
    if (keyboard.keyPressedOnce(KeyEvent.VK_ENTER) || 
    		keyboard.keyPressedOnce(KeyEvent.VK_RIGHT)) {
    	doSelection();
      game.getSoundLoader().play("click.wav");
    }
    if (keyboard.keyPressedOnce(KeyEvent.VK_DOWN)) {
      selectionDown();
      game.getSoundLoader().play("click.wav");
      display();
    }
    if (keyboard.keyPressedOnce(KeyEvent.VK_UP)) {
      selectionUp();
      game.getSoundLoader().play("click.wav");
      display();
    }
  }

	@Override
	public void draw(Graphics2D g, ImageLoader images) {
//		g.setColor(new Color(100, 100, 100, 0));
//		g.fillRect(screenX, screenY, menuW, menuH);

		//draw menu options
		for(int i = 0; i < options.length; i++) {
			g.setColor(new Color(255, 255, 255, 50));
			if(curSelection == i)
				g.setColor(new Color(150, 200, 150, 120));
			int h = menuH / options.length;
			g.fillRect(screenX, screenY + i * h, menuW, h);
			
			g.setColor(new Color(255, 255, 255, 150));
			if (curSelection == i)
				g.setColor(Color.white);
			g.drawString(options[i], screenX + (menuW - metrics.stringWidth(options[i])) / 2,
					screenY + i * h + (h + metrics.getHeight()) / 2);
			}
		
	}
}
