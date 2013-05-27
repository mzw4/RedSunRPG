package redsun.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import redsun.MapLoader;
import redsun.entities.Map;
import redsun.entities.Tile;
import redsun.resources.ImageLoader;

import com.thoughtworks.xstream.XStream;

/*
 * Launches a tile based map editor that can load and save maps designed for Red Sun RPG
 */
public class MapEditor extends JFrame {

	// location of map files
	private final String mapDir = "./src/Maps/";

	private Container contentPane;
	private MapPanel mp;
	private TilePanel tp;
	private JScrollPane mScroll;
	private JScrollPane tScroll;
	private JTextArea textPanel;
	JList<SelectionType> list;

	// default dimensions for panel windows
	private final int pHeight = 2 * Tile.height;
	private final int mScrollWidth = 21 * Tile.width, mScrollHeight = 15 * Tile.height;
	private final int tScrollWidth = 5 * Tile.width, tScrollHeight = mScrollHeight - pHeight;
	private final int barSize = (int) UIManager.get("ScrollBar.width");
	// length of dash and dash space for grid lines
	private final int dSize = 1;
	private final int dSpace = 2;
	// number of tiles to buffer the edge of the visible area
	private final int tileBuffer = 2;

	private ImageLoader iLoader;
	private MapLoader mLoader;
	private XStream xs;

	private enum SelectionType {
		TILE, MAP_OBJECT, ACTOR, TRIGGER
	}

	// current selection type for drawing
	private SelectionType selection;

	// contains all tile images
	// key: tile ID, object: tile image
	private TreeMap<String, BufferedImage> tileImgs;
	private TreeMap<String, ArrayList<BufferedImage>> actorImgs;
	private TreeMap<String, BufferedImage> mapObjImgs;

	public MapEditor() {
		super("Red Sun Map Editor");
		contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		iLoader = new ImageLoader();
		mLoader = new MapLoader();
		xs = new XStream();
		tileImgs = iLoader.getTiles();
		tileImgs.put("Eraser", null);
		actorImgs = iLoader.getActors();
		System.out.println(actorImgs);
		mapObjImgs = iLoader.getMapObj();

		initMenu();
		initUI();

		contentPane.setFocusable(true);
		contentPane.requestFocus();

		contentPane.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				handleKeyPress(e);
			}
		});

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setVisible(true);
	}

	// ------------------------------ MapEditor GUI Components
	// ------------------------------------
	private void initMenu() {
		// set UI to Windows look and feel
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		// menu stuff
		// change- lots of duplicate code here
		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu options = new JMenu("Options");
		menu.add(file);
		menu.add(options);

		/*
		 * creates a dialog allowing for the creation of a new map with the
		 * specified dimensions
		 */
		JMenuItem newMap = new JMenuItem("New");
		newMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField wInput = new JTextField();
				JTextField hInput = new JTextField();
				JTextField idInput = new JTextField();

				Object[] inputs = new Object[] { "New Map Dimensions: ", new JLabel("Map name:"), idInput,
						new JLabel("Width: "), wInput, new JLabel("Height: "), hInput };
				JOptionPane jop = new JOptionPane("New Map Dimensions: ", JOptionPane.PLAIN_MESSAGE,
						JOptionPane.OK_CANCEL_OPTION);
				jop.setMessage(inputs);
				JDialog dialog = jop.createDialog(mScroll, null);
				dialog.setVisible(true);
				Object result = jop.getValue();
				if (result != null) {
					int answer = (int) jop.getValue();
					String id = idInput.getText();
					if (id.length() == 0)
						id = "Default";
					String w = wInput.getText();
					String h = hInput.getText();
					if (answer == 0) {
						if (w == null || h == null)
							textPanel.append("Gotta give it dimensions bro!\n");
						else
							try {
								int wval = Integer.parseInt(w);
								int hval = Integer.parseInt(h);
								mp.makeNewMap(id, wval, hval);
								textPanel.append("New map '" + id + "' created! Width: " + w + " Height: " + h
										+ "\n");
							} catch (NumberFormatException ex) {
								textPanel.append("Invalid input format\n");
							}
					}
				}
			}
		});

		/*
		 * saves file, if no file path currently exists, prompts to save as
		 */
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mp.getFilePath() == null) {
					JFileChooser fc = new JFileChooser(mapDir);
					int returnVal = fc.showSaveDialog(mScroll);
					if (returnVal == JFileChooser.APPROVE_OPTION)
						mp.saveMap(fc.getSelectedFile(), true);
				} else
					mp.saveMap(new File(mp.getFilePath()), false);
			}
		});

		/*
		 * saves files as the specified file name
		 */
		JMenuItem saveAs = new JMenuItem("Save As");
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(mapDir);
				int returnVal = fc.showSaveDialog(mScroll);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mp.saveMap(fc.getSelectedFile(), true);
				}
			}
		});

		/*
		 * loads the selected map file
		 */
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(mapDir);
				int returnVal = fc.showOpenDialog(mScroll);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mp.loadMap(fc.getSelectedFile());
				}
			}
		});

		file.add(newMap);
		file.add(save);
		file.add(saveAs);
		file.add(load);

		JMenuItem changeName = new JMenuItem("Change Map Name");
		changeName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Current Map Name: " + mp.getId()
						+ "\nPlease Enter New Name: ");
				if (name != null) {
					textPanel.append("Successfully changed map name '" + mp.getId() + "' to '" + name + "'\n");
					mp.setId(name);
				}
			}
		});

		options.add(changeName);

		setJMenuBar(menu);
	}

	// initialize UI
	private void initUI() {
		// initialize panels with default sizes
		mp = new MapPanel("Default", mScrollWidth / Tile.width, mScrollHeight / Tile.height);
		tp = new TilePanel(tScrollWidth / Tile.width, tScrollHeight / Tile.height);

		// create scroll panes for panel components
		tScroll = new JScrollPane(tp);
		mScroll = new JScrollPane(mp);
		mScroll.setPreferredSize(new Dimension(mScrollWidth + barSize, mScrollHeight + barSize));
		mScroll.getVerticalScrollBar().setUnitIncrement(16);
		tScroll.setPreferredSize(new Dimension(tScrollWidth + barSize, tScrollHeight + barSize));
		tScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tScroll.getVerticalScrollBar().setUnitIncrement(16);

		// create tile selection type list
		SelectionType[] data = { SelectionType.TILE, SelectionType.ACTOR, SelectionType.MAP_OBJECT,
				SelectionType.TRIGGER };
		list = new JList<>(data);
		JScrollPane lScroll = new JScrollPane(list);
		lScroll.setPreferredSize(new Dimension(tScrollWidth + barSize, pHeight));
		lScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					tp.setSelectionType(e.getFirstIndex());
					contentPane.requestFocus();
				}
			}
		});

		// create text panel to display notifications
		textPanel = new JTextArea();
		JScrollPane tpScroll = new JScrollPane(textPanel);
		tpScroll.setPreferredSize(new Dimension(mScrollWidth + tScrollWidth + 2 * barSize, pHeight));
		textPanel.setEditable(false);

		// define UI layout for panel components
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		contentPane.add(mScroll, c);

		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 1;
		c.gridy = 0;
		contentPane.add(lScroll, c);

		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.gridx = 1;
		c.gridy = 1;
		contentPane.add(tScroll, c);

		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 2;
		contentPane.add(tpScroll, c);
	}

	private void handleKeyPress(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_Z)
			mp.undo(true);
		if (key == KeyEvent.VK_X)
			mp.undo(false);
		if (key == KeyEvent.VK_F)
			mp.fillAll();
		mp.repaint();
	}

	// ------------------------------ MapPanel class
	// ------------------------------------
	public class MapPanel extends JPanel {
		// the entire absolute path of the file
		private String filePath;
		private String id;
		private int mWidth;
		private int mHeight;

		private int mouseX;
		private int mouseY;

		private HashMap<String, Boolean> collision;
		// contains all tiles placed on the map panel
		// key: coordinate location, value: tile type
		private HashMap<Point, String> tiles = new HashMap<>();
		// current type being painted in the map panel
		private String curType;
		// holds a stack of all actions for undoing
		private Stack<HashMap<Point, String>> actions = new Stack<>();

		public MapPanel(String id, int w, int h) {
			makeNewMap(id, w, h);
			XStream xs = new XStream();
			try {
				FileInputStream fis = new FileInputStream("walkable.xml");
				collision = (HashMap<String, Boolean>) xs.fromXML(fis);
			} catch (Exception e) {
				e.printStackTrace();
			}

			addMouseListener(new MouseInputAdapter() {
				public void mousePressed(MouseEvent e) {
					handleMousePress(e);
				}
			});

			addMouseMotionListener(new MouseInputAdapter() {
				public void mouseMoved(MouseEvent e) {
					handleMouseMove(e);
				}

				public void mouseDragged(MouseEvent e) {
					handleMousePress(e);
				}
			});
		}

		// creates a new empty map of the specified id and dimensions
		public void makeNewMap(String id, int w, int h) {
			// System.out.println(Runtime.getRuntime().totalMemory());
			// System.out.println(Runtime.getRuntime().freeMemory());
			// System.out.println(Runtime.getRuntime().maxMemory());

			this.id = id;
			mWidth = w;
			mHeight = h;
			tiles.clear();
			actions.empty();
			filePath = null;
			setPreferredSize(new Dimension(mWidth * Tile.width, mHeight * Tile.height));
			if (mScroll != null)
				mScroll.revalidate();
		}

		// creates a Map object corresponding to the tiles placed in the editor
		private void saveMap(File file, boolean checkOverwrite) {
			String fPath = file.getAbsolutePath();
			filePath = fPath;

			Tile[][] tileSet = new Tile[mWidth][mHeight];
			for (Point p : tiles.keySet()) {
				int x = (int) p.getX();
				int y = (int) p.getY();
				boolean walkable;
				if (collision.get(tiles.get(p)) != null) {
					walkable = collision.get(tiles.get(p));
				} else
					walkable = true;
				tileSet[x][y] = new Tile(x, y, tiles.get(p).replaceFirst("[.][^.]+$", ""), walkable);
			}
			Map map = new Map(id, tileSet);

			// checks if the specified file path already exist
			boolean alreadyExists = false;
			if (checkOverwrite) {
				File dir = file.getParentFile();
				File[] files;
				if (dir != null) {
					files = dir.listFiles();
					if (files != null)
						for (File f : files)
							if (f.getAbsolutePath().equals(fPath))
								alreadyExists = true;
				}
			}
			// if the file path already exists, prompts for overwrite approval
			boolean doOutput = false;
			if (alreadyExists) {
				int overwrite = JOptionPane.showConfirmDialog(mScroll,
						"Are you sure you want to overwrite?", "Confirm Overwrite", JOptionPane.OK_OPTION);
				if (overwrite == JFileChooser.APPROVE_OPTION)
					doOutput = true;
			} else
				doOutput = true;
			if (doOutput) {
				outputMap(map, fPath);
				textPanel.append("Succesfully saved map '" + map.getId() + "'\n");
			}
		}

		// outputs the Map created in the editor to a file
		private void outputMap(Map map, String fPath) {
			try {
				String xml = xs.toXML(map);
				BufferedWriter bw = new BufferedWriter(new FileWriter(fPath));
				bw.write(xml);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// loads a map of the specified filePath
		private void loadMap(File file) {
			HashMap<Point, String> loadedMap = new HashMap<>();
			String fPath = file.getAbsolutePath();
			mLoader = new MapLoader();
			Map map = mLoader.getMap(file.getName());

			// if a Map object was successfully loaded from file, create a
			// representation in the editor
			if (map != null) {
				textPanel.append("Map '" + map.getId() + "' loaded!\n");
				makeNewMap(map.getId(), map.getWidth(), map.getHeight());
				for (int i = 0; i < map.getWidth(); i++) {
					for (int j = 0; j < map.getHeight(); j++) {
						Tile t = map.getTileAt(i, j);
						if (t != null)
							loadedMap.put(new Point(i, j), t.getType());
					}
				}
				tiles = loadedMap;
			} else
				textPanel.append("No map loaded. Map file invalid.\n");
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			// calculate viewpoint coordinates within the scroll pane
			int viewX = (int) mScroll.getViewport().getViewPosition().getX() / Tile.width * Tile.width;
			int viewY = (int) mScroll.getViewport().getViewPosition().getY() / Tile.height * Tile.height;
			int viewX2 = (int) mScroll.getViewport().getExtentSize().getWidth() / Tile.width * Tile.width
					+ viewX + tileBuffer * Tile.width;
			int viewY2 = (int) mScroll.getViewport().getExtentSize().getHeight() / Tile.height
					* Tile.width + viewY + tileBuffer * Tile.height;

			// fill in background
			g2d.setColor(Color.lightGray);
			g2d.fillRect(viewX, viewY, viewX2, viewY2);

			// draws panel elements
			drawVisibleTiles(g2d, viewX / Tile.width, viewY / Tile.height, viewX2 / Tile.width, viewY2
					/ Tile.height);
			drawGrid(g2d, viewX, viewY, viewX2, viewY2);
			highlightTile(g2d, mouseX, mouseY);

			g2d.dispose();
		}

		// for each point that has been painted, draws tile image onto map
		private void drawVisibleTiles(Graphics2D g, int x, int y, int x2, int y2) {
			for (int i = x; i < x2; i++)
				for (int j = y; j < y2; j++) {
					String type = tiles.get(new Point(i, j));
					if(type != null) {
						if (tileImgs.get(type) != null)
							g.drawImage(tileImgs.get(type), i * Tile.width, j * Tile.height, Tile.width,
									Tile.height, null);
						else if (actorImgs.get(type) != null)
							g.drawImage(actorImgs.get(type).get(0), i * Tile.width, j * Tile.height, Tile.width,
									Tile.height, null);
						else if (mapObjImgs.get(type) != null)
							g.drawImage(mapObjImgs.get(type), i * Tile.width, j * Tile.height, Tile.width,
									Tile.height, null);
					}
				}
		}

		// draws grid with dashed lines in the visible area
		// does not use Swing's setStroke for the dashes because this runs faster
		private void drawGrid(Graphics2D g, int x, int y, int x2, int y2) {
			g.setColor(new Color(60, 80, 200, 70));
			for (int i = x; i <= x2; i += Tile.width) {
				for (int j = y; j <= y2; j += dSize + dSpace) {
					g.drawLine(i, j, i, j + dSize);
				}
			}
			for (int i = x; i <= x2; i += dSize + dSpace) {
				for (int j = y; j <= y2; j += Tile.height) {
					g.drawLine(i, j, i + dSize, j);
				}
			}
		}

		// highlights the tile that the mouse is currently hovering over
		private void highlightTile(Graphics2D g2d, int x, int y) {
			g2d.setColor(new Color(60, 80, 200, 70));
			g2d.fillRect(x * Tile.width, y * Tile.height, Tile.width, Tile.height);
		}

		// replaces the tile at the coordinate with the specified one if it is
		// different
		// returns true if added, false otherwise
		private boolean addPoint(Point p, String type) {
			int x = (int) p.getX();
			int y = (int) p.getY();
			if (type != tiles.get(new Point(x, y)) && x < mWidth && x >= 0 && y < mHeight && y >= 0) {
				HashMap<Point, String> map = new HashMap<>();
				map.put(p, tiles.get(p));
				actions.add(map);
				tiles.remove(p);
				tiles.put(p, type);
				return true;
			}
			return false;
		}

		public void undo(boolean successive) {
			if (!actions.isEmpty()) {
				if (successive) {
					for (Point p : actions.peek().keySet())
						tiles.put(p, actions.pop().get(p));
				} else {
					for (Point p : actions.peek().keySet())
						addPoint(p, actions.peek().get(p));
				}
				repaint();
			}
		}

		public void fillAll() {
			for (int i = 0; i < mWidth; i++)
				for (int j = 0; j < mHeight; j++) {
					addPoint(new Point(i, j), curType);
				}
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setCurType(String type) {
			this.curType = type;
		}

		public int getMapWidth() {
			return mWidth;
		}

		public void setMapWidth(int mWidth) {
			this.mWidth = mWidth;
		}

		public int getMapHeight() {
			return mHeight;
		}

		public void setMapHeight(int mHeight) {
			this.mHeight = mHeight;
		}

		private void handleMousePress(MouseEvent e) {
			// gets tile coordinate of click
			mouseX = e.getX() / Tile.width;
			mouseY = e.getY() / Tile.height;
			addPoint(new Point(mouseX, mouseY), curType);
			repaint();
		}

		private void handleMouseMove(MouseEvent e) {
			// gets tile coordinate of click
			mouseX = e.getX() / Tile.width;
			mouseY = e.getY() / Tile.height;
			repaint();
		}
	}

	// ------------------------------ TilePanel class
	// ------------------------------------
	public class TilePanel extends JPanel {
		private int tWidth;
		private int tHeight;

		// current tile selection for drawing
		private HashMap<Point, String> types;

		public TilePanel(int w, int h) {
			super(new GridLayout(w, h));
			tWidth = w;
			tHeight = h;

			// default selection type
			selection = SelectionType.TILE;
			// key: coordinate on tile selection grid, value: tile type
			types = new HashMap<Point, String>();

			addMouseListener(new MouseInputAdapter() {
				public void mousePressed(MouseEvent e) {
					handleMousePress(e);
				}
			});
			tHeight = tileImgs.size() / tWidth;
			setPreferredSize(new Dimension(tWidth * Tile.height, tHeight * Tile.height));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			g.setColor(Color.gray);
			g.fillRect(0, 0, (tWidth + tileBuffer * 2) * Tile.width, (tHeight + tileBuffer * 2)
					* Tile.height);

			if (selection == SelectionType.TILE) {
				types.clear();
				// change - this is messy
				int x = 0, y = 0;
				for (String t : tileImgs.keySet()) {
					types.put(new Point(x, y), t);
					g2d.drawImage(tileImgs.get(t), x * Tile.width, y * Tile.height, 36, 36, null);
					if (x < tWidth - 1)
						x++;
					else {
						x = 0;
						y++;
					}
				}
			} else if (selection == SelectionType.ACTOR) {
				// change - this is messy
				types.clear();
				int x = 0, y = 0;
				for (String t : actorImgs.keySet()) {
					types.put(new Point(x, y), t);
					g2d.drawImage(actorImgs.get(t).get(0), x * Tile.width, y * Tile.height, 36, 36, null);
					if (x < tWidth - 1)
						x++;
					else {
						x = 0;
						y++;
					}
				}
			} else if (selection == SelectionType.MAP_OBJECT)
				;

			// draws dashed grid for tile panel
			g2d.setColor(new Color(60, 80, 200, 150));
			for (int i = 0; i <= tWidth * Tile.width; i += Tile.width) {
				for (int j = 0; j <= tHeight * Tile.height; j += dSize + dSpace) {
					g.drawLine(i, j, i, j + dSize);
				}
			}
			for (int i = 0; i <= tWidth * Tile.width; i += dSize + dSpace) {
				for (int j = 0; j <= tHeight * Tile.height; j += Tile.height) {
					g.drawLine(i, j, i + dSize, j);
				}
			}
		}

		public void setSelectionType(int index) {
			selection = list.getSelectedValue();
			repaint();
		}

		public void handleMousePress(MouseEvent e) {
			// gets tile coordinate of click
			int mouseX = e.getX();
			int mouseY = e.getY();

			mp.setCurType(types.get(new Point(mouseX / Tile.width, mouseY / Tile.height)));

			// every the panel is clicked, repaint to update selection change
			repaint();
		}
	}

	// ------------------------------ Main ------------------------------------
	public static void main(String[] args) {
		MapEditor m = new MapEditor();
	}
}
