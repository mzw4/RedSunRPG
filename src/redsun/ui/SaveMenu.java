package redsun.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import redsun.GameData;
import redsun.RedSunGame;
import redsun.resources.ImageLoader;

public class SaveMenu extends Menu {

  public SaveMenu(RedSunGame game, UIHandler ui) {
    super(game, ui);
    id = "SaveMenu";
    parent = "SystemMenu";

    menuW = gameW;
    menuH = gameH;
    screenX = (gameW - menuW)/2;
    screenY = (gameH - menuH)/2;
    
    options = new String[100];
    for(int i = 0; i < options.length; i++)
    	options[i] = "Save Slot " + (i + 1);
    
    
    GameData data = game.getData();
  }

  public void doSelection() {
    
  }

  public void draw(Graphics2D g, ImageLoader images) {
    if(hidden)
      return;
    
    //draw menu box
    g.setColor(color);
    g.fillRect(screenX, screenY, menuW, menuH);  
    for(int i = 0; i < options.length; i++) {
      g.setColor(new Color(255, 255, 255, 150));
      if(curSelection == i)
	g.setColor(Color.white);
      g.drawString((String)options[i], xInset, metrics.getHeight() * (i + 1) );
    }
  }
}
