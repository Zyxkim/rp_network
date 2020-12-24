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
                if (mRoomNameInput.getText().toString().trim().equals("")) {
                    mRoomNameInput.setError("Add Room name");
                    //Toast.makeText(getActivity(), "Add Room name please", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference collectionReference = db.collection("rooms");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    DocumentReference docRef = collectionReference.document();
                    Room newRoom = new Room(docRef.getId(), mRoomNameInput.getText().toString().trim(), mRoomDescriptionInput.getText().toString().trim(), mAuth.getCurrentUser().getUid(), (new Date()).toString(), (mImgUri != null));

                    docRef.set(newRoom);

                    uploadImageToFirebase(mImgUri, docRef.getId());
                    replaceWithGeneralActivity();
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
                            //Picasso.get().load(uri).into(mProfileImage);
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
