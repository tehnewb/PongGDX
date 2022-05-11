package app.game;

import game.GameApplicationListener;

public class PongGameListener extends GameApplicationListener {

	@Override
	public void create() {
		super.create();
		this.setScreen(new LoadingScreen(this));
	}

}
