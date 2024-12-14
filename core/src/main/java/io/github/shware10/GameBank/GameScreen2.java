package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen2 implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private TextureAtlas characterAtlas;
    private TextureRegion characterTexture;

    private float characterX;
    private float characterY;
    private float velocityY;
    private boolean isJumping;
    private float gravity = -9.8f; // 중력
    private float jumpStrength = 300f; // 점프 힘

    public GameScreen2(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        characterAtlas = new TextureAtlas("penguin_Right_Idle.atlas"); // 캐릭터 텍스처 아틀라스
        characterTexture = characterAtlas.findRegion("penguin_Right_Idle"); // 캐릭터 텍스처

        characterX = (Gdx.graphics.getWidth() - characterTexture.getRegionWidth()) / 2.0f; // 화면 중앙에 위치
        characterY = 100; // 초기 Y 위치
        isJumping = false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1); // 배경색을 흰색으로 설정
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 점프 로직
        if (isJumping) {
            velocityY += gravity * delta; // 중력을 적용하여 속도를 감소시킴
            characterY += velocityY * delta; // Y 위치 업데이트

            // 바닥에 닿으면 점프 종료
            if (characterY <= 100) {
                characterY = 100;
                isJumping = false;
            }
        }

        // 화면 터치 시 점프 시작
        if (Gdx.input.justTouched() && !isJumping) {
            isJumping = true;
            velocityY = jumpStrength; // 점프 힘 적용
        }

        // 캐릭터 그리기
        batch.begin();
        batch.draw(characterTexture, characterX, characterY); // 캐릭터를 화면에 그리기
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
        characterAtlas.dispose();
    }
}
