package com.adzteam.urbook.general.ui.rooms;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;

import com.adzteam.urbook.adapters.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class CreateRoomRepo {

    private final MediatorLiveData<UploadImageProgress> mUploadImageProgressLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<SaveRoomProgress> mSaveRoomProgressLiveData = new MediatorLiveData<>();

    public MediatorLiveData<UploadImageProgress> getUploadImageProgressLiveData() {
        return mUploadImageProgressLiveData;
    }

    public MediatorLiveData<SaveRoomProgress> getSaveRoomProgressLiveData() {
        return mSaveRoomProgressLiveData;
    }

    private Uri mImgUri;

    private void uploadImageToFirebase(Uri imgUri, String id) {
        if (imgUri != null) {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("rooms/" + id + "/image.jpg");
            profileRef.putFile(imgUri).addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                //Picasso.get().load(uri).into(mProfileImage);
                CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("rooms");
                DocumentReference docRef = collectionReference.document(id);
                docRef
                        .update("roomImg", uri.toString())
                        .addOnSuccessListener(aVoid -> Log.i("log", "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e -> Log.w("log", "Error updating document", e));

            })).addOnFailureListener(e -> mUploadImageProgressLiveData.setValue(UploadImageProgress.FAILED_TO_UPLOAD));
        }
    }

    public void getImage(Uri uri) {
        mImgUri = uri;
    }

    public void addRoomToFirestore(String name, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("rooms");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = collectionReference.document();
        Room newRoom = new Room(docRef.getId(), name, description, mAuth.getCurrentUser().getUid(), (new Date()).toString(), (mImgUri != null), "https://firebasestorage.googleapis.com/v0/b/urbook-43535.appspot.com/o/users%2FIYX73GrBRqOFgLG941ZXFqsKN6v2%2Fprofile.jpg?alt=media&token=b029217c-5a10-4ab4-b41f-7c877174e9ef");
        docRef.set(newRoom).addOnSuccessListener(aVoid -> mSaveRoomProgressLiveData.setValue(SaveRoomProgress.DONE))
                .addOnFailureListener(e -> mSaveRoomProgressLiveData.setValue(SaveRoomProgress.FAILED));
        uploadImageToFirebase(mImgUri, docRef.getId());
    }

    public enum SaveRoomProgress {
        DONE,
        FAILED,
    }

    public enum UploadImageProgress {
        FAILED_TO_UPLOAD,
    }
}
