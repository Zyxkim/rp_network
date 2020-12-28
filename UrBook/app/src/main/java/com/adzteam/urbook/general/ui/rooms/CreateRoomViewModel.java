package com.adzteam.urbook.general.ui.rooms;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;



public class CreateRoomViewModel extends AndroidViewModel {

    private final CreateRoomRepo mRepo = new CreateRoomRepo();

    private final MediatorLiveData<CreateRoomState> mCreateRoomState = new MediatorLiveData<>();

    public MediatorLiveData<CreateRoomState> getRoomStateLiveData() {
        return mCreateRoomState;
    }

    public CreateRoomViewModel(@NonNull Application application) {
        super(application);
    }

    public void getImage(Uri uri) {
        mRepo.getImage(uri);
    }

    public void createRoom(String name, String description) {
        if (name.equals("")) {
            mCreateRoomState.setValue(CreateRoomState.EMPTY_NAME);
        } else {
            mCreateRoomState.addSource(mRepo.getUploadImageProgressLiveData(), uploadImageProgress -> {
                if (uploadImageProgress == CreateRoomRepo.UploadImageProgress.FAILED_TO_UPLOAD) {
                    mCreateRoomState.setValue(CreateRoomState.FAILED_TO_UPLOAD_IMAGE);
                }
            });

            mCreateRoomState.addSource(mRepo.getSaveRoomProgressLiveData(), saveRoomProgress -> {
                if (saveRoomProgress == CreateRoomRepo.SaveRoomProgress.DONE) {
                    mCreateRoomState.setValue(CreateRoomState.DONE);
                } else if (saveRoomProgress == CreateRoomRepo.SaveRoomProgress.FAILED) {
                    mCreateRoomState.setValue(CreateRoomState.FAILED_TO_UPLOAD_ROOM);
                }
            });

            mRepo.addRoomToFirestore(name, description);
        }
    }

    public enum CreateRoomState {
        EMPTY_NAME,
        FAILED_TO_UPLOAD_IMAGE,
        FAILED_TO_UPLOAD_ROOM,
        DONE,
    }
}
