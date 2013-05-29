package com.joemax.sigilorbs;

import com.badlogic.gdx.InputProcessor;
import com.joemax.sigilorbs.objects.Orb;

public class GameInput implements InputProcessor {

	public GameInput() {

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// convert screenY to standard XY coordinates
		screenY = (int) (GameScreen.SCREEN_HEIGHT - screenY);
		if (GameScreen.flicker.getBounds().contains(screenX, screenY)) {
			GameScreen.flicker.setPressed(true);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		GameScreen.flicker.setPressed(false);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screenY = (int) (GameScreen.SCREEN_HEIGHT - screenY);
		if (GameScreen.flicker.isPressed()) {
			GameScreen.flicker.setX(screenX - (Orb.ORB_WIDTH / 2f));
			GameScreen.flicker.setY(screenY - (Orb.ORB_HEIGHT / 2f));
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
