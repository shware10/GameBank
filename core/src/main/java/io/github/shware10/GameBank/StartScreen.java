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
import com.badlogic.gdx.InputProcessor;

public class StartScreen implements Screen, InputProcessor{
    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture imageTexture; // 이미지 텍스처
    private final DBHelperInterface dbHelper;

    private String currentInput = "";
    private String username = "";
    private String password = "";
    private String message = "Enter Username:"; // 초기 메시지
    private boolean isPasswordInput = false; // 비밀번호 입력 중 여부


    public StartScreen(Game game, DBHelperInterface dbHelper) {
        this.game = game;
        this.dbHelper = dbHelper; // 주입된 DBHelperInterface 사용
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

        Gdx.input.setInputProcessor(this); // InputProcessor 설정


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

        // 입력 메시지와 사용자 입력 텍스트 출력
        float textX = 50f; // 텍스트 X 좌표
        float textY = Gdx.graphics.getHeight() - 100f; // 화면 상단에서 100픽셀 아래
        font.draw(batch, message, textX, textY); // 메시지 출력
        font.draw(batch, currentInput, textX, textY - 50f); // 입력 텍스트 출력 (메시지 아래)

        batch.end();

        // 화면 터치 시 LobbyScreen으로 이동
        if (Gdx.input.isTouched()) {
            game.setScreen(new LobbyScreen(game));
        }
    }

    @Override
    public boolean keyTyped(char character) {
        // 엔터 입력 시
        if (character == '\r' || character == '\n') {
            if (!isPasswordInput) {
                // 사용자 이름 입력 완료, 비밀번호 입력으로 전환
                username = currentInput;
                currentInput = "";
                message = "Enter Password:";
                isPasswordInput = true;
            } else {
                // 비밀번호 입력 완료
                password = currentInput;

                // 사용자 등록 또는 로그인 시도
                boolean success;
                if (dbHelper.registerUser(username, password)) {
                    message = "User Registered! Logging in...";
                    success = dbHelper.loginUser(username, password);
                } else {
                    success = dbHelper.loginUser(username, password);
                    message = success ? "Login Successful!" : "Login Failed!";
                }

                if (success) {
                    game.setScreen(new LobbyScreen(game));
                } else {
                    currentInput = "";
                    username = "";
                    password = "";
                    message = "Try Again: Enter Username:";
                    isPasswordInput = false;
                }
            }
        } else if (character == '\b' && currentInput.length() > 0) {
            // 백스페이스 처리
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else if (Character.isLetterOrDigit(character)) {
            // 입력된 문자 추가
            currentInput += character;
        }
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // 터치가 취소되었을 때의 처리 (여기서는 아무 작업도 하지 않음)
        return false;
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
