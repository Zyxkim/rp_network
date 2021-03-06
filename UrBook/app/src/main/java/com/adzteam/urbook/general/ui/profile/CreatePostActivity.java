package com.adzteam.urbook.general.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.general.GeneralActivity;
import com.adzteam.urbook.room.RoomActivity;
import com.adzteam.urbook.room.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.appcompat.view.menu.ActionMenuItemView;

import java.util.Date;

import static com.adzteam.urbook.adapters.RoomsAdapter.CURRENT_ROOM_ID;

public class CreatePostActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1002;

    TextInputEditText mPostName;
    TextInputEditText mPostContent;
    ActionMenuItemView mSaveItem;
    ActionMenuItemView mBackItem;
    MaterialButton mPostImage;
    private ImageView mPostImg;
    private Uri mImgUri;

    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_create_post);

        mPostName = findViewById(R.id.new_character_name_content);
        mPostContent = findViewById(R.id.new_post_content);
        mPostImage = findViewById(R.id.editPostImageBtn);
        mSaveItem = findViewById(R.id.save);
        mBackItem = findViewById(R.id.back);
        mPostImg = findViewById(R.id.post_image);

        mSaveItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (GeneralActivity.hasConnection(getApplicationContext())) {
                    if (mPostName.getText().toString().trim().equals("")) {
                        mPostName.setError("Add Character name");
                    } else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference collectionReference = db.collection("posts");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    mUserName = (String) document.get("name");
                                    DocumentReference docRef = collectionReference.document();
                                    Post newPost = new Post(docRef.getId(), System.currentTimeMillis(), mUserName, mAuth.getCurrentUser().getUid(), mPostName.getText().toString().trim(), mPostContent.getText().toString().trim());

                                    docRef.set(newPost);
                                    uploadImageToFirebase(mImgUri, docRef.getId());
                                    replaceWithGeneralActivity();
                                    ;
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
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
                Picasso.get().load(mImgUri).into(mPostImg);
            }
        }
    }

    private void uploadImageToFirebase(Uri imgUri, String id) {
        if (imgUri != null) {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("posts/" + id + "/image.jpg");
            profileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Picasso.get().load(uri).into(mPostImg);
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
