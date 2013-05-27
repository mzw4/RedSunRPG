package redsun.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import redsun.RedSunGame;
import redsun.resources.ImageLoader;
import redsun.entities.Character;


public class AttackMenu extends Menu {

  private Character selected;
  private Hashtable<String, Character> targets;
  
  /*
   * A new one must be created each time an attack is initiated
   */
  public AttackMenu(RedSunGame game, UIHandler ui) {
    super(game, ui);
    
//    menuW = gameW/4;
//    menuH = gameH/2;
//    screenX = (gameW - menuW)/2;
//    screenY = (gameH - menuH)/2;
    
    id = "AttackMenu";
  }

  @Override
  public void update() {
    this.selected = ui.getSelected();

    //this works at least for a standard attack
    ArrayList<Character> adj = selected.getAdjacentCharacters();
    if(adj != null) {
      for(int i = 0; i < adj.size(); i++)
        targets.put(Integer.toString(i), adj.get(i));
      
      Set<String> keys = targets.keySet();
      options = new String[keys.size()];
      keys.toArray(options);
    }
  }
  
  @Override
  public void doSelection() {
    Character target = targets.get(options[curSelection]);
    if(target != null)
      selected.attack(target);
    
    //set the selected character to face its target
    selected.setDir(target.getDirFrom(selected));
    
    //reset cursor back to selected, clear all menus and selections after this attack
    ui.getCursor().setLoc(selected.getLoc());
    ui.clearSelected();
    ui.exitAllMenus();
    options = null;
  }
  
  @Override
  public void processKeys(KeyInputHandler keyboard) {
    super.processKeys(keyboard);
    
    //change
    if(options != null)
      ui.getCursor().setLoc(targets.get(options[curSelection]).getLoc());
  }

  @Override
  public void draw(Graphics2D g, ImageLoader images) {}

}
