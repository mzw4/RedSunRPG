package redsun.util;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * Entity editor and creator for Red Sun RPG.
 * Allows creation, editing, and removal of characters, items, dialogue, quests, skills, and save state
 * change - need to add mass and movedistance to characters
 */
public class EntityEditor {

//  private int eWidth = 756, eHeight = 540;
  private JTabbedPane tabs;
    
  public EntityEditor() {
    JFrame frame = new JFrame("Red Sun Entity Editor");

    //set UI to Windows look and feel
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (ClassNotFoundException | InstantiationException
    		| IllegalAccessException | UnsupportedLookAndFeelException e1) {
      e1.printStackTrace();
    }
    
    JMenuBar menu = new JMenuBar();
    JMenu file = new JMenu("File");
    menu.add(file);    
    frame.setJMenuBar(menu);
        
    initPanel();
    frame.add(tabs);
    
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setVisible(true);
  }
  
  public void initPanel() {
    tabs = new JTabbedPane();
//    tabs.setPreferredSize(new Dimension(eWidth, eHeight));
    
    EECharacterPanel characters = new EECharacterPanel();
    EEItemPanel items = new EEItemPanel();
    JPanel dialogue = new JPanel();
    JPanel quests = new JPanel();
    JPanel skills = new JPanel();
    JPanel saves = new JPanel();

    tabs.addTab("Characters", null, characters);
    tabs.addTab("Items", null, items);
    tabs.addTab("Dialogue", null, dialogue);
    tabs.addTab("Quests", null, quests);
    tabs.addTab("Skills", null, skills);
    tabs.addTab("Save State", null, saves);
  }
  
  public static void main(String[] args) {
    EntityEditor e = new EntityEditor();
  }
}
