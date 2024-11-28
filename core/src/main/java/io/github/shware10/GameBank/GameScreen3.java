package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen3 implements Screen {
    private final Game game;
    private SpriteBatch batch;

    private TextureAtlas leftWalkAtlas, rightWalkAtlas;
    private Animation<TextureRegion> leftWalkAnimation;
    private Animation<TextureRegion> rightWalkAnimation;
    private Animation<TextureRegion> currentAnimation;

    private float stateTime;
    private boolean isLeft = true;
    private float characterX; // 캐릭터의 X 위치
    private float characterY = 100; // 캐릭터의 Y 위치 (고정된 위치)
    private float speed = 300f; // 캐릭터 이동 속도 (초당 200픽셀)

    public GameScreen3(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // 각 애니메이션에 대한 TextureAtlas 로드
        leftWalkAtlas = new TextureAtlas("penguin_Left_Walk.atlas"); // 왼쪽 이동 애니메이션 아틀라스
        rightWalkAtlas = new TextureAtlas("penguin_Right_Walk.atlas"); // 오른쪽 이동 애니메이션 아틀라스

        // 각 애니메이션 정의
        leftWalkAnimation = new Animation<>(0.1f, leftWalkAtlas.findRegions("LeftWalk"), Animation.PlayMode.LOOP);
        rightWalkAnimation = new Animation<>(0.1f, rightWalkAtlas.findRegions("RightWalk"), Animation.PlayMode.LOOP);

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
        // 화면 터치 시 방향 전환
        if (Gdx.input.justTouched()) {
            isLeft = !isLeft;
            currentAnimation = isLeft ? leftWalkAnimation : rightWalkAnimation;
        }

        // 캐릭터의 위치 업데이트
        if (isLeft) {
            characterX -= speed * delta;
            if (characterX < -170) {
                characterX = -170; // 화면 왼쪽 끝을 벗어나지 않도록 설정
            }
        } else {
            characterX += speed * delta;
            if (characterX + currentAnimation.getKeyFrame(stateTime).getRegionWidth() > Gdx.graphics.getWidth()+170) {
                characterX = Gdx.graphics.getWidth() - currentAnimation.getKeyFrame(stateTime).getRegionWidth()+170; // 화면 오른쪽 끝을 벗어나지 않도록 설정
            }
        }

        // 현재 애니메이션의 프레임을 가져오기
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, characterX, characterY); // 캐릭터를 화면에 그리기
        batch.end();
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
        leftWalkAtlas.dispose();
        rightWalkAtlas.dispose();
    }
}
