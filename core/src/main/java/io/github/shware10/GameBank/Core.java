package io.github.shware10.GameBank;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends Game {
    @Override
    public void create() {
        setScreen(new StartScreen(this));
    }
}
