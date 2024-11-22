package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class GameScreen1 implements Screen
{
    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;

    private TextureAtlas leftWalkAtlas, rightWalkAtlas;
    private Animation<TextureRegion> leftWalkAnimation, rightWalkAnimation, currentAnimation;

    private Texture trashCan, shrimpCan, tunaCan, krabCan;
    private Array<Sprite> obstacles;
    private Array<Item> items;

    private float stateTime;
    private float characterX, characterY = 100;
    private float speed = 300f;
    private boolean isLeft = true;

    private int score = 0;
    private boolean isGameOver = false;

    public GameScreen1(Game game)
    {
        this.game = game;
    }

    @Override
    public void show()
    {
        batch = new SpriteBatch();
        font = new BitmapFont(); // BitmapFont 초기화 (기본 폰트 사용)
        font.getData().setScale(5f);
        // 캐릭터 애니메이션 로드
        leftWalkAtlas = new TextureAtlas("penguin_Left_Walk.atlas");
        rightWalkAtlas = new TextureAtlas("penguin_Right_Walk.atlas");

        leftWalkAnimation = new Animation<>(0.1f, leftWalkAtlas.findRegions("LeftWalk"), Animation.PlayMode.LOOP);
        rightWalkAnimation = new Animation<>(0.1f, rightWalkAtlas.findRegions("RightWalk"), Animation.PlayMode.LOOP);
        currentAnimation = leftWalkAnimation;
        characterX = (Gdx.graphics.getWidth() - leftWalkAnimation.getKeyFrame(0).getRegionWidth()) / 2.0f;

        // 장애물 및 아이템 텍스처 로드
        trashCan = new Texture("trashCan.png");
        shrimpCan = new Texture("shrimpCan.png");    // 5점 아이템
        tunaCan = new Texture("tunaCan.png");  // 10점 아이템
        krabCan = new Texture("krabCan.png");  // 20점 아이템
        obstacles = new Array<>();
        items = new Array<>();

        // 스코어 증가 타이머
        Timer.schedule(new Timer.Task()
        {
            @Override
            public void run()
            {
                if (!isGameOver)
                {
                    score++;
                }
            }
        }, 1, 1); // 매 1초마다 실행

        // 장애물 및 아이템 생성 타이머
        Timer.schedule(new Timer.Task()
        {
            @Override
            public void run()
            {
                if (!isGameOver)
                {
                    createObstacleOrItem();
                }
            }
        }, 1, 1.2f); // 매 2초마다 장애물이나 아이템 생성
    }

    @Override
    public void render(float delta)
    {
        if (isGameOver) return; // 게임 오버 시 렌더링 중단

        Gdx.gl.glClearColor(223 / 255f, 132 / 255f, 3 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        // 캐릭터 방향 전환
        if (Gdx.input.justTouched())
        {
            isLeft = !isLeft;
            currentAnimation = isLeft ? leftWalkAnimation : rightWalkAnimation;
        }

        // 캐릭터 위치 업데이트
        if (isLeft)
        {
            characterX -= speed * delta;
            characterX = Math.max(characterX, 0); // 왼쪽 화면 밖으로 나가지 않도록
        }
        else
        {
            characterX += speed * delta;
            characterX = Math.min(characterX, Gdx.graphics.getWidth() - currentAnimation.getKeyFrame(stateTime).getRegionWidth()); // 오른쪽 화면 밖으로 나가지 않도록
        }

        // 장애물 및 아이템 업데이트
        for (Sprite obstacle : obstacles)
        {
            obstacle.translateY(-400 * delta); // 장애물 아래로 이동
            if (obstacle.getY() + obstacle.getHeight() < 0) obstacles.removeValue(obstacle, true); // 화면 아래로 나가면 제거
            if (obstacle.getBoundingRectangle().overlaps(new Rectangle(characterX, characterY, currentAnimation.getKeyFrame(stateTime).getRegionWidth()*0.7f, currentAnimation.getKeyFrame(stateTime).getRegionHeight()*0.7f)))
            {
                isGameOver = true; // 장애물에 충돌 시 게임 종료
            }
        }

        for (Item item : items)
        {
            item.sprite.translateY(-400 * delta); // 아이템 아래로 이동
            if (item.sprite.getY() + item.sprite.getHeight() < 0) items.removeValue(item, true); // 화면 아래로 나가면 제거
            if (item.sprite.getBoundingRectangle().overlaps(new Rectangle(characterX, characterY, currentAnimation.getKeyFrame(stateTime).getRegionWidth()*0.7f, currentAnimation.getKeyFrame(stateTime).getRegionHeight()*0.7f))) {
                items.removeValue(item, true); // 아이템 획득 시 제거
                score += item.scoreValue; // 아이템 획득 시 해당 점수 증가
            }
        }

        // 화면에 그리기
        batch.begin();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, characterX, characterY); // 캐릭터 그리기

        for (Sprite obstacle : obstacles)
        {
            obstacle.draw(batch); // 장애물 그리기
        }

        for (Item item : items)
        {
            item.sprite.draw(batch); // 아이템 그리기
        }

        // 중앙 상단에 스코어 텍스트 그리기
        String scoreText =  "" + score;
        GlyphLayout layout = new GlyphLayout(font, scoreText);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2; // 가로 중앙
        float textY = Gdx.graphics.getHeight() - 200; // 화면 위에서 20 픽셀 아래

        font.draw(batch, scoreText, textX, textY);

        batch.end();
    }

    private void createObstacleOrItem()
    {
        // 장애물 생성
        float ratio = 1.3f;
        int randomCreateNum = MathUtils.random(3);
        for (int i = 0; i < randomCreateNum; i++)
        {
            int randomCreateItem = MathUtils.random(10);
            if (randomCreateItem <= 3)
            {
                Texture selectedTexture;
                int scoreValue;

                int randomItem = MathUtils.random(10);
                if (randomItem >= 6)
                {
                    selectedTexture = shrimpCan;
                    scoreValue = 5;
                }
                else if (randomItem >= 2)
                {
                    selectedTexture = tunaCan;
                    scoreValue = 10;
                }
                else
                {
                    selectedTexture = krabCan;
                    scoreValue = 20;
                }
                Sprite itemSprite = new Sprite(selectedTexture);
                if(selectedTexture == shrimpCan)
                {
                    itemSprite.setSize(65*ratio, 104*ratio);
                }
                else if(selectedTexture == tunaCan)
                {
                    itemSprite.setSize(108*ratio, 87*ratio);
                }
                else //krabCan
                {
                    itemSprite.setSize(99*ratio, 100*ratio);
                }
                float maxPosition = Gdx.graphics.getWidth() - itemSprite.getWidth();
                float[] spawnPosition = {0f, maxPosition * 0.1f, maxPosition * 0.2f, maxPosition * 0.3f, maxPosition * 0.4f, maxPosition * 0.5f,
                    maxPosition * 0.6f, maxPosition * 0.7f, maxPosition * 0.8f, maxPosition * 0.9f, maxPosition};
                int randomInt = MathUtils.random(9);
                itemSprite.setPosition(spawnPosition[randomInt], Gdx.graphics.getHeight());
                items.add(new Item(itemSprite, scoreValue));
            }
            else
            {
                Sprite obstacle = new Sprite(trashCan);
                obstacle.setSize(103*ratio, 86*ratio); // 크기 설정
                float maxPosition = Gdx.graphics.getWidth() - obstacle.getWidth();
                float[] spawnPosition = {0f, maxPosition * 0.1f, maxPosition * 0.2f, maxPosition * 0.3f, maxPosition * 0.4f, maxPosition * 0.5f,
                maxPosition * 0.6f, maxPosition * 0.7f, maxPosition * 0.8f, maxPosition * 0.9f, maxPosition};
                int randomInt = MathUtils.random(9);
                obstacle.setPosition(spawnPosition[randomInt], Gdx.graphics.getHeight());
                obstacles.add(obstacle);
            }
        }
    }
    // 아이템 클래스 (각 아이템마다 점수 값을 포함)
    private static class Item
    {
        Sprite sprite;
        int scoreValue;

        public Item(Sprite sprite, int scoreValue)
        {
            this.sprite = sprite;
            this.scoreValue = scoreValue;
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide()
    {
        dispose();
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        font.dispose(); // BitmapFont 해제
        leftWalkAtlas.dispose();
        rightWalkAtlas.dispose();
        trashCan.dispose();
        shrimpCan.dispose();
        tunaCan.dispose();
        krabCan.dispose();
    }
}
