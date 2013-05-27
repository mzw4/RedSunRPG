package redsun.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.imageio.ImageIO;

import redsun.MapEntity;
import redsun.Sprite;

public class Tile extends MapEntity implements Serializable {
	public static final int width = 36;
	public static final int height = 36;

	// tile properties
	// change - type necessary with id?
	private String type;
	// should tile really have this info?
	private MapEntity entity;
	private Loot loot;
	private Character character;

	// tells us if the tile type is walkable
	private boolean walkable;

	// contains all adjacent tiles
	// key: Map direction, object: Tile
	private HashMap<Map.Dir, Tile> adjacent;

	// ------------------------------- Constructor -------------------------------

	public Tile(int x, int y, String id, boolean walkable) {
		super(id);
		this.x = x;
		this.y = y;
		this.type = id;
		this.imgId = id;
		this.walkable = walkable;
		adjacent = new HashMap<>();
	}

	// ------------------------------- Methods -------------------------------

	public boolean isBlocked() {
		return walkable == false || character != null || entity != null;
	}

	// returns the adjacent tile of the specified direction
	public Tile getAdjacent(Map.Dir dir) {
		if (adjacent != null)
			return adjacent.get(dir);
		return null;
	}

	public boolean setAdjacent(Map.Dir dir, Tile t) {
		if (dir == null || t == null)
			return false;
		adjacent.put(dir, t);
		return true;
	}

	public HashMap<Map.Dir, Tile> getAllAdjacent() {
		return adjacent;
	}

	// returns the direction traveled from the specified parent tile to this tile
	// null if not adjacent
	// change - unused
	public Map.Dir getDirFrom(Tile tile) {
		if (tile == null)
			return null;
		for (Map.Dir dir : tile.getAllAdjacent().keySet())
			if (tile.getAdjacent(dir) == this)
				return dir;
		return null;
	}

	// returns the distance from the specified tile by foot
	public int distanceFrom(Tile t) {
		return Math.abs(t.x - x) + Math.abs(t.y - y);
	}

	public String getEntityName() {
		String name = "";
		if (entity != null)
			name = entity.getId();
		return name;
	}

	// returns true if this tile is currently holding a character
	public boolean hasCharacter() {
		return entity != null && (entity instanceof Character);
	}

	// ------------------------------- Getters and Setters -------------------------------

	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MapEntity getEntity() {
		return entity;
	}

	public void setEntity(MapEntity entity) {
		this.entity = entity;
	}

	public Loot getLoot() {
		return loot;
	}

	public void setLoot(Loot loot) {
		this.loot = loot;
	}

	public Character getCharacter() {
		return character;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}

	public String toString() {
		return "Tile x: " + x + " y: " + y;
	}
}
