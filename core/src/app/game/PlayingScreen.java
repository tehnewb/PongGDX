package app.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import game.GameApplicationListener;
import game.library.font.FontBuilder;
import game.library.font.FontData;
import game.library.screen.GameScreen;

public class PlayingScreen extends GameScreen {

	private final float PADDLE_SPEED = 1000f; // The speed at which the paddle moves every frame
	private float BALL_SPEED = 400f; // The speed at which the ball travels every frame
	private final int PADDLE_HEIGHT = 180; // The width of both player paddles
	private final int PADDLE_WIDTH = 40; // The height of both player paddles
	private final int SIDE_PADDING = 100; // The padding between the edge of the screen and the player paddles
	private final int BALL_RADIUS = 15; // The radius of the ball
	private final int COUNTDOWN_TIME = 5; // The time in seconds before the ball releases
	private final int DIFFICULTY_COUNTDOWN_TIME = 15; // The time in seconds before the ball increases speed

	private Rectangle leftScoreBounds, rightScoreBounds; //The bounds each opposing player must hit to score a point
	private Rectangle leftPaddle, rightPaddle; // The bounds of each player's paddle
	private Rectangle ballBounds;
	private Circle ball;
	private int leftScore, rightScore;
	private int ballDeltaX, ballDeltaY;

	private ShapeRenderer shaper;
	private BitmapFont scoreFont;
	private GlyphLayout fontLayout;

	private float ballDifficultyTimer;
	private float ballResetTimer;
	private boolean ballMoving;

	public PlayingScreen(GameApplicationListener game) {
		super(game);
	}

	@Override
	public void create() {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		this.leftPaddle = new Rectangle(SIDE_PADDING, (screenHeight - PADDLE_HEIGHT) / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
		this.rightPaddle = new Rectangle(screenWidth - SIDE_PADDING - PADDLE_WIDTH, (screenHeight - PADDLE_HEIGHT) / 2, PADDLE_WIDTH, PADDLE_HEIGHT);

		this.leftScoreBounds = new Rectangle(0, 0, SIDE_PADDING / 2, screenHeight);
		this.rightScoreBounds = new Rectangle(screenWidth - SIDE_PADDING, 0, SIDE_PADDING / 2, screenHeight);

		this.shaper = new ShapeRenderer();

		this.scoreFont = FontData.generateFont("Arial", FontBuilder.create().borderColor(Color.BLACK).borderWidth(1).size(25).color(Color.WHITE).toParameter());
		this.fontLayout = new GlyphLayout();
		this.resetBall(false);
		Gdx.graphics.setResizable(true);
	}

	@Override
	public void render() {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		String leftScoreString = "Left Score:  " + leftScore;
		String rightScoreString = "Right Score:  " + rightScore;
		String countdownString = "The ball will release in " + (int) Math.ceil(this.ballResetTimer) + " seconds!";
		String difficultyString = "The ball will increase speed in " + (int) Math.ceil(this.ballDifficultyTimer) + " seconds!";

		this.screenBatch.begin();
		/**
		 * Display left score
		 */
		this.fontLayout.setText(this.scoreFont, leftScoreString);
		this.scoreFont.draw(this.screenBatch, leftScoreString, SIDE_PADDING * 2, screenHeight - this.fontLayout.height * 2);

		/**
		 * Display right score
		 */
		this.fontLayout.setText(this.scoreFont, rightScoreString);
		this.scoreFont.draw(this.screenBatch, rightScoreString, screenWidth - this.fontLayout.width - SIDE_PADDING * 2, screenHeight - this.fontLayout.height * 2);

		/**
		 * Display countdown timer
		 */
		if (!this.ballMoving) {
			this.fontLayout.setText(this.scoreFont, countdownString);
			this.scoreFont.draw(this.screenBatch, countdownString, (screenWidth - this.fontLayout.width) / 2, this.fontLayout.height * 3);
		} else {
			this.fontLayout.setText(this.scoreFont, difficultyString);
			this.scoreFont.draw(this.screenBatch, difficultyString, (screenWidth - this.fontLayout.width) / 2, this.fontLayout.height * 3);
		}
		this.screenBatch.end();

		this.shaper.begin(ShapeType.Filled);
		this.shaper.setProjectionMatrix(this.batchCamera.combined);

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
			this.resetBall(false);
			this.BALL_SPEED += 10; // increase ball speed every time someone scores
		}
		if (this.ballBounds.overlaps(this.rightScoreBounds) || this.ballBounds.x > screenWidth) {
			this.leftScore++;
			this.resetBall(false);

			this.BALL_SPEED += 10; // increase ball speed every time someone scores
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
	public void update(float delta) {
		this.ballResetTimer -= delta;

		if (this.ballMoving) {
			this.ballDifficultyTimer -= delta;
		}
		if (this.ballResetTimer <= 0 && !ballMoving) {
			this.resetBall(true);
		}

		if (this.ballDifficultyTimer <= 0) {
			this.BALL_SPEED += 15;
			this.ballDifficultyTimer = DIFFICULTY_COUNTDOWN_TIME;
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		this.leftPaddle.setX(SIDE_PADDING);
		this.rightPaddle.setX(width - SIDE_PADDING - PADDLE_WIDTH);
		this.leftScoreBounds = new Rectangle(0, 0, SIDE_PADDING, height);
		this.rightScoreBounds = new Rectangle(width - SIDE_PADDING, 0, SIDE_PADDING, height);
		resetBall(false);
	}

	@Override
	public void destroy() {

	}

	public void resetBall(boolean beginMovement) {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		if (beginMovement) {
			Random random = new Random();
			this.ballDeltaX = random.nextBoolean() ? -1 : 1;
			this.ballDeltaY = random.nextBoolean() ? -1 : 1;
		} else {
			this.ballDeltaX = 0;
			this.ballDeltaY = 0;
		}
		this.ballDifficultyTimer = DIFFICULTY_COUNTDOWN_TIME;
		this.ballResetTimer = COUNTDOWN_TIME;
		this.ballMoving = beginMovement;
		this.ball = new Circle((screenWidth - BALL_RADIUS) / 2, (screenHeight - BALL_RADIUS) / 2, BALL_RADIUS);
		this.ballBounds = new Rectangle(ball.x, ball.y, ball.radius * 2, ball.radius * 2);
	}

}
