package redsun.entities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

import redsun.entities.Item.ItemType;

/*
 * In-game inventory
 * 
 * Categories: weapons, armor, consumables, items, keyitems
 * ArrayList implementation
 */

public class Inventory
{
	//make these all strings?
  private ArrayList<String> weapons;
  private ArrayList<String> armor;
  private ArrayList<String> consumables;
  private ArrayList<String> items;
  private ArrayList<String> keyItems;

  public Inventory() {
    weapons = new ArrayList<>();
    armor = new ArrayList<>();
    consumables = new ArrayList<>();
    items = new ArrayList<>();
    keyItems = new ArrayList<>();
  }
  
  //add a specified item to the inventory
  public void add(Item i) {
  	String id = i.getId();
  	switch(i.getType()) {
  	case WEAPON: 
  		weapons.add(id);
  		break;
  	case ARMOR:
  		armor.add(id);
  		break;
  	case CONSUMABLE:
  		consumables.add(id);
  		break;
  	case ITEM:
  		items.add(id);
  		break;
  	case KEYITEM:
  		keyItems.add(id);
  		break;
  	}
  }

  //removes specified item from inventory
  public void remove(Item i) {
  	String id = i.getId();
  	switch(i.getType()) {
  	case WEAPON: 
  		weapons.remove(id);
  		break;
  	case ARMOR:
  		armor.remove(id);
  		break;
  	case CONSUMABLE:
  		consumables.remove(id);
  		break;
  	case ITEM:
  		items.remove(id);
  		break;
  	case KEYITEM:
  		keyItems.remove(id);
  		break;
  	}
  }
    
  public ArrayList<String> getWeapons() {
		return weapons;
	}

	public ArrayList<String> getArmor() {
		return armor;
	}

	public ArrayList<String> getConsumables() {
		return consumables;
	}

	public ArrayList<String> getItems() {
		return items;
	}

	public ArrayList<String> getKeyItems() {
		return keyItems;
	}

	//returns size of inventory
  public int getSize() {
    return items.size() + weapons.size() + armor.size() + 
    		keyItems.size() + consumables.size();
  }
  
  public String toString() {
    String result = "";
    for(String s: weapons) {
      result += s + ", ";
    }
    for(String s: armor) {
      result += s + ", ";
    }
    for(String s: consumables) {
      result += s + ", ";
    }
    for(String s: items) {
      result += s + ", ";
    }
    for(String s: keyItems) {
      result += s + ", ";
    }
    return result;
  }
}
