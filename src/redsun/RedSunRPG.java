package redsun;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

public class RedSunRPG extends JFrame implements WindowListener{
  private RedSunGame redSun;
  
  public RedSunRPG() {
    super("Red Sun RPG");
    
    redSun = new RedSunGame();
    add(redSun);
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    addWindowListener(this);
    setResizable(false);
    setSize(new Dimension(redSun.getPanelWidth(), redSun.getPanelHeight()));
    pack();
    setVisible(true);
  }
  
  // ------------------------------ Window Handlers ------------------------------------
  public void windowActivated(WindowEvent arg0) {
    redSun.resumeGame();
  }

  public void windowDeactivated(WindowEvent arg0) {
    redSun.pauseGame(); 
  }

  public void windowDeiconified(WindowEvent arg0) {
    redSun.resumeGame();
  }

  public void windowIconified(WindowEvent arg0) {
    redSun.pauseGame(); 
  }
  
  public void windowClosing(WindowEvent arg0) {
    redSun.stopGame();
  }

  public void windowOpened(WindowEvent arg0) {}
  public void windowClosed(WindowEvent arg0) {}
  
  // ------------------------------ Main ------------------------------------
  public static void main(String[] args) {
    RedSunRPG game = new RedSunRPG();
  }
}
