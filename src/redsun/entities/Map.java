package redsun.entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Map {
	// map name
	private String id;
	// 2D array of tiles of which the map consists
	private Tile[][] tiles;
	// tile-wise dimensions of map
	private int width;
	private int height;

	// the set of all characters present on this map
	private TreeMap<String, Point> characters;
	private TreeMap<String, Point> triggers;
	private TreeMap<Map.Dir, Map> adjacent;

	public enum Dir {
		NORTH, SOUTH, EAST, WEST
	}

	// ------------------------------- Constructor -------------------------------

	public Map(String id, Tile[][] t) {
		this.id = id;
		this.tiles = t;
		width = tiles.length;
		height = tiles[0].length;
	}

	// ------------------------------- Methods -------------------------------

	public boolean addChar(Character c) {
		if (characters.containsKey(c.getId()))
			return false;
		characters.put(c.getId(), new Point(c.getX(), c.getY()));
		return true;
	}

	public boolean removeChar(String id) {
		characters.remove(id);
		return false;
	}

	// returns a list of all tiles within a specified distance
	public ArrayList<Tile> getTilesWithin(Tile tile, int dist) {
		ArrayList<Tile> tilesWithin = new ArrayList<>();
		int x = tile.getX();
		int y = tile.getY();

		// for each distance i from 1-d, gets all tiles at that distance
		for (int a = -dist; a <= dist; a++) {
			for (int b = -(dist - Math.abs(a)); b <= dist - Math.abs(a); b++) {
				int tx = x + a;
				int ty = y + b;
				if (tx >= 0 && tx < width && ty >= 0 && ty < height)
					tilesWithin.add(tiles[tx][ty]);
			}
		}
		return tilesWithin;
	}

	// links the adjacent tiles for each tile in the map
	public void linkAll() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] != null) {
					if (j - 1 >= 0)
						tiles[i][j].setAdjacent(Dir.NORTH, tiles[i][j - 1]);
					if (j + 1 < tiles[0].length)
						tiles[i][j].setAdjacent(Dir.SOUTH, tiles[i][j + 1]);
					if (i - 1 >= 0)
						tiles[i][j].setAdjacent(Dir.WEST, tiles[i - 1][j]);
					if (i + 1 < tiles.length)
						tiles[i][j].setAdjacent(Dir.EAST, tiles[i + 1][j]);
				}
			}
		}
	}

	// ------------------------------- Getters and Setters -------------------------------

	public String getId() {
		return id;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public Tile getTileAt(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		return tiles[x][y];
	}

	public void setTile(Tile tile) {
		tiles[tile.getX()][tile.getY()] = tile;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// ------------------------------ Draw ------------------------------------
	// public void draw(Graphics2D g, Camera cam) {
	//
	// }
}
