package com.adzteam.urbook.general.ui.rooms;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.general.GeneralActivity;
import com.adzteam.urbook.room.RoomActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.appcompat.view.menu.ActionMenuItemView;

import java.util.Date;

public class CreateRoomActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;

    private TextInputEditText mRoomDescriptionInput;
    private TextInputEditText mRoomNameInput;
    private ActionMenuItemView mSaveItem;
    private ActionMenuItemView mBackItem;
    private MaterialButton mAddRoomCover;
    private ImageView mRoomImg;
    private Uri mImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_create_room);

        mRoomDescriptionInput = findViewById(R.id.new_room_description);
        mRoomNameInput = findViewById(R.id.new_room_name);
        mSaveItem = findViewById(R.id.save);
        mBackItem = findViewById(R.id.back);
        mAddRoomCover = findViewById(R.id.editRoomImageBtn);
        mRoomImg = findViewById(R.id.profile_image);

        mSaveItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (GeneralActivity.hasConnection(getApplicationContext())) {
                    if (mRoomNameInput.getText().toString().trim().equals("")) {
                        mRoomNameInput.setError("Add Room name");
                        //Toast.makeText(getActivity(), "Add Room name please", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference collectionReference = db.collection("rooms");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        DocumentReference docRef = collectionReference.document();
                        Room newRoom = new Room(docRef.getId(), mRoomNameInput.getText().toString().trim(), mRoomDescriptionInput.getText().toString().trim(), mAuth.getCurrentUser().getUid(), (new Date()).toString(), (mImgUri != null), "https://firebasestorage.googleapis.com/v0/b/urbook-43535.appspot.com/o/users%2FIYX73GrBRqOFgLG941ZXFqsKN6v2%2Fprofile.jpg?alt=media&token=b029217c-5a10-4ab4-b41f-7c877174e9ef");

                        docRef.set(newRoom);

                        uploadImageToFirebase(mImgUri, docRef.getId());
                        replaceWithGeneralActivity();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBackItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                replaceWithGeneralActivity();
            }
        });

        mAddRoomCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, RC_SIGN_IN);

            }
        });
    }

    public void replaceWithGeneralActivity() {
        Intent intent = new Intent(this, GeneralActivity.class);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                mImgUri = data.getData();
                Picasso.get().load(mImgUri).into(mRoomImg);
            }
        }
    }

    private void uploadImageToFirebase(Uri imgUri, String id) {
        if (imgUri != null) {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("rooms/" + id + "/image.jpg");
            profileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //You will get donwload URL in uri
                            Log.i("check", "Download URL = "+ uri.toString());
                            //Adding that URL to Realtime database
                            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("rooms");
                            DocumentReference docRef = collectionReference.document(id);
                            Log.i("check",  id);

                            // Set the "isCapital" field of the city 'DC'
                            docRef
                                    .update("roomImg", uri.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("log", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("log", "Error updating document", e);
                                        }
                                    });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
