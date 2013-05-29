package com.joemax.sigilorbs.objects;

import com.joemax.sigilorbs.GameScreen;

/**
 * Flicker orb - the orb which is 'flicked' by the player
 * 
 * @author Joseph Prokopyszyn
 * 
 */
public class Flicker extends Orb {
	// the default x-position of the flicker
	public static final float defaultX = (GameScreen.SCREEN_WIDTH / 2)
			- (ORB_WIDTH / 2);
	// the default y-position of the flicker
	public static final float defaultY = 30 + (ORB_HEIGHT / 2);
	// indicates whether the flicker orb is currently pressed or not
	private boolean isPressed;
	private boolean isFired;
	private float xVelocity;
	private float yVelocity;

	public Flicker(Colour orbColour) {
		super(orbColour, defaultX, defaultY);
	}

	/**
	 * Resets the orb back to its starting position
	 * 
	 * @param isNewColour
	 *            whether to reset a random colour or maintain the same colour
	 *            (e.g. failed flick)
	 */
	public void reset(boolean isNewColour) {
		setX(defaultX);
		setY(defaultY);
		if (isNewColour)
			setOrbColour(Colour.getRandom());
		setXVelocity(0);
		setYVelocity(0);
		setFired(false);
		setPressed(false);
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}

	public boolean isFired() {
		return isFired;
	}

	public void setFired(boolean isFired) {
		this.isFired = isFired;
	}
	
	public void setXVelocity(float newXVelocity) {
		xVelocity = newXVelocity;
	}
	
	public void setYVelocity(float newYVelocity) {
		yVelocity = newYVelocity;
	}
	
	public float getXVelocity() {
		return xVelocity;
	}
	
	public float getYVelocity() {
		return yVelocity;
	}
}
