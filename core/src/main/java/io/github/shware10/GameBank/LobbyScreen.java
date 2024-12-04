package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.HashMap;
import java.util.Map;

public class LobbyScreen implements Screen {
    private final Game game;
    private Stage stage;
    private BitmapFont font;
    private SpriteBatch batch;
    private Map<Integer, CheckBox> gameCheckBoxes;
    private int selectedGameIndex = -1;

    public LobbyScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // FreeTypeFontGenerator로 커스텀 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("zai_PencilTypewriter.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 70; // 폰트 크기 설정
        parameter.color = Color.WHITE; // 폰트 색상 설정
        font = generator.generateFont(parameter);
        generator.dispose(); // 생성기 해제

        // UI 스킨 설정
        Skin skin = new Skin();
        skin.add("default-font", font);

        // 체크박스 스타일 설정
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = font;
        checkBoxStyle.fontColor = Color.WHITE;

        // 버튼 스타일 설정
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.BLACK;

        // 메인 테이블 레이아웃 설정
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        mainTable.padTop(50); // 상단 여백 조정

        // 게임 선택 체크박스 생성
        gameCheckBoxes = new HashMap<>();
        Table gameTable = new Table();
        gameTable.defaults().width(400).height(150).pad(10); // 버튼 크기와 여백 설정
        for (int i = 1; i <= 4; i++) {
            final int gameIndex = i;
            CheckBox checkBox = new CheckBox("GAME " + i, checkBoxStyle);
            checkBox.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectGame(gameIndex);
                }
            });
            gameCheckBoxes.put(gameIndex, checkBox);
            gameTable.add(checkBox).width(300).height(150); // 크기 조정
            if (i % 2 == 0) gameTable.row(); // 2열로 배치
        }

        // 테이블에 게임 체크박스 추가
        mainTable.add(gameTable).padBottom(50);
        mainTable.row();

        // "GAME START" 버튼 추가
        TextButton startButton = new TextButton("GAME START", buttonStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedGameIndex != -1) {
                    startSelectedGame();
                }
            }
        });
        mainTable.add(startButton).pad(10).width(500).height(200); // 크기 조정
        mainTable.row();

        // "LEADER BOARD" 버튼 추가
        TextButton leaderBoardButton = new TextButton("LEADER BOARD", buttonStyle);
        mainTable.add(leaderBoardButton).pad(10).width(500).height(120); // 크기 조정

        // 스테이지에 메인 테이블 추가
        stage.addActor(mainTable);
    }

    private void selectGame(int gameIndex) {
        selectedGameIndex = gameIndex;
        for (Map.Entry<Integer, CheckBox> entry : gameCheckBoxes.entrySet()) {
            entry.getValue().setChecked(entry.getKey() == gameIndex); // 하나만 선택되도록 설정
        }
    }

    private void startSelectedGame() {
        switch (selectedGameIndex) {
            case 1:
                game.setScreen(new PreGameScreen1(game));
                break;
            case 2:
                game.setScreen(new PreGameScreen2(game)); // PreGameScreen2
                break;
            case 3:
                game.setScreen(new PreGameScreen3(game)); // PreGameScreen3
                break;
            case 4:
                game.setScreen(new PreGameScreen4(game)); // PreGameScreen4
                break;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.87f, 0.52f, 0.01f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        batch.dispose();
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
