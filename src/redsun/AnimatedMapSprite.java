package redsun;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import redsun.entities.Tile;
import redsun.resources.ImageLoader;

public class AnimatedMapSprite extends MapEntity {
	// duration of the animation sequence (ms)
	private long seqDur;
	// duration of one image frame (ms)
	// change - different imgDurs for animations? config file
	private long imgDur;
	// current time of the animation (ms)
	private long curTime;
	// tells us if the animation should loop
	private boolean loops;
	// tells us if the animation is currently playing
	private boolean isActive;
	// number of images in the animation loop
	private int numImgs;
	// tells us if the sprite depends on direction
	private boolean directional;
	// starting frame of the animations
	private int startFrame;

	StopWatch timer = new StopWatch();

	// the ArrayList index of the current image being shown
	private int curFrame;

	// ------------------------------ Constructor
	// ------------------------------------
	// change - imgdur and numimgs should be provided in a data file, not from the
	// constructor
	public AnimatedMapSprite(String id, long imgDur, int numImgs) {
		super(id);
		this.imgDur = imgDur;
		this.numImgs = numImgs;
		seqDur = numImgs * imgDur;
	}

	public AnimatedMapSprite(String id, long imgDur, int numImgs, boolean loops, boolean directional) {
		this(id, imgDur, numImgs);
		this.loops = loops;
		this.directional = directional;
		if (directional)
			seqDur /= 4;
	}

	// ------------------------------ Methods ------------------------------------
	public void startAnim() {
		// change - errors like these should be reported to a system log
		if (imgDur == 0 || numImgs == 0)
			System.out.println("Animation data for id:" + id + " is incorrect.");
		curFrame = startFrame;
		timer.start();
		isActive = true;
	}

	public void stopAnim() {
		timer.stop();
		timer.reset();
		isActive = false;
	}

	public void pauseAnim() {
		timer.stop();
	}

	public void updateSprite() {
		// automatically starts the animation from the beginning for a loop
		if (!isActive && loops)
			startAnim();

		// updates the animation sequence to display the correct frame
		// change - messay?
		if (directional && dir != null) {
			switch (dir) {
			case WEST:
				startFrame = 0;
				break;
			case SOUTH:
				startFrame = numImgs / 4;
				break;
			case NORTH:
				startFrame = numImgs / 4 * 2;
				break;
			case EAST:
				startFrame = numImgs / 4 * 3;
				break;
			}
			if (!isMoving())
				curFrame = startFrame;
		}

		if (isActive) {
			curTime = timer.getElapsed();
			if (imgDur != 0 && curTime <= seqDur * 1000000L)
				// delete this if once the event handler thing works since the above
				// thing handles it
				if (directional && !isMoving())
					curFrame = startFrame;
				else
					curFrame = (startFrame + (int) (curTime / (imgDur * 1000000L))) % numImgs;
			else
				stopAnim();
		}
	}

	// ------------------------------ Draw ------------------------------------
	public void drawAnimatedSprite(Graphics2D g2d, ImageLoader images) {
		g2d.drawImage(images.getAImg(imgId).get(curFrame), screenX, screenY, Tile.width, Tile.height,
				null);
	}
}
