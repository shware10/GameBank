package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.Texture;

public class LobbyScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont GLfont;
    private ShapeRenderer shapeRenderer;
    private Map<Integer, Boolean> gameSelections;
    private int selectedGameIndex = -1;
    private TextureAtlas button1Atlas, button2Atlas, button4Atlas; // 각 버튼의 스프라이트 시트
    private Texture button3Texture; // 버튼 3의 정적 PNG 이미지
    private Animation<TextureRegion> button1Animation, button2Animation, button4Animation; // 각 버튼의 애니메이션
    private float button1AnimationTime = 0f, button2AnimationTime = 0f,button3AnimationTime = 0f, button4AnimationTime = 0f; // 각 버튼의 애니메이션 시간
    private float button4Timer = 0f; // 4번 애니메이션 타이머
    private boolean button4Animating = false; // 4번 애니메이션 재생 상태

    public LobbyScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // 커스텀 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("zai_PencilTypewriter.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 65;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);

        FreeTypeFontGenerator.FreeTypeFontParameter GLparameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        GLparameter.size = 140;
        GLparameter.color = Color.WHITE;
        GLfont = generator.generateFont(GLparameter);

        generator.dispose();

        // 각 버튼의 애니메이션 초기화
        button1Atlas = new TextureAtlas(Gdx.files.internal("penguin_Right_Walk.atlas"));
        button1Animation = new Animation<>(0.1f, button1Atlas.findRegions("RightWalk"), Animation.PlayMode.LOOP);

        button2Atlas = new TextureAtlas(Gdx.files.internal("Top_Slide_Idle.atlas"));
        button2Animation = new Animation<>(0.1f, button2Atlas.findRegions("Top_Slide_Idle"), Animation.PlayMode.LOOP);

        button3Texture = new Texture(Gdx.files.internal("Game3_Explain.png"));

        button4Atlas = new TextureAtlas(Gdx.files.internal("penguin_Right_Attack.atlas"));
        button4Animation = new Animation<>(0.1f, button4Atlas.findRegions("penguin_RightAtack"), Animation.PlayMode.LOOP);

        // 게임 선택 초기화
        gameSelections = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            gameSelections.put(i, false);
        }
        gameSelections.put(1, true); // 기본으로 1번 선택
        selectedGameIndex = 1; // 기본 선택된 게임 인덱스 설정
    }

    private void drawButton(float x, float y, float width, float height, String text, boolean isSelected, boolean isGameStart, boolean isLeaderboard) {
        float cornerRadius = 30f; // 모서리 반경 설정
        float borderThickness = 7f; // 테두리 두께

        if(!isGameStart && !isLeaderboard)
        {
            // 큰 네모 (바깥 네모) 그리기
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.WHITE); // 테두리 색상 (흰색)

            // 바깥 네모 중심 부분
            shapeRenderer.rect(x + cornerRadius, y, width - 2 * cornerRadius, height); // 상하 직사각형
            shapeRenderer.rect(x, y + cornerRadius, width, height - 2 * cornerRadius); // 좌우 직사각형

            // 바깥 네모 둥근 모서리
            shapeRenderer.arc(x + cornerRadius, y + cornerRadius, cornerRadius, 180, 90); // Bottom-left
            shapeRenderer.arc(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270, 90); // Bottom-right
            shapeRenderer.arc(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 0, 90); // Top-right
            shapeRenderer.arc(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90, 90); // Top-left
            shapeRenderer.end();

        }

        // 작은 네모 (내부 네모) 그리기
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (isGameStart) {
            shapeRenderer.setColor(new Color(0.8f, 0.2f, 0.2f, 1)); // "GAME START" 버튼 배경
        } else if(isLeaderboard){
            shapeRenderer.setColor(new Color(0.4f, 0.8f, 0.2f, 1));
        } else if (isSelected) {
            shapeRenderer.setColor(new Color(1 , 1, 1, 1f)); // 선택된 게임 버튼 배경
        } else {
            shapeRenderer.setColor(new Color(0.87f, 0.52f, 0.01f, 0.5f)); // 비선택 게임 버튼 배경
        }

        // 내부 네모 중심 부분
        shapeRenderer.rect(x + cornerRadius + borderThickness, y + borderThickness, width - 2 * (cornerRadius + borderThickness), height - 2 * borderThickness); // 상하 직사각형
        shapeRenderer.rect(x + borderThickness, y + cornerRadius + borderThickness, width - 2 * borderThickness, height - 2 * (cornerRadius + borderThickness)); // 좌우 직사각형

        // 내부 네모 둥근 모서리
        shapeRenderer.arc(x + cornerRadius + borderThickness, y + cornerRadius + borderThickness, cornerRadius, 180, 90); // Bottom-left
        shapeRenderer.arc(x + width - cornerRadius - borderThickness, y + cornerRadius + borderThickness, cornerRadius, 270, 90); // Bottom-right
        shapeRenderer.arc(x + width - cornerRadius - borderThickness, y + height - cornerRadius - borderThickness, cornerRadius, 0, 90); // Top-right
        shapeRenderer.arc(x + cornerRadius + borderThickness, y + height - cornerRadius - borderThickness, cornerRadius, 90, 90); // Top-left
        shapeRenderer.end();

        // 버튼 텍스트 그리기
        batch.begin();
        GlyphLayout layout = new GlyphLayout(font, text);
        float textX = x + (width - layout.width) / 2f;
        float textY = y + (height + layout.height) / 2f;
        font.draw(batch, text, textX, textY);
        batch.end();
    }



    private void selectGame(int gameIndex) {
        for (int i = 1; i <= 4; i++) {
            gameSelections.put(i, i == gameIndex); // 선택된 게임만 true로 설정
        }
        selectedGameIndex = gameIndex;
    }

    private void startSelectedGame() {
        if (selectedGameIndex != -1) {
            switch (selectedGameIndex) {
                case 1:
                    game.setScreen(new PreGameScreen1(game));
                    break;
                case 2:
                    game.setScreen(new GameScreen2(game));
                    break;
                case 3:
                    game.setScreen(new GameScreen3(game));
                    break;
                case 4:
                    game.setScreen(new GameScreen4(game));
                    break;
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.87f, 0.52f, 0.01f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 애니메이션 시간 업데이트
        button1AnimationTime += delta;
        button2AnimationTime += delta;
        button3AnimationTime += delta;

        // 4번 애니메이션 로직 (4번 버튼이 선택된 경우에만 처리)
        if (gameSelections.get(4)) {
            if (button4Animating) {
                button4AnimationTime += delta; // 애니메이션 진행
                if (button4Animation.isAnimationFinished(button4AnimationTime)) {
                    button4Animating = false; // 애니메이션 종료
                    button4AnimationTime = 0f; // 애니메이션 시간 초기화
                }
            } else {
                button4Timer += delta; // 대기 타이머
                if (button4Timer >= 0.5f) { // 대기 시간이 끝났을 때
                    button4Animating = true; // 애니메이션 시작
                    button4Timer = 0f; // 타이머 초기화
                }
            }
        }

        // 버튼 크기 및 위치 설정
        float buttonWidth = Gdx.graphics.getWidth() * 0.9f;
        float buttonHeight = 250;
        float startX = (Gdx.graphics.getWidth() - buttonWidth) / 2f;
        float startY = Gdx.graphics.getHeight() - 600; // 첫 버튼의 Y 좌표

        String[] GameNames = {"Avoid Trash", "Swipe Lane", "Sudoku", "Destroy Enemy"};


        // 상단에 "Game List" 텍스트 추가 (크기 애니메이션 적용)
        batch.begin();

        // Math.sin을 사용하여 폰트 크기를 주기적으로 변경
        float scale = 1.0f + 0.05f * -(float) Math.sin(button1AnimationTime * 2 * Math.PI); // 주기적인 변화
        GLfont.getData().setScale(scale); // 폰트 크기 조정

        GlyphLayout titleLayout = new GlyphLayout(GLfont, "Game List");
        float titleX = (Gdx.graphics.getWidth() - titleLayout.width) / 2f;
        float titleY = Gdx.graphics.getHeight() - 120; // 화면 상단에서 내려온 위치

        GLfont.draw(batch, "Game List", titleX, titleY);

        // 폰트 크기 원래대로 복원
        GLfont.getData().setScale(1.0f);

        batch.end();

        // 버튼 1
        float button1Y = startY;
        drawButton(startX, button1Y, buttonWidth, buttonHeight, GameNames[0], gameSelections.get(1), false, false);
        drawAnimation(button1Animation, button1AnimationTime, startX, button1Y, buttonWidth, buttonHeight, gameSelections.get(1));

        // 버튼 2
        float button2Y = startY - (buttonHeight + 30);
        drawButton(startX, button2Y, buttonWidth, buttonHeight, GameNames[1], gameSelections.get(2), false, false);
        drawAnimation(button2Animation, button2AnimationTime, startX, button2Y, buttonWidth, buttonHeight, gameSelections.get(2));

        // 버튼 3 (PNG 이미지 표시, 선택된 경우만 표시)
        float button3Y = startY - 2 * (buttonHeight + 30);
        drawButton(startX, button3Y, buttonWidth, buttonHeight, GameNames[2], gameSelections.get(3), false, false);
        drawStaticImage(button3Texture, startX, button3Y, buttonWidth, buttonHeight, gameSelections.get(3), button3AnimationTime);

        // 버튼 4
        float button4Y = startY - 3 * (buttonHeight + 30);
        drawButton(startX, button4Y, buttonWidth, buttonHeight, GameNames[3], gameSelections.get(4), false, false);
        if (gameSelections.get(4)) {
            if (button4Animating) {
                drawAnimation(button4Animation, button4AnimationTime, startX, button4Y, buttonWidth, buttonHeight, true);
            } else {
                // 첫 번째 프레임에서 멈춘 상태로 유지
                TextureRegion firstFrame = button4Animation.getKeyFrames()[2];
                drawStaticFrame(firstFrame, startX, button4Y, buttonWidth, buttonHeight);
            }
        }

        // "GAME START" 버튼을 원형으로 변경
        float startButtonRadius = 150; // 반지름 설정
        float startButtonX = Gdx.graphics.getWidth()-220f; // 화면 중앙
        float startButtonY = 240; // 화면 하단에서 위로 이동

        drawCircleButton(startButtonX, startButtonY, startButtonRadius, "Start", button1AnimationTime);

        // "LEADERBOARD" 버튼
        float leaderboardButtonWidth = 600;
        float leaderboardButtonHeight = 200;
        float leaderboardButtonX = (Gdx.graphics.getWidth() - leaderboardButtonWidth)-420f;
        float leaderboardButtonY = 140;
        drawButton(leaderboardButtonX, leaderboardButtonY, leaderboardButtonWidth, leaderboardButtonHeight, "LeaderBoard", false, false, true);

        // 입력 처리
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < 4; i++) {
                float buttonY = startY - i * (buttonHeight + 30);
                if (touchX >= startX && touchX <= startX + buttonWidth && touchY >= buttonY && touchY <= buttonY + buttonHeight) {
                    selectGame(i + 1);
                }
            }

            // 원형 "GAME START" 버튼 클릭 처리
            if (Math.sqrt(Math.pow(touchX - startButtonX, 2) + Math.pow(touchY - startButtonY, 2)) <= startButtonRadius) {
                startSelectedGame();
            }
        }
    }
    private void drawCircleButton(float centerX, float centerY, float radius, String text, float time) {
        float scale = 1.0f + 0.04f * (float) Math.sin(time * 2 * Math.PI); // 1초 주기

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.8f, 0.2f, 0.2f, 1)); // 버튼 배경색
        shapeRenderer.circle(centerX, centerY, radius * scale); // 원 그리기
        shapeRenderer.end();

        // 폰트 크기 애니메이션 (0.95배에서 1.05배로 변동)

        font.getData().setScale(scale); // 폰트 크기 조정

        // 텍스트 중앙 정렬
        batch.begin();
        GlyphLayout layout = new GlyphLayout(font, text);
        float textX = centerX - layout.width / 2f;
        float textY = centerY + layout.height / 2f;
        font.draw(batch, text, textX, textY);
        batch.end();

        // 폰트 크기 원래대로 복원
        font.getData().setScale(1.0f);
    }


    private void drawAnimation(Animation<TextureRegion> animation, float animationTime, float x, float y, float width, float height, boolean isSelected) {
        if (isSelected) {
            TextureRegion currentFrame = animation.getKeyFrame(animationTime);

            // 원본 텍스처 크기 가져오기
            float originalWidth = currentFrame.getRegionWidth() * 0.7f;
            float originalHeight = currentFrame.getRegionHeight()* 0.7f;

            // 버튼의 중앙에 텍스처 배치
            float drawX = x + (width - originalWidth) /2f;
            float drawY = y + (height - originalHeight) / 2f;

            // 텍스처 그리기
            batch.begin();
            batch.draw(currentFrame, drawX, drawY, originalWidth, originalHeight);
            batch.end();
        }
    }

    private void drawStaticFrame(TextureRegion frame, float x, float y, float width, float height) {

        float originalWidth = frame.getRegionWidth() * 0.7f;
        float originalHeight = frame.getRegionHeight()* 0.7f;

        // 버튼의 중앙에 텍스처 배치
        float drawX = x + (width - originalWidth) /2f;
        float drawY = y + (height - originalHeight) / 2f;

        batch.begin();
        batch.draw(frame, drawX, drawY, originalWidth, originalHeight);
        batch.end();
    }

    private void drawStaticImage(Texture texture, float x, float y, float width, float height, boolean isSelected, float time) {
        if (isSelected) { // 버튼이 선택된 경우에만 이미지를 표시
            // Math.sin을 사용한 펄스 효과 (0.9배에서 1.1배 사이로 변동)
            float scale = 1.0f + 0.03f * (float) Math.sin(time * 2 * Math.PI); // 1초 주기로 크기 변화

            float originalWidth = texture.getWidth() * scale * 0.8f; // 0.8f 기본 크기
            float originalHeight = texture.getHeight() * scale * 0.8f;

            float drawX = x + (width - originalWidth) / 2f;
            float drawY = y + (height - originalHeight) / 2f;

            batch.begin();
            batch.draw(texture, drawX, drawY, originalWidth, originalHeight);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();

        button1Atlas.dispose();
        button2Atlas.dispose();
        button4Atlas.dispose();
        button3Texture.dispose(); // 버튼 3 PNG 이미지 해제
    }


    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }
}
