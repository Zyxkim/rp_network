package com.adzteam.urbook.general.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.general.GeneralActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "";
    private CircleImageView mProfileImage, mEditProfileImageBtn;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFStore;
    TextInputEditText mEditName, mEditStatus;
    ActionMenuItemView mBackBtn, mSaveBtn;

    private static final int RC_SIGN_IN = 1000;

    public boolean isNameValidate() {
        String sEditName = mEditName.getText().toString();
        return !sEditName.isEmpty();
    }

    public boolean isStatusValidate() {
        String sEditStatus = mEditStatus.getText().toString();
        return !sEditStatus.isEmpty();
    }

    public void replaceWithGeneralActivity() {
        Intent intent = new Intent(getApplicationContext(), GeneralActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_edit_profile);

        Intent userData = getIntent();
        String userName = userData.getStringExtra("name");
        String userStatus = userData.getStringExtra("status");
        mEditName = findViewById(R.id.new_name);
        mEditStatus = findViewById(R.id.new_status);
        mEditName.setText(userName);
        mEditStatus.setText(userStatus);

        mProfileImage = findViewById(R.id.profile_image);
        mEditProfileImageBtn = findViewById(R.id.editProfileImageBtn);
        mBackBtn = findViewById(R.id.back);
        mSaveBtn = findViewById(R.id.save);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFStore = FirebaseFirestore.getInstance();

        StorageReference profileRef = mStorageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfileImage);
            }
        });

        mEditProfileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, RC_SIGN_IN);
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeneralActivity.hasConnection(getApplicationContext())) {
                    if (isNameValidate()) {
                        DocumentReference docRef = mFStore.collection("users").document(mAuth.getCurrentUser().getUid());
                        Map<String, Object> edited = new HashMap<>();
                        String name = mEditName.getText().toString();
                        edited.put("name", mEditName.getText().toString());
                        edited.put("status", mEditStatus.getText().toString());

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference collectionReference = db.collection("posts");

                        db.collection("posts")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.get("creator").toString().equals(mAuth.getCurrentUser().getUid())) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                    document.getReference().update("name", name);
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "profile edited successfully", Toast.LENGTH_SHORT).show();
                                replaceWithGeneralActivity();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imgUri = data.getData();
                uploadImageToFirebase(imgUri);
            }
        }
    }
    private void uploadImageToFirebase(Uri imgUri) {
        StorageReference profileRef = mStorageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mProfileImage);
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