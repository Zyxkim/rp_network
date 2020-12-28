package com.adzteam.urbook.general.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.adzteam.urbook.general.GeneralActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "";

    private EditProfileViewModel mViewModel;
    private CircleImageView mProfileImage, mEditProfileImageBtn;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private Uri mUri;
    TextInputEditText mEditName, mEditStatus;
    ActionMenuItemView mBackBtn, mSaveBtn;

    private static final int RC_SIGN_IN = 1000;

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
        mViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
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

        mViewModel.getEditStateMediatorLiveData().observe(this, new EditStateObserver());

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
                    mViewModel.editProfile(mEditName.getText().toString().trim(), mEditStatus.getText().toString().trim());
                    mViewModel.uploadImageToFirebase(mUri);
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
                mUri = data.getData();
                mProfileImage.setImageURI(mUri);
            }
        }
    }

    private class EditStateObserver implements Observer<EditProfileViewModel.EditState> {

        @Override
        public void onChanged(EditProfileViewModel.EditState editState) {
            if (editState == EditProfileViewModel.EditState.DONE) {
                Toast.makeText(getApplicationContext(), "profile edited successfully", Toast.LENGTH_SHORT).show();
                replaceWithGeneralActivity();
            } else if (editState == EditProfileViewModel.EditState.FAILED) {
                Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
            } else if (editState == EditProfileViewModel.EditState.EMPTY_NAME) {
                
            }
        }
    }

}