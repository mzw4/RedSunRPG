package redsun.resources;

import java.io.File;
import java.util.TreeMap;

import com.thoughtworks.xstream.XStream;

import redsun.entities.Item;

/*
 * Loads all items from the file items.xml to a map
 */
public class ItemLoader {
	private XStream xs = new XStream();
	private TreeMap<String, Item> items = new TreeMap<>();
	
	public ItemLoader() {
		loadItems();
	}
	
	private void loadItems() {
		try {
			items = (TreeMap<String, Item>) xs.fromXML(new File("items.xml"));
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	public Item getItem(String s) {
		return items.get(s);
	}
}
