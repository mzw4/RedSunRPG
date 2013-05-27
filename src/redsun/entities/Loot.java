package redsun.entities;

import java.util.ArrayList;

/*
 * Wrapper class for loot, which is simply a list of items
 */
public class Loot {
	private ArrayList<Item> items = new ArrayList<>();
	
	public Loot(ArrayList<Item> items) {
		this.items = items;
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}
}
