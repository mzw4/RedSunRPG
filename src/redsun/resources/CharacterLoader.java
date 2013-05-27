package redsun.resources;

import java.io.File;
import java.util.Hashtable;
import java.util.TreeMap;

import com.thoughtworks.xstream.XStream;
import redsun.entities.Character;

/*
 * Loads all characters from the file characters.xml into a map
 */
public class CharacterLoader {
  private XStream xs = new XStream();
  private TreeMap<String, Character> characters = new TreeMap<>();
;
  
  public CharacterLoader() {    
    loadChars();
  }
  
  private void loadChars() {
    try {
      characters = (TreeMap<String, Character>)xs.fromXML(new File("characters.xml"));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void clone(Character c) {
    
  }
  
  public Character getCharacter(String name) {
    return characters.get(name);
  }
}
