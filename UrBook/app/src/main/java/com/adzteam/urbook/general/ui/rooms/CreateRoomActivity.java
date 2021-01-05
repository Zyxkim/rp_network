package com.adzteam.urbook.general.ui.rooms;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.adzteam.urbook.general.GeneralActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.lifecycle.Observer;

public class CreateRoomActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;

    private final CreateRoomViewModel mViewModel = new CreateRoomViewModel(getApplication());

    private TextInputEditText mRoomDescriptionInput;
    private TextInputEditText mRoomNameInput;
    private ImageView mRoomImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_create_room);

        mRoomDescriptionInput = findViewById(R.id.new_room_description);
        mRoomNameInput = findViewById(R.id.new_room_name);
        ActionMenuItemView mSaveItem = findViewById(R.id.save);
        ActionMenuItemView mBackItem = findViewById(R.id.back);
        MaterialButton mAddRoomCover = findViewById(R.id.editRoomImageBtn);
        mRoomImg = findViewById(R.id.profile_image);

        mViewModel.getRoomStateLiveData().observe(this, new CreateRoomStateObserver());

        mSaveItem.setOnClickListener(v -> {
            if (GeneralActivity.hasConnection(getApplication().getApplicationContext())) {
                mViewModel.createRoom(mRoomNameInput.getText().toString().trim(), mRoomDescriptionInput.getText().toString().trim());
            } else {
                Toast.makeText(getApplicationContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
            }
        });

        mBackItem.setOnClickListener(v -> replaceWithGeneralActivity());

        mAddRoomCover.setOnClickListener(v -> {
            Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGallery, RC_SIGN_IN);

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
                mViewModel.getImage(data.getData());
                Picasso.get().load(data.getData()).into(mRoomImg);
            }
        }
    }

    private class CreateRoomStateObserver implements Observer<CreateRoomViewModel.CreateRoomState> {

        @Override
        public void onChanged(CreateRoomViewModel.CreateRoomState createRoomState) {
            if (createRoomState == CreateRoomViewModel.CreateRoomState.EMPTY_NAME) {
                mRoomNameInput.setError("Add Room name");
            } else if (createRoomState == CreateRoomViewModel.CreateRoomState.FAILED_TO_UPLOAD_IMAGE) {
                Toast.makeText(getApplicationContext(), "Failed to upload img", Toast.LENGTH_SHORT).show();
            } else if (createRoomState == CreateRoomViewModel.CreateRoomState.DONE) {
                replaceWithGeneralActivity();
            } else if (createRoomState == CreateRoomViewModel.CreateRoomState.FAILED_TO_UPLOAD_ROOM) {
                Toast.makeText(getApplicationContext(), "Failed to upload room", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
