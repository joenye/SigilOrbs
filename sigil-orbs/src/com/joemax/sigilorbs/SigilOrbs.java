package com.joemax.sigilorbs;

import com.badlogic.gdx.Game;

/**
 * The main project class containing global variables and handling the various
 * services that persist throughout the game.
 * @author Joseph Prokopyszyn
 *
 */
public class SigilOrbs extends Game {	

	@Override
	public void create() {
		setScreen(new GameScreen());
	}

	@Override
	public void dispose() {
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
