package redsun.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*
 * Interprets the current state of the mouse
 */

public class MouseInputHandler implements MouseListener, MouseMotionListener {
  
  private int numButtons = 3;
  
  //Once is when a button has just been pressed,
  //clicked is when a button has just been released
  private enum MouseState {
    RELEASED, PRESSED, ONCE, CLICKED
  }
  
  //holds state of mouse buttons as true if down and false if up
  private boolean[] curMouse = new boolean[numButtons];
  //holds the advanced state of mouse buttons based on when buttons were activated
  private MouseState[] mouseStates = new MouseState[numButtons];
  //current position of the mouse
  private int x = 0, y;
  
  public MouseInputHandler() {
    for(int i = 0; i < numButtons; i++)
      mouseStates[i] = MouseState.RELEASED;
    x = 0;
    y = 0;
  }
  
  //assigns advanced mouse states based on when buttons were activated
  public void update() {
    for(int i = 0; i < numButtons; i++)
      if(curMouse[i])
	if(mouseStates[i] == MouseState.RELEASED || mouseStates[i] == MouseState.CLICKED)
	  mouseStates[i] = MouseState.ONCE;
	else 
	  mouseStates[i] = MouseState.PRESSED;
      else
	if(mouseStates[i] == MouseState.PRESSED || mouseStates[i] == MouseState.ONCE)
	  mouseStates[i] = MouseState.CLICKED;
	else
	  mouseStates[i] = MouseState.RELEASED;
  }
  
  public void mousePressed(MouseEvent e) {
    curMouse[e.getButton()-1] = true;
  }
  
  public void mouseDragged(MouseEvent e) {
    x = e.getX();
    y = e.getY();
  }
  
  public void mouseMoved(MouseEvent e) {
    x = e.getX();
    y = e.getY();
  }
  
  public void mouseReleased(MouseEvent e) {
    curMouse[e.getButton()-1] = false;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  public boolean buttonPressed(int button) {
    return mouseStates[button] == MouseState.PRESSED;
  }
  
  public boolean buttonPressedOnce(int button) {
    return mouseStates[button] == MouseState.ONCE;
  }
  
  public boolean buttonReleased(int button) {
    return mouseStates[button] == MouseState.RELEASED;
  }

  //not used
  public void mouseClicked(MouseEvent e) {}
  //not used
  public void mouseEntered(MouseEvent e) {}
  //not used
  public void mouseExited(MouseEvent e) {}
}
