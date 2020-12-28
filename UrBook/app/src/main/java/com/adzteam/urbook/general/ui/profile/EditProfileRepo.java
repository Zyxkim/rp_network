package com.adzteam.urbook.general.ui.profile;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileRepo {

    private MediatorLiveData<EditProgress> editProgressMediatorLiveData = new MediatorLiveData<>();

    public MediatorLiveData<EditProgress> getEditProgressMediatorLiveData() {
        return editProgressMediatorLiveData;
    }

    public void editProfile(String name, String status) {
        FirebaseFirestore mFStore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = mFStore.collection("users").document(mAuth.getCurrentUser().getUid());
        Map<String, Object> edited = new HashMap<>();
        edited.put("name", name);
        edited.put("status", status);

        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editProgressMediatorLiveData.setValue(EditProgress.DONE);
            }
        });
    }

    public void uploadImageToFirebase(Uri imgUri) {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        StorageReference profileRef = mStorageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.putFile(imgUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                editProgressMediatorLiveData.setValue(EditProgress.FAILED);
            }
        });
    }

    public enum EditProgress {
        DONE,
        FAILED,
    }
}

