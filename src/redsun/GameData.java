package redsun;

import java.util.Hashtable;

import redsun.entities.GameProgress;
import redsun.entities.Item;
import redsun.entities.Player;
import redsun.entities.Character;


/*
 * Contains all data prevalent to the current game
 * 
 * Holds instance of player, all characters and items in game, game progress, etc 
 */
public class GameData {

	//character for now - change to player
	private Character player;
	private Hashtable<String, Character> characters;
	private Hashtable<String, Item> items;
	private GameProgress progress;
	
	private long playTime;
	
	// ------------------------------- Constructor -------------------------------
	
	public GameData() {
		this.player = null;
		this.characters = new Hashtable<>();
		this.items = new Hashtable<>();
		this.progress = new GameProgress();
	}

	public GameData(Player player, Hashtable<String, Character> characters,
			Hashtable<String, Item> items, GameProgress progress) {
		this.player = player;
		this.characters = characters;
		this.items = items;
		this.progress = progress;
	}
	
	// ------------------------------- Methods -------------------------------

	public void updatePlayer(Character player) {
		this.player = player;
	}
	
	public void updateChar(Character c) {
		String n = c.getCharName();
		if(characters.containsKey(n)) {
			characters.remove(n);
		}
		characters.put(n, c);
	}
	
	public void updateItem(Item i) {
		String n = i.getId();
		if(items.containsKey(n)) {
			items.remove(n);
		}
		items.put(n, i);
	}
	
	public void updateProgress(GameProgress progress) {
		this.progress = progress;
	}
	
	// ------------------------------- Getters and Setters -------------------------------

	public Character getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Hashtable<String, Character> getCharacters() {
		return characters;
	}

	public void setCharacters(Hashtable<String, Character> characters) {
		this.characters = characters;
	}

	public Hashtable<String, Item> getItems() {
		return items;
	}

	public void setItems(Hashtable<String, Item> items) {
		this.items = items;
	}

	public GameProgress getProgress() {
		return progress;
	}

	public void setProgress(GameProgress progress) {
		this.progress = progress;
	}
	
	public long getPlayTime() {
		return playTime;
	}
}
