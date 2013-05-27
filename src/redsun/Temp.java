package redsun;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

import redsun.entities.Character;
import redsun.entities.Inventory;
import redsun.entities.Item;
import redsun.entities.SkillTree;
import redsun.entities.Character.Class;
import redsun.entities.Weapon;

import com.thoughtworks.xstream.XStream;

/*
 * basically just used for outputting template xmls in whatever format i need them like 
 * arraylists or whatever for my loaders to open up and parse
 */
public class Temp {
  
  public static void main(String[] args) {
    //tile collision template
  	TreeMap<String, Boolean> walkable = new TreeMap<>();
    walkable.put("guyFE.png", true);
    walkable.put("outdoor_grass_1.png", true);

    //character list template
    TreeMap<String, Character> chars = new TreeMap<>();
    String name = "Template";
    Character.Class charClass = Character.Class.WARRIOR;
    int level = 0;
    int gold = 0;
    int exp = 0;
    int move = 0;
    int mass = 0;
    HashMap<Character.Stat, Integer> stats = new HashMap<>();
    for(Character.Stat s: Character.Stat.values())
      stats.put(s, 0);
    Inventory i = new Inventory();
    SkillTree skills = null;
    Character c = new Character(name, charClass, i, stats, skills, level, exp, gold, move, mass);
    
    chars.put("Template", c);
    
    //item list template
    TreeMap<String, Item> items = new TreeMap<>();
    items.put("Template Weapon", new Weapon("Template Weapon", Weapon.WepClass.SWORD));
    
    //dialogue reference config(player name -> dialogue text)
    TreeMap<String, String> dialogue = new TreeMap<>();
    dialogue.put("Man", "Hello! How are you young traveler? If it please you, I'd like to offer you my daughter.");
    
    XStream xs = new XStream();
    String xmlTest = xs.toXML(items);

    String fname = "items.xml";
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
      bw.write(xmlTest);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      System.out.println(fname + " was created");
    }    
  }
}
