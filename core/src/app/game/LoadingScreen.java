package app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import game.GameApplicationListener;
import game.LibraryConstants;
import game.library.font.FontBuilder;
import game.library.font.FontData;
import game.library.screen.GameScreen;

public class LoadingScreen extends GameScreen {

	private ShapeRenderer shaper;
	private VisLabel loadingLabel;
	private VisTable screenTable;

	public LoadingScreen(GameApplicationListener game) {
		super(game);
	}

	@Override
	public void create() {
		this.shaper = new ShapeRenderer();
		this.screenTable = new VisTable();
		this.screenTable.setFillParent(true);

		FontData.loadFont("Arial", Gdx.files.internal("fonts/Arial.ttf"));
		AssetManager am = LibraryConstants.getAssetManager();

		BitmapFont font = FontData.generateFont("Arial", FontBuilder.create().borderColor(Color.BLACK).borderWidth(1).size(20).color(Color.WHITE).toParameter());

		this.loadingLabel = new VisLabel("Loading Assets...", new LabelStyle(font, Color.WHITE));

		this.screenTable.add(this.loadingLabel).center();
		this.screenStage.addActor(this.screenTable);

		am.load("buttons/Up.png", Texture.class);
		am.load("buttons/Down.png", Texture.class);
		am.load("buttons/Hover.png", Texture.class);
		am.load("logo.png", Texture.class);
		
		Gdx.graphics.setResizable(false);
	}

	@Override
	public void render() {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		float rectWidth = 350;
		float rectHeight = 35;
		this.shaper.setColor(Color.GRAY);
		this.shaper.begin(ShapeType.Line);
		this.shaper.rect(screenWidth / 2f - rectWidth / 2f, screenHeight / 2f - rectHeight / 2f, rectWidth, rectHeight);
		this.shaper.end();

		rectWidth = 345;
		rectHeight = 30;

		float progress = LibraryConstants.getAssetManager().getProgress() * 100;
		this.loadingLabel.setText("Loading Assets..." + (int) (progress) + "%");

		this.shaper.begin(ShapeType.Filled);
		this.shaper.rect(screenWidth / 2f - rectWidth / 2f, screenHeight / 2f - rectHeight / 2f, rectWidth * progress / 100f, rectHeight);
		this.shaper.end();
	}

	@Override
	public void update(float delta) {
		AssetManager am = LibraryConstants.getAssetManager();
		if (!am.update()) {
			this.transitionToScreen(new MenuScreen(this.game));
		}
	}

	@Override
	public void destroy() {

	}

}
