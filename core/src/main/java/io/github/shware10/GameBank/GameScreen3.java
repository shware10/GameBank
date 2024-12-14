package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameScreen3 implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private int[][] board; // 스도쿠 보드
    private int selectedRow = -1; // 선택된 셀의 행
    private int selectedCol = -1; // 선택된 셀의 열

    private float boardSize; // 보드 크기
    private float cellSize; // 셀 크기
    private float boardX; // 보드 시작 X 좌표
    private float boardY; // 보드 시작 Y 좌표

    private Rectangle lobbyButton; // 로비 버튼 영역

    public GameScreen3(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2); // 텍스트 크기를 조정합니다.
        shapeRenderer = new ShapeRenderer();

        // 보드 크기 계산
        boardSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.9f;
        cellSize = boardSize / 9f;
        boardX = (Gdx.graphics.getWidth() - boardSize) / 2f;
        boardY = (Gdx.graphics.getHeight() - boardSize) / 2f;

        // 간단한 스도쿠 보드 초기화 (0은 빈 칸을 의미)
        board = new int[][]{
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        // 로비 버튼 정의
        float buttonWidth = 200;
        float buttonHeight = 80;
        lobbyButton = new Rectangle(20, Gdx.graphics.getHeight() - buttonHeight - 20, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 로비 버튼 렌더링
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("87ceeb")); // 연한 파란색
        shapeRenderer.rect(lobbyButton.x, lobbyButton.y, lobbyButton.width, lobbyButton.height);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, "Lobby", lobbyButton.x + 50, lobbyButton.y + 50);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("fffafa"));
        shapeRenderer.rect(boardX, boardY, boardSize, boardSize);
        shapeRenderer.end();

        if (selectedRow != -1 && selectedCol != -1) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.valueOf("add8e6"));
            shapeRenderer.rect(
                boardX + selectedCol * cellSize,
                boardY + boardSize - (selectedRow + 1) * cellSize,
                cellSize,
                cellSize
            );
            shapeRenderer.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        for (int i = 0; i <= 9; i++) {
            float y = boardY + i * cellSize;
            shapeRenderer.line(boardX, y, boardX + boardSize, y);

            float x = boardX + i * cellSize;
            shapeRenderer.line(x, boardY, x, boardY + boardSize);
        }

        shapeRenderer.end();

        batch.begin();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                float x = boardX + col * cellSize;
                float y = boardY + boardSize - (row + 1) * cellSize;

                if (board[row][col] != 0) {
                    font.setColor(Color.BLACK);
                    font.draw(batch, String.valueOf(board[row][col]),
                        x + cellSize * 0.3f, y + cellSize * 0.7f); // 위치 보정
                }
            }
        }

        batch.end();

        handleInput();
    }

    private void handleInput() {
        // 마우스 클릭으로 셀 선택
        if (Gdx.input.justTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            // 로비 버튼 클릭 처리
            if (lobbyButton.contains(touchPos)) {
                if (game instanceof Core) {
                    ((Core) game).setScreen(((Core) game).getLobbyScreen());
                }
                return; // 다른 입력은 무시
            }

            // 셀 선택
            int col = (int) ((touchPos.x - boardX) / cellSize);
            int row = (int) ((boardY + boardSize - touchPos.y) / cellSize);

            if (row >= 0 && row < 9 && col >= 0 && col < 9) {
                selectedRow = row;
                selectedCol = col;
            }
        }

        // 키보드 숫자 입력
        if (selectedRow != -1 && selectedCol != -1) {
            for (int key = Input.Keys.NUM_1; key <= Input.Keys.NUM_9; key++) {
                if (Gdx.input.isKeyJustPressed(key)) {
                    board[selectedRow][selectedCol] = key - Input.Keys.NUM_1 + 1;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        boardSize = Math.min(width, height) * 0.9f;
        cellSize = boardSize / 9f;
        boardX = (width - boardSize) / 2f;
        boardY = (height - boardSize) / 2f;
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
        font.dispose();
        shapeRenderer.dispose();
    }
}
