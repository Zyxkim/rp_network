package com.adzteam.urbook.general.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.adzteam.urbook.authentification.AuthActivity;
import com.example.flatdialoglibrary.dialog.FlatDialog;

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

public class ProfileFragment extends Fragment {

    private ProfileViewModel mProfileViewModel;
    private ActionMenuItemView mLogOutBottom;
    private ActionMenuItemView mEditProfileBtn;

    private ImageButton mNewRoomBtn;
    
    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final PostsAdapter mAdapter = new PostsAdapter(mPostsData);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                startActivity(intent);
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
                showEditDialog();
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

        NodeList nameItems = dom.getElementsByTagName("name");
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
        }
         */
    }

    private void showEditDialog() {
        final FlatDialog flatDialog = new FlatDialog(getActivity());
        flatDialog.setTitle("NewRoom")
                .setBackgroundColor(Color.parseColor("#442D68"))
                .setFirstButtonColor(Color.parseColor("#F97794"))
                .setSecondButtonColor(Color.WHITE)
                .setSecondButtonTextColor(Color.parseColor("#F97794"))
                .setFirstTextFieldHint("Room Name")
                .setSecondTextFieldHint("Room Description")
                .setFirstButtonText("CREATE")
                .setSecondButtonText("CANCEL")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(flatDialog.getFirstTextField())) {
                            Toast.makeText(getActivity(), "Add Room name please", Toast.LENGTH_SHORT).show();
                        } else {
                            mPostsData.add(new Post(flatDialog.getFirstTextField(), flatDialog.getSecondTextField()));
                            Toast.makeText(getActivity(), "The Room " + flatDialog.getFirstTextField() + " was created", Toast.LENGTH_SHORT).show();
                            flatDialog.dismiss();
                        }
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flatDialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}