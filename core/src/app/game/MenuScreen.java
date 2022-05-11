package app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

import game.GameApplicationListener;
import game.LibraryConstants;
import game.library.font.FontBuilder;
import game.library.font.FontData;
import game.library.screen.GameScreen;

public class MenuScreen extends GameScreen {

	private VisTextButton playButton, exitButton;
	private Texture logo;

	public MenuScreen(GameApplicationListener game) {
		super(game);
	}

	@Override
	public void create() {
		VisTextButtonStyle buttonStyle = new VisTextButtonStyle();

		buttonStyle.down = new TextureRegionDrawable(LibraryConstants.getAsset("buttons/Down.png", Texture.class));
		buttonStyle.up = new TextureRegionDrawable(LibraryConstants.getAsset("buttons/Up.png", Texture.class));
		buttonStyle.over = new TextureRegionDrawable(LibraryConstants.getAsset("buttons/Hover.png", Texture.class));
		buttonStyle.font = FontData.generateFont("Arial", FontBuilder.create().borderColor(Color.BLACK).borderWidth(1).size(20).color(Color.BLACK).toParameter());

		this.playButton = new VisTextButton("PLAY", buttonStyle);
		this.exitButton = new VisTextButton("EXIT", buttonStyle);

		this.playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				transitionToScreen(new PlayingScreen(game));
			}
		});

		this.logo = LibraryConstants.getAsset("logo.png", Texture.class);

		this.screenStage.addActor(this.playButton);
		this.screenStage.addActor(this.exitButton);
	}

	@Override
	public void render() {
		this.screenBatch.begin();
		this.screenBatch.getColor().a = this.screenStage.getRoot().getColor().a;
		this.screenBatch.draw(this.logo, (Gdx.graphics.getWidth() - this.logo.getWidth()) / 2, Gdx.graphics.getHeight() - this.logo.getHeight() - 50);
		this.screenBatch.end();
	}

	@Override
	public void update(float delta) {
		int logoHeight = Gdx.graphics.getHeight() - this.logo.getHeight() - 50;
		this.playButton.setPosition(Gdx.graphics.getWidth() / 2f - this.playButton.getWidth() / 2, logoHeight - 100);
		this.exitButton.setPosition(Gdx.graphics.getWidth() / 2f - this.exitButton.getWidth() / 2, logoHeight - 200);
	}

	@Override
	public void destroy() {

	}

}
