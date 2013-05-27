package redsun.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.thoughtworks.xstream.XStream;

import redsun.entities.Character.Class;
import redsun.entities.Inventory;
import redsun.entities.Item;
import redsun.entities.SkillTree;
import redsun.entities.Character;
import redsun.entities.Weapon;
import redsun.resources.ImageLoader;

public class EECharacterPanel extends JPanel implements ActionListener{
  
  private XStream xs;
  private ImageLoader iLoader;
  private int pWidth = 756, pHeight = 540;
  private final int insetW = 5;
  private final String charFile = "characters.xml";
  private final String itemFile = "items.xml";
  
  //a map of all characters in the game in alphabetical order
  private TreeMap<String, Character> characters = new TreeMap<>();
  //a map of all items in the game in alphabetical order
  private TreeMap<String, Item> items = new TreeMap<>();
  
  private JList<String> charListField;
  private String portraitId;
  private JTextField nameField;
  private JSpinner levelField;
  private JSpinner expField;
  private JSpinner goldField;
  private TreeMap<Character.Stat, JSpinner> statsField = new TreeMap<>();
  private TreeMap<Character.Class, JRadioButton> classField = new TreeMap<>();
  private JList<String> inventoryField;
  
  /*
   * Constructor for Character editor panel
   */
  public EECharacterPanel() {
    iLoader = new ImageLoader();
    xs = new XStream();
    
    try {
      characters = (TreeMap<String, Character>)xs.fromXML(new File(charFile));
      items = (TreeMap<String, Item>)xs.fromXML(new File(itemFile));
    } catch (Exception e) {
    	e.printStackTrace();
    }

    setPreferredSize(new Dimension(pWidth, pHeight));
    setLayout(new GridBagLayout());
    makePanel();
  
    charListField.setSelectedIndex(0);
  }
  
  /*
   * Creates and initializes all components of the character panel
   */
  public void makePanel() {    
    //character selection list
    String[] charNames = new String[characters.size()];
    int i = 0;
    for(String s: characters.keySet()) {
      charNames[i] = s;
      i++;
    }
    JList<String> charList = new JList<>(charNames);
    charList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					switchChar();
				}
      }
    });
    charListField = charList;
    JScrollPane charListScroll = new JScrollPane(charList);
    charListScroll.setPreferredSize(new Dimension(pWidth/5, pHeight*7/8));
    charListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    
    //edits character potrait
    portraitId = "Default";
    JPanel portraitPanel = new JPanel();
    portraitPanel.setBorder(BorderFactory.createTitledBorder("Portrait"));
		JPanel pic = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(iLoader.getPortraits().get(portraitId), 0, 0, 150, 150, null);
			}
		};
    pic.setPreferredSize(new Dimension(150, 150));	
    JButton changePortrait = new JButton("Change");
    changePortrait.setActionCommand("changePortrait");
    portraitPanel.setLayout(new BoxLayout(portraitPanel, BoxLayout.Y_AXIS));
    portraitPanel.add(pic);
    portraitPanel.add(changePortrait);
    
    //edits name, level, gold, and experience
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBorder(BorderFactory.createTitledBorder("Info"));
    
    JTextField name = new JTextField("Default", 15);
    addLabeledComponent(infoPanel, name, "Character Name: ");
    nameField = name;
    
    JSpinner levelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    addLabeledComponent(infoPanel, levelSpinner, "Level: ");
    levelField = levelSpinner;
    
    JSpinner goldSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    addLabeledComponent(infoPanel, goldSpinner, "Gold: ");
    goldField = goldSpinner;

    JSpinner expSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    addLabeledComponent(infoPanel, expSpinner, "Experience: ");
    expField = expSpinner;
    
    //edits class
    JPanel classPanel = new JPanel();
    classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.Y_AXIS));
    classPanel.setBorder(BorderFactory.createTitledBorder("Class: "));
    ButtonGroup group = new ButtonGroup();
    for(Character.Class c: Character.Class.values()) {
      JRadioButton rb = new JRadioButton(c.toString());
      group.add(rb);
      classPanel.add(rb);
      classField.put(c, rb);
      if(c == Character.Class.CITIZEN)
	rb.setSelected(true);
    }
  
    //edits stats
    JPanel statsPanel = new JPanel();
    statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
    statsPanel.setBorder(BorderFactory.createTitledBorder("Stats: "));
    for(Character.Stat s: Character.Stat.values()) {
      JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
      addLabeledComponent(statsPanel, spinner, s + ": ");
      statsField.put(s, spinner);
    }
    
    //edits inventory
    JPanel inventoryPanel = new JPanel();
    JList<String> inventoryList = new JList<>(new DefaultListModel<String>());
    inventoryField = inventoryList;
    JScrollPane inventoryScroll = new JScrollPane(inventoryList);
    inventoryScroll.setPreferredSize(new Dimension(pWidth/5, pHeight/2 - insetW));
    inventoryScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    JButton addItem = new JButton("Add Item");
    addItem.setActionCommand("addItem");
    addItem.addActionListener(this);
    JButton remItem = new JButton("Remove Item");
    remItem.setActionCommand("remItem");
    remItem.addActionListener(this);
    inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
    inventoryPanel.add(inventoryScroll);
    inventoryPanel.add(addItem);
    inventoryPanel.add(remItem);
    
    //system buttons
    JPanel buttonPanel = new JPanel();
    JButton newChar = new JButton("New Character");
    newChar.setActionCommand("new");
    newChar.addActionListener(this);
    
    JButton save = new JButton("Add to List");
    save.setActionCommand("add");
    save.addActionListener(this);
    
    JButton load = new JButton("Export to File");
    load.setActionCommand("export");
    load.addActionListener(this);
    
    JButton delete = new JButton("Remove from List");
    delete.setActionCommand("remove");
    delete.addActionListener(this);
    
    buttonPanel.add(newChar);
    buttonPanel.add(save);
    buttonPanel.add(load);
    buttonPanel.add(delete);
    
    //add all components to tab panel
    addGridComponent(this, charListScroll, 0, 0, 1, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, portraitPanel, 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, classPanel, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, infoPanel, 2, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, statsPanel, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, inventoryPanel, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, buttonPanel, 0, 2, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

  //inventory
  //skills  
    
//    newChar();
  }
  
  //cause its used a lot
  public void addLabeledComponent(JPanel parent, Component c, String str) {
    JLabel label = new JLabel(str);
    label.setLabelFor(c);
    
    parent.add(label);
    parent.add(c);
  }
  
  public void addGridComponent(Container parent, Component c, int x, int y, int gridW, int gridH,
      int anchor, int fill) {
    GridBagConstraints gc = new GridBagConstraints();
    gc.anchor = anchor;
    gc.weightx = 1.0;
    gc.weighty = 1.0;
    gc.fill = fill;
    gc.gridheight = gridH;
    gc.gridwidth = gridW;
    gc.gridx = x;
    gc.gridy = y;
    parent.add(c, gc);
  }

  @Override
  /*
   * Determines the functions of each button command
   */
  public void actionPerformed(ActionEvent e) {
    //dialogue for "save?"
    switch(e.getActionCommand()) {
    case "new":
      newChar();
      break;
    case "add":
      addChar();
      break;
    case "export":
      export();
      break;
    case "remove":
      removeChar(charListField.getSelectedValue());
      break;
    case "addItem":
      addNewItem(new Weapon("BIG ASS SWORD", Weapon.WepClass.SWORD));
      break;
    case "remItem":
      removeItem(items.get(inventoryField.getSelectedValue()));
      break;
    case "changePortrait":
      changePortrait();
      break;
    }
  }
  
  //creates a new character with default settings
  private void newChar() {
    portraitId = "Default";
    nameField.setText("Default");
    classField.get(Character.Class.CITIZEN).setSelected(true);
    levelField.setValue(0);
    goldField.setValue(0);
    expField.setValue(0);
    for (Character.Stat s: Character.Stat.values())
      statsField.get(s).setValue(0);
    Inventory inventory = new Inventory();
//    Weapon wep = new Weapon("Template Weapon", Weapon.WepClass.ANIMUS);
//    String[] wepId = {wep.getId()};
//    
//    inventoryField.setListData(wepId);
    
    SkillTree skills = null;    
  }
  
  //saves the character into the character configuration file
  private void addChar() {
    //change - character needs this as a field
    String name = nameField.getText();
    Character.Class charClass = Character.Class.CITIZEN;
    for(Character.Class c: Character.Class.values()) {
      if(classField.get(c).isSelected())
      	charClass = c;
    }
    int level = (int)levelField.getValue();
    int gold = (int)goldField.getValue();
    int exp = (int)expField.getValue();
    HashMap<Character.Stat, Integer> stats = new HashMap<>();
    for (Character.Stat s : Character.Stat.values()) {
      stats.put(s, (int)statsField.get(s).getValue());
    }
    Inventory inventory = new Inventory();
    for(int i = 0; i < inventoryField.getModel().getSize(); i++) {
      String itemId = inventoryField.getModel().getElementAt(i);
      inventory.add(items.get(itemId));
    }
    
    SkillTree skills = null;

    if(characters.get(name) == null) {
      Character c = new Character(name, charClass, inventory,
		stats, skills, level, exp, gold, 0, 0);
      c.setImgId(portraitId);
    characters.put(name, c);
    }
    else
      System.out.println("Character already exists. Change the name, you stupid ho.");
    
    refreshCharList();
  }
  
  /*
   * Loads the selected character's information
   * If the selection is null, loads the default character
   */
  private void loadChar(String name) {
    if(name == null || characters.isEmpty())
      newChar();
    else {
      Character c = characters.get(name);
      if(c != null) {
        nameField.setText(c.getId());
        classField.get(c.getCharClass()).setSelected(true);	
        levelField.setValue(c.getLevel());
        goldField.setValue(c.getGold());
        expField.setValue(c.getExp());
        for (Character.Stat s: Character.Stat.values())
          statsField.get(s).setValue(c.getStat(s));
        if(c.getInventory() != null) {
					// change - must add for each category
					ArrayList<String> list = c.getInventory().getItems();
					String[] items = new String[list.size()];
					for (int i = 0; i < list.size(); i++) {
						items[i] = list.get(i);
					}
					inventoryField.setListData(items);
				}
			}
		}
	}
  
  private void removeChar(String name) {
    if(name != null) {
      characters.remove(name);
      refreshCharList(); 
      charListField.setSelectedIndex(0);
    }
  }
  
  /*
   * Exports the character data to file
   * change - should do this when user selects "save"
   */
  private void export() {
    String xml = xs.toXML(characters);
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(charFile));
      bw.write(xml);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void switchChar() {
    loadChar(charListField.getSelectedValue());
  }
  
  private void refreshCharList() {
    String[] newList = new String[characters.size()];
    int i = 0;
    for(String s: characters.keySet()) {
      newList[i] = s;
      i++;
    }
    charListField.setListData(newList);
  }  
  
  private void addNewItem(Item item) {
    Inventory inventory = characters.get(charListField.getSelectedValue()).getInventory();
    inventory.add(item);
    refreshItemList(inventory);
  }
  
  private void removeItem(Item item) {

    Inventory inventory = characters.get(charListField.getSelectedValue()).getInventory();

    inventory.remove(item);
    inventoryField.setSelectedIndex(0);

    refreshItemList(inventory);
  }
  
  private void refreshItemList(Inventory inventory) {
    if(inventory != null) {
    ArrayList<String> list = inventory.getItems();
	  String[] items = new String[list.size()];
	  for (int i = 0; i < list.size(); i++) {
	    items[i] = list.get(i);
	  }
	inventoryField.setListData(items);
    }
  }
  
  private void changePortrait() {
    
  }
}
