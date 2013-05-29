package com.joemax.sigilorbs;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.joemax.sigilorbs.objects.Orb;

/**
 * The main game screen
 * @author Joseph Prokopyszyn
 *
 */
public class GameScreen implements Screen {
	// global game-specific variables
	public static final float SCREEN_WIDTH = 480;
	public static final float SCREEN_HEIGHT = 800;
	public static final float NEWROW_INTERVAL = 10;
	public static final int ORBS_PER_ROW = 8;
	public static final int NUM_STARTING_ROWS = 3;
	
	OrthographicCamera camera;
	SpriteBatch batch;
	// dynamic array storing every orb on the screen
	Array<Orb> orbs;
	// the number of seconds that timeRunning must reach in order for a new row
	// of orbs to be spawned - incremented by NEWROW_INTERVAL each time that
	// this occurs
	float newRowTime;
	// determines the rate at which the number of spawned rows increases
	float gameSpeedMultiplier;
	long timeRunning;
	long lastRenderTime;
	// flag to indicate whether the game is paused or not
	boolean isGameRunning = false;

	/**
	 * Constructor method for game screen
	 */
	public GameScreen() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		batch = new SpriteBatch();
		orbs = new Array<Orb>();
		newRowTime = NEWROW_INTERVAL;
		gameSpeedMultiplier = 1;
		isGameRunning = true;
		createNewRows(NUM_STARTING_ROWS);
		lastRenderTime = TimeUtils.nanoTime();
	}

	/**
	 * Main game loop
	 */
	@Override
	public void render(float delta) {
		// clear the screen black
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// update camera matrices
		camera.update();

		// tell SpriteBatch to render in coordinate system specified by the
		// camera
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// draw every orb
		Iterator<Orb> iter = orbs.iterator();
		while (iter.hasNext()) {
			Orb orb = iter.next();
			batch.draw(orb.getOrbTexture(), orb.getX(), orb.getY(),
					Orb.ORB_WIDTH, Orb.ORB_HEIGHT);
		}
		batch.end();

		if (getTimeRunning() > newRowTime) {
			createNewRows(1);
			shiftRows();
			newRowTime += (NEWROW_INTERVAL * gameSpeedMultiplier);
		}

		// keeps track of how long the game has been running
		if (isGameRunning)
			timeRunning += (TimeUtils.nanoTime() - lastRenderTime);
		lastRenderTime = TimeUtils.nanoTime();
	}

	/**
	 * Returns the time (in seconds) that the game has been running - used to
	 * calculate various things including the amount of time before a new row
	 * should be spawned
	 * 
	 * @return
	 */
	public float getTimeRunning() {
		return (Float) (timeRunning / 1000000000f);
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
				shiftRows();
			numRowsCreated += 1;
		}
	}

	/**
	 * Shifts the y-position of each orb in each row downwards
	 */
	public void shiftRows() {
		int numRows = (orbs.size / ORBS_PER_ROW);
		for (int j = 0; j < numRows; j++) {
			for (int i = 0 + (j * ORBS_PER_ROW); i < ((j * ORBS_PER_ROW) + ORBS_PER_ROW); i++) {
				Orb currentOrb = orbs.get(i);
				currentOrb.setY(currentOrb.getY() - Orb.ORB_HEIGHT);
			}
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
