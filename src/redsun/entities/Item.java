package redsun.entities;

import java.util.ArrayList;
import java.util.TreeMap;

import redsun.Sprite;

/*
 * Generic Item class
 */
public class Item extends Sprite
{
  private boolean equiptable;
  private boolean equipted;
  
  public enum ItemType {
  	WEAPON("Weapon"), ARMOR("Armor"), CONSUMABLE("Consumable"), ITEM("Item"), KEYITEM("Key Item");
  	
    private String name = "";
    ItemType(String s) {
      name = s;
    }
    public String toString() {
      return name;
    }
  }
  
  private ItemType type;
  //change - for revamped naming system to come
  private String name;
  
	//all effects of the item
  //key: effect name, value: magnitude
  protected TreeMap<Character.Stat, Integer> effects = new TreeMap<>();
  //tile location of the item for reference
  protected int x, y;
  //currency value of the item
  protected int value;
  //level at which the item can be used
  protected int level;

	// ------------------------------- Constructor -------------------------------

  public Item(String id, ItemType type, boolean equiptable) {
  	super(id);
  	this.type = type;
  }
  
	public Item(String id, ItemType type, TreeMap<Character.Stat, Integer> effects, boolean equiptable) {
    super(id);
    this.type = type;
    this.effects = effects;
    this.equiptable = equiptable;
  }
  
	// ------------------------------- Methods -------------------------------

  public void putOn(Tile tile) {
  	ArrayList<Item> items = new ArrayList<>();
  	items.add(this);
  	Loot loot = new Loot(items);
    tile.setLoot(loot);
  }
  
  public void putIn(MapObject obj) {
    obj.setItem(this);
  }
  
  public String toString() {
    String result = id;
    if(effects != null)
      for(Character.Stat s: effects.keySet())
        result += " " + s.toString() + ": " + effects.get(s);
    return result;
  }

	// ------------------------------- Getters and Setters -------------------------------
  
  public boolean isEquiptable() {
    return equiptable;
  }

  public void setEquiptable(boolean equiptable) {
    this.equiptable = equiptable;
  }
  
  public boolean isEquipted() {
    return equipted;
  }

  public void setEquipted(boolean equipted) {
    this.equipted = equipted;
  }
  
	public ItemType getType() {
		return type;
	}
	
  public TreeMap<Character.Stat, Integer> getEffects() {
    return effects;
  }

  public void setEffects(TreeMap<Character.Stat, Integer> effect) {
    this.effects = effect;
  }
  
  public void addEffect(Character.Stat name, int mag) {
    effects.put(name, mag);
  }
  
  public void removeEffect(String name) {
    effects.remove(name);
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
  
  public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
