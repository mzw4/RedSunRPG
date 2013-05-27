package redsun.ui;

import java.awt.Graphics2D;
import java.util.ArrayList;

import redsun.AnimatedMapSprite;
import redsun.EventManager;
import redsun.MapEntity;
import redsun.RedSunGame;
import redsun.entities.Character;
import redsun.entities.Map;
import redsun.entities.Tile;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.resources.ImageLoader;
import redsun.events.EventType;

//change - maybe just have the camera return the visible stuff instead of drawing and manipulating them too
public class Camera {
	private float panSpeedX = 5, panSpeedY;
	// tile-wise dimensions of camera span
	private int width, height;
	// pixel coordinates of camera center on map
	private int x, y;
	// pixel coordinates of camera center on screen
	private int screenX, screenY;
	// camera interpolation delta
	private float dx, dy;
	// center tile of camera position
	private Tile center;
	// character that the camera is following
	private Character following;
	// whether or not the camera is currently panning;
	private boolean panning;

	// list of visible items on screen with the current camera position
	private ArrayList<Tile> visibleTiles;
	private ArrayList<MapEntity> visibleSprites;
	// number of tiles rendered offscreen on the screen edges
	private final int edgeBuffer = 1;

	private EventManager eventMgr;
	private ImageLoader images;
	private Map map;
	private UIHandler ui;

	public Camera(EventManager eventMgr, RedSunGame game, ImageLoader images, Map map, UIHandler ui) {
		width = game.getPanelWidth() / Tile.width;
		height = game.getPanelHeight() / Tile.height;
		screenX = (game.getPanelWidth() - Tile.width) / 2;
		screenY = (game.getPanelHeight() - Tile.height) / 2;

		this.eventMgr = eventMgr;
		this.images = images;
		this.map = map;
		this.ui = ui;

		visibleTiles = new ArrayList<Tile>();
		visibleSprites = new ArrayList<MapEntity>();

		center = map.getTileAt(0, 0);
	}

	// updates the camera properties based on the current center tile
	// change - try to optimize so that a new visibleTiles set doesn't have to be
	// made every single frame
	public void update() {
		visibleTiles.clear();
		visibleSprites.clear();

		// calculate camera offset delta for panning movement
		if (dx > 0) {
			dx -= panSpeedX;
			if (dx <= 0)
				dx = 0;
		} else if (dx < 0) {
			dx += panSpeedX;
			if (dx >= 0)
				dx = 0;
		}
		if (dy > 0) {
			dy -= panSpeedY;
			if (dy <= 0)
				dy = 0;
		} else if (dy < 0) {
			dy += panSpeedY;
			if (dy >= 0)
				dy = 0;
		}
		if (panning && dx == 0 && dy == 0) {
			panning = false;
			endPan();
		}

		// set current camera x,y pixel position
		if (following != null) {
			center = following.getLoc();
			x = (int) (following.getX() * Tile.width + following.getDx() + dx);
			y = (int) (following.getY() * Tile.height + following.getDy() + dy);
		} else {
			x = center.getX() * Tile.width + (int) dx;
			y = center.getY() * Tile.height + (int) dy;
		}

		// calculate camera view boundaries
		int offsetX = x - width / 2 * Tile.width;
		int offsetY = y - height / 2 * Tile.height;
		int xmin = center.getX() - (width / 2 + edgeBuffer);
		int xmax = center.getX() + (width / 2 + edgeBuffer);
		int ymin = center.getY() - (height / 2 + edgeBuffer);
		int ymax = center.getY() + (height / 2 + edgeBuffer);

		// calculate current visible elements
		for (int i = xmin; i <= xmax; i++) {
			for (int j = ymin; j <= ymax; j++) {
				Tile t = map.getTileAt(i, j);
				if (t != null) {
					visibleTiles.add(t);
					t.calcScreenPos(offsetX, offsetY);

					MapEntity obj = t.getEntity();
					if (obj != null) {
						visibleSprites.add(obj);
						obj.calcScreenPos(offsetX, offsetY);
					}
				}
			}
		}
		if (ui.getCursor() != null)
			ui.getCursor().calcScreenPos(offsetX, offsetY);
	}

	// dispatches an event telling other objects that the camera is finished
	// panning
	public void endPan() {
		eventMgr.addEvent(new Event(EventType.Event_UI_CameraPanEnded));
	}

	// tells the camera to follow a particular character
	public void follow(Character c) {
		following = c;
	}

	// ------------------------------ Getters and Setters
	// ------------------------------------
	public Tile getCenterTile() {
		return center;
	}

	public void setCenterTile(Tile center, boolean pan) {
		if (panning)
			return;
		if (pan) {
			dx = (this.center.getX() - center.getX()) * Tile.width;
			dy = (this.center.getY() - center.getY()) * Tile.height;
			// to maintain the ratio of x,y pan speed
			if (dx == 0)
				panSpeedY = panSpeedX;
			else
				panSpeedY = Math.abs(dy / dx * panSpeedX);
			following = null;
			eventMgr.addEvent(new Event(EventType.Event_UI_CameraPanStarted));
			panning = true;
		}
		this.center = center;
	}

	// pans one tile in the specified direction
	public void pan(Map.Dir dir) {
		if (panning)
			return;
		if (dir == Map.Dir.NORTH)
			dy = Tile.height;
		else if (dir == Map.Dir.EAST)
			dx = -Tile.width;
		else if (dir == Map.Dir.SOUTH)
			dy = -Tile.height;
		else if (dir == Map.Dir.WEST)
			dx = Tile.width;

		// to maintain the ratio of x,y pan speed
		panSpeedY = Math.abs(dy / dx * panSpeedX);
		following = null;
		eventMgr.addEvent(new Event(EventType.Event_UI_CameraPanStarted));
		panning = true;
		this.center = center.getAdjacent(dir);
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public ArrayList<Tile> getVisTiles() {
		return visibleTiles;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getScreenX() {
		return screenX;
	}

	public int getScreenY() {
		return screenY;
	}

	public float getPanSpeed() {
		return panSpeedX;
	}

	public ArrayList<MapEntity> getVisibleSprites() {
		return visibleSprites;
	}

	// ------------------------------ Draw ------------------------------------
	public void drawMap(Graphics2D g) {
		for (int i = 0; i < visibleTiles.size(); i++) {
			Tile t = visibleTiles.get(i);
			if (t != null && images != null)
				t.drawSprite(g, images);
		}
	}

	public void drawSprites(Graphics2D g) {
		// change - right now only animated stuff is shown since for the iloader
		// only sheets are animated by
		// I extended mapsprite from animatedsprite which might be weird
		for (int i = 0; i < visibleSprites.size(); i++) {
			MapEntity m = visibleSprites.get(i);
			if (images != null) {
				if (m instanceof AnimatedMapSprite) {
					AnimatedMapSprite a = (AnimatedMapSprite) m;
					a.drawAnimatedSprite(g, images);
				} else {
					// change
					m.drawSprite(g, images);
				}
			}
		}
	}

	public void drawUI(Graphics2D g) {
		ui.drawUI(g, images);
	}
}
