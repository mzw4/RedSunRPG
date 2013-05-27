package redsun;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import redsun.entities.Tile;


//change - custom frame sequences?
public class AnimatedSprite extends Sprite
{
  //duration of the animation sequence (ms)
  private long seqDur = 1600;
  //duration of one image frame (ms)
  //change - different imgDurs for animations?
  private long imgDur = 100;
  //current time of the animation (ms)
  private long curTime;
  //tells us if the animation should loop
  private boolean loops;
  //tells us if the animation is currently playing
  private boolean isActive;
  //number of images in the animation loop
//  private int numImgs;
  //tells us if the sprite depends on direction
  //change - needed for a non map sprite?
  private boolean directional;
  
  StopWatch timer = new StopWatch();

  //the ArrayList index of the current image being shown
  private int curFrame;
  
  // ------------------------------ Constructor ------------------------------------
  public AnimatedSprite(String id, long imgDur, int numImgs) {
    super(id);
    this.imgDur = imgDur;
    seqDur = numImgs*imgDur;
  }
  
  public AnimatedSprite(String id, long imgDur, int numImgs, boolean loops, boolean dirDependent) {
    this(id, imgDur, numImgs);
    this.loops = loops;
    this.directional = dirDependent;
  }
  
  // ------------------------------ Methods ------------------------------------
  public void startAnim() {
    timer.start();
    isActive = true;
  }

  public void stopAnim() {
    curFrame = 0;
    timer.stop();
    timer.reset();
    isActive = false;
  }
  
  public void pauseAnim() {
    timer.stop();
  }
  
  public void updateSprite() {    
    //automatically starts the animation from
    if(!isActive && loops) {
      startAnim();
    }
    //updates the animation sequence to display the correct frame
    if(isActive && directional) {
      curTime = timer.getElapsed();
      
      if(imgDur != 0 && curTime <= imgDur) {
	
      }
    }
    
    if(isActive) {
      curTime = timer.getElapsed();
      if(imgDur != 0 && curTime <= seqDur*1000000L)
	curFrame = (int)(curTime/(imgDur*1000000L));   
      else
	stopAnim();      
    }
  }
  
  // ------------------------------ Draw ------------------------------------
  public void drawAnimatedSprite(Graphics2D g2d, HashMap<String, ArrayList<BufferedImage>> images) {
    g2d.drawImage(images.get(id).get(curFrame), screenX, screenY, Tile.width, Tile.height, null);
  }
}
