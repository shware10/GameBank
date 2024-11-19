package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class GameScreen7 implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private float stateTime;
    private boolean isJumping = false; // 점프 상태를 관리하는 변수
    private float characterX; // 캐릭터의 X 위치
    private float characterY = 100; // 캐릭터의 Y 위치 (고정된 위치)
    private int currentLane = 1; // 캐릭터의 현재 lane (0: 왼쪽, 1: 가운데, 2: 오른쪽)
    private float targetX; // 목표 X 위치
    private float moveSpeed = 1000f; // 캐릭터의 부드러운 이동 속도
    private Texture krabCanTxt;
    private float krabCanY;

    private List<Obstacle> obstacles;
    private float obstacleSpawnTimer;
    private float obstacleSpawnInterval = 1.0f; // 장애물 생성 간격 (초 단위)
    private Random random;

    private TextureAtlas leftWalkAtlas;
    private Animation<TextureRegion> leftWalkAnimation;
    private Animation<TextureRegion> currentAnimation;

    private float touchStartX = -1; // 슬라이드 시작 X 좌표
    private float touchEndX = -1;   // 슬라이드 끝 X 좌표

    public class Obstacle {
        private Texture texture;
        private float x;
        private float y;
        private float speed = 500f; // 장애물의 하강 속도

        public Obstacle(Texture texture, float x, float y) {
            this.texture = texture;
            this.x = x;
            this.y = y;
        }



        public void update(float delta) {
            // 장애물이 아래로 내려가도록 업데이트
            this.y -= speed * delta;
        }

        public boolean isOutOfScreen() {
            // 장애물이 화면 아래로 나갔는지 확인
            return this.y + texture.getHeight() < 0;
        }

        public Texture getTexture() {
            return texture;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }

    private boolean isCollision(float characterX, float characterY, TextureRegion characterTexture, Obstacle obstacle) {
        // 캐릭터의 경계 생성
        Rectangle characterBounds = new Rectangle(
            characterX,
            characterY,
            characterTexture.getRegionWidth(),
            characterTexture.getRegionHeight()
        );

        // 장애물의 경계 생성
        Rectangle obstacleBounds = new Rectangle(
            obstacle.getX(),
            obstacle.getY(),
            obstacle.getTexture().getWidth(),
            obstacle.getTexture().getHeight()
        );

        // 경계가 겹치는지 확인
        return characterBounds.overlaps(obstacleBounds);
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

        krabCanTxt = new Texture(Gdx.files.internal("krabCan.png"));

        // 애니메이션에 대한 TextureAtlas 로드
        leftWalkAtlas = new TextureAtlas("penguin_Left_Walk.atlas"); // 왼쪽 이동 애니메이션 아틀라스

        // 애니메이션 정의
        leftWalkAnimation = new Animation<>(0.1f, leftWalkAtlas.findRegions("LeftWalk"), Animation.PlayMode.LOOP);

        // 초기 애니메이션을 leftWalk로 설정
        currentAnimation = leftWalkAnimation;
        stateTime = 0f;

        // 캐릭터의 초기 위치 설정 (가운데 lane)
        characterX = Gdx.graphics.getWidth() / 3f; // 가운데 lane
        targetX = characterX; // 초기 목표 위치 설정
        krabCanY = Gdx.graphics.getHeight() - krabCanTxt.getHeight() - 10;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        obstacleSpawnTimer += delta;

        // 장애물 업데이트 및 충돌 체크
        for (Iterator<Obstacle> iterator = obstacles.iterator(); iterator.hasNext();) {
            Obstacle obstacle = iterator.next();
            obstacle.update(delta);

            // 충돌 체크
            if (isCollision(characterX, characterY, leftWalkAnimation.getKeyFrame(stateTime, true), obstacle)) {
                Gdx.app.log("Collision", "Character collided with obstacle!");
                // 충돌 시 원하는 행동 수행 (예: 게임 종료, 점수 차감 등)
                // 예: 장애물을 리스트에서 제거
                iterator.remove();
            }

            // 장애물이 화면 아래로 나가면 제거
            if (obstacle.isOutOfScreen()) {
                iterator.remove();
            }
        }

        // 일정 주기마다 장애물 생성
        if (obstacleSpawnTimer >= obstacleSpawnInterval) {
            spawnObstacle();
            obstacleSpawnTimer = 0;
        }

        // 장애물 업데이트
        for (Iterator<Obstacle> iterator = obstacles.iterator(); iterator.hasNext();) {
            Obstacle obstacle = iterator.next();
            obstacle.update(delta);
            if (obstacle.isOutOfScreen()) {
                iterator.remove(); // 화면 밖으로 나간 장애물 제거
            }
        }

        // 점프 애니메이션이 끝났는지 확인
        if (isJumping && currentAnimation.isAnimationFinished(stateTime)) {
            isJumping = false;
            stateTime = 0f; // 걷기 애니메이션을 처음부터 시작
        }

        // 터치 입력 처리
        if (Gdx.input.isTouched()) {
            if (touchStartX == -1) {
                // 터치 시작 시 X 좌표 기록
                touchStartX = Gdx.input.getX();
            }
            // 터치 종료 시 X 좌표 기록
            touchEndX = Gdx.input.getX();
        } else if (touchStartX != -1 && touchEndX != -1) {
            // 슬라이드 종료 시, 터치 이동 방향에 따라 lane 변경
            if (touchEndX - touchStartX > 100 && currentLane < 2) {
                // 오른쪽으로 슬라이드
                currentLane++;
            } else if (touchStartX - touchEndX > 100 && currentLane > 0) {
                // 왼쪽으로 슬라이드
                currentLane--;
            }

            // 터치 후 초기화
            touchStartX = -1;
            touchEndX = -1;

            // 목표 위치 설정
            if (currentLane == 0) {
                targetX = 0; // 왼쪽 lane
            } else if (currentLane == 1) {
                targetX = Gdx.graphics.getWidth() / 3f; // 가운데 lane
            } else if (currentLane == 2) {
                targetX = Gdx.graphics.getWidth() * 2 / 3f; // 오른쪽 lane
            }
        }

        // 캐릭터의 위치를 부드럽게 업데이트
        if (Math.abs(targetX - characterX) > 1) { // 목표 위치에 거의 도달하지 않은 경우에만 이동
            characterX += Math.signum(targetX - characterX) * moveSpeed * delta;
        } else {
            // 목표 위치에 도달했을 때는 정확히 위치를 맞춤
            characterX = targetX;
        }

        // 현재 애니메이션의 프레임을 가져오기
        TextureRegion currentFrame = leftWalkAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, characterX, characterY); // 캐릭터를 화면에 그리기
        for (Obstacle obstacle : obstacles) {
            batch.draw(obstacle.getTexture(), obstacle.getX(), obstacle.getY());
        }
        batch.end();

        // 기존 코드에 선 그리기 부분
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // 선을 더 두껍게 표현하려면 Filled 사용
        shapeRenderer.setColor(1, 1, 1, 1);
        float laneWidth = Gdx.graphics.getWidth() / 3f;
        shapeRenderer.rectLine(laneWidth, 0, laneWidth, Gdx.graphics.getHeight(), 5); // 선 두께 5
        shapeRenderer.rectLine(2 * laneWidth, 0, 2 * laneWidth, Gdx.graphics.getHeight(), 5);
        shapeRenderer.end();
    }

    private void spawnObstacle() {
        // 장애물을 1, 2, 3번 레인 중 랜덤한 위치에 생성
        int lane = random.nextInt(3); // 0, 1, 2 중 랜덤 선택
        float laneWidth = Gdx.graphics.getWidth() / 3f;
        float x = lane * laneWidth + (laneWidth - krabCanTxt.getWidth()) / 2;
        float y = Gdx.graphics.getHeight(); // 화면 상단에서 생성

        obstacles.add(new Obstacle(krabCanTxt, x, y));
    }

    @Override
    public void resize(int width, int height) {
        // 뷰포트 업데이트
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        krabCanTxt.dispose();
    }
}
