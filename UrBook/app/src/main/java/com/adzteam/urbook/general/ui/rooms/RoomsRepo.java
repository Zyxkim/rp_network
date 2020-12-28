package com.adzteam.urbook.general.ui.rooms;

import androidx.lifecycle.MediatorLiveData;

import com.adzteam.urbook.adapters.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class RoomsRepo {
    private final MediatorLiveData<ArrayList<Room>> mRoomsData = new MediatorLiveData<>();
    private final MediatorLiveData<RefreshProgress> mRefreshProgress = new MediatorLiveData<>();

    public MediatorLiveData<ArrayList<Room>> getRoomsLiveData() {
        return mRoomsData;
    }
    public MediatorLiveData<RefreshProgress> getRefreshProgress() {
        return mRefreshProgress;
    }

    public void downloadRooms() {
        mRoomsData.setValue(new ArrayList<>());
        ArrayList<Room> listOfRooms = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("rooms");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = (String) document.get("name");
                    String description = (String) document.get("description");
                    String creator = (String) document.get("creator");
                    String date = (String) document.get("date");
                    Boolean isThereImage = document.getBoolean("thereImage");
                    if (isThereImage == null) isThereImage = false;
                    String uri = (String) document.get("roomImg");
                    Room newRoom = new Room(document.getId(), name, description, creator, date, isThereImage, uri);
                    listOfRooms.add(newRoom);
                }
                mRoomsData.setValue(listOfRooms);
                mRefreshProgress.postValue(RefreshProgress.DONE);
            }
        });
    }

    public boolean isAuthor(String creator) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return creator.equals(mAuth.getCurrentUser().getUid());
    }

    public enum RefreshProgress {
        DONE,
    }
}
