package com.joemax.sigilorbs.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Orb class
 * 
 * @author Joseph Prokopyszyn
 * 
 */
public class Orb {
	// enum variable for the orb's colour
	public enum Colour {
		ORANGE(0), PURPLE(1), RED(2), YELLOW(3), GREEN(4), BLUE(5);

		private final int value;

		private Colour(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}

		// returns a random colour - useful in generating a random orb
		public static Colour getRandom() {
			return values()[(int) (Math.random() * values().length)];
		}
	};

	public static final Texture orangeOrb = new Texture(
			Gdx.files.internal("data/orb1.png"));
	public static final Texture purpleOrb = new Texture(
			Gdx.files.internal("data/orb2.png"));
	public static final Texture redOrb = new Texture(
			Gdx.files.internal("data/orb3.png"));
	public static final Texture yellowOrb = new Texture(
			Gdx.files.internal("data/orb4.png"));
	public static final Texture greenOrb = new Texture(
			Gdx.files.internal("data/orb5.png"));
	public static final Texture blueOrb = new Texture(
			Gdx.files.internal("data/orb6.png"));

	public static final float ORB_WIDTH = 60;
	public static final float ORB_HEIGHT = 60;

	private float x;
	private float y;
	private Colour orbColour;

	public Orb(Colour orbColour, float x, float y) {
		this.orbColour = orbColour;
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Colour getOrbColour() {
		return orbColour;
	}

	public Texture getOrbTexture() {
		switch (orbColour.getValue()) {
		case 0:
			return orangeOrb;
		case 1:
			return purpleOrb;
		case 2:
			return redOrb;
		case 3:
			return yellowOrb;
		case 4:
			return greenOrb;
		case 5:
			return blueOrb;
		}
		return null;
	}
}