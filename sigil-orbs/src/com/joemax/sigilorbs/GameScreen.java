package com.joemax.sigilorbs;

import java.util.Iterator;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.joemax.sigilorbs.objects.Flicker;
import com.joemax.sigilorbs.objects.FlickerAccessor;
import com.joemax.sigilorbs.objects.Orb;
import com.joemax.sigilorbs.objects.Orb.Colour;
import com.joemax.sigilorbs.objects.OrbAccessor;

/**
 * The main game screen
 * 
 * @author Joseph Prokopyszyn
 * 
 */
public class GameScreen implements Screen {
	// global game-specific variables
	public static final float SCREEN_WIDTH = 480;
	public static final float SCREEN_HEIGHT = 800;
	public static final float NEWROW_INTERVAL = 1f;
	public static final int ORBS_PER_ROW = 8;
	public static final int NUM_STARTING_ROWS = 3;
	public static final float FLICKER_CEILING = 3 * Orb.ORB_HEIGHT;
	// flicker orb
	public static Flicker flicker;
	public static FlickerAccessor flickerAccessor;
	public static OrbAccessor orbAccessor;

	OrthographicCamera camera;
	Vector3 touchPos;
	float touchXOffset;
	float touchYOffset;
	SpriteBatch batch;
	// dynamic array storing every orb on the screen
	Array<Orb> orbs;
	Array<Flicker> flickers;
	Iterator<Orb> orbIter;
	Iterator<Flicker> flickerIter;

	// the number of seconds that timeRunning must reach in order for a new row
	// of orbs to be spawned - incremented by NEWROW_INTERVAL each time that
	// this occurs
	float newRowTime;
	// determines the rate at which the number of spawned rows increases
	float gameSpeedMultiplier;
	float timeRunning;
	long lastRenderTime;
	// flag to indicate whether the game is paused or not
	boolean isGameRunning = false;

	// used for tweening
	TweenManager manager;

	// used for framerate throttling
	long lastRender;
	long now;

	/**
	 * Constructor method for game screen
	 */
	public GameScreen() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		batch = new SpriteBatch();
		orbs = new Array<Orb>();
		flickers = new Array<Flicker>();
		flicker = new Flicker(Colour.getRandom());
		flickers.add(flicker);
		newRowTime = NEWROW_INTERVAL;
		touchPos = new Vector3();
		flickerAccessor = new FlickerAccessor();
		orbAccessor = new OrbAccessor();
		Tween.registerAccessor(Flicker.class, flickerAccessor);
		Tween.registerAccessor(Orb.class, orbAccessor);
		manager = new TweenManager();
		gameSpeedMultiplier = 1f;
		isGameRunning = true;
		// Gdx.input.setInputProcessor(new GameInput());
		createNewRows(NUM_STARTING_ROWS);
		lastRenderTime = TimeUtils.nanoTime();
	}

	/**
	 * Main game loop - various elements need to be handled in their own
	 * respective methods / classes later...
	 */
	@Override
	public void render(float delta) {

		/**
		 * (1) RENDERING STAGE
		 */

		// clear the screen black
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		// update camera matrices
		camera.update();
		// get current flicker
		flicker = flickers.peek();
		// tell SpriteBatch to render in coordinate system specified by the
		// camera
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// draw every orb in orbs array
		orbIter = orbs.iterator();
		while (orbIter.hasNext()) {
			Orb orb = orbIter.next();
			batch.draw(orb.getOrbTexture(), orb.getX(), orb.getY(),
					Orb.ORB_WIDTH, Orb.ORB_HEIGHT);
		}
		// draw every flicker orb in flicker orbs array
		flickerIter = flickers.iterator();
		while (flickerIter.hasNext()) {
			Flicker flicker = flickerIter.next();
			batch.draw(flicker.getOrbTexture(), flicker.getX(), flicker.getY(),
					Orb.ORB_WIDTH, Orb.ORB_HEIGHT);
		}
		batch.end();

		/**
		 * (2) INPUT STAGE
		 */

		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			touchPos.y = (GameScreen.SCREEN_HEIGHT - touchPos.y);
			// if just pressed flicker, then set the initial touch position and
			// derive distance from bottom-left corner to prevent jerkiness
			if (flicker.getBounds().contains(touchPos.x, touchPos.y)
					&& (flicker.isPressed() == false)) {
				touchXOffset = touchPos.x - flicker.getX();
				touchYOffset = touchPos.y - flicker.getY();
				flicker.setPressed(true);
			}
			// dragging the flicker
			if (flicker.isPressed()) {
				flicker.setX(touchPos.x - touchXOffset);
				flicker.setY(touchPos.y - touchYOffset);
				if (flicker.getY() >= FLICKER_CEILING)
					flicker.setY(FLICKER_CEILING);
				// reset flicker if touches walls - encourages player to avoid
				// this bad practice
				if (flicker.getX() <= 0) {
					flicker.setPressed(false);
					Tween.to(flicker, FlickerAccessor.POSITION_XY, 0.5f)
							.target(Flicker.defaultX, Flicker.defaultY)
							.ease(Elastic.OUT).start(manager);
				}
				if (flicker.getX() > (SCREEN_WIDTH - Orb.ORB_WIDTH)) {
					flicker.setPressed(false);
					Tween.to(flicker, FlickerAccessor.POSITION_XY, 0.5f)
							.target(Flicker.defaultX, Flicker.defaultY)
							.ease(Elastic.OUT).start(manager);
				}
			}

		} else {
			// attempt to fire flicker
			if (flicker.isPressed()) {
				flicker.setPressed(false);
				if ((flicker.getY() > (Flicker.defaultY + 30))
						&& (flicker.getX() > 0)
						&& (flicker.getX() < (SCREEN_WIDTH - Flicker.ORB_WIDTH))) {
					flicker.setFired(true);
					System.out.println("Flicker fired!");
					// calculate velocity
					float xDistance = touchPos.x - Flicker.defaultX;
					float yDistance = touchPos.y - Flicker.defaultY;
					System.out.println(yDistance);
					// prevents too fast flicks
					if (yDistance > 250)
						yDistance = 250;
					flicker.setXVelocity(xDistance);
					flicker.setYVelocity(yDistance);
				}
				// failed flick - reset flicker - [insert interpolation later]
				else {
					Tween.to(flicker, FlickerAccessor.POSITION_XY, 0.5f)
							.target(Flicker.defaultX, Flicker.defaultY)
							.ease(Elastic.OUT).start(manager);
				}
			}
		}

		/**
		 * (3) LOGIC STAGE
		 */
		// update flicker if fired
		if (flicker.isFired()) {
			flicker.setX(flicker.getX() + (flicker.getXVelocity() / 8f));
			flicker.setY(flicker.getY() + (flicker.getYVelocity() / 12f));
			// checking for and adjusting flicker for game wall collisions
			if (flicker.getX() <= 0) {
				flicker.setXVelocity(flicker.getXVelocity() * -1);
			}
			if (flicker.getX() >= (SCREEN_WIDTH - Flicker.ORB_WIDTH)) {
				flicker.setXVelocity(flicker.getXVelocity() * -1);
			}
			if (flicker.getY() >= SCREEN_HEIGHT) {
				flicker.reset(true);
			}
			// checking for contact with orbs
			for (int i = 0; i < ORBS_PER_ROW; i++) {
				Orb orb = orbs.get(i);
				if (Intersector.overlaps(orb.getBounds(), flicker.getBounds())) {
					flicker.setFired(false);
					break;
				}
			}
			// checking for contact with previous flickers
			flickerIter = flickers.iterator();
			// iterates through all Flickers except the final one
			while (flickerIter.hasNext()) {
				// ensures there is no collision check for itself
				Flicker flickerTarget = flickerIter.next();
				if (!flickerTarget.equals(flickers.peek())) {
					if (Intersector.overlaps(flickerTarget.getBounds(),
							flicker.getBounds())) {
						flicker.setFired(false);
						break;
					}
				}
			}
			// if a collision has occured
			if (flicker.isFired() == false) {
				flickers.add(new Flicker(Colour.getRandom()));
			}
		}

		// if sufficient time has elapsed, create new rows
		if (timeRunning > newRowTime) {
			createNewRows(1);
			shiftRows(false);
			newRowTime += (NEWROW_INTERVAL * gameSpeedMultiplier);
		}

		// delete orbs if they reach bottom
		orbIter = orbs.iterator();
		while (orbIter.hasNext()) {
			Orb orb = orbIter.next();
			if (orb.getY() <= FLICKER_CEILING)
				orbIter.remove();
		}
		orbs.shrink();

		// keeps a track of how long the game has been running
		if (isGameRunning)
			timeRunning += delta;
		manager.update(delta);
	}	

	/**
	 * Creates new row(s) of orbs
	 * 
	 * @param numRowsToCreate
	 *            the number of rows to be created
	 */
	public void createNewRows(int numRowsToCreate) {
		int numRowsCreated = 1;
		while (numRowsCreated <= numRowsToCreate) {
			for (int i = 0; i < ORBS_PER_ROW; i++) {
				Orb newOrb = new Orb(Orb.Colour.getRandom(),
						(i * Orb.ORB_WIDTH), SCREEN_HEIGHT);
				orbs.add(newOrb);
			}
			// if another row is to be created, we need to shift the previous
			// row downwards to make way for the new one
			if (numRowsToCreate > 1)
				shiftRows(true);
			numRowsCreated += 1;
		}
	}

	/**
	 * Shifts the y-position of each orb and flicker orb downwards
	 */
	public void shiftRows(boolean isHardShift) {
		for (int i = 0; i < orbs.size; i++) {
			Orb currentOrb = orbs.get(i);
			if (isHardShift)
				currentOrb.setY(currentOrb.getY() - Orb.ORB_HEIGHT);
			else
				Tween.to(currentOrb, OrbAccessor.POSITION_Y, 1f)
						.target(currentOrb.getY() - Orb.ORB_HEIGHT)
						.start(manager);
		}
		for (int i = 0; i < (flickers.size - 1); i++) {
			Flicker currentFlicker = flickers.get(i);
			Tween.to(currentFlicker, FlickerAccessor.POSITION_Y, 1f)
					.target(currentFlicker.getY() - Flicker.ORB_HEIGHT)
					.start(manager);
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}
