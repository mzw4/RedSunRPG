package redsun.util;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import redsun.entities.Armor;
import redsun.entities.Character;
import redsun.entities.Item;
import redsun.entities.Weapon;
import redsun.resources.ImageLoader;

import com.thoughtworks.xstream.XStream;

/*
 * Item editor
 */
public class EEItemPanel extends JPanel implements ActionListener {
	
  private XStream xs;
  private ImageLoader iLoader;
  private int pWidth = 756, pHeight = 540;
  private final int insetW = 5;
	// location of map files
	private final String imgDir = "./src/Images/";
  private final String itemFile = "items.xml";
  
  //a map of all items in the game in alphabetical order
  private TreeMap<String, Item> items = new TreeMap<>();
  
  private JList<String> itemListField;
  private String portraitId;
  private JTextField idField;
  private JSpinner levelSpinner;
  private JSpinner valueSpinner;
  private TreeMap<Character.Stat, JSpinner> effects = new TreeMap<>();
  private JComboBox<Item.ItemType> type;
  private JPanel typePanel = new JPanel(new CardLayout());
  private JPanel portraitPanel = new JPanel();
  //weapon details
  private JSpinner attack;
  private JSpinner elementa;
  private JSpinner weight;
  private TreeMap<Weapon.WepClass, JRadioButton> wclasses = new TreeMap<>();
  //armor details
  private JSpinner armor;
  private JSpinner earmor;
  private JSpinner weight2;
  private TreeMap<Armor.BodyPart, JRadioButton> bparts = new TreeMap<>();

	private GraphicsConfiguration gc;

  public EEItemPanel () {
    iLoader = new ImageLoader();
    xs = new XStream();
    
    try {
      items = (TreeMap<String, Item>)xs.fromXML(new File(itemFile));
    } catch (Exception e) {
    	e.printStackTrace();
    }

    setPreferredSize(new Dimension(pWidth, pHeight));
    setLayout(new GridBagLayout());
    makePanel();
    newItem();
  
    if(itemListField.getFirstVisibleIndex() >= 0)
    	itemListField.setSelectedIndex(0);
  }
  
  /*
   * Creates and initializes all components of the Item panel
   */
  public void makePanel() {    
    //item selection list
    String[] itemNames = new String[items.size()];
    items.keySet().toArray(itemNames);
		JList<String> itemList = new JList<>(itemNames);
		itemList.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting())
							loadItem(itemListField.getSelectedValue());
					}
				});
    itemListField = itemList;
    JScrollPane itemListScroll = new JScrollPane(itemList);
    itemListScroll.setPreferredSize(new Dimension(pWidth/5, pHeight*7/8));
		itemListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    
    //edits item potrait
    portraitId = "DefaultItem";
    portraitPanel = new JPanel();
    portraitPanel.setBorder(BorderFactory.createTitledBorder("Portrait"));
		JPanel pic = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(iLoader.getItemImgs().get(portraitId), 0, 0, 150, 150, null);
			}
		};
    pic.setPreferredSize(new Dimension(150, 150));	
    JButton changePortrait = new JButton("Change");
    changePortrait.addActionListener(this);
    changePortrait.setActionCommand("changePortrait");
    portraitPanel.setLayout(new BoxLayout(portraitPanel, BoxLayout.Y_AXIS));
    portraitPanel.add(pic);
    portraitPanel.add(changePortrait);
    
    //edits id, type, level, and value
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBorder(BorderFactory.createTitledBorder("Info"));
    
    idField = new JTextField("Default", 15);
    type = new JComboBox<Item.ItemType>(Item.ItemType.values());
    type.addItemListener(new ItemListener() {
    	 public void itemStateChanged(ItemEvent evt) {
         CardLayout cl = (CardLayout)(typePanel.getLayout());
         System.out.println(evt.getItem().toString());
         cl.show(typePanel, evt.getItem().toString());
    	 }
    });
    levelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    valueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    
    addLabeledComponent(infoPanel, idField, "Item id: ");
    addLabeledComponent(infoPanel, type, "Item Type");
    addLabeledComponent(infoPanel, levelSpinner, "Use Level: ");
    addLabeledComponent(infoPanel, valueSpinner, "Value: "); 
    
    //edits the effects of items
    JPanel effectsPanel = new JPanel();
    effectsPanel.setLayout(new BoxLayout(effectsPanel, BoxLayout.Y_AXIS));
    effectsPanel.setBorder(BorderFactory.createTitledBorder("Item Effects"));
    
    for(Character.Stat s: Character.Stat.values()) {
    	JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
      addLabeledComponent(effectsPanel, spinner, s.toString());
      effects.put(s, spinner);
    }
    
    //edits specific details for each item type
    typePanel.setBorder(BorderFactory.createTitledBorder("Item Type Details"));
    
    //weapon specific details
    JPanel wep = new JPanel();
    wep.setLayout(new BoxLayout(wep, BoxLayout.Y_AXIS));
    attack = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    elementa = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    weight = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    JPanel wclassPanel = new JPanel();
    wclassPanel.setLayout(new BoxLayout(wclassPanel, BoxLayout.Y_AXIS));
    ButtonGroup group = new ButtonGroup();
    for(Weapon.WepClass w: Weapon.WepClass.values()) {
      JRadioButton rb = new JRadioButton(w.toString());
      group.add(rb);
      wclassPanel.add(rb);
      wclasses.put(w, rb);
      if(w == Weapon.WepClass.SWORD)
      	rb.setSelected(true);
    }
    addLabeledComponent(wep, attack, "Attack");
    addLabeledComponent(wep, elementa, "Elementa");
    addLabeledComponent(wep, weight, "Weight");
    addLabeledComponent(wep, wclassPanel, "Weapon Class");

    //armor specific details
    JPanel arm = new JPanel();
    arm.setLayout(new BoxLayout(arm, BoxLayout.Y_AXIS));
    armor = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    earmor = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    weight2 = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    JPanel bpartPanel = new JPanel();
    bpartPanel.setLayout(new BoxLayout(bpartPanel, BoxLayout.Y_AXIS));
    ButtonGroup group2 = new ButtonGroup();
    for(Armor.BodyPart b: Armor.BodyPart.values()) {
      JRadioButton rb = new JRadioButton(b.toString());
      group2.add(rb);
      bpartPanel.add(rb);
      bparts.put(b, rb);
      if(b == Armor.BodyPart.HEAD)
      	rb.setSelected(true);
    }
    addLabeledComponent(arm, armor, "Armor");
    addLabeledComponent(arm, earmor, "Elemental Armor");
    addLabeledComponent(arm, weight2, "Weight");
    addLabeledComponent(arm, bpartPanel, "Bodypart");
    
    addLabeledComponent(typePanel, wep, Item.ItemType.WEAPON.toString());
    addLabeledComponent(typePanel, arm, Item.ItemType.ARMOR.toString());
    CardLayout cl = (CardLayout)(typePanel.getLayout());
    cl.show(typePanel, Item.ItemType.WEAPON.toString());
    
    //system buttons
    JPanel buttonPanel = new JPanel();
    JButton newChar = new JButton("New Item");
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
    addGridComponent(this, itemListScroll, 0, 0, 1, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, portraitPanel, 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, infoPanel, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, typePanel, 2, 0, 1, 4, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, effectsPanel, 3, 0, 1, 4, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    addGridComponent(this, buttonPanel, 0, 2, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
  }
  
  //labels a component and adds it to the specified parent
  public void addLabeledComponent(JPanel parent, Component c, String str) {
    JLabel label = new JLabel(str);
    label.setLabelFor(c);
    parent.add(label);
    parent.add(c, str);
  }
  
  //adds a component to the parent according to grid constraints
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
	public void actionPerformed(ActionEvent e) {
    switch(e.getActionCommand()) {
    case "new":
      newItem();
      break;
    case "add":
      addItem();
      break;
    case "export":
      export();
      break;
    case "remove":
      removeItem(itemListField.getSelectedValue());
      break;
    case "changePortrait":
      changePortrait();
      break;
    }
	}
	
	public void newItem() {
    portraitId = "DefaultItem";
		portraitPanel.repaint();
    idField.setText("Default");
    type.setSelectedItem(Item.ItemType.WEAPON);
    levelSpinner.setValue(0);
    valueSpinner.setValue(0);
    for (Character.Stat s: Character.Stat.values())
      effects.get(s).setValue(0);
    attack.setValue(0);
    elementa.setValue(0);
    weight.setValue(0);
    wclasses.get(Weapon.WepClass.SWORD).setSelected(true);
    armor.setValue(0);
    earmor.setValue(0);
    weight2.setValue(0);
    bparts.get(Armor.BodyPart.HEAD).setSelected(true);
 	}
	
	/*
	 * Adds the current item currently in the form to the list
	 */
	public void addItem() {
		String id = idField.getText();
		Item.ItemType t = (Item.ItemType) type.getSelectedItem();
		int level = (int)levelSpinner.getValue();
		int val = (int)valueSpinner.getValue();
		TreeMap<Character.Stat, Integer> eff = new TreeMap<>();
		for(Character.Stat s: Character.Stat.values())
			eff.put(s, (int)effects.get(s).getValue());
		
		Item item;
		switch(t) {
		case WEAPON:  		
  		int att = (int)attack.getValue();
			int elm = (int)elementa.getValue();
			int wg = (int)weight.getValue();
  		Weapon.WepClass wclass = null;
  		for(Weapon.WepClass w: wclasses.keySet())
  			if(wclasses.get(w).isSelected())
  				wclass = w;
			item = new Weapon(id, wclass, eff, att, elm, wg);
			break;
		case ARMOR:
  		int ar = (int)armor.getValue();
			int ear = (int)earmor.getValue();
			int wg2 = (int)weight2.getValue();
  		Armor.BodyPart bpart = null;
  		for(Armor.BodyPart b: bparts.keySet())
  			if(bparts.get(b).isSelected())
  				bpart = b;
			item = new Armor(id, bpart, eff, ar, ear, wg2);
			break;
		case CONSUMABLE:
			item = new Item(id, Item.ItemType.CONSUMABLE, eff, false);
			break;
		case ITEM:
			item = new Item(id, Item.ItemType.ITEM, eff, false);
			break;
		case KEYITEM:
			item = new Item(id, Item.ItemType.KEYITEM, eff, false);
			break;
		default:
			item = new Item(id, Item.ItemType.ITEM, false);
			break;
		}
		item.setImgId(portraitId);
		
		if(!items.containsKey(id))
			items.put(id, item);
		else
			System.out.println("An Item with that ID already exists, bitch");
		
		itemListField.setSelectedValue(item.getId(), true);
		refreshItemList();
	}
	
	/*
	 * Loads the specified item from the list and populates the form
	 */
	public void loadItem(String id) {
		if(id == null || items.isEmpty()) 
			newItem();
		else {
			Item item = items.get(id);
			
			portraitId = item.getImgId();
			portraitPanel.repaint();
			idField.setText(item.getId());
			type.setSelectedItem(item.getType());
			levelSpinner.setValue(item.getLevel());
			valueSpinner.setValue(item.getValue());
			TreeMap<Character.Stat, Integer> eff = item.getEffects();
			for(Character.Stat s: eff.keySet()) 
				effects.get(s).setValue(eff.get(s));
			
			switch(item.getType()) {
			case WEAPON:  		
				Weapon wep = (Weapon) item;
				attack.setValue(wep.getAttack());
				elementa.setValue(wep.getElementa());
				weight.setValue(wep.getWeight());
				wclasses.get(wep.getWclass()).setSelected(true);
				break;
			case ARMOR:
				Armor arm = (Armor) item;
				armor.setValue(arm.getArmor());
				earmor.setValue(arm.getEarmor());
				weight2.setValue(arm.getWeight());
				bparts.get(arm.getPart()).setSelected(true);
				break;
			default:
				break;
			}
		}
	}
	
	/*
	 * Saves the current item list to file
	 */
	public void export() {
		String xml = xs.toXML(items);
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(itemFile));
      bw.write(xml);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
	
	/*
	 * Deletes an item from the list
	 */
	public void removeItem(String id) {
		if(id != null) {
			items.remove(id);
			refreshItemList();
			itemListField.setSelectedIndex(0);
		}
	}
	
	/*
	 * Changes the portrait of an item
	 */
	public void changePortrait() {
		JFileChooser fc = new JFileChooser(imgDir);
		int returnVal = fc.showOpenDialog(portraitPanel);
		
		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		}
		if(file != null) {
			portraitId = file.getName().replaceFirst("[.][^.]+$", "");
			portraitPanel.repaint();
		}
	}
	
	//refreshes the item list after it has been updated
  private void refreshItemList() {
    String[] newList = new String[items.size()];
    int i = 0;
    for(String s: items.keySet()) {
    	newList[i] = s;
    	i++;
    }
    itemListField.setListData(newList);
  }  
}
