package com.adzteam.urbook.authentification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.util.Collection;
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
        mGoogleSignInClient.setValue(GoogleAuth.getGoogleSignInClient(mContext));
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

    public void loginWithEmail(final LoginViewModel.LoginData loginData) {
        mAuth.getValue().signInWithEmailAndPassword(loginData.getLogin(), loginData.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mLoginProgress.setValue(LoginProgress.SUCCESS);

                    try {
                        FileOutputStream fos = mContext.openFileOutput("Profile.txt", mContext.MODE_PRIVATE);

                        XmlSerializer serializer = Xml.newSerializer();
                        serializer.setOutput(fos, "UTF-8");
                        serializer.startDocument(null, Boolean.valueOf(true));
                        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                        serializer.startTag(null, "root");

                        serializer.startTag(null, "name");
                        serializer.text("NAME");
                        serializer.endTag(null, "name");

                        serializer.startTag(null, "email");
                        serializer.text(loginData.getLogin());
                        serializer.endTag(null, "email");

                        serializer.startTag(null, "password");
                        serializer.text(loginData.getPassword());
                        serializer.endTag(null, "password");

                        serializer.endDocument();
                        serializer.flush();

                        fos.close();
                    } catch (Exception e) {
                        //Toast.makeText(this,"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                } else {
                    mLoginProgress.setValue(LoginProgress.FAILED);
                }
            }
        });


    }

    public void loginWithGoogle() {
        mGoogleSignInIntent.setValue(GoogleAuth.getGoogleIntent(mGoogleSignInClient.getValue()));
        mGoogleSignInIntent.setValue(null);

        try {
            FileOutputStream fos = mContext.openFileOutput("Profile.txt", mContext.MODE_PRIVATE);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "root");

            serializer.startTag(null, "name");
            serializer.text("NAME");
            serializer.endTag(null, "name");

            serializer.startTag(null, "email");
            serializer.text("gmail");
            serializer.endTag(null, "email");

            serializer.startTag(null, "password");
            serializer.text("-");
            serializer.endTag(null, "password");

            serializer.endDocument();
            serializer.flush();

            fos.close();
        } catch (Exception e) {
            //Toast.makeText(this,"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void catchGoogleResult(@Nullable Intent data) {
        GoogleAuth.catchResult(data, mAuth.getValue());
        mLoginProgress.setValue(LoginProgress.SUCCESS);
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

    public boolean isLoggedIn() {
        return mAuth.getValue().getCurrentUser() != null;
    }

    public void signOut() {
        mAuth.getValue().signOut();
        if (mGoogleSignInClient.getValue() != null) {
            mGoogleSignInClient.getValue().signOut();
        }
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

