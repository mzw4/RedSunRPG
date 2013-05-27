package redsun.entities;

import java.util.ArrayList;


/*
 * Holds all save information:
 * Player and party stats, inventory, skills
 * Plot progress
 * Locations of all dynamic game objects
 */
public class SaveState {
  
  private long playTime;
  
  private Player player;
  private ArrayList<Character> party;
  
  // ------------------------------ Window Handlers ------------------------------------
  public long getPlayTime() {
    return playTime;
  }

  public void setPlayTime(long playTime) {
    this.playTime = playTime;
  }
  
  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public ArrayList<Character> getParty() {
    return party;
  }

  public void setParty(ArrayList<Character> party) {
    this.party = party;
  }
  
}
