package com.adzteam.urbook.general.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.adzteam.urbook.R;
import com.adzteam.urbook.authentification.AuthActivity;
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

public class ProfileFragment extends Fragment {

    private ProfileViewModel mProfileViewModel;
    private ActionMenuItemView mLogOutBottom;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        //View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLogOutBottom = view.findViewById(R.id.logout);

        mLogOutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileViewModel.signOut();
                ((GeneralActivity) getActivity()).replaceWithAuthActivity();
            }
        });

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
    }
}