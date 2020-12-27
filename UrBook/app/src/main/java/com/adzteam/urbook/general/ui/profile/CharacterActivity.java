package com.adzteam.urbook.general.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import com.adzteam.urbook.adapters.UserCharactersAdapter;
import com.adzteam.urbook.general.GeneralActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.adzteam.urbook.R;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.adzteam.urbook.adapters.UserCharactersAdapter.CURRENT_CHARACTER_ID;

public class CharacterActivity extends AppCompatActivity {

    private final UserCharactersAdapter mCharactersAdapter = new UserCharactersAdapter(UserCharactersAdapter.mCharactersList);

    private MaterialToolbar mToolbar;

    private ActionMenuItemView mBtGoBack;
    private ActionMenuItemView mDeleteBtn;

    private StorageReference mStorageReference;
    
    private TextView mCharacterName;
    private TextView mCharacterSurname;
    private TextView mFandom;
    private TextView mCharacterDescription;
    public ImageView mCharacterImg;

    public CharacterActivity() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_character, container, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        mCharacterName = findViewById(R.id.character_name);
        mCharacterSurname = findViewById(R.id.character_surname);
        mFandom = findViewById(R.id.fandom);
        mCharacterDescription = findViewById(R.id.character_description);
        mCharacterImg = findViewById(R.id.character_image);

        mDeleteBtn = (ActionMenuItemView) findViewById(R.id.delete);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mToolbar = (MaterialToolbar)findViewById(R.id.character_bar);
        FirebaseFirestore.getInstance()
                .collection("characters")
                .document(CURRENT_CHARACTER_ID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String creator = (String) document.get("creator");

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("users").document(creator);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                            if (documentSnapshot != null) {
                                mToolbar.setTitle(documentSnapshot.getString("name"));
                            }
                        }
                    });

                    if (creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mDeleteBtn.setVisibility(View.VISIBLE);
                    } else {
                        mDeleteBtn.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        FirebaseFirestore.getInstance()
                .collection("characters")
                .document(CURRENT_CHARACTER_ID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String name = (String) document.get("characterName");
                    String surname = (String) document.get("characterSurname");
                    String fandom = (String) document.get("fandom");
                    String description = (String) document.get("content");

                    mStorageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference profileRef = mStorageReference.child("characters/" + CURRENT_CHARACTER_ID + "/image.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(mCharacterImg);
                        }
                    });

                    mCharacterName.setText(name);
                    mCharacterSurname.setText(surname);
                    mFandom.setText(fandom);
                    mCharacterDescription.setText(description);
                }
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralActivity.hasConnection(view.getContext())) {
                    db.collection("characters").document(CURRENT_CHARACTER_ID).delete();
                    UserCharactersAdapter.mCharactersList.remove(UserCharactersAdapter.POSITION);
                    mCharactersAdapter.notifyDataSetChanged();
                    replaceWithGeneralActivity();
                } else {
                    Toast.makeText(view.getContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtGoBack = (ActionMenuItemView) findViewById(R.id.back);
        mBtGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void replaceWithGeneralActivity() {
        Intent intent = new Intent(this, GeneralActivity.class);
        startActivity(intent);
    }
}
