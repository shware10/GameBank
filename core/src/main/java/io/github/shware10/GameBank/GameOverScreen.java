package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class GameOverScreen implements Screen {
    private final Game game;
    private final Class<? extends Screen> previousGameClass;
    private final float finalScore;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont ScoreFont;
    private BitmapFont GameOverFont;
    private ShapeRenderer shapeRenderer;
    private Rectangle restartButton;
    private Rectangle quitButton;
    private Texture imageTexture;
    private float time;


    public GameOverScreen(Game game, Class<? extends Screen> previousGameClass, float score) {
        this.game = game;
        this.previousGameClass = previousGameClass;
        this.finalScore = score;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // FreeTypeFontGenerator로 커스텀 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("zai_PencilTypewriter.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter GameOverFontparameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        GameOverFontparameter.size = 150; // 원하는 폰트 크기
        GameOverFontparameter.color = Color.WHITE; // 폰트 색상
        GameOverFont = generator.generateFont(GameOverFontparameter);

        FreeTypeFontGenerator.FreeTypeFontParameter ScoreFontparameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        ScoreFontparameter.size = 90; // 원하는 폰트 크기
        ScoreFontparameter.color = Color.WHITE; // 폰트 색상
        ScoreFont = generator.generateFont(ScoreFontparameter);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 70; // 원하는 폰트 크기
        parameter.color = Color.WHITE; // 폰트 색상
        font = generator.generateFont(parameter);

        generator.dispose(); // 생성기 해제

        // 버튼 위치 및 크기 설정
        float buttonWidth = 400;
        float buttonHeight = 120;
        float centerX = Gdx.graphics.getWidth() / 2f - buttonWidth / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        restartButton = new Rectangle(centerX, 300, buttonWidth, buttonHeight);
        quitButton = new Rectangle(centerX, 470, buttonWidth, buttonHeight);
        imageTexture = new Texture(Gdx.files.internal("penguin_GameOver.png"));

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        time += delta;

        String textGameOver = "Game Over";
        String textScore = "Score: " +(int) finalScore;


        GlyphLayout layoutScore = new GlyphLayout(ScoreFont, textScore);
        float ScoreWidth = layoutScore.width;
        float ScoreHeight = layoutScore.height;

        batch.begin();

        // Math.sin을 사용하여 폰트 크기를 주기적으로 변경
        float scale = 1.0f + 0.03f * -(float) Math.sin(time * 2 * Math.PI); // 주기적인 변화
        GameOverFont.getData().setScale(scale); // 폰트 크기 조정

        GlyphLayout layoutGameOver = new GlyphLayout(GameOverFont, textGameOver);
        float titleX = (Gdx.graphics.getWidth() - layoutGameOver.width) / 2f;
        float titleY = Gdx.graphics.getHeight() - 410; // 화면 상단에서 내려온 위치

        GameOverFont.draw(batch, textGameOver, titleX, titleY);

        // 폰트 크기 원래대로 복원
        GameOverFont.getData().setScale(1.0f);

        batch.end();
        // 텍스트 렌더링
        batch.begin();

        ScoreFont.draw(batch, textScore, (Gdx.graphics.getWidth() - ScoreWidth) / 2f, Gdx.graphics.getHeight() - 610);

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.5f, 0.8f, 1); // Restart 버튼 색상
        drawRoundedRectangle(shapeRenderer, restartButton.x, restartButton.y, restartButton.width, restartButton.height, 20);

        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1); // Quit 버튼 색상
        drawRoundedRectangle(shapeRenderer, quitButton.x, quitButton.y, quitButton.width, quitButton.height, 20);
        shapeRenderer.end();

        // 버튼 텍스트 렌더링
        batch.begin();
        String restartText = "Restart";
        GlyphLayout restartLayout = new GlyphLayout(font, restartText);
        float restartTextX = restartButton.x + (restartButton.width - restartLayout.width) / 2f; // 버튼 중심 X 좌표
        float restartTextY = restartButton.y + (restartButton.height + restartLayout.height) / 2f; // 버튼 중심 Y 좌표
        font.draw(batch, restartText, restartTextX, restartTextY);

        String quitText = "Quit";
        GlyphLayout quitLayout = new GlyphLayout(font, quitText);
        float quitTextX = quitButton.x + (quitButton.width - quitLayout.width) / 2f; // 버튼 중심 X 좌표
        float quitTextY = quitButton.y + (quitButton.height + quitLayout.height) / 2f; // 버튼 중심 Y 좌표
        font.draw(batch, quitText, quitTextX, quitTextY);
        batch.end();

        batch.begin();
        if (imageTexture != null) {
            float desiredWidth = imageTexture.getWidth() * 1.5f;
            float desiredHeight = imageTexture.getHeight() * 1.5f;
            float imageX = (Gdx.graphics.getWidth() - desiredWidth) / 2.0f;
            float imageY = (Gdx.graphics.getHeight() - desiredHeight) / 2.0f - 70f;
            batch.draw(imageTexture, imageX, imageY, desiredWidth, desiredHeight);
        }
        batch.end();

        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (restartButton.contains(touchX, touchY)) {
                restartGame();
            } else if (quitButton.contains(touchX, touchY)) {
                quitToLobby();
            }
        }
    }


    private void drawRoundedRectangle(ShapeRenderer shapeRenderer, float x, float y, float width, float height, float radius) {
        // 사각형의 본체
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height); // 상단과 하단 사이 직사각형
        shapeRenderer.rect(x, y + radius, width, height - 2 * radius); // 좌우 사이 직사각형

        // 네 모서리의 원호
        shapeRenderer.arc(x + radius, y + radius, radius, 180, 90); // Bottom-left
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270, 90); // Bottom-right
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0, 90); // Top-right
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90, 90); // Top-left
    }
    private void restartGame() {
        try {
            Screen newGameScreen = previousGameClass.getConstructor(Game.class).newInstance(game);
            game.setScreen(newGameScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quitToLobby() {
        if (game instanceof Core) {
            Core coreGame = (Core) game;
            coreGame.setScreen(coreGame.getLobbyScreen());
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
        ScoreFont.dispose();
        GameOverFont.dispose();
        shapeRenderer.dispose();
        if (imageTexture != null) {
            imageTexture.dispose(); // 이미지 텍스처 해제
        }
    }
}
