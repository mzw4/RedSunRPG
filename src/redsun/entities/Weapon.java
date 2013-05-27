package redsun.entities;

import java.util.HashMap;
import java.util.TreeMap;


public class Weapon extends Item 
{ 
	
	public enum WepClass {
		SWORD, AXE, SPEAR, DAGGER, BOW, ANIMUS, STAFF
	}
	
	private int attack, elementa, weight;
	private WepClass wclass;
	
	// ------------------------------- Constructors -------------------------------
	
  public Weapon(String id, WepClass wclass) {
    super(id, Item.ItemType.WEAPON, true);
    this.wclass = wclass;
  }
  
  public Weapon(String id, WepClass wclass, TreeMap<Character.Stat, Integer> effects,
  		int attack, int elementa, int weight) {
    super(id, Item.ItemType.WEAPON, true);
    this.wclass = wclass;
    this.effects = effects;
    this.attack = attack;
    this.elementa = elementa;
    this.weight = weight;
  }
  
	// ------------------------------- Getters and Setters -------------------------------

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getElementa() {
		return elementa;
	}

	public void setElementa(int elementa) {
		this.elementa = elementa;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public WepClass getWclass() {
		return wclass;
	}

	public void setWclass(WepClass wclass) {
		this.wclass = wclass;
	}
}
