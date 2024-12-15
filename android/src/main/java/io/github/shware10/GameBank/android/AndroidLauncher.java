package io.github.shware10.GameBank.android;

import android.content.Intent;
import android.os.Bundle;

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

import io.github.shware10.GameBank.BuildConfig;
import io.github.shware10.GameBank.Core;
import io.github.shware10.GameBank.GoogleSignInService;
import io.github.shware10.GameBank.StartScreen;

public class AndroidLauncher extends AndroidApplication implements GoogleSignInService {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        // Core와 StartScreen 초기화, this를 GoogleSignInService로 전달
        initialize(new Core(this), config);

        // Google Sign-In 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID)
            .requestEmail()
            .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void startSignIn() {
        // Google 로그인 시작
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Gdx.app.log("GoogleSignIn", "Firebase login successful: ");

                    GoogleSignInAccount account = task.getResult();
                    if (account != null) {
                        firebaseAuthWithGoogle(account);
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        // 로그인 실패의 상세 예외 메시지를 로그캣에 출력
                        Gdx.app.log("GoogleSignIn", "Sign-In failed: " + exception.getMessage());

                        // 예외 스택 트레이스를 추가로 로그로 출력 (보다 자세한 오류 분석을 위해)
                        exception.printStackTrace();
                    }
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

