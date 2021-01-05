package com.adzteam.urbook.general.ui.profile;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class EditProfileViewModel extends AndroidViewModel {

    private final EditProfileRepo mRepo = new EditProfileRepo();
    private MediatorLiveData<EditState> editStateMediatorLiveData = new MediatorLiveData<>();
    public EditProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public MediatorLiveData<EditState> getEditStateMediatorLiveData() {
        return editStateMediatorLiveData;
    }

    public boolean isNameValidate(String name) {
        return !name.isEmpty();
    }

    public void editProfile(String name, String status) {
        if (isNameValidate(name)) {
            MediatorLiveData<EditProfileRepo.EditProgress> editProgressMediatorLiveData = mRepo.getEditProgressMediatorLiveData();
            editStateMediatorLiveData.addSource(editProgressMediatorLiveData, new Observer<EditProfileRepo.EditProgress>() {
                @Override
                public void onChanged(EditProfileRepo.EditProgress editProgress) {
                    if (editProgress == EditProfileRepo.EditProgress.DONE) {
                        editStateMediatorLiveData.setValue(EditState.DONE);
                    } else if (editProgress == EditProfileRepo.EditProgress.FAILED) {
                        editStateMediatorLiveData.setValue(EditState.FAILED);
                    }
                }
            });
            mRepo.editProfile(name, status);
        } else {
            editStateMediatorLiveData.setValue(EditState.EMPTY_NAME);
        }
    }

    public void uploadImageToFirebase(Uri imgUri) {
        mRepo.uploadImageToFirebase(imgUri);
    }

    public enum EditState {
        EMPTY_NAME,
        DONE,
        FAILED,
    }
}
