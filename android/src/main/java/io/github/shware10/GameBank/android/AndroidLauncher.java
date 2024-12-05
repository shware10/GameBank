package io.github.shware10.GameBank.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import io.github.shware10.GameBank.Core;
import io.github.shware10.GameBank.DBHelperInterface;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBHelperInterface dbHelper = new DBHelper(this);

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        initialize(new Core(dbHelper), configuration);
    }
}
