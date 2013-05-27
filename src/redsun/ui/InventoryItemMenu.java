package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeMap;

import redsun.RedSunGame;
import redsun.entities.Armor;
import redsun.entities.Inventory;
import redsun.entities.Item;
import redsun.entities.Weapon;
import redsun.entities.Character;
import redsun.resources.ImageLoader;

/*
 * The item list when browsing the inventory
 * Displays information about the currently selected item
 */
public class InventoryItemMenu extends Menu {		
	private Item curItem;
	private int scrollIndex;
	
	public InventoryItemMenu(RedSunGame game, UIHandler ui, String id) {
		super(game, ui);
		this.id = id;
		
		menuW = gameW * 3 / 4;
		menuH = gameH - (metrics.getHeight() + yInset * 2);
		
		screenX = gameW / 4;
		screenY = gameH - menuH;
	}

	//updates the inventory state for this menu to display
  public void update() {
		Inventory i = game.getData().getPlayer().getInventory();
		switch(id) {
		case "Weapons":
			ArrayList<String> w = i.getWeapons();
			String[] wlst = new String[w.size()];
			options = w.toArray(wlst);
			break;
		case "Armor":
			ArrayList<String> a = i.getArmor();
			String[] alst = new String[a.size()];
			options = a.toArray(alst);
			break;
		case "Consumables":
			ArrayList<String> c = i.getConsumables();
			String[] clst = new String[c.size()];
			options = c.toArray(clst);
			break;
		case "Items":
			ArrayList<String> it = i.getItems();
			String[] ilst = new String[it.size()];
			options = it.toArray(ilst);
			break;
		case "Key Items":
			ArrayList<String> k = i.getKeyItems();
			String[] klst = new String[k.size()];
			options = k.toArray(klst);
			break;
		}
		display();
  }

  //displays information about the currently selected item
  private void display() {
  	if(options.length != 0)
			curItem = game.getItems().getItem(options[curSelection]);
  }
  
	@Override
	public void doSelection() {
		//open item option menu (use, discard etc)
	}

	@Override
  public void processKeys(KeyInputHandler keyboard) {
    if(keyboard.keyPressedOnce(KeyEvent.VK_ESCAPE) ||
    		parent != null && keyboard.keyPressedOnce(KeyEvent.VK_LEFT)) {
      ui.setActiveMenu(parent);
      game.getSoundLoader().play("click.wav");
    }
    if (keyboard.keyPressedOnce(KeyEvent.VK_ENTER)) {
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
//		g.setColor(Color.white);
//		g.fillRect(screenX, screenY, menuW, menuH);
		
		drawInventory(g, scrollIndex);
		drawItemInfo(g, images);
	}
	
	//draw inventory item selections
	public void drawInventory(Graphics2D g, int init) {
		int scroll = scrollIndex;
		int h = (yInset + metrics.getHeight());
		for(int i = 0; i < options.length; i++) {
			int y = screenY + scroll * h;
			
			g.setColor(new Color (255, 255, 255, 50));
			//update current scroll state index
			if(curSelection == i) {
				g.setColor(new Color(150, 200, 150, 120));
				if(y >= gameH - h)
					scrollIndex--;
				else if (y < screenY)
					scrollIndex++;
			}
			if(curSelection == 0)
				scrollIndex = 0;
			
			//draw item selections
			if(y >= screenY && y < gameH - h) {
				g.fillRect(screenX, y, menuW / 3, h);
				
				g.setColor(new Color(255, 255, 255, 150));
				if(curSelection == i)
					g.setColor(Color.white);
				g.drawString(options[i], screenX + (menuW / 3 - metrics.stringWidth(options[i])) / 2,
						y + (h + metrics.getHeight()) / 2);
			}
			scroll++;
		}
		//draw scroll bar
		g.setColor(new Color(150, 200, 150, 20));
		g.fillRect(screenX + menuW / 3 - xInset, screenY, xInset, menuH);
		g.setColor(new Color(150, 200, 150, 120));
		//the number of item over the display diemensions
		int scrollSize = options.length - menuH / h;
		if(scrollSize < 0)
			scrollSize = 0;
		g.fillRect(screenX + menuW / 3 - xInset,
				screenY + (-scrollIndex) * menuH / (scrollSize + 5),
				xInset, menuH -  menuH * scrollSize / (scrollSize + 5));
	}
	
	//draw info for currently selected item
	public void drawItemInfo(Graphics2D g, ImageLoader images) {
		if(curItem != null) {
			BufferedImage img = images.getItemImgs().get(curItem.getImgId());
			g.drawImage(img, screenX + menuW / 3 + (menuW * 2 / 3 - menuW * 3 / 8) / 2,
					screenY + yInset * 2, menuW * 3 / 8, menuW * 3 / 8, null);
			
			//general item info
			g.setColor(Color.white);
			int h = yInset + metrics.getHeight();
			String id = curItem.getId();
			String level = Integer.toString(curItem.getLevel());
			String val = Integer.toString(curItem.getValue());
			g.drawString(id, screenX + menuW / 3 + xInset * 2, menuH / 2 + h * 2);
			g.drawString("Wield level: " + level, screenX + menuW / 3 + xInset * 2, menuH / 2 + h * 3);
			g.drawString("Value: " + val, screenX + menuW / 3 + xInset * 2, menuH / 2 + h * 4);
			
			//item type specific details
			if(curItem instanceof Weapon) {
				Weapon w = (Weapon) curItem;
				g.drawString("Weapon class: " + w.getWclass().toString(),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 2);
				g.drawString("Attack: " + Integer.toString(w.getAttack()),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 3);		
				g.drawString("Elementa: " + Integer.toString(w.getElementa()),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 4);		
				g.drawString("Weight: " + Integer.toString(w.getWeight()),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 5);		
			}
			else if(curItem instanceof Armor) {
				Armor a = (Armor) curItem;
				g.drawString("Armor section: " + a.getPart().toString(),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 2);
				g.drawString("Armor: " + Integer.toString(a.getArmor()),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 3);		
				g.drawString("Elemental Armor: " + Integer.toString(a.getEarmor()),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 4);	
				g.drawString("Weight: " + Integer.toString(a.getWeight()),
						screenX + menuW * 2 / 3 + xInset * 2, menuH / 2 + h * 5);	
			}
			
			//item effects
			TreeMap<Character.Stat, Integer> effects = curItem.getEffects();
			g.drawString("Effects:", screenX + menuW / 3 + xInset * 2, menuH * 3 / 4 + h);
			int row = 1;
			int col = 1;
			for(Character.Stat s: effects.keySet()) {
				g.drawString(s.toString() + ": " + Integer.toString(effects.get(s)),
						screenX + menuW / 3 * col + xInset * 2, menuH * 7 / 8 + h * (row-1));
				if(row % 4 == 0) {
					col++;
					row = 0;
				}
				row++;
			}
		}
	}
}
