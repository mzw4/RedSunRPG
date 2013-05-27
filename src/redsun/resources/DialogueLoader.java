package redsun.resources;

import java.io.File;
import java.util.HashMap;

import redsun.entities.Map;

import com.thoughtworks.xstream.XStream;

/*
 * Loads all dialogue given by the dialogue data file
 */
public class DialogueLoader {
  
  private XStream xs;

  private HashMap<String, String> dialogue;
  
  public DialogueLoader() {
    xs = new XStream();
    dialogue = new HashMap<>();
    
    loadDialogue();
  }
  
  private void loadDialogue() {
    try{
      dialogue = (HashMap<String, String>)xs.fromXML(new File("dialogue.xml"));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public String getDialogue(String charName) {
    return dialogue.get(charName);
  }
}
