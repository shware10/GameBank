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
    private final GoogleSignInService googleSignInService;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture imageTexture;
    private float time;

    public StartScreen(Game game, GoogleSignInService googleSignInService) {
        this.game = game;
        this.googleSignInService = googleSignInService; // Google 로그인 서비스 초기화
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("zai_PencilTypewriter.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 120;
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            System.err.println("Font file not found or failed to load.");
            e.printStackTrace();
        }

        layout = new GlyphLayout();
        layout.setText(font, "Tap to Start");

        try {
            imageTexture = new Texture(Gdx.files.internal("penguinStart.png"));
        } catch (Exception e) {
            System.err.println("Image file not found or failed to load.");
            e.printStackTrace();
        }

        googleSignInService.startSignIn();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        time += delta;

        batch.begin();

        if (imageTexture != null) {
            float desiredWidth = Gdx.graphics.getWidth();
            float desiredHeight = imageTexture.getHeight() * (desiredWidth / imageTexture.getWidth());
            float imageX = (Gdx.graphics.getWidth() - desiredWidth) / 2.0f;
            float imageY = -2;
            batch.draw(imageTexture, imageX, imageY, desiredWidth, desiredHeight);
        }

        if (font != null && layout != null) {
            float scale = 1.0f + 0.03f * -(float) Math.sin(time * 2 * Math.PI); // 주기적인 변화
            font.getData().setScale(scale); // 폰트 크기 조정

            GlyphLayout titleLayout = new GlyphLayout(font, "Tap to Start");


            float textX = (Gdx.graphics.getWidth() - titleLayout.width) / 2.0f;
            float textY = (Gdx.graphics.getHeight() + titleLayout.height) / 2.0f;

            font.draw(batch, "Tap to Start", textX, textY + 300f);

            // 폰트 크기 원래대로 복원
            font.getData().setScale(1.0f);

        }

        batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(((Core) game).getLobbyScreen());
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
