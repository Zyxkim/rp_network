package com.adzteam.urbook.authentification;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adzteam.urbook.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleAuth {
    private static final int RC_SIGN_IN = 9001;

    public static void firebaseAuthWithGoogle(String idToken, final FirebaseAuth auth) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(activity, "logged in successfully", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                } else {
                    //throw new Exception("Exception message");
                    //Toast.makeText(activity, "error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static GoogleSignInClient getGoogleSignInClient(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
        return googleSignInClient;
    }

    public static Intent getGoogleIntent(GoogleSignInClient googleSignInClient) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        return signInIntent;
    }
    public static void catchResult(@Nullable Intent data, FirebaseAuth auth, AuthRepo.Callback callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            callback.setSuccess();
            //Toast.makeText(activity, "firebaseAuthWithGoogle:" + account.getId(), Toast.LENGTH_SHORT).show();
            GoogleAuth.firebaseAuthWithGoogle(account.getIdToken(), auth);
        } catch (ApiException e) {
            callback.setFailed();
            //Toast.makeText(activity, "Google sign in failed" + e, Toast.LENGTH_SHORT).show();
        }
    }
}
