package redsun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import redsun.entities.Map;
import redsun.entities.Character;
import redsun.entities.Tile;

public class MapEntity extends Sprite {

	// Tile location of sprite on map
	protected Tile loc;
	// Destination of a move
	protected Tile dest;
	// tile-wise coordinates of sprite on map
	protected int x, y;
	// current interpolation delta for pixel-wise movement
	private float dx, dy;
	// direction sprite is facing
	protected Map.Dir dir;
	// smoothest if speed divides the tile dimensions
	private float moveSpeed = 2;
	// true if the entity ignores collision
	private boolean ghost;
	// true if the character has reached its move destination
	private boolean reachedDest;

	private LinkedList<Map.Dir> moveQueue;

	public MapEntity(String id) {
		super(id);
		moveQueue = new LinkedList<Map.Dir>();

		// default is to not ignore collision
		ghost = false;
		// default direction is south
		dir = Map.Dir.SOUTH;
	}

	// ------------------------------ Methods ------------------------------------

	// character moves to the target destination tile
	// change - only moves one tile now i think
	public void moveTo(Tile dest) {
		this.dest = dest;
		if (dest == null)
			return;

		ArrayList<Map.Dir> heading = dirRoute(loc, dest);

		if (heading == null)
			return;

		// traverse the path by calling move(Map.Dir) for each element in path
		moveQueue.clear();
		for (int i = 0; i < heading.size(); i++)
			moveQueue.offer(heading.get(i));
	}

	public void move(Map.Dir dir) {
		if (!isMoving()) {
			this.dir = dir;
			Tile dest = loc.getAdjacent(dir);
			if (dest != null && (!dest.isBlocked() || ghost)) {
				switch (dir) {
				case EAST:
					dx = -Tile.width;
					break;
				case WEST:
					dx = Tile.width;
					break;
				case SOUTH:
					dy = -Tile.height;
					break;
				case NORTH:
					dy = Tile.height;
					break;
				}

				if (!ghost)
					loc.setEntity(null);
				loc = dest;
				if (!ghost)
					loc.setEntity(this);
			}
		}
	}

	// updates the state of the MapSprite
	public void updateLogic() {
		if (isMoving()) {
			if (dir == Map.Dir.EAST) {
				dx += moveSpeed;
				if (dx >= 0)
					dx = 0;
			} else if (dir == Map.Dir.WEST) {
				dx -= moveSpeed;
				if (dx <= 0)
					dx = 0;
			} else if (dir == Map.Dir.SOUTH) {
				dy += moveSpeed;
				if (dy >= 0)
					dy = 0;
			} else if (dir == Map.Dir.NORTH) {
				dy -= moveSpeed;
				if (dy <= 0)
					dy = 0;
			}
		} else if (!moveQueue.isEmpty()) {
			move(moveQueue.poll());
		}

		if (dest == null)
			reachedDest = false;
		if (loc == dest && dx == 0 && dy == 0) {
			reachedDest = true;
			dest = null;
		}

		if (loc != null) {
			x = loc.getX();
			y = loc.getY();
		}
		// x = loc.getX()*Tile.width + dx;
		// y = loc.getY()*Tile.height + dy;
	}

	// true if the MapSprite is currently moving
	public boolean isMoving() {
		return dx != 0 || dy != 0;
	}

	// returns the Tile that the MapSprite is currently facing
	public Tile getFacingTile() {
		return loc.getAdjacent(dir);
	}

	public void calcScreenPos(int offsetX, int offsetY) {
		screenX = (x * Tile.width - offsetX) + (int) dx;
		screenY = (y * Tile.height - offsetY) + (int) dy;
	}

	// returns a path of tiles from start to goal
	public ArrayList<Tile> tileRoute(Tile start, Tile goal) {
		return findTilePath(aStar(start, goal));
	}

	// returns a path of dirs from start to goal
	public ArrayList<Map.Dir> dirRoute(Tile start, Tile goal) {
		return findDirPath(aStar(start, goal));
	}

	/*
	 * Calculate the shortest path to a specified target location change - make
	 * its own class?
	 */
	public PathNode aStar(Tile start, Tile goal) {
		if (start == null || goal == null)
			return null;

		// set of tiles already evaluated
		ArrayList<PathNode> closed = new ArrayList<PathNode>();
		// set of tiles not yet evaluated
		HashSet<PathNode> open = new HashSet<PathNode>();
		PathNode startNode = new PathNode(start);
		PathNode goalNode = new PathNode(goal);
		open.add(startNode);

		// initialize nodes
		PathNode current = startNode;
		current.setG(0);
		current.setH(calcHeuristic(current, goalNode));
		while (!open.isEmpty()) {
			// pick a random tile
			// if all tiles have the same priority, this tile will be current
			if (current == null) {
				int rand = new Random().nextInt(open.size());
				int i = 0;
				for (PathNode n : open) {
					if (i == rand)
						current = n;
					i++;
				}
				// sets the current tile to the open tile of lowest F score
				for (PathNode n : open) {
					if (current != null && n.getF() < current.getF())
						current = n;
				}
			}

			// the goal is reached
			// returns a path map with a reference to the end node. The actual
			// path is found by
			// traversing this path backwards through parent nodes
			if (current.getTile() == goalNode.getTile()) {
				return current;
			}

			// evaluate the current tile
			open.remove(current);
			closed.add(current);
			for (Tile t : current.getTile().getAllAdjacent().values()) {
				PathNode n = new PathNode(t);
				if (closed.contains(n) || n.isBlocked())
					continue;

				if (!open.contains(n))
					open.add(n);

				// if open contains n, check to see if n can be
				// reached faster through current rather than its parent
				if (open.contains(n) && n.getG() > current.getG() + 1) {
					n.setG(current.getG() + 1);
					n.setParent(current);
				} else {
					n.setParent(current);
					n.setG(current.getG() + 1);
					n.setH(calcHeuristic(n, goalNode));
					open.add(n);
				}
			}
			current = null;
		}

		// the goal tile was not reached. There is no path.
		return null;
	}

	public int calcHeuristic(PathNode n, PathNode goal) {
		return Math.abs(n.getX() - goal.getX())
				+ Math.abs(n.getY() - goal.getY());
	}

	// appends the path into a list of headings by beginning at the goal node
	// and recursively traversing through parent nodes until the start node is
	// reached
	public ArrayList<Map.Dir> findDirPath(PathNode end) {
		if (end == null)
			return null;
		ArrayList<Map.Dir> heading = new ArrayList<Map.Dir>();
		PathNode parent = end.getParent();
		if (parent != null) {
			heading = findDirPath(end.getParent());
			heading.add(end.getDirFrom(parent));
		}
		return heading;
	}

	public ArrayList<Tile> findTilePath(PathNode end) {
		if (end == null)
			return null;
		ArrayList<Tile> heading = new ArrayList<Tile>();
		PathNode parent = end.getParent();
		if (parent != null) {
			heading = findTilePath(end.getParent());
			heading.add(end.getTile());
		}
		return heading;
	}

	// returns a list of all characters adjacent to this entity, if any
	public ArrayList<Character> getAdjacentCharacters() {
		ArrayList<Character> list = new ArrayList<Character>();
		for (Tile t : loc.getAllAdjacent().values()) {
			MapEntity e = t.getEntity();
			if (e instanceof Character)
				list.add((Character) e);
		}
		return list;
	}

	// ------------------------------ Getters and setters ------------------------------------

	public Tile getLoc() {
		return loc;
	}

	public void setLoc(Tile loc) {
		this.loc = loc;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public Map.Dir getDir() {
		return dir;
	}

	public void setDir(Map.Dir dir) {
		this.dir = dir;
	}

	public void setMoveSpeed(float val) {
		moveSpeed = val;
	}

	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public boolean reachedDest() {
		return reachedDest;
	}
}
