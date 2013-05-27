package redsun.resources;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class SoundLoader
{
  
  private String soundDir = "./src/Sounds/";
  
  private HashMap<String, Clip> sounds;
  
  private HashMap<String, Float> fadingOut;
  private HashMap<String, Float> fadingIn;
  
  private final int fadeOutThreshold = -40;
  private final int fadeInThreshold = 2;

  public SoundLoader() {    
    sounds = new HashMap<>();
    loadAllSounds(new File(soundDir));
    
    fadingOut = new HashMap<>();
    fadingIn = new HashMap<>();
    
    //temp
//    changeVolume("click.wav", -5);
//    changeVolume("clash.wav", -10);
  }
  
  // ------------------------------ Methods ------------------------------------
  
  //updated each frame
  public void update() {
    //fade a clip out
    //removeOut specifies which clips need to be removed from the fading out list
    //to avoid concurrent modification error
    if(!fadingOut.isEmpty()) {
      LinkedList<String> removeOut = new LinkedList<>();

      for(String s: fadingOut.keySet()) {
	if(!isRunning(s)) {
	  removeOut.add(s);
	  continue;
	}
	float vol = ((FloatControl) sounds.get(s).getControl(FloatControl.Type.MASTER_GAIN)).getValue();
	if (vol <= fadeOutThreshold) {
	  removeOut.add(s);
	  stop(s);
	}
	else
	  changeVolume(s, vol - fadingOut.get(s));
      }
      
      for(String s: removeOut)
	      fadingOut.remove(s);
    }

    
    //fade a clip in
    //removeIn specifies which clips need to be removed from the fading in list
    //to avoid concurrent modification error
    if(!fadingIn.isEmpty()) {
      LinkedList<String> removeIn = new LinkedList<>();

      for(String s: fadingIn.keySet()) {
	if(!isRunning(s)) {
	  removeIn.add(s);
	  continue;
	}
	float vol = ((FloatControl) sounds.get(s).getControl(FloatControl.Type.MASTER_GAIN)).getValue();
	if(vol >= fadeInThreshold)
	  removeIn.add(s);
	else
	  changeVolume(s, vol + fadingIn.get(s));
      }
      
      for(String s: removeIn)
	      fadingIn.remove(s);
    }
  }
  
  public void loadAllSounds(File file) {   
    if(file.isFile())
      loadSound(file);
    else if(file.isDirectory()) {
      File[] files = file.listFiles(new FilenameFilter() {
        public boolean accept(File dir, String fname) {
	  return fname.endsWith(".wav") || dir.isDirectory();
        }
      });
      if(files != null)
        for(File f: files)
          loadAllSounds(f);
      else
	System.out.println("File directory " + file + " is empty.");
    }
  }
  
  public void loadSound(File file) {
    try {
      Clip clip = AudioSystem.getClip();
      AudioInputStream ais = AudioSystem.getAudioInputStream(file);
      clip.open(ais);
      if(clip != null)
	sounds.put(file.getName(), clip);
    } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
      e.printStackTrace();
    }
  }
  
  public void play(String sound) {
    Clip clip = sounds.get(sound);
    if(clip != null) {
      reset(sound);
      clip.start();
    }
  }
  
  public void loop(String sound) {
    Clip clip = sounds.get(sound);
    if(clip != null) {
      reset(sound);
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }
  
  public void pause(String sound) {
    Clip clip = sounds.get(sound);
    if(clip != null) {
      clip.stop();
    }
  }
  
  //resume doesnt preserve the loop right now
  public void resume(String sound) {
    Clip clip = sounds.get(sound);
    if(clip != null) {
      clip.start();
    }
  }
  
  public void stop(String sound) {
    reset(sound); 
  }
  
  //just cause its used often ~not sure what drain and flush actually do
  private void reset(String sound) {
    Clip clip = sounds.get(sound);
    if(clip != null) {
      clip.stop();
      clip.drain();
      clip.flush();
      clip.setFramePosition(0);
    }
  }
  
  //value of gain increase must not exceed 6.0206
  public void changeVolume(String sound, float val) {
    FloatControl control = (FloatControl)sounds.get(sound).getControl(FloatControl.Type.MASTER_GAIN);
    control.setValue(val);
  }
  
  public void pauseAll() {
    for(String s: sounds.keySet())
      stop(s);
  }
  
  //wait wat? no it should actually resume all clips that were playing BEFORE
  //change - need to keep a list of all currently playing clips
  public void resumeAll() {
    for(String s: sounds.keySet())
      resume(s);
  }
  
  //eventually want to make it FADE out
  public void stopAll() {
    for(String s: sounds.keySet())
      reset(s);
  }
  
  //initilizes a fade in
  public void fadeIn(String sound, float step, boolean loop) {
    fadingIn.put(sound, step);
    if(loop)
      loop(sound);
    else
      play(sound);
    changeVolume(sound, -20f);
  }
  
  //initilizes a fade out
  public void fadeOut(String sound, float step) {
    fadingOut.put(sound, step);
  }
  
  public boolean isRunning(String sound) {
    return sounds.get(sound).isActive();
  }
}
