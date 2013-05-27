package redsun.entities;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import redsun.EventListener;
import redsun.EventManager;
import redsun.events.Event;

public class Player extends Character implements EventListener
{      
  
  private int magic;
  
  public Player(String name, Class charClass) {
    super(name, charClass);
  }
}
