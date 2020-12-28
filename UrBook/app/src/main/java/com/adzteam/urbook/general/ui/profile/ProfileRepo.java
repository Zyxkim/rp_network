package com.adzteam.urbook.general.ui.profile;

import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;

import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileRepo {

    private final MediatorLiveData<ArrayList<Post>> mPostsData = new MediatorLiveData<>();
    private final MediatorLiveData<ArrayList<Characters>> mCharactersData = new MediatorLiveData<>();

    public MediatorLiveData<ArrayList<Post>> getPostsLiveData() {
        return mPostsData;
    }
    public MediatorLiveData<ArrayList<Characters>> getCharactersLiveData() {
        return mCharactersData;
    }

    private MediatorLiveData<String> nameLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> statusLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> subsLiveData = new MediatorLiveData<>();

    public MediatorLiveData<String> getStatusLiveData() {
        return statusLiveData;
    }
    public MediatorLiveData<String> getNameLiveData() {
        return nameLiveData;
    }
    public MediatorLiveData<String> getSubsLiveData() {
        return subsLiveData;
    }

    public void uploadProfileData() {
        FirebaseFirestore mFStore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = mFStore.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    nameLiveData.setValue(documentSnapshot.getString("name"));
                    statusLiveData.setValue(documentSnapshot.getString("status"));
                    if (documentSnapshot.get("subs") != null) {
                        subsLiveData.setValue(documentSnapshot.getLong("subs").toString() + " subscribers");
                    }
                }
            }
        });
    }

    public void donwload() {
        mPostsData.setValue(new ArrayList<>());
        ArrayList<Post> listOfPosts = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        listOfPosts.clear();
                        for (QueryDocumentSnapshot document : value) {
                            String creator = (String) document.get("creator");

                            if (creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                String date = (String) document.get("date");
                                //String id = (String) document.get("id");
                                String name = (String) document.get("name");
                                String characterName = (String) document.get("characterName");
                                String content = (String) document.get("content");

                                Post newPost = new Post(document.getId(), Long.parseLong(date), name, creator, characterName, content);
                                listOfPosts.add(newPost);
                            }
                        }
                    }
                }
                mPostsData.setValue(listOfPosts);
            }
        });

        mCharactersData.setValue(new ArrayList<>());
        ArrayList<Characters> listOfCharacters = new ArrayList<>();

        db.collection("characters").orderBy("fandom", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        listOfCharacters.clear();
                        for (QueryDocumentSnapshot document : value) {
                            String creator = (String) document.get("creator");

                            if (creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                String date = (String) document.get("date");
                                String fandom = (String) document.get("fandom");
                                String name = (String) document.get("name");
                                String characterName = (String) document.get("characterName");
                                String characterSurname = (String) document.get("characterSurname");
                                String content = (String) document.get("content");

                                Boolean isThereImage;
                                isThereImage = document.getBoolean("thereImage");
                                if (isThereImage == null) isThereImage =false;

                                Characters newCharacter = new Characters(document.getId(), Long.parseLong(date), name, creator, fandom, characterName, characterSurname, content, isThereImage);
                                listOfCharacters.add(newCharacter);
                            }

                        }
                    }
                }
                mCharactersData.setValue(listOfCharacters);
            }
        });
    }
}
