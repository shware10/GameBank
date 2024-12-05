package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameScreen7 implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private float stateTime;
    private boolean isJumping = false;
    private float characterX;
    private float characterY = 100;
    private int currentLane = 1;
    private float targetX;
    private float moveSpeed = 1000f;
    private boolean isGameOver = false;
    private String gameOverMessage = "";
    private float obstacleSpeed = 500f; // 장애물 초기 속도
    private float speedIncreaseRate = 10f; // 속도 증가율
    // 점수 변수와 시간 관리 변수 추가
    private int score = 0; // 점수
    private float scoreTimer = 0; // 점수 갱신을 위한 타이머
    private TextureAtlas orcaAtlas;  // Top_Orca.atlas
    private TextureAtlas moleAtlas;  // Top_Mole.atlas
    private Animation<TextureRegion> moleAnimation; // Top_Mole 애니메이션
    private Animation<TextureRegion> orcaAnimation; // Top_Orca 애니메이션


    private List<Obstacle> obstacles;
    private float obstacleSpawnTimer;
    private float obstacleSpawnInterval = 1.0f;
    private Random random;

    private TextureAtlas leftWalkAtlas;
    private TextureAtlas topSlideAtlas;
    private TextureAtlas leftSlideAtlas;
    private TextureAtlas rightSlideAtlas;
    private Animation<TextureRegion> topSlideAnimation;

    private Animation<TextureRegion> currentAnimation;

    private float touchStartX = -1;
    private float touchEndX = -1;

    private boolean isSlide = false;

    private Texture obstacleTexture; // 장애물 텍스처

    private Stage stage;  // UI를 위한 Stage
    private Skin skin;    // UI 스타일을 위한 Skin
    private TextButton restartButton;  // 재시작 버튼
    private TextButton backToLobbyButton;  // 로비로 돌아가기 버튼

    private enum AnimationState {
        IDLE,
        LEFT_SLIDE,
        RIGHT_SLIDE
    }

    public class Obstacle {
        private Animation<TextureRegion> animation;
        private float x;
        private float y;
        private float stateTime; // 애니메이션 상태 시간
        private float speed = 500f;

        public Obstacle(Animation<TextureRegion> animation, float x, float y) {
            this.animation = animation;
            this.x = x;
            this.y = y;
            this.stateTime = 0f;
        }

        public void update(float delta) {
            this.y -= speed * delta;
            this.stateTime += delta; // 애니메이션 진행
        }

        public boolean isOutOfScreen() {
            return this.y + animation.getKeyFrame(0).getRegionHeight() < 0;
        }

        public TextureRegion getCurrentFrame() {
            return animation.getKeyFrame(stateTime, true); // 반복 애니메이션
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }

    private float calculateTargetX() {
        float laneWidth = Gdx.graphics.getWidth() / 3f;
        return currentLane * laneWidth + (laneWidth - topSlideAnimation.getKeyFrame(0).getRegionWidth()) / 2;
    }

    // 캐릭터 이동 처리 함수
    private void moveCharacterToTarget(float delta) {
        // 목표 위치로 부드럽게 이동
        if (Math.abs(targetX - characterX) > 1) {
            characterX += Math.signum(targetX - characterX) * moveSpeed * delta;
        } else {
            characterX = targetX;  // 목표 위치에 도달하면 정확히 이동
        }
    }

    private boolean isCollision(float characterX, float characterY, TextureRegion characterTexture, Obstacle obstacle) {
        // 캐릭터와 장애물의 충돌 범위를 줄이기 위한 패딩 값
        float characterPadding = 50f; // 캐릭터의 충돌 범위를 줄이는 값
        float obstaclePadding = 50f; // 장애물의 충돌 범위를 줄이는 값

        TextureRegion obstacleTexture = obstacle.getCurrentFrame(); // 현재 애니메이션 프레임

        Rectangle characterBounds = new Rectangle(
            characterX + characterPadding, // 패딩 적용
            characterY + characterPadding,
            characterTexture.getRegionWidth() - 2 * characterPadding, // 너비 감소
            characterTexture.getRegionHeight() - 2 * characterPadding // 높이 감소
        );

        Rectangle obstacleBounds = new Rectangle(
            obstacle.getX() + obstaclePadding, // 패딩 적용
            obstacle.getY() + obstaclePadding,
            obstacleTexture.getRegionWidth() - 2 * obstaclePadding, // 너비 감소
            obstacleTexture.getRegionHeight() - 2 * obstaclePadding // 높이 감소
        );

        return characterBounds.overlaps(obstacleBounds);
    }


    private void restartGame() {
        isGameOver = false;
        gameOverMessage = "";
        characterX = Gdx.graphics.getWidth() *3/ 7f;
        currentLane = 1;
        targetX = characterX;
        obstacles.clear();
        stateTime = 0f;
        obstacleSpawnTimer = 0f;
    }

    public GameScreen7(Game game) {
        this.game = game;
        this.obstacles = new ArrayList<>();
        this.random = new Random();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        obstacleTexture = new Texture(Gdx.files.internal("TrashBlock_1.png"));

        leftWalkAtlas = new TextureAtlas("penguin_Left_Walk.atlas");
        topSlideAtlas = new TextureAtlas("Top_Slide_Idle.atlas");
        leftSlideAtlas = new TextureAtlas("Top_Slide_Left.atlas");
        rightSlideAtlas = new TextureAtlas("Top_Slide_Right.atlas");

        moleAtlas = new TextureAtlas(Gdx.files.internal("Top_Mole.atlas"));
        orcaAtlas = new TextureAtlas(Gdx.files.internal("Top_Orca.atlas"));

        moleAnimation = new Animation<>(0.1f, moleAtlas.findRegions("Top_Mole"), Animation.PlayMode.LOOP);
        orcaAnimation = new Animation<>(0.1f, orcaAtlas.findRegions("Top_Orca"), Animation.PlayMode.LOOP);

        topSlideAnimation = new Animation<>(0.1f, topSlideAtlas.findRegions("Top_Slide_Idle"), Animation.PlayMode.LOOP);

        currentAnimation = topSlideAnimation;
        stateTime = 0f;

        characterX = Gdx.graphics.getWidth() *3/ 7f;
        targetX = characterX;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);  // Stage가 터치 이벤트를 처리하도록 설정

        // Skin 로드 (자신의 skin.json, uiskin.atlas, font.fnt 등을 준비해야 합니다)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // 재시작 버튼 생성
        restartButton = new TextButton("Restart", skin);
        restartButton.setPosition(Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f - 50);
        restartButton.addListener(event -> {
            restartGame();  // 재시작 함수 호출
            return true;
        });

        // 로비로 돌아가기 버튼 생성
        backToLobbyButton = new TextButton("Back to Lobby", skin);
        backToLobbyButton.setPosition(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 100);
        backToLobbyButton.addListener(event -> {
            game.setScreen(new LobbyScreen(game));  // 로비 화면으로 이동
            return true;
        });

        // 버튼들을 Stage에 추가
        stage.addActor(restartButton);
        stage.addActor(backToLobbyButton);
    }

    private AnimationState currentState = AnimationState.IDLE;
    private float animationChangeTimer = 1f; // 애니메이션 상태 유지 시간


    @Override
    public void render(float delta) {
        if (isGameOver) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // 배경 색 설정 (게임 오버 화면)
            batch.begin();
            BitmapFont font = new BitmapFont();
            font.getData().setScale(10f);
            font.draw(batch, "Game Over", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f + 50);
            font.draw(batch, gameOverMessage, Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);

            batch.end();

            // Stage를 그린다
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // Stage 업데이트
            stage.draw();  // UI 렌더링

            return;
        }

        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        obstacleSpawnTimer += delta;
        scoreTimer += delta;

        // 점수 업데이트
        if (scoreTimer >= 3.0f) {
            score += 100;
            scoreTimer = 0;
        }

        for (Iterator<Obstacle> iterator = obstacles.iterator(); iterator.hasNext();) {
            Obstacle obstacle = iterator.next();
            obstacle.update(delta);

            if (isCollision(characterX, characterY, currentAnimation.getKeyFrame(stateTime, true), obstacle)) {
                isGameOver = true;
                gameOverMessage = "Final Score: " + score;
                return;
            }

            if (obstacle.isOutOfScreen()) {
                iterator.remove();
            }
        }


        // 장애물 생성
        if (obstacleSpawnTimer >= obstacleSpawnInterval) {
            spawnObstacle();
            obstacleSpawnTimer = 0;
        }

        // 화면 슬라이드 처리
        if (Gdx.input.isTouched()) {
            if (touchStartX == -1) {
                touchStartX = Gdx.input.getX();  // 터치 시작 위치 저장
            }
            touchEndX = Gdx.input.getX();  // 터치 종료 위치 업데이트
        } else if (touchStartX != -1 && touchEndX != -1) {
            // 슬라이드가 끝났을 때 좌우 방향을 판단하여 레인 변경
            if (touchEndX - touchStartX > 100 && currentLane < 2) {
                currentLane++;  // 오른쪽으로 슬라이드
            } else if (touchStartX - touchEndX > 100 && currentLane > 0) {
                currentLane--;  // 왼쪽으로 슬라이드
            }

            // 슬라이드가 끝난 후, 목표 위치를 새로 설정
            touchStartX = -1;  // 슬라이드가 끝나면 초기화
            touchEndX = -1;
            targetX = calculateTargetX();  // 현재 lane에 맞춰 목표 위치 갱신

            // 슬라이드가 발생했으므로 이동 처리 함수 호출
            isSlide = true;
        }

        // 슬라이드가 발생했을 때만 캐릭터 이동 처리
        if (isSlide) {
            moveCharacterToTarget(delta);
            isSlide = false;  // 슬라이드가 끝났으므로 이동 후 리셋
        }

        characterX = MathUtils.lerp(characterX, targetX, 0.1f); // 부드러운 이동

        // 현재 애니메이션 항상 Top_Slide_Idle 유지
        currentAnimation = topSlideAnimation;
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        // 렌더링
        batch.begin();
        batch.draw(currentAnimation.getKeyFrame(stateTime, true), characterX, characterY);
        for (Obstacle obstacle : obstacles) {
            batch.draw(obstacle.getCurrentFrame(), obstacle.getX(), obstacle.getY());
        }

        // 점수 표시
        BitmapFont font = new BitmapFont();
        font.getData().setScale(5f);
        font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 10);
        batch.end();

        // 레인 구분 선
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        float laneWidth = Gdx.graphics.getWidth() / 3f;
        shapeRenderer.rectLine(laneWidth, 0, laneWidth, Gdx.graphics.getHeight(), 5);
        shapeRenderer.rectLine(2 * laneWidth, 0, 2 * laneWidth, Gdx.graphics.getHeight(), 5);
        shapeRenderer.end();
    }

    private void spawnObstacle() {
        int lane = random.nextInt(3); // 랜덤 레인 선택
        float laneWidth = Gdx.graphics.getWidth() / 3f;
        float x = lane * laneWidth + (laneWidth - moleAnimation.getKeyFrame(0).getRegionWidth()) / 2;
        float y = Gdx.graphics.getHeight();

        // 랜덤으로 Mole 또는 Orca 선택
        Animation<TextureRegion> selectedAnimation = random.nextBoolean() ? moleAnimation : orcaAnimation;

        if (selectedAnimation == orcaAnimation) {
            x -= 30; // Top_Orca의 x 값을 -10으로 설정
        }

        obstacles.add(new Obstacle(selectedAnimation, x, y));
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
        shapeRenderer.dispose();
        moleAtlas.dispose();
        orcaAtlas.dispose();
    }
}

