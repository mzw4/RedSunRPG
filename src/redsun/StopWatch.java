package redsun;

public class StopWatch {
  
  private long startTime;
  private long stopTime;
  private long elapsed;
  private boolean running;
  
  //starts the timer as a stopwatch
  public void start() {
    startTime = System.nanoTime();
    running = true;
  }
  
  //pauses the timer
  public void stop() {
    stopTime = System.nanoTime();
    elapsed += stopTime - startTime; 
    startTime = stopTime;
    running = false;
  }

  //returns the elapsed time on the timer
  public long getElapsed() {
    if(running)
      return elapsed + (System.nanoTime() - startTime);
    else
      return elapsed;
  }
  
  //resets timer values to 0
  public void reset() {
    startTime = 0;
    stopTime = 0;
    elapsed = 0;
    running = false;
  }
  
  public boolean isRunning() {
    return running;
  }
}
