package com.adzteam.urbook.general.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.general.GeneralActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.appcompat.view.menu.ActionMenuItemView;

public class CreateCharacterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1002;

    TextInputEditText mFandom;
    TextInputEditText mCharactersName;
    TextInputEditText mCharacterSurname;
    TextInputEditText mPostContent;
    ActionMenuItemView mSaveItem;
    ActionMenuItemView mBackItem;
    MaterialButton mPostImage;
    private MaterialButton mAddPostCover;
    private ImageView mPostImg;
    private Uri mImgUri;

    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_create_character);

        mFandom = findViewById(R.id.fandom);
        mCharactersName = findViewById(R.id.new_character_name_content);
        mCharacterSurname = findViewById(R.id.new_character_surname_content);
        mPostContent = findViewById(R.id.new_post_content);
        mPostImage = findViewById(R.id.editPostImageBtn);
        mSaveItem = findViewById(R.id.save);
        mBackItem = findViewById(R.id.back);
        mAddPostCover = findViewById(R.id.editPostImageBtn);
        mPostImg = findViewById(R.id.post_image);

        mSaveItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (GeneralActivity.hasConnection(getApplicationContext())) {
                    if (mCharactersName.getText().toString().trim().equals("")) {
                        mCharactersName.setError("Add Character name");
                    } else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference collectionReference = db.collection("characters");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    mUserName = (String) document.get("name");
                                    DocumentReference docRef = collectionReference.document();
                                    Characters newCharacter = new Characters(docRef.getId(), System.currentTimeMillis(), mUserName, mAuth.getCurrentUser().getUid(), mFandom.getText().toString().trim(), mCharactersName.getText().toString().trim(), mCharacterSurname.getText().toString().trim(), mPostContent.getText().toString().trim(), (mImgUri != null));

                                    docRef.set(newCharacter);
                                    uploadImageToFirebase(mImgUri, docRef.getId());
                                    replaceWithGeneralActivity();
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

        mAddPostCover.setOnClickListener(new View.OnClickListener() {
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
                Picasso.get().load(mImgUri).into(mPostImg);
            }
        }
    }

    private void uploadImageToFirebase(Uri imgUri, String id) {
        if (imgUri != null) {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = mStorageReference.child("characters/" + id + "/image.jpg");
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
