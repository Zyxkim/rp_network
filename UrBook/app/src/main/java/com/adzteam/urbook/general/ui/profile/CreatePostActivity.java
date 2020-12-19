package com.adzteam.urbook.general.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.general.GeneralActivity;
import com.adzteam.urbook.room.RoomActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.view.menu.ActionMenuItemView;

import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {

    TextInputEditText mPostName;
    TextInputEditText mPostContent;
    ActionMenuItemView mSaveItem;
    ActionMenuItemView mBackItem;
    MaterialButton mPostImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_post);

        mPostName = findViewById(R.id.new_character_name_content);
        mPostContent = findViewById(R.id.new_post_content);
        mPostImage = findViewById(R.id.editPostImageBtn);
        mSaveItem = findViewById(R.id.save);
        mBackItem = findViewById(R.id.back);

        mSaveItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPostName.getText().toString().trim().equals("")) {
                    mPostName.setError("Add Character name");
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference collectionReference = db.collection("posts");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    Post newPost = new Post((new Date()).toString(), mAuth.getCurrentUser().getUid(), mPostName.getText().toString().trim(), mPostContent.getText().toString().trim());
                    collectionReference.add(newPost);
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
