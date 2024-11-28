package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameOverScreen implements Screen {
    private final Game game;
    private final float finalScore;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Rectangle restartButton;
    private Rectangle quitButton;

    public GameOverScreen(Game game, float score) {
        this.game = game;
        this.finalScore = score;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(3);

        // 버튼 위치 및 크기 설정
        float buttonWidth = 300;
        float buttonHeight = 100;
        float centerX = Gdx.graphics.getWidth() / 2f - buttonWidth / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        restartButton = new Rectangle(centerX, centerY + 120, buttonWidth, buttonHeight);
        quitButton = new Rectangle(centerX, centerY - 20, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Game Over", Gdx.graphics.getWidth() / 2f - 130, Gdx.graphics.getHeight() - 150);
        font.draw(batch, "Score: " + (int) finalScore, Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() - 250);
        batch.end();

        // 버튼 렌더링
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.5f, 0.8f, 1);
        shapeRenderer.rect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1);
        shapeRenderer.rect(quitButton.x, quitButton.y, quitButton.width, quitButton.height);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Restart", restartButton.x + 80, restartButton.y + 65);
        font.draw(batch, "Quit", quitButton.x + 110, quitButton.y + 65);
        batch.end();

        // 입력 처리
        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (restartButton.contains(touchX, touchY)) {
                game.setScreen(new GameScreen4(game)); // 현재 게임 재시작
            } else if (quitButton.contains(touchX, touchY)) {
                game.setScreen(((Core) game).getLobbyScreen()); // 종료
            }
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
        shapeRenderer.dispose();
    }
}
