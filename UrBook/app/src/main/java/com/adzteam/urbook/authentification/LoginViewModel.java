package com.adzteam.urbook.authentification;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends AndroidViewModel {

    private AuthRepo mRepo = new AuthRepo(getApplication());

    private LoginData mCurLoginData = new LoginData("", "");

    private LiveData<GoogleSignInClient> mGoogleSignInClient = mRepo.getGoogleSignInClient();
    private LiveData<GoogleSignInOptions> mGoogleSignInOptions = mRepo.getGoogleSignInOptions();
    private LiveData<Intent> mGoogleSignInIntent = mRepo.getGoogleSignInIntent();

    private LiveData<FirebaseAuth> mAuth = mRepo.getAuth();

    private MediatorLiveData<LoginState> mLoginState = new MediatorLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mLoginState.setValue(LoginState.NONE);
    }

    public LiveData<LoginState> getLoginState() {
        return mLoginState;
    }

    public LiveData<Intent> getGoogleSignInIntent() {
        return mGoogleSignInIntent;
    }

    public void loginWithEmail(String email, String password) {
        mCurLoginData = new LoginData(email, password);

        boolean isEmailValidate = mCurLoginData.validateEmail();
        boolean isPasswordValidate = mCurLoginData.validatePassword();

        if (!isEmailValidate && !isPasswordValidate) {
            mLoginState.setValue(LoginState.NOT_VALID_EMAIL_AND_PASSWORD);
        } else if (!isEmailValidate) {
            mLoginState.setValue(LoginState.NOT_VALID_EMAIL);
        } else if (!isPasswordValidate) {
            mLoginState.setValue(LoginState.NOT_VALID_PASSWORD);
        } else {
            mLoginState.postValue(LoginState.IN_PROGRESS);
            final LiveData<AuthRepo.LoginProgress> progressLiveData = mRepo.getLoginProgress();
            mLoginState.addSource(progressLiveData, new Observer<AuthRepo.LoginProgress>() {
                @Override
                public void onChanged(AuthRepo.LoginProgress loginProgress) {
                    if (loginProgress == AuthRepo.LoginProgress.SUCCESS) {
                        mLoginState.postValue(LoginState.SUCCESS);
                        mLoginState.removeSource(progressLiveData);
                    } else if (loginProgress == AuthRepo.LoginProgress.FAILED) {
                        mLoginState.postValue(LoginState.FAILED);
                        mLoginState.removeSource(progressLiveData);
                    }
                }
            });

            mRepo.loginWithEmail(mCurLoginData);
        }
    }

    public void loginWithGoogle() {
        mLoginState.postValue(LoginState.IN_PROGRESS);
        final LiveData<AuthRepo.LoginProgress> progressLiveData = mRepo.getLoginProgress();
        mLoginState.addSource(progressLiveData, new Observer<AuthRepo.LoginProgress>() {
            @Override
            public void onChanged(AuthRepo.LoginProgress loginProgress) {
                if (loginProgress == AuthRepo.LoginProgress.SUCCESS) {
                    mLoginState.postValue(LoginState.SUCCESS);
                    mLoginState.removeSource(progressLiveData);
                } else if (loginProgress == AuthRepo.LoginProgress.FAILED) {
                    mLoginState.postValue(LoginState.FAILED);
                    mLoginState.removeSource(progressLiveData);
                }
            }
        });
        mRepo.loginWithGoogle();
        //mLoginState.setValue(LoginViewModel.LoginState.SUCCESS);
    }

    public void catchGoogleResult(@Nullable Intent data) {
        mRepo.catchGoogleResult(data);
    }

    enum LoginState {
        NONE,
        NOT_VALID_EMAIL,
        NOT_VALID_PASSWORD,
        NOT_VALID_EMAIL_AND_PASSWORD,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }

    public static class LoginData {
        private final String mEmail;
        private final String mPassword;

        private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        private final Pattern mPattern = Pattern.compile(EMAIL_PATTERN);

        public LoginData(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        public String getLogin() {
            return mEmail;
        }

        public String getPassword() {
            return mPassword;
        }

        public boolean validateEmail() {
            Matcher matcher;
            matcher = mPattern.matcher(mEmail);
            return matcher.matches();
        }

        public boolean validatePassword() {
            return mPassword.length() > 5;
        }

    }
}
