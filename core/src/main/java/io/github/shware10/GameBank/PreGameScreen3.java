package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class PreGameScreen3 implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture imageTexture;

    public PreGameScreen3(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("zai_PencilTypewriter.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 70;
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            System.err.println("Font file not found or failed to load.");
            e.printStackTrace();
        }

        layout = new GlyphLayout();
        layout.setText(font, "You know Sudoku, right?");

        try {
            imageTexture = new Texture(Gdx.files.internal("Game3_Explain.png"));
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

        if (imageTexture != null) {
            float desiredWidth = imageTexture.getWidth() * 2.5f;
            float desiredHeight = imageTexture.getHeight() * 2.5f;
            float imageX = (Gdx.graphics.getWidth() - desiredWidth) / 2.0f;
            float imageY = (Gdx.graphics.getHeight() - desiredHeight) / 2.0f + 100f;
            batch.draw(imageTexture, imageX, imageY, desiredWidth, desiredHeight);
        }

        if (font != null && layout != null) {
            float textX = (Gdx.graphics.getWidth() - layout.width) / 2.0f;
            float textY = 470.0f;
            font.draw(batch, layout, textX, textY);
        }

        batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen3(game));
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
