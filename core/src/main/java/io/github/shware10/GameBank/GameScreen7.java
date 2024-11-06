package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen7 implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private Stage stage;
    private Button leftButton, jumpButton, rightButton;

    private TextureAtlas leftWalkAtlas, rightWalkAtlas, leftJumpAtlas, rightJumpAtlas;
    private Animation<TextureRegion> leftWalkAnimation;
    private Animation<TextureRegion> rightWalkAnimation;
    private Animation<TextureRegion> leftJumpAnimation;
    private Animation<TextureRegion> rightJumpAnimation;
    private Animation<TextureRegion> currentAnimation;

    private float stateTime;
    private boolean isLeft = true;
    private boolean isJumping = false; // 점프 상태를 관리하는 변수
    private float characterX; // 캐릭터의 X 위치
    private float characterY = 100; // 캐릭터의 Y 위치 (고정된 위치)
    private float speed = 300f; // 캐릭터 이동 속도

    public GameScreen7(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Stage 생성 및 초기화
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);  // 입력을 Stage가 처리하도록 설정

        // Skin과 버튼 생성
        Skin skin = new Skin(Gdx.files.internal("uiskin.json")); // 기본 스킨

        // 왼쪽 이동 버튼 생성 및 위치 설정
        leftButton = new TextButton("Left", skin);
        leftButton.setSize(200, 100);
        leftButton.setPosition(10, 10); // 좌측 하단

        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isJumping) {
                    isLeft = true;
                    currentAnimation = leftWalkAnimation;
                }
            }
        });

        // 점프 버튼 생성 및 위치 설정
        jumpButton = new TextButton("Jump", skin);
        jumpButton.setSize(200, 100);
        float jumpButtonX = (Gdx.graphics.getWidth() - 100) / 2; // 중앙 하단
        jumpButton.setPosition(jumpButtonX, 10);

        jumpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isJumping) { // 점프 중이 아닐 때만 점프 실행
                    isJumping = true;
                    currentAnimation = isLeft ? leftJumpAnimation : rightJumpAnimation;
                    stateTime = 0f; // 애니메이션이 처음부터 시작하도록 설정
                }
            }
        });

        // 오른쪽 이동 버튼 생성 및 위치 설정
        rightButton = new TextButton("Right", skin);
        rightButton.setSize(200, 100);
        rightButton.setPosition(Gdx.graphics.getWidth() - 110, 10); // 우측 하단

        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isJumping) {
                    isLeft = false;
                    currentAnimation = rightWalkAnimation;
                }
            }
        });

        // Stage에 버튼 추가
        stage.addActor(leftButton);
        stage.addActor(jumpButton);
        stage.addActor(rightButton);

        // 각 애니메이션에 대한 TextureAtlas 로드
        leftWalkAtlas = new TextureAtlas("penguin_Left_Walk.atlas"); // 왼쪽 이동 애니메이션 아틀라스
        rightWalkAtlas = new TextureAtlas("penguin_Right_Walk.atlas"); // 오른쪽 이동 애니메이션 아틀라스
        leftJumpAtlas = new TextureAtlas("penguin_Left_Jump.atlas");
        rightJumpAtlas = new TextureAtlas("penguin_Right_Jump.atlas");

        // 각 애니메이션 정의
        leftWalkAnimation = new Animation<>(0.1f, leftWalkAtlas.findRegions("LeftWalk"), Animation.PlayMode.LOOP);
        rightWalkAnimation = new Animation<>(0.1f, rightWalkAtlas.findRegions("RightWalk"), Animation.PlayMode.LOOP);
        leftJumpAnimation = new Animation<>(0.1f, leftJumpAtlas.findRegions("penguin_LeftJump"), Animation.PlayMode.NORMAL);
        rightJumpAnimation = new Animation<>(0.1f, rightJumpAtlas.findRegions("penguin_RightJump"), Animation.PlayMode.NORMAL);

        // 초기 애니메이션을 leftWalk로 설정
        currentAnimation = leftWalkAnimation;
        stateTime = 0f;
        characterX = (Gdx.graphics.getWidth() - leftWalkAnimation.getKeyFrame(0).getRegionWidth()) / 2.0f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        // 점프 애니메이션이 끝났는지 확인
        if (isJumping && currentAnimation.isAnimationFinished(stateTime)) {
            isJumping = false;
            currentAnimation = isLeft ? leftWalkAnimation : rightWalkAnimation;
            stateTime = 0f; // 걷기 애니메이션을 처음부터 시작
        }

        // 캐릭터의 위치 업데이트
        if (!isJumping) { // 점프 중이 아닐 때만 좌우 이동
            if (isLeft) {
                characterX -= speed * delta;
                if (characterX < -170) {
                    characterX = -170; // 화면 왼쪽 끝을 벗어나지 않도록 설정
                }
            } else {
                characterX += speed * delta;
                if (characterX + currentAnimation.getKeyFrame(stateTime).getRegionWidth() > Gdx.graphics.getWidth() + 170) {
                    characterX = Gdx.graphics.getWidth() - currentAnimation.getKeyFrame(stateTime).getRegionWidth() + 170; // 화면 오른쪽 끝을 벗어나지 않도록 설정
                }
            }
        }

        // 현재 애니메이션의 프레임을 가져오기
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, characterX, characterY); // 캐릭터를 화면에 그리기
        batch.end();

        // Stage의 UI 요소를 그리기
        stage.act(delta); // Stage 내의 액터를 업데이트
        stage.draw();     // Stage의 액터를 화면에 그리기
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        leftWalkAtlas.dispose();
        rightWalkAtlas.dispose();
        leftJumpAtlas.dispose();
        rightJumpAtlas.dispose();
    }
}
