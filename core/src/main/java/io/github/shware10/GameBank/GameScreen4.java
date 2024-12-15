package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen4 implements Screen {
    private final Game game;
    private SpriteBatch batch;

    private TextureAtlas rightWalkAtlas, attackAtlas, slideAtlas, sideMoleAtlas;
    private Animation<TextureRegion> walkAnimation, attackAnimation, slideAnimation, sideMoleAnimation;
    private Animation<TextureRegion> currentAnimation;

    private Vector2 dinosaurPosition;
    private Vector2 dinosaurVelocity;
    private float gravity = -1000f;
    private boolean isJumping = false;
    private boolean isAttacking = false;
    private boolean isSliding = false;

    private Array<Obstacle> obstacles;
    private long lastObstacleTime;
    private float obstacleSpeed = 400f;
    private float stateTime;
    private float score = 0;

    private float initialTouchY;
    private float groundLevel = 100;

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

        // FreeTypeFontGenerator를 사용하여 커스텀 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("zai_PencilTypewriter.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100; // 폰트 크기
        parameter.color = Color.WHITE; // 폰트 색상
        font = generator.generateFont(parameter);
        generator.dispose(); // 생성기 해제


        // Load animations
        rightWalkAtlas = new TextureAtlas("penguin_Right_Walk.atlas");
        attackAtlas = new TextureAtlas("penguin_Right_Attack.atlas");
        slideAtlas = new TextureAtlas("penguin_Right_Slide.atlas");
        sideMoleAtlas = new TextureAtlas("Side_Mole.atlas");

        walkAnimation = new Animation<>(0.1f, rightWalkAtlas.findRegions("RightWalk"), Animation.PlayMode.LOOP);
        attackAnimation = new Animation<>(0.1f, attackAtlas.findRegions("penguin_RightAtack"), Animation.PlayMode.LOOP);
        slideAnimation = new Animation<>(0.1f, slideAtlas.findRegions("penguin_RightSlide"), Animation.PlayMode.LOOP);

        // Initialize Side_Mole animation
        Array<TextureAtlas.AtlasRegion> moleFrames = sideMoleAtlas.findRegions("Side_Mole");
        if (moleFrames == null || moleFrames.size == 0) {
            throw new IllegalStateException("No frames found for 'Side_Mole'.");
        }
        sideMoleAnimation = new Animation<>(0.1f, moleFrames, Animation.PlayMode.LOOP);


        currentAnimation = walkAnimation;

        dinosaurPosition = new Vector2(50, groundLevel);
        dinosaurVelocity = new Vector2(0, 0);

        obstacles = new Array<>();
        spawnObstacle();

        stateTime = 0f;
    }

    private void spawnObstacle() {
        float obstacleY = MathUtils.randomBoolean() ? 100 : 600;
        Obstacle obstacle = new Obstacle(Gdx.graphics.getWidth(), obstacleY);
        obstacles.add(obstacle);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        // 중력 및 바닥 충돌 처리
        if (dinosaurPosition.y > groundLevel || isJumping) {
            dinosaurVelocity.y += gravity * delta;
        }
        if (dinosaurPosition.y < groundLevel) {
            dinosaurPosition.y = groundLevel;
            isJumping = false;
            dinosaurVelocity.y = 0;
        }

        dinosaurPosition.mulAdd(dinosaurVelocity, delta);

        // 애니메이션 완료 상태 확인
        if (isSliding && slideAnimation.isAnimationFinished(stateTime)) {
            isSliding = false;
            currentAnimation = walkAnimation;
        }
        if (isAttacking && attackAnimation.isAnimationFinished(stateTime)) {
            isAttacking = false;
            currentAnimation = walkAnimation;
        }

        // 사용자 입력 처리
        handleInput();

        // 장애물 업데이트
        updateObstacles(delta);

        batch.begin();

        // 플레이어 애니메이션 렌더링
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, dinosaurPosition.x, dinosaurPosition.y);

        // 장애물 애니메이션 렌더링
        for (Obstacle obstacle : obstacles) {
            TextureRegion obstacleFrame = sideMoleAnimation.getKeyFrame(obstacle.stateTime, true);
            batch.draw(obstacleFrame, obstacle.x, obstacle.y);
        }

        // 스코어 텍스트 그리기
        String scoreText = "" + (int)score;
        GlyphLayout layout = new GlyphLayout(font, scoreText);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2;
        float textY = Gdx.graphics.getHeight() - 200;
        font.draw(batch, scoreText, textX, textY);

        batch.end();
    }



    private void handleInput() {
        if (Gdx.input.isTouched()) {
            if (initialTouchY == 0) initialTouchY = Gdx.input.getY();

            float deltaY = initialTouchY - Gdx.input.getY();
            if (deltaY > 50 && !isJumping && !isSliding && !isAttacking) {
                // 점프 애니메이션
                dinosaurVelocity.y = 1000f;
                isJumping = true;
                currentAnimation = walkAnimation; // 점프는 걷기로 돌아가기
            } else if (deltaY < -50 && !isSliding&& !isAttacking) {
                // 슬라이드 애니메이션
                isSliding = true;
                currentAnimation = slideAnimation;
                dinosaurVelocity.y = 0f;
                dinosaurPosition.y = 100;
                stateTime = 0f;
            } else if (deltaY == 0 && !isAttacking && !isSliding) {
                // 공격 애니메이션
                isAttacking = true;
                currentAnimation = attackAnimation;
                stateTime = 0f;
            }
        } else {
            initialTouchY = 0;
        }
    }



    private void updateObstacles(float delta) {
        long spawnDelay = MathUtils.clamp(2000000000 - (int) (score / 20) * 100000000, 1000000000, 2000000000);

        if (TimeUtils.nanoTime() - lastObstacleTime > spawnDelay) {
            spawnObstacle();
        }

        Iterator<Obstacle> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Obstacle obstacle = iter.next();
            obstacle.x -= obstacleSpeed * delta;
            obstacle.stateTime += delta;

            Rectangle obstacleBounds = obstacle.getBounds(
                sideMoleAnimation.getKeyFrame(0).getRegionWidth(),
                sideMoleAnimation.getKeyFrame(0).getRegionHeight()
            );
            Rectangle playerBounds = new Rectangle(
                dinosaurPosition.x,
                dinosaurPosition.y,
                walkAnimation.getKeyFrame(stateTime).getRegionWidth(),
                walkAnimation.getKeyFrame(stateTime).getRegionHeight()
            );

            if (isAttacking && obstacleBounds.overlaps(playerBounds)) {
                iter.remove();
                score++;
            } else if (obstacleBounds.overlaps(playerBounds)) {
                gameOver();
            }

            if (obstacle.x + obstacleBounds.width < 0) {
                iter.remove();
                score++;
            }
        }
    }

    private void gameOver() {
        game.setScreen(new GameOverScreen(game, this.getClass(), score));
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
        rightWalkAtlas.dispose();
        attackAtlas.dispose();
        slideAtlas.dispose();
        sideMoleAtlas.dispose();
    }

    private static class Obstacle {
        float x, y;
        float stateTime;

        Obstacle(float x, float y) {
            this.x = x;
            this.y = y;
            this.stateTime = 0f;
        }

        Rectangle getBounds(float width, float height) {
            return new Rectangle(x, y, width, height);
        }
    }
}
