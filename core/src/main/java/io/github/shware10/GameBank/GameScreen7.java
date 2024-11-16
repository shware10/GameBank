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

public class GameScreen7 implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private float stateTime;
    private boolean isJumping = false; // 점프 상태를 관리하는 변수
    private float characterX; // 캐릭터의 X 위치
    private float characterY = 100; // 캐릭터의 Y 위치 (고정된 위치)
    private float speed = 300f; // 캐릭터 이동 속도
    private int currentLane = 1; // 캐릭터의 현재 lane (0: 왼쪽, 1: 가운데, 2: 오른쪽)
    private float targetX; // 목표 X 위치
    private float moveSpeed = 500f; // 캐릭터의 부드러운 이동 속도
    private Texture krabCanTxt;
    private float krabCanY;
    private float krabCanSpeed = 200f;
    private float characterSpeed;

    private TextureAtlas leftWalkAtlas, rightWalkAtlas, leftJumpAtlas, rightJumpAtlas;
    private Animation<TextureRegion> leftWalkAnimation;
    private Animation<TextureRegion> rightWalkAnimation;
    private Animation<TextureRegion> leftJumpAnimation;
    private Animation<TextureRegion> rightJumpAnimation;
    private Animation<TextureRegion> currentAnimation;

    private float touchStartX = -1; // 슬라이드 시작 X 좌표
    private float touchEndX = -1;   // 슬라이드 끝 X 좌표

    public GameScreen7(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        krabCanTxt = new Texture(Gdx.files.internal("krabCan.png"));

        // 각 애니메이션에 대한 TextureAtlas 로드
        leftWalkAtlas = new TextureAtlas("penguin_Left_Walk.atlas"); // 왼쪽 이동 애니메이션 아틀라스
        rightWalkAtlas = new TextureAtlas("penguin_Right_Walk.atlas"); // 오른쪽 이동 애니메이션 아틀라스
        leftJumpAtlas = new TextureAtlas("penguin_Left_Jump.atlas");
        rightJumpAtlas = new TextureAtlas("penguin_Right_Jump.atlas");

        // 각 애니메이션 정의
        leftWalkAnimation = new Animation<>(0.1f, leftWalkAtlas.findRegions("LeftWalk"), Animation.PlayMode.LOOP);
        rightWalkAnimation = new Animation<>(0.1f, rightWalkAtlas.findRegions("RightWalk"), Animation.PlayMode.LOOP);
        leftJumpAnimation = new Animation<>(0.15f, leftJumpAtlas.findRegions("penguin_LeftJump"), Animation.PlayMode.NORMAL);
        rightJumpAnimation = new Animation<>(0.15f, rightJumpAtlas.findRegions("penguin_RightJump"), Animation.PlayMode.NORMAL);

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

        krabCanY -= krabCanSpeed * delta;
        if (krabCanY < -krabCanTxt.getHeight()) {
            krabCanY = Gdx.graphics.getHeight() - krabCanTxt.getHeight() - 10; // 화면 상단에서 다시 시작
        }

        // 점프 애니메이션이 끝났는지 확인
        if (isJumping && currentAnimation.isAnimationFinished(stateTime)) {
            isJumping = false;
            currentAnimation = leftWalkAnimation; // 기본 애니메이션은 왼쪽으로 설정
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
        characterX += (targetX - characterX) * Math.min(1, moveSpeed * delta);

        // 현재 애니메이션의 프레임을 가져오기
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, characterX, characterY); // 캐릭터를 화면에 그리기
        float x = (Gdx.graphics.getWidth() - krabCanTxt.getWidth()) / 2f; // 중앙에 맞추기 위한 x 좌표
        float y = Gdx.graphics.getHeight() - krabCanTxt.getHeight() - 10; // 화면 상단에 약간의 여백을 둔다 (y 좌표)

        float krabCanX = (Gdx.graphics.getWidth() - krabCanTxt.getWidth()) / 2f; // 중앙에 맞추기 위한 x 좌표
        batch.draw(krabCanTxt, krabCanX, krabCanY); // krabCan을 새로운 y 좌표에 그리기
        batch.end();

        // 기존 코드에 선 그리기 부분
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // 선을 더 두껍게 표현하려면 Filled 사용
        shapeRenderer.setColor(1, 1, 1, 1);
        float laneWidth = Gdx.graphics.getWidth() / 3f;
        shapeRenderer.rectLine(laneWidth, 0, laneWidth, Gdx.graphics.getHeight(), 5); // 선 두께 5
        shapeRenderer.rectLine(2 * laneWidth, 0, 2 * laneWidth, Gdx.graphics.getHeight(), 5);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        // 뷰포트 업데이트
    }

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
        leftWalkAtlas.dispose();
        rightWalkAtlas.dispose();
        leftJumpAtlas.dispose();
        rightJumpAtlas.dispose();
    }
}
