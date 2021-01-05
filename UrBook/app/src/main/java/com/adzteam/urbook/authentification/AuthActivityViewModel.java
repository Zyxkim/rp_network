package com.adzteam.urbook.authentification;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class AuthActivityViewModel extends AndroidViewModel {
    private AuthRepo mRepo = new AuthRepo(getApplication());

    public AuthActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isLoggedIn() {
        return mRepo.isLoggedIn();
    }
}
