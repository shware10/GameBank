package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;

public class Core extends Game {
    private StartScreen startScreen;
    private LobbyScreen lobbyScreen;

    @Override
    public void create() {
        startScreen = new StartScreen(this);
        lobbyScreen = new LobbyScreen(this);

        setScreen(startScreen);
    }

    public LobbyScreen getLobbyScreen() {
        return lobbyScreen;
    }
}
