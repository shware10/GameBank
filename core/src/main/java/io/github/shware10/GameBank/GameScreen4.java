package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen4 implements Screen {
    private final Game game;
    private SpriteBatch batch;

    private TextureAtlas leftWalkAtlas, rightWalkAtlas, attackAtlas, slideAtlas;
    private Animation<TextureRegion> leftwalkAnimation, walkAnimation, attackAnimation, slideAnimation;
    private Animation<TextureRegion> currentAnimation;

    private Vector2 dinosaurPosition;
    private Vector2 dinosaurVelocity;
    private float gravity = -1000f;
    private boolean isJumping = false;
    private boolean isAttacking = false;
    private boolean isSliding = false;

    private Array<Rectangle> obstacles;
    private long lastObstacleTime;
    private float obstacleSpeed = 400f;
    private float stateTime;
    private float score = 0;

    private float initialTouchY;
    private float groundLevel = 100; // 바닥 높이를 설정

    private BitmapFont font;

    public GameScreen4(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(0.5f, 0.5f, 0.5f, 1);
        font.getData().setScale(4);

        leftWalkAtlas = new TextureAtlas("penguin_Left_Walk.atlas");
        rightWalkAtlas = new TextureAtlas("penguin_Right_Walk.atlas");
        attackAtlas = new TextureAtlas("penguin_Right_Attack.atlas");
        slideAtlas = new TextureAtlas("penguin_Right_Slide.atlas");

        walkAnimation = new Animation<>(0.1f, rightWalkAtlas.findRegions("RightWalk"), Animation.PlayMode.LOOP);
        leftwalkAnimation = new Animation<>(0.1f, leftWalkAtlas.findRegions("LeftWalk"), Animation.PlayMode.LOOP);
        attackAnimation = new Animation<>(0.1f, attackAtlas.findRegions("penguin_RightAtack"), Animation.PlayMode.LOOP);
        slideAnimation = new Animation<>(0.1f, slideAtlas.findRegions("penguin_RightSlide"), Animation.PlayMode.LOOP);

        currentAnimation = walkAnimation;

        dinosaurPosition = new Vector2(50, groundLevel);
        dinosaurVelocity = new Vector2(0, 0);

        obstacles = new Array<>();
        spawnObstacle();

        stateTime = 0f;
    }

    private void spawnObstacle() {
        float obstacleY = MathUtils.random(100, 600 + leftWalkAtlas.findRegion("LeftWalk").getRegionHeight());
        if(obstacleY % 2 == 1)obstacleY = 100;
        else obstacleY = 600;
        Rectangle obstacle = new Rectangle(Gdx.graphics.getWidth(), obstacleY, leftWalkAtlas.findRegion("LeftWalk").getRegionWidth(), leftWalkAtlas.findRegion("LeftWalk").getRegionHeight());
        obstacles.add(obstacle);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        // 중력 적용 및 바닥 높이 제한
        if (dinosaurPosition.y > groundLevel || isJumping) {
            dinosaurVelocity.y += gravity * delta;
        }
        // 캐릭터가 groundLevel 아래로 내려가지 않도록 제한
        if (dinosaurPosition.y < groundLevel) {
            dinosaurPosition.y = groundLevel;
            isJumping = false;
            dinosaurVelocity.y = 0;

            if (isSliding && slideAnimation.isAnimationFinished(stateTime)) {
                isSliding = false;
                currentAnimation = walkAnimation;
            }
        }

        dinosaurPosition.mulAdd(dinosaurVelocity, delta);

        handleInput();
        updateObstacles(delta);

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, dinosaurPosition.x, dinosaurPosition.y);
        for (Rectangle obstacle : obstacles) {
            batch.draw(leftWalkAtlas.findRegion("LeftWalk"), obstacle.x, obstacle.y);
        }
        font.draw(batch, "Score: " + (int)score, Gdx.graphics.getWidth() / 2f - 110, Gdx.graphics.getHeight() - 20);
        batch.end();
    }


    private void handleInput() {
        if (Gdx.input.isTouched()) {
            if (initialTouchY == 0) initialTouchY = Gdx.input.getY();

            float deltaY = initialTouchY - Gdx.input.getY();
            if (deltaY > 50 && !isJumping && !isAttacking && !isSliding) {
                dinosaurVelocity.y = 1000f;
                isJumping = true;
                currentAnimation = walkAnimation;
            } else if (deltaY < -50 && !isSliding && !isAttacking) {
                isSliding = true;
                dinosaurPosition.y = 100;
                currentAnimation = slideAnimation;
                stateTime = 0f;
            } else if (deltaY == 0 && !isAttacking && !isSliding) {
                isAttacking = true;
                currentAnimation = attackAnimation;
                stateTime = 0f;
            }
        } else {
            initialTouchY = 0;
        }
    }

    private void updateObstacles(float delta) {
        // 점수에 따라 스폰 딜레이 감소 (최소 500ms까지 제한)
        long spawnDelay = MathUtils.clamp(2000000000 - (int)(score / 20) * 100000000, 1000000000, 2000000000);

        if (TimeUtils.nanoTime() - lastObstacleTime > spawnDelay) {
            spawnObstacle();
        }

        Iterator<Rectangle> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Rectangle obstacle = iter.next();
            obstacle.x -= obstacleSpeed * delta;

            if (isAttacking && obstacle.overlaps(new Rectangle(dinosaurPosition.x, dinosaurPosition.y, attackAnimation.getKeyFrame(stateTime).getRegionWidth(), attackAnimation.getKeyFrame(stateTime).getRegionHeight()))) {
                iter.remove();
                score++;
            } else if (obstacle.overlaps(new Rectangle(dinosaurPosition.x, dinosaurPosition.y, walkAnimation.getKeyFrame(stateTime).getRegionWidth(), walkAnimation.getKeyFrame(stateTime).getRegionHeight()))) {
                gameOver();
            }

            if (obstacle.x + obstacle.width < 0) {
                iter.remove();
                score++;
            }
        }

        if (isAttacking && attackAnimation.isAnimationFinished(stateTime)) {
            isAttacking = false;
            currentAnimation = walkAnimation;
        }

        if (isSliding && slideAnimation.isAnimationFinished(stateTime)) {
            isSliding = false;
            currentAnimation = walkAnimation;
        }
    }


    private void gameOver() {

        //game.setScreen(new GameOverScreen(game, score));

        dinosaurPosition.set(50, 100);
        dinosaurVelocity.set(0, 0);
        obstacles.clear();
        spawnObstacle();
        isAttacking = false;
        isSliding = false;
        isJumping = false;
        currentAnimation = walkAnimation;
        stateTime = 0f;
    }


    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        leftWalkAtlas.dispose();
        rightWalkAtlas.dispose();
        attackAtlas.dispose();
        slideAtlas.dispose();
    }
}
