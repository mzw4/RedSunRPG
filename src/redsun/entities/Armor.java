package redsun.entities;

import java.util.HashMap;
import java.util.TreeMap;


public class Armor extends Item
{
  
  public enum BodyPart {
    HEAD, ARMS, BODY, LEGS, ACCESSORY
  }
  
  private int armor, earmor, weight;
  private BodyPart part;
  
	// ------------------------------- Constructors -------------------------------

  public Armor(String id, BodyPart part) {
    super(id, Item.ItemType.ARMOR, true);
    this.part = part;
  }
  
  public Armor(String id, BodyPart part, TreeMap<Character.Stat, Integer> effects,
  		int armor, int earmor, int weight) {
  	super(id, Item.ItemType.ARMOR, true);
  	this.part = part;
  	this.effects = effects;
  	this.armor = armor;
  	this.earmor = earmor;
  	this.weight = weight;
  }

	// ------------------------------- Getters and Setters -------------------------------

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}
	
	public int getEarmor() {
		return earmor;
	}

	public void setEarmor(int earmor) {
		this.earmor = earmor;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public BodyPart getPart() {
		return part;
	}

	public void setPart(BodyPart part) {
		this.part = part;
	}
}
