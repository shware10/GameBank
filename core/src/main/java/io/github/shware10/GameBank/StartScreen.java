package io.github.shware10.GameBank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Game;

public class StartScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture imageTexture; // 이미지 텍스처

    public StartScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // 커스텀 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pencilized.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;
        font = generator.generateFont(parameter);
        generator.dispose();

        layout = new GlyphLayout();
        layout.setText(font, "Tap to Start");

        // PNG 이미지 로드
        imageTexture = new Texture(Gdx.files.internal("penguinStart.png")); // 이미지 파일 이름을 실제 파일명으로 교체

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // 이미지 크기와 위치 설정
        float desiredWidth = Gdx.graphics.getWidth(); // 화면 전체 너비에 맞추기
        float desiredHeight = imageTexture.getHeight() * (desiredWidth / imageTexture.getWidth()); // 비율에 맞춰 높이 조정
        float imageX = (Gdx.graphics.getWidth() - desiredWidth) / 2.0f;
        float imageY = -2; // 화면 하단에 맞추기 위해 Y 좌표를 0으로 설정

        // 이미지 그리기
        batch.draw(imageTexture, imageX, imageY, desiredWidth, desiredHeight);

        // 텍스트를 화면 중앙에서 400픽셀 위에 그리기
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2.0f;
        float textY = (Gdx.graphics.getHeight() + layout.height) / 2.0f;
        font.draw(batch, layout, textX, textY + 300f);

        batch.end();

        // 화면 터치 시 LobbyScreen으로 이동
        if (Gdx.input.isTouched()) {
            game.setScreen(new LobbyScreen(game));
        }
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
        imageTexture.dispose(); // 이미지 텍스처 자원 해제
    }
}
