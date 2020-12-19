package com.adzteam.urbook.authentification.registration;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.adzteam.urbook.authentification.AuthRepo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationViewModel extends AndroidViewModel {

    private AuthRepo mRepo = new AuthRepo(getApplication());

    private MediatorLiveData<RegistrationData> mRegistrationData = new MediatorLiveData<>();
    private MediatorLiveData<RegistrationState> mRegistrationState = new MediatorLiveData<>();
    private MediatorLiveData<LoginState> mLoginState = new MediatorLiveData<>();
    private MediatorLiveData<AddUserToDatabaseState> mAddUserToDatabaseState = new MediatorLiveData<>();

    private LiveData<Intent> mGoogleSignInIntent = mRepo.getGoogleSignInIntent();

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<RegistrationData> getRegistrationLiveData() {
        return mRegistrationData;
    }

    public LiveData<RegistrationState> getRegistrationState() {
        return mRegistrationState;
    }

    public LiveData<LoginState> getLoginState() {
        return mLoginState;
    }

    public LiveData<AddUserToDatabaseState> getAddUserToDatabaseState() {
        return mAddUserToDatabaseState;
    }

    public void registerWithEmail(String email, String name, String password, String confirmPassword) {
        mRegistrationData.setValue(new RegistrationData(email, name, password, confirmPassword));
        if (mRegistrationData.getValue().isValidate()) {
            mRegistrationState.postValue(RegistrationState.IN_PROGRESS);

            final LiveData<AuthRepo.RegistrationProgress> registrationProgress = mRepo.getRegistrationProgress();
            final LiveData<AuthRepo.AddUserToDatabaseProgress> addUserToDatabaseState = mRepo.getAddUserToDatabaseProgress();

            mRegistrationState.addSource(registrationProgress, new Observer<AuthRepo.RegistrationProgress>() {
                @Override
                public void onChanged(AuthRepo.RegistrationProgress progress) {
                    if (progress == AuthRepo.RegistrationProgress.SUCCESS) {
                        mRegistrationState.postValue(RegistrationState.SUCCESS);
                        mRegistrationState.removeSource(registrationProgress);
                    } else if (progress == AuthRepo.RegistrationProgress.FAILED) {
                        mRegistrationState.postValue(RegistrationState.FAILED);
                        mRegistrationState.removeSource(registrationProgress);
                    } else if (progress == AuthRepo.RegistrationProgress.SEND_EMAIL) {
                        mRegistrationState.postValue(RegistrationState.SEND_EMAIL);
                        mRegistrationState.removeSource(registrationProgress);
                    }
                }
            });

            mAddUserToDatabaseState.addSource(addUserToDatabaseState, new Observer<AuthRepo.AddUserToDatabaseProgress>() {
                @Override
                public void onChanged(AuthRepo.AddUserToDatabaseProgress progress) {
                    if (progress == AuthRepo.AddUserToDatabaseProgress.SUCCESS) {
                        mRegistrationState.postValue(RegistrationState.SUCCESS);
                        mRegistrationState.removeSource(addUserToDatabaseState);
                    } else if (progress == AuthRepo.AddUserToDatabaseProgress.FAILED) {
                        mRegistrationState.postValue(RegistrationState.FAILED);
                        mRegistrationState.removeSource(addUserToDatabaseState);
                    }
                }
            });

            mRepo.registerWithEmail(email, password, name);
        }
    }

    public void registerWithGoogle() {

        final LiveData<AuthRepo.LoginProgress> registrationProgress = mRepo.getLoginProgress();

        mLoginState.addSource(registrationProgress, new Observer<AuthRepo.LoginProgress>() {
            @Override
            public void onChanged(AuthRepo.LoginProgress progress) {
                if (progress == AuthRepo.LoginProgress.SUCCESS) {
                    mLoginState.postValue(LoginState.SUCCESS);
                    mLoginState.removeSource(registrationProgress);
                } else if (progress == AuthRepo.LoginProgress.IN_PROGRESS) {
                    mLoginState.postValue(LoginState.IN_PROGRESS);
                    mLoginState.removeSource(registrationProgress);
                } else if (progress == AuthRepo.LoginProgress.FAILED) {
                    mLoginState.postValue(LoginState.FAILED);
                    mLoginState.removeSource(registrationProgress);
                }

            }
        });

        mRepo.loginWithGoogle();
    }

    public void catchGoogleResult(@Nullable Intent data) {
        mRepo.catchGoogleResult(data);
    }

    public LiveData<Intent> getGoogleSignInIntent() {
        return mGoogleSignInIntent;
    }

    enum RegistrationState {
        IN_PROGRESS,
        SUCCESS,
        FAILED,
        SEND_EMAIL,
        NONE
    }

    enum LoginState {
        IN_PROGRESS,
        SUCCESS,
        FAILED,
        NONE
    }

    enum AddUserToDatabaseState {
        NONE,
        SUCCESS,
        FAILED,
    }

    class RegistrationData {
        private String mEmail;
        private String mName;
        private String mPassword;
        private String mConfirmPassword;
        private boolean mIsValidate;

        private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        private final Pattern mPattern = Pattern.compile(EMAIL_PATTERN);

        public RegistrationData(String email, String name, String password, String confirmPassword) {
            mEmail = email;
            mName = name;
            mPassword = password;
            mConfirmPassword = confirmPassword;
            mIsValidate = isPasswordValidate() && isNameValidate() && isEmailValidate() && isConfirmPasswordValidate();
        }

        public boolean isValidate() {
            return mIsValidate;
        }
        public boolean isEmailValidate() {
            return validateEmail(mEmail);
        }

        public boolean isPasswordValidate() {
            return validatePassword(mPassword);
        }

        public boolean isNameValidate() {
            return !mName.isEmpty();
        }

        public boolean isConfirmPasswordValidate() {
            return mPassword.equals(mConfirmPassword);
        }

        public boolean validateEmail(String email) {
            Matcher matcher;
            matcher = mPattern.matcher(email);
            return matcher.matches();
        }

        public boolean validatePassword(String password) {
            return password.length() > 5;
        }
    }



}
