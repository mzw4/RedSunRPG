package redsun;


import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class ScreenManager {

  private GraphicsDevice vc;
  
  //gives vc access to monitor screen
  public ScreenManager() {
    GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
    vc = e.getDefaultScreenDevice();
  }
  
  //get all compatible display modes for the graphics card
  public DisplayMode[] getPublicDisplayModes() {
    return vc.getDisplayModes();
  }
  
  //compares DM passed in to vc DM
  public DisplayMode findCompatibleMode(DisplayMode[] modes) {
    DisplayMode[] vcModes = vc.getDisplayModes();
    for(int i = 0; i < modes.length; i++)
      for(int j = 0; j < vcModes.length; j++)
	if(displayModesMatch(modes[i], vcModes[j])) {
	  return modes[i];
	}
    return null;
  }
  
  //checks of given DMs match
  public boolean displayModesMatch(DisplayMode d1, DisplayMode d2) {
    return (d1.getWidth() == d2.getWidth() && d1.getHeight() == d2.getHeight())
    	&& (d1.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI
    	    || d2.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI 
    	    || d1.getBitDepth() == d2.getBitDepth())
  	&& (d1.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN 
  	    || d2.getBitDepth() == DisplayMode.REFRESH_RATE_UNKNOWN
  	    || d1.getRefreshRate() == d2.getRefreshRate());
  }
  
  //returns current DM
  public DisplayMode getCurrentDisplayMode() {
    return vc.getDisplayMode();
  }
  
  public void setFullScreen(DisplayMode dm) {
    JFrame f = new JFrame();
    f.setUndecorated(true);
    f.setIgnoreRepaint(true);
    f.setResizable(false);
    vc.setFullScreenWindow(f);
    
    if(dm != null && vc.isDisplayChangeSupported()) {
      try {
	vc.setDisplayMode(dm);
      } catch(Exception e) {e.printStackTrace();}
    }
    f.createBufferStrategy(2);
  }
  
  //exits full screen
  public void restoreScreen() {
    Window w = vc.getFullScreenWindow();
    if(w != null) {
      w.dispose();
    }
    vc.setFullScreenWindow(null);
  }
  
  //set graphics object equal to 
  public Graphics2D getGraphics() {
    Window w = vc.getFullScreenWindow();
    if(w != null) {
      BufferStrategy s = w.getBufferStrategy();
      return (Graphics2D)s.getDrawGraphics();
    }
    else
      return null;
  }
  
  //updates display 
  public void update() {
    Window w = vc.getFullScreenWindow();
    if(w != null) {
      BufferStrategy s = w.getBufferStrategy();
      if(!s.contentsLost())
	s.show();
    }
  }
  
  //returns full screen window
  public Window getFullScreenWindow() {
    return vc.getFullScreenWindow();
  }
  
  //gets width of window
  public int getWidth() {
    Window w = vc.getFullScreenWindow();
    if(w != null)
      return w.getWidth();
    else
      return 0;
  }
  
  //gets height of window
  public int getHeight() {
    Window w = vc.getFullScreenWindow();
    if(w != null)
      return w.getHeight();
    else
      return 0;
  }
  
  //creates image compatible with monitor
  public BufferedImage createCompatibleImage(int w, int h, int t) {
    Window win =  vc.getFullScreenWindow();
    if(win != null) {
      GraphicsConfiguration gc = win.getGraphicsConfiguration();
      return gc.createCompatibleImage(w, h, t);
    }
    return null;    
  }
}
