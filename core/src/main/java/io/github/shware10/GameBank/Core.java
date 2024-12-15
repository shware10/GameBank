package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;

public class Core extends Game {
    private StartScreen startScreen;
    private LobbyScreen lobbyScreen;


    public LobbyScreen getLobbyScreen() {
        return lobbyScreen;
    }

    private final GoogleSignInService googleSignInService;

    // Core 생성자에서 GoogleSignInService를 전달받음
    public Core(GoogleSignInService googleSignInService) {
        this.googleSignInService = googleSignInService;
    }

    @Override
    public void create() {
        // StartScreen에 GoogleSignInService 전달
        setScreen(new StartScreen(this, googleSignInService));
        lobbyScreen = new LobbyScreen(this);
    }
}
