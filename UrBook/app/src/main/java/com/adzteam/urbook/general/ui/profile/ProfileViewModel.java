package com.adzteam.urbook.general.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.adzteam.urbook.authentification.AuthRepo;

public class ProfileViewModel extends AndroidViewModel {

    private AuthRepo mRepo = new AuthRepo(getApplication());

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public void signOut() {
        mRepo.signOut();

    }
}