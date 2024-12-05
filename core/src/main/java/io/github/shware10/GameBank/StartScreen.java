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
    private final DBHelperInterface dbHelper;
    private Texture loginButtonTexture, signupButtonTexture; // 로그인 버튼과 회원가입 버튼 텍스처

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

        loginButtonTexture = new Texture(Gdx.files.internal("krabCan.png"));
        signupButtonTexture = new Texture(Gdx.files.internal("shrimpCan.png"));

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

        // 화면 중앙에 버튼 배치
        float buttonWidth = loginButtonTexture.getWidth();
        float buttonHeight = loginButtonTexture.getHeight();
        float screenCenterX = Gdx.graphics.getWidth() / 2f;

        // 버튼 위치 설정
        float loginButtonX = screenCenterX - buttonWidth / 2f;
        float loginButtonY = Gdx.graphics.getHeight() / 2f + 50;

        float signupButtonX = screenCenterX - signupButtonTexture.getWidth() / 2f;
        float signupButtonY = Gdx.graphics.getHeight() / 2f - 50;

        // 이미지 그리기
        batch.draw(imageTexture, imageX, imageY, desiredWidth, desiredHeight);
        // 버튼 그리기
        batch.draw(loginButtonTexture, loginButtonX, loginButtonY);
        batch.draw(signupButtonTexture, signupButtonX, signupButtonY);

        // 입력 메시지와 사용자 입력 텍스트 출력
        float textX = 50f; // 텍스트 X 좌표
        float textY = Gdx.graphics.getHeight() - 100f; // 화면 상단에서 100픽셀 아래
        font.draw(batch, message, textX, textY); // 메시지 출력
        font.draw(batch, currentInput, textX, textY - 50f); // 입력 텍스트 출력 (메시지 아래)

        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // 로그인 버튼 클릭 시
            if (touchX >= loginButtonX && touchX <= loginButtonX + loginButtonTexture.getWidth()
                && touchY >= loginButtonY && touchY <= loginButtonY + loginButtonTexture.getHeight()) {
                game.setScreen(new LoginScreen(game, dbHelper)); // 로그인 화면으로 이동
            }

            // 회원가입 버튼 클릭 시
            if (touchX >= signupButtonX && touchX <= signupButtonX + signupButtonTexture.getWidth()
                && touchY >= signupButtonY && touchY <= signupButtonY + signupButtonTexture.getHeight()) {
                game.setScreen(new SignupScreen(game, dbHelper)); // 회원가입 화면으로 이동
            }
        }

        // 화면 터치 시 LobbyScreen으로 이동
        /*if (Gdx.input.isTouched()) {
            //game.setScreen(new LobbyScreen(game));
        }*/
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
