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

import com.adzteam.urbook.R;
import com.adzteam.urbook.authentification.login.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        mGoogleSignInClient.setValue(getGoogleSignInClient(mContext));
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
                    writeIsLogin();
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
        mGoogleSignInIntent.setValue(getGoogleIntent(mGoogleSignInClient.getValue()));
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
        Log.i("www", "CATCH");
        catchResult(data, mAuth.getValue(), new LoginCallback());
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
        try {
            FileInputStream fis = mContext.openFileInput("IsLogin.txt");
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            if (line != null) stringBuilder.append(line);
            line = reader.readLine();
            while (line != null) {
                stringBuilder.append('\n').append(line);
                line = reader.readLine();
            }
            if (stringBuilder.toString().equals("true")) return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public void signOut() {
        mAuth.getValue().signOut();
        if (mGoogleSignInClient.getValue() != null) {
            mGoogleSignInClient.getValue().signOut();
        }
        writeIsNotLogin();
    }

    public enum LoginProgress {
        NONE,
        SUCCESS,
        IN_PROGRESS,
        FAILED,
    }

    public enum RegistrationProgress {
        NONE,
        SUCCESS,
        FAILED,
        SEND_EMAIL,
    }

    public enum AddUserToDatabaseProgress {
        NONE,
        SUCCESS,
        FAILED,
    }

    public enum ResetPasswordProgress {
        NONE,
        SUCCESS,
        FAILED,
    }

    public interface Callback {

        public void setSuccess();
        public void setFailed();
    }


    public void writeIsLogin() {
        try {
            FileOutputStream fos = mContext.openFileOutput("IsLogin.txt", mContext.MODE_PRIVATE);
            fos.write("true".getBytes());
        } catch (Exception e) {
        }
    }

    public void writeIsNotLogin() {
        try {
            FileOutputStream fos = mContext.openFileOutput("IsLogin.txt", mContext.MODE_PRIVATE);
            fos.write("false".getBytes());
        } catch (Exception e) {
        }
    }

    public class LoginCallback implements Callback {

        @Override
        public void setSuccess() {
            writeIsLogin();
            mLoginProgress.setValue(LoginProgress.SUCCESS);

        }

        @Override
        public void setFailed() {
            mLoginProgress.setValue(LoginProgress.FAILED);
        }
    }


    public void firebaseAuthWithGoogle(GoogleSignInAccount account, Callback callback) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.getValue().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getValue().getCurrentUser();
                    addUserToDataBase(account.getEmail(), account.getDisplayName());
                    callback.setSuccess();
                } else {
                    //Toast.makeText(activity, "error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public GoogleSignInClient getGoogleSignInClient(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
        return googleSignInClient;
    }

    public Intent getGoogleIntent(GoogleSignInClient googleSignInClient) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        return signInIntent;
    }

    public void catchResult(@Nullable Intent data, FirebaseAuth auth, AuthRepo.Callback callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account, callback);
        } catch (ApiException e) {
            callback.setFailed();
        }
    }


}

