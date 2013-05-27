package redsun;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import redsun.entities.Map;

import com.thoughtworks.xstream.XStream;

public class MapLoader {
	private final String mapDir = "./src/Maps/";

	// holds an instance of every map in the game
	private HashMap<String, Map> maps;
	private XStream xs;
	
	// ------------------------------- Constructor -------------------------------

	public MapLoader() {
		maps = new HashMap<String, Map>();
		xs = new XStream();

		File dir = new File(mapDir);
		loadAllMaps(dir);
	}

	// ------------------------------- Methods -------------------------------

	// loads all maps specified in maps.xml into a map
	private void loadAllMaps(File file) {
		String fname = file.getName();
		if (file.isFile()) {
			loadMap(fname);
		} else if (file.isDirectory()) {
			File[] files = file.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String fname) {
					return fname.endsWith(".map") || fname.endsWith(".xml");
				}
			});
			if (files != null)
				for (File f : files)
					loadAllMaps(f);
			else
				System.out.println("File directory " + file + " is empty.");
		}
	}

	//loads a specific map file
	private void loadMap(String fname) {
		try {
			Map map = (Map) xs.fromXML(new File(mapDir + fname));
			map.linkAll();
			// id or filename??
			maps.put(fname, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//returns the map object with the specified id
	public Map getMap(String id) {
		return maps.get(id);
	}

	//returns a map of all maps
	public HashMap<String, Map> getAllMaps() {
		return maps;
	}
}
