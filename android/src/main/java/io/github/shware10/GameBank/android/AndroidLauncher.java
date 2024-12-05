package io.github.shware10.GameBank.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.Gdx;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import io.github.shware10.GameBank.Core;
import io.github.shware10.GameBank.R;
import io.github.shware10.GameBank.StartScreen;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new Core(), config);

        // Google Sign-In 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Firebase 콘솔에서 제공된 ID
            .requestEmail()
            .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();

        // 앱 시작 시 자동으로 Google 로그인을 시도
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (lastSignedInAccount != null) {
            // 이전에 로그인한 계정이 있으면 Firebase 인증을 시도
            firebaseAuthWithGoogle(lastSignedInAccount);
        } else {
            // 로그인 계정이 없으면 Google 로그인 시작
            startGoogleSignIn();
        }
    }

    // Google 로그인 시작
    public void startGoogleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    GoogleSignInAccount account = task.getResult();
                    if (account != null) {
                        firebaseAuthWithGoogle(account);
                    }
                } else {
                    Gdx.app.log("GoogleSignIn", "Sign-In failed");
                }
            });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Gdx.app.log("FirebaseAuth", "Sign-In successful: " + (user != null ? user.getDisplayName() : "Unknown User"));
                } else {
                    Gdx.app.log("FirebaseAuth", "Authentication failed");
                }
            });
    }
}
