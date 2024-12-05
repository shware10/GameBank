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

public class SignupScreen implements Screen, InputProcessor{
    private final Game game;
    private final DBHelperInterface dbHelper;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backButtonTexture, signupButtonTexture;
    private String username = "";
    private String userId = "";
    private String password = "";
    private String confirmPassword = "";
    private String message = "Enter Username:"; // 초기 메시지
    private boolean isPasswordInput = false; // 현재 입력 상태
    private boolean isConfirmPasswordInput = false; // 비밀번호 확인 입력 상태

    public SignupScreen(Game game, DBHelperInterface dbHelper) {
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
        signupButtonTexture = new Texture(Gdx.files.internal("shrimpCan.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // 텍스트 출력
        font.draw(batch, message, 50, Gdx.graphics.getHeight() - 50);

        // 닉네임, 아이디, 비밀번호 입력 필드 표시
        font.draw(batch, "Username: " + username, 50, Gdx.graphics.getHeight() - 350);
        font.draw(batch, "User ID: " + userId, 50, Gdx.graphics.getHeight() - 650);

        // 비밀번호 입력 상태에 따른 텍스트
        if (isPasswordInput) {
            font.draw(batch, "Password: ******", 50, Gdx.graphics.getHeight() - 950);  // 비밀번호 표시
        } else {
            font.draw(batch, "Password: " + password, 50, Gdx.graphics.getHeight() - 950);  // 비밀번호 입력
        }

        // 비밀번호 확인 상태에 따른 텍스트
        if (isConfirmPasswordInput) {
            font.draw(batch, "Confirm Password: ******", 50, Gdx.graphics.getHeight() - 1250);  // 비밀번호 확인 표시
        } else {
            font.draw(batch, "Confirm Password: " + confirmPassword, 50, Gdx.graphics.getHeight() - 1250);  // 비밀번호 확인 입력
        }

        // 버튼 위치 계산
        float backX = 50;
        float backY = 50;
        float signupX = Gdx.graphics.getWidth() - signupButtonTexture.getWidth() - 50;
        float signupY = 50;

        // 버튼 렌더링
        batch.draw(backButtonTexture, backX, backY);
        batch.draw(signupButtonTexture, signupX, signupY);

        batch.end();

        // 입력 및 버튼 처리
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // 회원가입 버튼 클릭
            if (touchX >= signupX && touchX <= signupX + signupButtonTexture.getWidth()
                && touchY >= signupY && touchY <= signupY + signupButtonTexture.getHeight()) {

                // 비밀번호가 일치하는지 확인
                if (!password.equals(confirmPassword)) {
                    message = "Passwords do not match!";
                } else {
                    // 회원가입 진행
                    boolean success = dbHelper.registerUser(username, password);
                    message = success ? "Signup Successful!" : "Signup Failed!";
                    if (success) {
                        game.setScreen(new LoginScreen(game, dbHelper)); // 회원가입 성공 시 로그인 화면으로 전환
                    }
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
            if (username.isEmpty()) {
                message = "Enter User ID:";
            } else if (userId.isEmpty()) {
                message = "Enter Password:";
                isPasswordInput = true;
            } else if (!isConfirmPasswordInput) {
                message = "Confirm Password:";
                isConfirmPasswordInput = true;
            }
        } else if (character == '\b') {
            if (isPasswordInput && !password.isEmpty()) {
                password = password.substring(0, password.length() - 1);
            } else if (isConfirmPasswordInput && !confirmPassword.isEmpty()) {
                confirmPassword = confirmPassword.substring(0, confirmPassword.length() - 1);
            } else if (!isPasswordInput && !isConfirmPasswordInput && !userId.isEmpty()) {
                userId = userId.substring(0, userId.length() - 1);
            } else if (!isPasswordInput && !isConfirmPasswordInput && !username.isEmpty()) {
                username = username.substring(0, username.length() - 1);
            }
        } else if (Character.isLetterOrDigit(character)) {
            if (!isPasswordInput && !isConfirmPasswordInput) {
                if (username.isEmpty()) {
                    username += character;
                } else {
                    userId += character;
                }
            } else if (isPasswordInput && !isConfirmPasswordInput) {
                password += character;
            } else if (isConfirmPasswordInput) {
                confirmPassword += character;
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
        signupButtonTexture.dispose();
    }
}
