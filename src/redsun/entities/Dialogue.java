package redsun.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.Timer;

import redsun.EventListener;
import redsun.EventManager;
import redsun.RedSunGame;
import redsun.events.DialogueEvent;
import redsun.events.Event;
import redsun.events.EventType;
import redsun.resources.DialogueLoader;
import redsun.resources.ImageLoader;
import redsun.ui.KeyInputHandler;

public class Dialogue implements ActionListener, EventListener {
  //change to fit according to image dimensions
  //dimensions of dialogue box
  private final int bWidth = 540, bHeight = 108;
  //dimensions of a character
  private final int insetX = 20, insetY = 40;
  private final int textWidth;
  private final int textSpeed = 200;
  private final int blinkSpeed = 3;
  
  //temp
  private int screenX = (756-540)/2, screenY = 540 - 108;
  
  private Timer letterTimer;
  private Timer blinkTimer;
  
  private ImageLoader iLoader;
  private DialogueLoader dLoader;
  private EventManager eventMgr;
  
  private BufferedImage dialogueBox;
  private TreeMap<String, BufferedImage> ui;
  
  private final Font font = new Font("Calibri", Font.BOLD, 24);
  private FontMetrics metrics;

  private String text = "";
  private String displayLine1 = "";
  private String displayLine2 = "";
  
  private int curIndex;
  private int newLineIndex;
  private int showIndex;
  
  private boolean paused;
  private boolean blinkOn;
  private boolean end;
  
  public Dialogue(RedSunGame game, EventManager eventMgr) {
    //Times the display of characters, scrolling the text
    letterTimer = new Timer(1000/textSpeed, this);
    blinkTimer = new Timer(1000/blinkSpeed, this);
    letterTimer.setInitialDelay(0);
    blinkTimer.setInitialDelay(0);
    
    dLoader = new DialogueLoader();
    iLoader = new ImageLoader();
    
    this.eventMgr = eventMgr;
    eventMgr.register(this, EventType.Event_UI_DialogueOpened);
    eventMgr.register(this, EventType.Event_UI_DialogueClosed);

    ui = iLoader.getUI();
    dialogueBox = ui.get("dialoguebox");    
    metrics = game.getFontMetrics(font);
    textWidth = dialogueBox.getWidth() - 2 * insetY;
  }
 
  //loads the next dialogue text to be displayed
  public String loadNextText(String id) {
    return "";
  }
  
  public void display(String str) {
    //look up data file
    text = str;
    displayLine1 = "";
    displayLine2 = "";
    curIndex = 0;
    showIndex = 0;
    newLineIndex = 0;
    end = false;
    paused = false;
    blinkOn = false;
    updateDisplayText();
    letterTimer.start();
  }
  
  public void updateDisplayText() {
    if(!text.isEmpty()) {     
      
      //calculate word wrapping
      if(curIndex < text.length()) {
//	System.out.println("curIndex: " + curIndex + " " + displayLine1 + " " + newLineIndex + " " + displayLine2);

	if(newLineIndex > 0) {
	  displayLine1 = text.substring(showIndex, newLineIndex);
	  displayLine2 = text.substring(newLineIndex, curIndex + 1).trim();
	}
	else {
	  displayLine1 = text.substring(showIndex, curIndex + 1).trim();
	  displayLine2 = "";
	}
	
	//calculate the length of the next word
	int nextWordLength = 0;
	if(text.charAt(curIndex) == ' ') {
	  String nextWord = "";
	  for (int i = curIndex + 1; i < text.length(); i++) {
	    nextWord += text.charAt(i);
	    if(text.charAt(i) == ' ')
	      break;
	  }
	  nextWordLength = metrics.stringWidth(nextWord);
	}

	//determines text to display on each line - if newLineIndex > 0 the current index is on line 2
	String showSub;
	if(newLineIndex > 0)
	  showSub = text.substring(newLineIndex, curIndex + 1);
	else
	  showSub = text.substring(showIndex, curIndex + 1);
	
	if(metrics.stringWidth(showSub.trim()) + nextWordLength > textWidth) {
	  if(newLineIndex > 0) {
	    pause();
	    showIndex = curIndex;
	    newLineIndex = 0;
	  }
	  else {
	    newLineIndex = curIndex;
	  }
	}
      }
      else {
        end = true;
        letterTimer.stop();
        pause();
      }
    }
  }
  
  public void pause() {
    letterTimer.stop();
    paused = true;
    blinkOn = false;
    blinkTimer.start();
  }

  public void resume() {
    blinkTimer.stop();
    blinkOn = false;
    paused = false;
    letterTimer.start();
  }
  
  private void close() {
    letterTimer.stop();
    blinkTimer.stop();
  }
  
  public void drawText(Graphics2D g2d) {

    g2d.setColor(Color.black);
    g2d.setFont(font);
    
    g2d.drawImage(dialogueBox, screenX, screenY, dialogueBox.getWidth(), dialogueBox.getHeight(), null);
    g2d.drawString(displayLine1, screenX + insetX, screenY + insetY);
    g2d.drawString(displayLine2, screenX + insetX, screenY + 2 * insetY);
    
    if(blinkOn)
      g2d.drawImage(ui.get("arrow"), screenX + dialogueBox.getWidth() - 3 * insetX,
	  screenY + dialogueBox.getHeight() - insetY, null);
  }
  
  @Override
  /*
   * Handles events received by the Event Manager
   */
  public void handleEvent(Event e) {
    if(e instanceof DialogueEvent) {
      DialogueEvent de = (DialogueEvent)e;
      if(de.getType() == EventType.Event_UI_DialogueOpened)
	display(dLoader.getDialogue(de.getId()));
    }
  }
  
  @Override
  /*
   * Increments the current text display index per call, allowing text to scroll.
   */
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == letterTimer) {
      curIndex++;
      updateDisplayText();
    }
    else if(e.getSource() == blinkTimer) {
      blinkOn = !blinkOn;
    }
  }
  
  /*
   * Handles key input specific to dialogues
   * change - when choices are available need to extend
   */
  public void processKeys(KeyInputHandler keyboard) {
    if(keyboard.keyPressedOnce(KeyEvent.VK_E)) {
      if(end) {
	eventMgr.addEvent(new DialogueEvent(EventType.Event_UI_DialogueClosed, null));
	close();
      }
      else if(paused) {
	eventMgr.addEvent(new DialogueEvent(EventType.Event_UI_DialogueCont, null));
	resume();
      }
    }
    
    if(keyboard.keyPressedOnce(KeyEvent.VK_ESCAPE)) {
      eventMgr.addEvent(new DialogueEvent(EventType.Event_UI_DialogueClosed, null));
      close();
    }
  }
}
