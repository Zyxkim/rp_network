package com.adzteam.urbook.general.ui.feed;

import android.util.Log;

import androidx.lifecycle.MediatorLiveData;

import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.Post;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FeedRepo {

    private final MediatorLiveData<ArrayList<Post>> mPostsData = new MediatorLiveData<>();
    private final MediatorLiveData<ArrayList<Characters>> mCharactersData = new MediatorLiveData<>();
    private final MediatorLiveData<RefreshProgress> mRefreshProgress = new MediatorLiveData<>();

    public MediatorLiveData<ArrayList<Post>> getPostsLiveData() {
        return mPostsData;
    }
    public MediatorLiveData<ArrayList<Characters>> getCharactersLiveData() {
        return mCharactersData;
    }
    public MediatorLiveData<RefreshProgress> getRefreshProgress() {
        return mRefreshProgress;
    }

    public void downloadPosts() {
        mPostsData.setValue(new ArrayList<>());
        ArrayList<Post> listOfPosts = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String creator = (String) document.get("creator");
                    if (!creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        String name = (String) document.get("name");
                        String date = (String) document.get("date");
                        String id = (String) document.get("id");
                        String characterName = (String) document.get("characterName");
                        String content = (String) document.get("content");

                        Boolean isThereImage;
                        isThereImage = document.getBoolean("thereImage");
                        if (isThereImage == null) isThereImage = false;
                        Log.i("eee", String.valueOf(isThereImage));

                        Post newPost = new Post(document.getId(), Long.parseLong(date), name, creator, characterName, content);
                        listOfPosts.add(newPost);
                    }
                }
                mPostsData.setValue(listOfPosts);
                mRefreshProgress.postValue(RefreshProgress.DONE);
            }
        });

        mCharactersData.setValue(new ArrayList<>());
        ArrayList<Characters> listOfCharacters = new ArrayList<>();


        db.collection("characters").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                System.err.println("Listen failed: " + error);
                return;
            }

            for (DocumentChange dc : value.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    listOfCharacters.clear();
                    for (QueryDocumentSnapshot document : value) {
                        String creator = (String) document.get("creator");

                        if (!creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            String date = (String) document.get("date");
                            String fandom = (String) document.get("fandom");
                            String name = (String) document.get("name");
                            String characterName = (String) document.get("characterName");
                            String characterSurname = (String) document.get("characterSurname");
                            String content = (String) document.get("content");

                            Boolean isThereImage;
                            isThereImage = document.getBoolean("thereImage");
                            if (isThereImage == null) isThereImage = false;

                            Characters newCharacter = new Characters(document.getId(), Long.parseLong(date), name, creator, fandom, characterName, characterSurname, content, isThereImage);
                            listOfCharacters.add(newCharacter);
                        }
                    }
                }
            }
            mCharactersData.setValue(listOfCharacters);
        });
    }

    public enum RefreshProgress {
        DONE,
    }
}
