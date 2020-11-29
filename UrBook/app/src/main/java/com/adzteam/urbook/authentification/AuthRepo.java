package com.adzteam.urbook.authentification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepo {
    private static final int RC_SIGN_IN = 9001;

    private final Context mContext;

    private MutableLiveData<GoogleSignInClient> mGoogleSignInClient = new MutableLiveData<>();
    private MutableLiveData<GoogleSignInOptions> mGoogleSignInOptions = new MutableLiveData<>();
    private MediatorLiveData<Intent> mGoogleSignInIntent = new MediatorLiveData<>();
    private MutableLiveData<FirebaseAuth> mAuth = new MutableLiveData<>();

    private MediatorLiveData<LoginProgress> mLoginProgress = new MediatorLiveData<>();
    private MediatorLiveData<RegistrationProgress> mRegistrationProgress = new MediatorLiveData<>();
    private MediatorLiveData<ResetPasswordProgress> mResetPasswordProgress = new MediatorLiveData<>();
    private MediatorLiveData<AddUserToDatabaseProgress> mAddUserToDatabaseProgress = new MediatorLiveData<>();

    FirebaseFirestore db;
    String mUserId;

    public AuthRepo (Context context){
        mContext = context;
        mLoginProgress.setValue(LoginProgress.NONE);
        mResetPasswordProgress.setValue(ResetPasswordProgress.NONE);
        mAuth.setValue(FirebaseAuth.getInstance());
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<GoogleSignInClient> getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public LiveData<GoogleSignInOptions> getGoogleSignInOptions() {
        return mGoogleSignInOptions;
    }

    public LiveData<Intent> getGoogleSignInIntent() {
        return mGoogleSignInIntent;
    }

    public LiveData<FirebaseAuth> getAuth() {
        return mAuth;
    }

    public LiveData<LoginProgress> getLoginProgress() {
        return mLoginProgress;
    }

    public LiveData<RegistrationProgress> getRegistrationProgress() {
        return mRegistrationProgress;
    }

    public LiveData<AddUserToDatabaseProgress> getAddUserToDatabaseProgress() {
        return mAddUserToDatabaseProgress;
    }

    public LiveData<ResetPasswordProgress> getResetPasswordProgress() {
        return mResetPasswordProgress;
    }

    public void loginWithEmail(LoginViewModel.LoginData loginData) {
        mAuth.getValue().signInWithEmailAndPassword(loginData.getLogin(), loginData.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mLoginProgress.setValue(LoginProgress.SUCCESS);
                } else {
                    mLoginProgress.setValue(LoginProgress.FAILED);
                }
            }
        });
    }

    public void loginWithGoogle() {
        mGoogleSignInIntent.setValue(GoogleAuth.signInWithGoogle(mContext));
        mGoogleSignInIntent.setValue(null);
    }

    public void catchGoogleResult(@Nullable Intent data) {
        GoogleAuth.catchResult(data, mAuth.getValue());
    }

    public void resetPassword(String mail) {
        mAuth.getValue().sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mResetPasswordProgress.setValue(ResetPasswordProgress.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mResetPasswordProgress.setValue(ResetPasswordProgress.FAILED);
            }
        });
    }

    public void registerWithEmail(final String email, String password, final String name) {
        mAuth.getValue().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser mUser = mAuth.getValue().getCurrentUser();
                    mRegistrationProgress.setValue(RegistrationProgress.SUCCESS);
                    mUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mRegistrationProgress.setValue(RegistrationProgress.SEND_EMAIL);
                            addUserToDataBase(email, name);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mRegistrationProgress.setValue(RegistrationProgress.FAILED);
                            //Log.d(INFO, "Email wasn't sent" + e.getMessage());
                        }
                    });
                } else {
                    mRegistrationProgress.setValue(RegistrationProgress.FAILED);
                }
            }
        });
    }

    public void addUserToDataBase(String email, String name) {
        mUserId = mAuth.getValue().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("users").document(mUserId);
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mAddUserToDatabaseProgress.setValue(AddUserToDatabaseProgress.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAddUserToDatabaseProgress.setValue(AddUserToDatabaseProgress.FAILED);
            }
        });
    }

    enum LoginProgress {
        NONE,
        SUCCESS,
        FAILED,
    }

    enum RegistrationProgress {
        NONE,
        SUCCESS,
        FAILED,
        SEND_EMAIL,
    }

    enum AddUserToDatabaseProgress {
        NONE,
        SUCCESS,
        FAILED,
    }

    enum ResetPasswordProgress {
        NONE,
        SUCCESS,
        FAILED,
    }


}

