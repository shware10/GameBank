package io.github.shware10.GameBank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;

public class LoginScreen implements Screen, InputProcessor {
    private final Game game;
    private final DBHelperInterface dbHelper;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backButtonTexture, loginButtonTexture;
    private String username = "";
    private String password = "";
    private String message = "Enter Username:"; // 초기 메시지
    private boolean isPasswordInput = false; // 현재 입력 상태

    public LoginScreen(Game game, DBHelperInterface dbHelper) {
        this.game = game;
        this.dbHelper = dbHelper;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pencilized.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        font = generator.generateFont(parameter);
        generator.dispose();

        // 버튼 이미지 로드
        backButtonTexture = new Texture(Gdx.files.internal("krabCan.png"));
        loginButtonTexture = new Texture(Gdx.files.internal("shrimpCan.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // 텍스트 출력
        font.draw(batch, message, 50, Gdx.graphics.getHeight() - 50);
        font.draw(batch, isPasswordInput ? "******" : username, 50, Gdx.graphics.getHeight() - 150);

        // 버튼 위치 계산
        float backX = 50;
        float backY = 50;
        float loginX = Gdx.graphics.getWidth() - loginButtonTexture.getWidth() - 50;
        float loginY = 50;

        // 버튼 렌더링
        batch.draw(backButtonTexture, backX, backY);
        batch.draw(loginButtonTexture, loginX, loginY);

        batch.end();

        // 입력 및 버튼 처리
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // 로그인 버튼 클릭
            if (touchX >= loginX && touchX <= loginX + loginButtonTexture.getWidth()
                && touchY >= loginY && touchY <= loginY + loginButtonTexture.getHeight()) {
                boolean success = dbHelper.loginUser(username, password);
                message = success ? "Login Successful!" : "Login Failed!";
                if (success) {
                    game.setScreen(new LobbyScreen(game)); // 로그인 성공 시 로비 화면으로 전환
                }
            }

            // 뒤로가기 버튼 클릭
            if (touchX >= backX && touchX <= backX + backButtonTexture.getWidth()
                && touchY >= backY && touchY <= backY + backButtonTexture.getHeight()) {
                game.setScreen(new StartScreen(game, dbHelper)); // StartScreen으로 돌아가기
            }
        }
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '\n' || character == '\r') {
            if (!isPasswordInput) {
                message = "Enter Password:";
                isPasswordInput = true;
            }
        } else if (character == '\b') {
            if (!username.isEmpty() && !isPasswordInput) {
                username = username.substring(0, username.length() - 1);
            } else if (!password.isEmpty() && isPasswordInput) {
                password = password.substring(0, password.length() - 1);
            }
        } else if (Character.isLetterOrDigit(character)) {
            if (isPasswordInput) {
                password += character;
            } else {
                username += character;
            }
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
        backButtonTexture.dispose();
        loginButtonTexture.dispose();
    }
}
