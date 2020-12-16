package com.adzteam.urbook.authentification;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordViewModel extends AndroidViewModel {

    private AuthRepo mRepo = new AuthRepo(getApplication());

    public ResetPasswordViewModel(@NonNull Application application) {
        super(application);
    }

    private MediatorLiveData<ResetPasswordViewModel.ResetPasswordState> mResetPasswordState = new MediatorLiveData<>();

    public LiveData<ResetPasswordViewModel.ResetPasswordState> getResetPasswordState() {
        return mResetPasswordState;
    }

    public void resetPassword(String mail) {
        if (!validateEmail(mail)) {
            mResetPasswordState.setValue(ResetPasswordState.NOT_VALID_EMAIL);
        } else {
            mResetPasswordState.postValue(ResetPasswordState.IN_PROGRESS);
            final LiveData<AuthRepo.ResetPasswordProgress> progressLiveData = mRepo.getResetPasswordProgress();
            mResetPasswordState.addSource(progressLiveData, new Observer<AuthRepo.ResetPasswordProgress>() {
                @Override
                public void onChanged(AuthRepo.ResetPasswordProgress resetPasswordProgress) {
                    if (resetPasswordProgress == AuthRepo.ResetPasswordProgress.SUCCESS) {
                        mResetPasswordState.postValue(ResetPasswordState.SUCCESS);
                        mResetPasswordState.removeSource(progressLiveData);
                    } else if (resetPasswordProgress == AuthRepo.ResetPasswordProgress.FAILED) {
                        mResetPasswordState.postValue(ResetPasswordState.FAILED);
                        mResetPasswordState.removeSource(progressLiveData);
                    }
                }
            });

            mRepo.resetPassword(mail);
        }
    }

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final Pattern mPattern = Pattern.compile(EMAIL_PATTERN);

    public boolean validateEmail(String email) {
        Matcher matcher;
        matcher = mPattern.matcher(email);
        return matcher.matches();
    }

    enum ResetPasswordState {
        NONE,
        NOT_VALID_EMAIL,
        ERROR,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
}
