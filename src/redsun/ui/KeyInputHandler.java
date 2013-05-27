package redsun.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInputHandler extends KeyAdapter {

	private final int numKeys = 256;

	private enum KeyState {
		RELEASED, PRESSED, ONCE
	}

	// current state of all keys
	private boolean[] curKeys;
	// polled state of all keys
	private KeyState[] keyStates;

	public KeyInputHandler() {
		curKeys = new boolean[numKeys];
		keyStates = new KeyState[numKeys];
	}

	public void update() {
		for (int i = 0; i < numKeys; i++) {
			if (curKeys[i])
				if (keyStates[i] == KeyState.RELEASED)
					keyStates[i] = KeyState.ONCE;
				else
					keyStates[i] = KeyState.PRESSED;
			else
				keyStates[i] = KeyState.RELEASED;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		curKeys[key] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		curKeys[key] = false;
	}

	public boolean keyPressed(int key) {
		return keyStates[key] == KeyState.ONCE
				|| keyStates[key] == KeyState.PRESSED;
	}

	public boolean keyPressedOnce(int key) {
		return keyStates[key] == KeyState.ONCE;
	}

	public boolean keyReleased(int key) {
		return keyStates[key] == KeyState.RELEASED;
	}

	public boolean anyKeyPressedOnce() {
		for (KeyState k : keyStates)
			if (k == KeyState.ONCE)
				return true;
		return false;
	}
}
