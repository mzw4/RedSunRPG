package redsun.resources;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

import redsun.MapEntity;
import redsun.entities.Tile;

/*
 * Loads all images as compatible buffered images
 * change - maybe make separate lists for tile, map object, and characters
 */
public class ImageLoader {
	private final String imgDir = "./src/Images/";
	private final String animatedKeyword = "a_";

	private enum ImgType {
		TILES, MAP_OBJECT, ANIMATED_MAP_OBJECT, ANIMATED_ACTOR, PORTRAIT, BACKDROP, COMBAT, UI
	}

	private GraphicsConfiguration gc;

	// current directory the loader is searching through
	private String curDir;

	// contains all animated images used in-game - key: filename, value: array of
	// images
	// private HashMap<String, ArrayList<BufferedImage>> aImgs;
	// contains all static images used in-game - key: filename, value: image
	// private HashMap<String, BufferedImage> imgs;

	// treemap yeah?? its sorted..!
	private TreeMap<String, BufferedImage> tiles = new TreeMap<>();
	private TreeMap<String, BufferedImage> mapObj = new TreeMap<>();
	private TreeMap<String, ArrayList<BufferedImage>> aMapObj = new TreeMap<>();
	private TreeMap<String, ArrayList<BufferedImage>> actors = new TreeMap<>();
	private TreeMap<String, BufferedImage> portraits = new TreeMap<>();
	private TreeMap<String, BufferedImage> itemImgs = new TreeMap<>();
	private TreeMap<String, BufferedImage> backdrops = new TreeMap<>();
	private TreeMap<String, ArrayList<BufferedImage>> combat = new TreeMap<>();
	private TreeMap<String, BufferedImage> ui = new TreeMap<>();

	public ImageLoader() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

		loadAllImages(new File(imgDir));
	}

	/*
	 * Loads all game images Recursively searches through directories starting
	 * with the specified path. Loads any image files it encounters
	 */
	public void loadAllImages(File file) {
		String fname = file.getName();
		if (file.isFile()) {
			curDir = file.getParentFile().getName();
			if (isAnimated(fname))
				loadAnimatedImg(file);
			else
				loadImg(file);
		} else if (file.isDirectory()) {
			curDir = file.getName();
			File[] files = file.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String fname) {
					return fname.endsWith(".png") || fname.endsWith(".bmp") || fname.endsWith(".jpg")
							|| fname.endsWith(".gif") || dir.isDirectory();
				}
			});
			if (files != null)
				for (File f : files)
					loadAllImages(f);
			else
				System.out.println("File directory " + file + " is empty.");
		}
	}

	// checks to see if the file contains "_sheet", indicating it is a sheet image
	private boolean isAnimated(String fname) {
		return fname.length() >= animatedKeyword.length() && fname.startsWith(animatedKeyword) ? true
				: false;
	}

	// loads a sheet image, splits it into frames, and puts them into the aImgs
	// hash map
	private void loadAnimatedImg(File file) {
		ArrayList<BufferedImage> frames = new ArrayList<>();

		try {
			BufferedImage aImg = ImageIO.read(file);
			BufferedImage compIm = null;
			int transparency = aImg.getColorModel().getTransparency();
			compIm = gc.createCompatibleImage(aImg.getWidth(), aImg.getHeight(), transparency);
			Graphics2D g2d = compIm.createGraphics();
			g2d.drawImage(aImg, 0, 0, null);

			int xmax = aImg.getWidth() / Tile.width;
			int ymax = aImg.getHeight() / Tile.height;
			for (int j = 0; j < ymax; j++)
				for (int i = 0; i < xmax; i++)
					frames.add(aImg.getSubimage(i * Tile.width, j * Tile.height, Tile.width, Tile.height));

		} catch (IOException e) {
			e.printStackTrace();
		}

		String fname = file.getName().replaceFirst("[.][^.]+$", "");
		if (!frames.isEmpty()) {
			if (curDir.equals("Animated_Map_Objects"))
				aMapObj.put(fname, frames);
			else if (curDir.equals("Actors"))
				actors.put(fname, frames);
		}
	}

	// loads a single image and puts it into the imgs hash map
	// change to use compatible image thing
	private void loadImg(File file) {
		BufferedImage im = null;
		BufferedImage compIm = null;

		try {
			// reads image and creates an copy compatible with current display
			// settings
			im = ImageIO.read(file);
			int transparency = im.getColorModel().getTransparency();
			compIm = gc.createCompatibleImage(im.getWidth(), im.getHeight(), transparency);
			Graphics2D g2d = compIm.createGraphics();
			g2d.drawImage(im, 0, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String fname = file.getName().replaceFirst("[.][^.]+$", "");
		if (compIm != null) {
			switch(curDir) {
			case "Tiles":
				tiles.put(fname, compIm);
				break;
			case "Actors":
				ArrayList<BufferedImage> a = new ArrayList<>();
				a.add(compIm);
				actors.put(fname, a);
				break;
			case "Map_Objects":
				mapObj.put(fname, compIm);
				break;
			case "Portraits":
				portraits.put(fname, compIm);
				break;
			case "Items":
				itemImgs.put(fname, compIm);
				break;
			case "Backdrops":
				backdrops.put(fname, compIm);
				break;
			case "Combat":
				ArrayList<BufferedImage> a2 = new ArrayList<>();
				a2.add(compIm);
				combat.put(fname, a2);
				break;
			case "UI":
				ui.put(fname, compIm);
				break;
			default:
				break;				
			}
			
//			if (curDir.equals("Tiles"))
//				tiles.put(fname, compIm);
//			else if (curDir.equals("Actors")) {
//				ArrayList<BufferedImage> a = new ArrayList<>();
//				a.add(compIm);
//				actors.put(fname, a);
//			} else if (curDir.equals("Map_Objects"))
//				mapObj.put(fname, compIm);
//			else if (curDir.equals("Portraits"))
//				portraits.put(fname, compIm);
//			else if (curDir.equals("Backdrops"))
//				backdrops.put(fname, compIm);
//			else if (curDir.equals("Combat"))
//				mapObj.put(fname, compIm);
//			else if (curDir.equals("UI"))
//				ui.put(fname, compIm);
		}
	}

	// searches through every image list for the specified one and returns its
	// BufferedImage
	// change - should there just be another list for every single image? depends
	// on how often this needs to be used
	public BufferedImage getImg(String id) {
		if (tiles.keySet().contains(id))
			return tiles.get(id);
		else if (mapObj.keySet().contains(id))
			return mapObj.get(id);
		else if (portraits.keySet().contains(id))
			return portraits.get(id);
		else if (backdrops.keySet().contains(id))
			return backdrops.get(id);
		else if (ui.keySet().contains(id))
			return ui.get(id);
		return null;
	}

	// searches through every animated image list for the specified one and
	// returns its BufferedImage list
	public ArrayList<BufferedImage> getAImg(String id) {
		if (aMapObj.keySet().contains(id))
			return aMapObj.get(id);
		else if (actors.keySet().contains(id))
			return actors.get(id);
		else if (combat.keySet().contains(id))
			return combat.get(id);
		return null;
	}

	// ------------------------------ Getters and Setters
	// ------------------------------------

	public TreeMap<String, ArrayList<BufferedImage>> getActors() {
		return actors;
	}

	public TreeMap<String, BufferedImage> getTiles() {
		return tiles;
	}

	public TreeMap<String, BufferedImage> getMapObj() {
		return mapObj;
	}

	public TreeMap<String, ArrayList<BufferedImage>> getaMapObj() {
		return aMapObj;
	}

	public TreeMap<String, BufferedImage> getPortraits() {
		return portraits;
	}
	
	public TreeMap<String, BufferedImage> getItemImgs() {
		return itemImgs;
	}

	public TreeMap<String, BufferedImage> getBackdrops() {
		return backdrops;
	}

	public TreeMap<String, ArrayList<BufferedImage>> getCombat() {
		return combat;
	}

	public TreeMap<String, BufferedImage> getUI() {
		return ui;
	}
}
