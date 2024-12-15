package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends Game {

    private final GoogleSignInService googleSignInService;

    // Core 생성자에서 GoogleSignInService를 전달받음
    public Core(GoogleSignInService googleSignInService) {
        this.googleSignInService = googleSignInService;
    }

    @Override
    public void create() {
        // StartScreen에 GoogleSignInService 전달
        setScreen(new StartScreen(this, googleSignInService));
    }
}
