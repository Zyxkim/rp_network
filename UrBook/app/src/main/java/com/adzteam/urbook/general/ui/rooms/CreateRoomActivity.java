package com.adzteam.urbook.general.ui.rooms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.general.GeneralActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.view.menu.ActionMenuItemView;

import java.util.Date;

public class CreateRoomActivity extends AppCompatActivity {

    TextInputEditText mRoomDescriptionInput;
    TextInputEditText mRoomNameInput;
    ActionMenuItemView mSaveItem;
    ActionMenuItemView mBackItem;
    MaterialButton mAddRoomCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        mRoomDescriptionInput = findViewById(R.id.new_room_description);
        mRoomNameInput = findViewById(R.id.new_room_name);
        mSaveItem = findViewById(R.id.save);
        mBackItem = findViewById(R.id.back);
        mAddRoomCover = findViewById(R.id.editRoomImageBtn);

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
                    Room newRoom = new Room(mRoomNameInput.getText().toString().trim(), mRoomDescriptionInput.getText().toString().trim(), mAuth.getCurrentUser().getUid(), (new Date()).toString());
                    collectionReference.add(newRoom);
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

    }

    public void replaceWithGeneralActivity() {
        Intent intent = new Intent(this, GeneralActivity.class);
        startActivity(intent);
    }

}
