package com.adzteam.urbook.general.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.adzteam.urbook.authentification.AuthActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "";
    private CircleImageView mProfileImage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFStore;
    private MaterialButton mEditProfileImageBtn;
    TextInputEditText mEditName, mEditStatus, mEditEmail, mEditPassword, mConfirmNewPassword;
    ActionMenuItemView mBackBtn, mSaveBtn;

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final Pattern mPattern = Pattern.compile(EMAIL_PATTERN);

    private static final int RC_SIGN_IN = 1000;

    public boolean isEmailValidate() {
        String sEditEmail = mEditEmail.getText().toString();
        return validateEmail(sEditEmail);
    }

    public boolean isPasswordValidate() {
        return validatePassword(mEditPassword);
    }

    public boolean isNameValidate() {
        String sEditName = mEditName.getText().toString();
        return !sEditName.isEmpty();
    }

    public boolean isStatusValidate() {
        String sEditStatus = mEditStatus.getText().toString();
        return !sEditStatus.isEmpty();
    }

    public boolean isConfirmPasswordValidate() {
        return mEditPassword.equals(mConfirmNewPassword);
    }

    public boolean validateEmail(String email) {
        Matcher matcher;
        matcher = mPattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(TextInputEditText password) {
        return password.length() > 5;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent userData = getIntent();
        String userName = userData.getStringExtra("name");
        String userStatus = userData.getStringExtra("status");
        mEditName = findViewById(R.id.new_name);
        mEditStatus = findViewById(R.id.new_status);
        mEditEmail = findViewById(R.id.new_email);
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
                if (isNameValidate() && isStatusValidate() && isEmailValidate()) {
                    String email = mEditEmail.getText().toString();
                    mAuth.getCurrentUser().updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DocumentReference docRef = mFStore.collection("users").document(mAuth.getCurrentUser().getUid());
                            Map<String, Object> edited = new HashMap<>();
                            edited.put("email", email);
                            edited.put("name", mEditName.getText().toString());
                            edited.put("status", mEditStatus.getText().toString());
                            docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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