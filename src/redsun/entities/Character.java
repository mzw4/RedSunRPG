package redsun.entities;

import java.util.ArrayList;
import java.util.HashMap;

import redsun.AnimatedMapSprite;
import redsun.EventListener;
import redsun.EventManager;
import redsun.MapEntity;
import redsun.Path;
import redsun.RedSunGame;
import redsun.events.Event;
import redsun.events.EventType;

/*
 * Core character class for Red Sun
 */
public class Character extends AnimatedMapSprite implements EventListener
{     
  public enum Class {
    CITIZEN("Citizen"), SOLDIER("Soldier"), WARRIOR("Warrior"), ASSASSIN("Assassin"),
    ELEMENTAL("Elemental"), ELEMENTAL_WARRIOR("Elemental Warrior"),
    ELEMENTAL_ASSASSIN("Elemental Assassin"), DARK_SORCERER("Dark Sorcerer"), DRAGON("Dragon");
    
    private String name = "";
    Class(String s) {
      name = s;
    }
    public String toString() {
      return name;
    }
  }
  
  public enum Stat {
    STRENGTH("Strength"), ELEMENTAL_ATTACK("Elemental Attack"), DEFENSE("Defense"),
    ELEMENTAL_DEFENSE("Elemental Defense"), WEAPON_SKILL("Weapon Skill"), AGILITY("Agility"),
    INTELLIGENCE("Intelligence"), HEALTH("Health"), CRIT("Critical");
    
    private String name = "";
    Stat(String s) {
      name = s;
    }
    public String toString() {
      return name;
    }
  }
  
  //sprite id should be based on the name, detailed in a config file
//  private String portraitId;
  //nah - use name instead
  
  private String name;
  private Class charClass;
  private HashMap<Stat, Integer> stats;  
  private Inventory inventory;
  private SkillTree skills;
  private int level;
  private int exp;
  private int gold;
  private Dialogue dialogue;
  private int moveDistance;
  private int mass;
 
  // ------------------------------ Constructors ------------------------------------
  
  public Character(String name, Class charClass) {
    //temp change - this animation data stuff should depend on the class,
    //should make a config document with data for every animation in the game
    
    //default sprite
    super("a_bladelord_run", 70, 16, true, true);

    this.name = name;
    this.charClass = charClass;
    stats = new HashMap<>();
    inventory = new Inventory();
    skills = new SkillTree();
    
    for(Stat s: Stat.values())
      stats.put(s, 0);
  }
  
  public Character(String name, Class charClass,
      Inventory inventory, HashMap<Stat, Integer> stats, SkillTree skills,
      int level, int exp, int gold, int moveDistance, int mass) {
    //temp change
    this(name, charClass);
    this.charClass = charClass;
    this.id = name;
    this.inventory = inventory;
    this.stats = stats;
    this.skills = skills;
    this.level = level;
    this.exp = exp;
    this.gold = gold;    
    this.moveDistance = moveDistance;
    this.mass = mass;
  }
  
  // ------------------------------ Methods ------------------------------------  
  public void updateLogic() {
    super.updateLogic();
    
    //temp death
    if(stats.get(Stat.HEALTH) <= 0) 
      loc.setEntity(null);
  }
  
  public void attack(Character c) {
    if(c == null)
      return;
    
    c.setStat(Stat.HEALTH, c.getStat(Stat.HEALTH) - 5);
    System.out.println(c.getStat(Stat.HEALTH));
  }
  
  public void receiveItem(Item item) {
  	if(item != null)
  		inventory.add(item);
  }
  
  //for the moveArea calculation, this may be overkill - change
  public boolean canReach(Tile dest) {
    if(dest == null || dest.isBlocked())
      return false; 
    
    ArrayList<Tile> path = tileRoute(loc, dest);
    
    if(path == null)
      return false;
    
    return path.size() <= moveDistance;
  }
  
  public boolean canAttack(Tile target) {
    if(target == null || (target.isBlocked() && !target.hasCharacter()))
      return false;
    
    for(Tile t: target.getAllAdjacent().values())
      if(canReach(t))
	return true;
    return false;
  }
  
  //change for when targets are not only adjacent
  public boolean hasTargets() {
    return !getAdjacentCharacters().isEmpty();
  }
  
  public Map.Dir getDirFrom(Character c) {
    return loc.getDirFrom(c.getLoc());
  }
  
  public void handleEvent(Event e) {
    
  }
  
  // ------------------------------ Getters and setters ------------------------------------  
  
  public String getCharName() {
  	return name;
  }
  
  public void setCharName(String name) {
  	this.name = name;
  }
  
  public Class getCharClass() {
    return charClass;
  }

  public void setCharClass(Class charClass) {
    this.charClass = charClass;
  }
  
  public int getStat(Stat stat) {
    return stats.get(stat);
  }
  
  public void setStat(Stat stat, int val) {
    stats.put(stat, val);
  }
  
  public HashMap<Stat, Integer> getStats() {
    return stats;
  }
  
  public void setStats(HashMap<Stat, Integer> stats) {
    this.stats = stats;
  }
  
  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public SkillTree getSkills() {
    return skills;
  }

  public void setSkills(SkillTree skills) {
    this.skills = skills;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getExp() {
    return exp;
  }

  public void setExp(int exp) {
    this.exp = exp;
  }

  public int getGold() {
    return gold;
  }

  public void setGold(int gold) {
    this.gold = gold;
  }

  public int getMoveDistance() {
    return moveDistance;
  }

  public void setMoveDistance(int moveDistance) {
    this.moveDistance = moveDistance;
  }

  public int getMass() {
    return mass;
  }

  public void setMass(int mass) {
    this.mass = mass;
  }

  // ------------------------------ Draw ------------------------------------
//  public void draw(Graphics2D g2d) {
//    g2d.drawImage("guyFE.png", screenX + dx, screenY + dy, Tile.width, Tile.height, null);
//  }
}
