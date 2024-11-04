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
    private Texture imageTexture;

    public StartScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // 폰트 생성
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pencilized.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 150;
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            System.err.println("Font file not found or failed to load.");
            e.printStackTrace();
        }

        layout = new GlyphLayout();
        layout.setText(font, "Tap to Start");

        // 이미지 로드
        try {
            imageTexture = new Texture(Gdx.files.internal("penguinStart.png"));
        } catch (Exception e) {
            System.err.println("Image file not found or failed to load.");
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // 이미지와 텍스트 위치 계산 후 그리기
        if (imageTexture != null) {
            float desiredWidth = Gdx.graphics.getWidth();
            float desiredHeight = imageTexture.getHeight() * (desiredWidth / imageTexture.getWidth());
            float imageX = (Gdx.graphics.getWidth() - desiredWidth) / 2.0f;
            float imageY = -2;
            batch.draw(imageTexture, imageX, imageY, desiredWidth, desiredHeight);
        }

        if (font != null && layout != null) {
            float textX = (Gdx.graphics.getWidth() - layout.width) / 2.0f;
            float textY = (Gdx.graphics.getHeight() + layout.height) / 2.0f;
            font.draw(batch, layout, textX, textY + 300f);
        }

        batch.end();

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
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (imageTexture != null) imageTexture.dispose();
    }
}
