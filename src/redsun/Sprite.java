package redsun;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.TreeMap;

import redsun.entities.Tile;
import redsun.resources.ImageLoader;

public class Sprite
{  
  //change to imageid?
  protected String id;
  protected String imgId;
  
  //pixel location on screen as determined by Camera
  protected int screenX, screenY;
  
  public Sprite(String id) {
    this.id = id;
    //temp
    imgId = id;
  }
  
  // ------------------------------ Getters and setters ------------------------------------
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public String getImgId() {
    return imgId;
  }

  public void setImgId(String imgId) {
    this.imgId = imgId;
  }

  public int getScreenX() {
    return screenX;
  }

  public void setScreenX(int screenX) {
    this.screenX = screenX;
  }

  public int getScreenY() {
    return screenY;
  }

  public void setScreenY(int screenY) {
    this.screenY = screenY;
  }
  
  // ------------------------------ Draw ------------------------------------
  public void drawSprite(Graphics2D g2d, ImageLoader images) {
    g2d.drawImage(images.getImg(imgId), screenX, screenY, Tile.width, Tile.height, null);
  }
}
