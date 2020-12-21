package com.adzteam.urbook.general.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.PostsAdapter;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.authentification.AuthActivity;

import com.adzteam.urbook.general.ui.rooms.RoomsFragment;
import com.adzteam.urbook.room.model.Message;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.squareup.picasso.Picasso;
import com.adzteam.urbook.general.GeneralActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.adzteam.urbook.adapters.RoomsAdapter.CURRENT_ROOM_ID;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mProfileViewModel;
    private ActionMenuItemView mLogOutBottom;
    private ActionMenuItemView mEditProfileBtn;
    private CircleImageView mProfileImage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;

    private ImageButton mNewRoomBtn;
    
    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final PostsAdapter mAdapter = new PostsAdapter(mPostsData);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLogOutBottom = view.findViewById(R.id.logout);
        mEditProfileBtn = view.findViewById(R.id.edit);

        mLogOutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileViewModel.signOut();
                Log.i("ggg", "rrr");
                ((GeneralActivity) getActivity()).replaceWithAuthActivity();
            }
        });

        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        rv.setAdapter(mAdapter);

        mNewRoomBtn = view.findViewById(R.id.add_feed);
        mNewRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GeneralActivity) getActivity()).replaceWithCreatePostActivity();
            }
        });

        /*
        FileInputStream fis = null;
        InputStreamReader isr = null;

        try {
            fis = getActivity().openFileInput("Profile.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        isr = new InputStreamReader(fis);

        char[] inputBuffer = new char[0];
        try {
            inputBuffer = new char[fis.available()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            isr.read(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String data = new String(inputBuffer);

        try {
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is = null;
        try {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DocumentBuilderFactory dbf;
        DocumentBuilder db = null;
        Document dom = null;

        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            dom = db.parse(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        dom.getDocumentElement().normalize();

        NodeList nameItems = dom.getElementsByTagName("characterName");
        NodeList emailItems = dom.getElementsByTagName("email");
        NodeList passwordItems = dom.getElementsByTagName("password");

        for (int i = 0; i < nameItems.getLength(); i++) {
            Node item = nameItems.item(i);
            ((TextView)view.findViewById(R.id.profile_name)).setText(item.getTextContent());
        }

        for (int i = 0; i < emailItems.getLength(); i++) {
            Node item = emailItems.item(i);
            ((TextView)view.findViewById(R.id.email_profile)).setText(item.getTextContent());
        }

        for (int i = 0; i < passwordItems.getLength(); i++) {
            Node item = passwordItems.item(i);
            String password = "";
            for (int j = 0; j < item.getTextContent().length(); j++) password += "*";
            ((TextView)view.findViewById(R.id.password_profile)).setText(password);
        }*/
        mProfileImage = view.findViewById(R.id.profile_image);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        StorageReference profileRef = mStorageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        /*profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfileImage);
            }
        });*/

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        mPostsData.clear();
                        for (QueryDocumentSnapshot document : value) {
                            String creator = (String) document.get("creator");

                            if (creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                String date = (String) document.get("date");
                                String characterName = (String) document.get("characterName");
                                String content = (String) document.get("content");

                                Post newPost = new Post(Long.parseLong(date), creator, characterName, content);
                                mPostsData.add(newPost);
                            }
                            mAdapter.notifyDataSetChanged();
                            rv.smoothScrollToPosition(rv.getAdapter().getItemCount() - 1);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}