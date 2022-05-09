package app.game;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PongGameListener implements ApplicationListener {

	private ScreenViewport viewport;

	private final float PADDLE_SPEED = 2000f; // The speed at which the paddle moves every frame
	private float BALL_SPEED = 500f; // The speed at which the ball travels every frame
	private final int PADDLE_HEIGHT = 180; // The width of both player paddles
	private final int PADDLE_WIDTH = 40; // The height of both player paddles
	private final int SIDE_PADDING = 100; // The padding between the edge of the screen and the player paddles
	private final int BALL_RADIUS = 15;

	private Rectangle leftScoreBounds, rightScoreBounds; //The bounds each opposing player must hit to score a point
	private Rectangle leftPaddle, rightPaddle; // The bounds of each player's paddle
	private Rectangle ballBounds;
	private Circle ball;
	private int leftScore, rightScore;
	private int ballDeltaX, ballDeltaY;

	private ShapeRenderer shaper;
	private SpriteBatch batch;
	private BitmapFont font;
	private GlyphLayout fontLayout;

	private boolean paused; // The pause flag

	@Override
	public void create() {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		this.leftPaddle = new Rectangle(SIDE_PADDING, (screenHeight - PADDLE_HEIGHT) / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
		this.rightPaddle = new Rectangle(screenWidth - SIDE_PADDING - PADDLE_WIDTH, (screenHeight - PADDLE_HEIGHT) / 2, PADDLE_WIDTH, PADDLE_HEIGHT);

		this.leftScoreBounds = new Rectangle(0, 0, SIDE_PADDING, screenHeight);
		this.rightScoreBounds = new Rectangle(screenWidth - SIDE_PADDING, 0, SIDE_PADDING, screenHeight);

		this.shaper = new ShapeRenderer();
		this.batch = new SpriteBatch();

		/**
		 * Generating the game font
		 */
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Big Pixel Font.otf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		parameter.color = Color.WHITE;
		this.font = generator.generateFont(parameter);
		generator.dispose();
		this.fontLayout = new GlyphLayout();

		this.viewport = new ScreenViewport(new OrthographicCamera(screenWidth, screenHeight));
		this.resetBall();
	}

	@Override
	public void render() {
		if (paused) {
			return;
		}
		ScreenUtils.clear(0, 0, 0, 1);

		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		this.batch.setProjectionMatrix(this.viewport.getCamera().combined);

		String leftScoreString = "Left Score: " + leftScore;
		String rightScoreString = "Right Score: " + rightScore;

		this.batch.begin();
		/**
		 * Display left score
		 */
		this.fontLayout.setText(this.font, leftScoreString);
		this.font.draw(this.batch, leftScoreString, SIDE_PADDING * 2, screenHeight - this.fontLayout.height * 2);

		/**
		 * Display right score
		 */
		this.fontLayout.setText(this.font, rightScoreString);
		this.font.draw(this.batch, rightScoreString, screenWidth - this.fontLayout.width - SIDE_PADDING * 2, screenHeight - this.fontLayout.height * 2);
		this.batch.end();

		this.shaper.begin(ShapeType.Filled);
		this.shaper.setProjectionMatrix(this.viewport.getCamera().combined);

		/**
		 * Display ball
		 */
		this.shaper.circle(this.ball.x, this.ball.y, this.ball.radius);

		/**
		 * Display left paddle
		 */
		this.shaper.rect(this.leftPaddle.x, this.leftPaddle.y, this.leftPaddle.width, this.leftPaddle.height);
		/**
		 * Display right paddle
		 */
		this.shaper.rect(this.rightPaddle.x, this.rightPaddle.y, this.rightPaddle.width, this.rightPaddle.height);

		this.shaper.end();

		float delta = Gdx.graphics.getDeltaTime();
		float ballMovementDelta = BALL_SPEED * delta;
		float paddleMovementDelta = PADDLE_SPEED * delta;

		int ballSize = BALL_RADIUS * 2;

		/**
		 * Ball physics
		 */

		this.ball.x += ballMovementDelta * this.ballDeltaX;
		this.ball.y += ballMovementDelta * this.ballDeltaY;

		if (this.ball.x <= 0) this.ballDeltaX = -1;
		if (this.ball.x - ballSize >= screenWidth) this.ballDeltaX = 1;
		if (this.ball.y <= 0) this.ballDeltaY = 1;
		if (this.ball.y - ballSize >= screenHeight) this.ballDeltaY = -1;

		this.ballBounds.set(this.ball.x, this.ball.y, this.BALL_RADIUS * 2, this.BALL_RADIUS * 2);

		if (this.ballBounds.overlaps(this.leftPaddle)) this.ballDeltaX = 1;
		if (this.ballBounds.overlaps(this.rightPaddle)) this.ballDeltaX = -1;

		if (this.ballBounds.overlaps(this.leftScoreBounds) || this.ballBounds.x < 0) {
			this.rightScore++;
			this.resetBall();
			this.BALL_SPEED += 25;
		}
		if (this.ballBounds.overlaps(this.rightScoreBounds) || this.ballBounds.x > screenWidth) {
			this.leftScore++;
			this.resetBall();

			this.BALL_SPEED += 25;
		}

		/**
		 * Move left paddle up
		 */
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			this.leftPaddle.y = Math.min(screenHeight - PADDLE_HEIGHT, this.leftPaddle.y + paddleMovementDelta);
		}

		/**
		 * Move left paddle down
		 */
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			this.leftPaddle.y = Math.max(0, this.leftPaddle.y - paddleMovementDelta);
		}

		/**
		 * Move right paddle up
		 */
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			this.rightPaddle.y = Math.min(screenHeight - PADDLE_HEIGHT, this.rightPaddle.y + paddleMovementDelta);
		}

		/**
		 * Move right paddle down
		 */
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			this.rightPaddle.y = Math.max(0, this.rightPaddle.y - paddleMovementDelta);
		}

	}

	@Override
	public void dispose() {}

	@Override
	public void resize(int width, int height) {
		this.viewport.update(width, height, true);

		this.leftPaddle.setX(SIDE_PADDING);
		this.rightPaddle.setX(width - SIDE_PADDING - PADDLE_WIDTH);
		this.leftScoreBounds = new Rectangle(0, 0, SIDE_PADDING, height);
		this.rightScoreBounds = new Rectangle(width - SIDE_PADDING, 0, SIDE_PADDING, height);
		resetBall();
	}

	@Override
	public void pause() {
		this.paused = true;
	}

	@Override
	public void resume() {
		this.paused = false;
	}

	public void resetBall() {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		Random random = new Random();
		this.ballDeltaX = random.nextBoolean() ? -1 : 1;
		this.ballDeltaY = random.nextBoolean() ? -1 : 1;
		this.ball = new Circle((screenWidth - BALL_RADIUS) / 2, (screenHeight - BALL_RADIUS) / 2, BALL_RADIUS);
		this.ballBounds = new Rectangle(ball.x, ball.y, ball.radius * 2, ball.radius * 2);
	}
}
