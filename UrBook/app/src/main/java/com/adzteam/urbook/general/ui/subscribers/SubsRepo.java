package com.adzteam.urbook.general.ui.subscribers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;

import com.adzteam.urbook.adapters.Friend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SubsRepo {
    private final MediatorLiveData<ArrayList<Friend>> mFriendsData = new MediatorLiveData<>();
    private final MediatorLiveData<com.adzteam.urbook.general.ui.subscribers.SubsRepo.RefreshProgress> mRefreshProgress = new MediatorLiveData<>();

    public MediatorLiveData<ArrayList<Friend>> getRoomsLiveData() {
        return mFriendsData;
    }

    public MediatorLiveData<com.adzteam.urbook.general.ui.subscribers.SubsRepo.RefreshProgress> getRefreshProgress() {
        return mRefreshProgress;
    }

    public void downloadFriends() {
        mFriendsData.setValue(new ArrayList<>());
        ArrayList<Friend> listOfFriends = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("subscribers");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = (String) document.get("id");

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(id)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String name = (String) document.get("name");
                                String status = (String) document.get("status");
                                String uri = (String) document.get("profileImg");

                                Friend newFriend = new Friend(id, name, status, uri);
                                listOfFriends.add(newFriend);

                                mFriendsData.setValue(listOfFriends);
                                Log.i("test friend", "add");
                                mRefreshProgress.postValue(com.adzteam.urbook.general.ui.subscribers.SubsRepo.RefreshProgress.DONE);
                            }
                        }
                    });
                }
            }
        });
    }

    public enum RefreshProgress {
        DONE,
    }
}

