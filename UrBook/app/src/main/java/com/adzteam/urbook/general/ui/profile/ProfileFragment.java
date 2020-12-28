package com.adzteam.urbook.general.ui.profile;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Characters;
import com.adzteam.urbook.adapters.Post;
import com.adzteam.urbook.adapters.UserCharactersAdapter;
import com.adzteam.urbook.adapters.UserPostsAdapter;

import com.adzteam.urbook.general.ui.subscribers.SubsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.adzteam.urbook.general.GeneralActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mProfileViewModel;
    private ActionMenuItemView mLogOutBottom;
    private ActionMenuItemView mEditProfileBtn;
    private CircleImageView mProfileImage;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;

    private ImageButton mNewPostBtn;
    private ImageButton mNewCharacterBtn;

    private TextView mName;
    private TextView mStatus;
    private TextView mSubs;

    private final ArrayList<Post> mPostsData = new ArrayList<>();
    private final UserPostsAdapter mPostsAdapter = new UserPostsAdapter(mPostsData);

    private final ArrayList<Characters> mCharactersData = new ArrayList<>();
    private final UserCharactersAdapter mCharacterAdapter = new UserCharactersAdapter(mCharactersData);

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
        mName = view.findViewById(R.id.profile_name);
        mStatus = view.findViewById(R.id.profile_status);
        mSubs = view.findViewById(R.id.profile_subs);

        mLogOutBottom.setOnClickListener(v -> {
            mProfileViewModel.signOut();
            ((GeneralActivity) getActivity()).replaceWithAuthActivity();
        });

        mProfileViewModel.getNameLiveData().observe(getViewLifecycleOwner(), new NameObserver());
        mProfileViewModel.getStatusLiveData().observe(getViewLifecycleOwner(), new StatusObserver());
        mProfileViewModel.getSubsLiveData().observe(getViewLifecycleOwner(), new SubsObserver());
        mProfileViewModel.uploadProfileData();

        mEditProfileBtn.setOnClickListener(v -> {
            if (GeneralActivity.hasConnection(getContext())) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("name", mName.getText().toString());
                intent.putExtra("status", mStatus.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
            }
        });

        mSubs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SubsActivity.class);
            startActivity(intent);
        });

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        rv.setAdapter(mPostsAdapter);

        RecyclerView rvc = view.findViewById(R.id.characters_view);
        rvc.setHasFixedSize(true);
        rvc.setLayoutManager(new GridLayoutManager(view.getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        rvc.setAdapter(mCharacterAdapter);

        mNewPostBtn = view.findViewById(R.id.add_feed);
        mNewPostBtn.setOnClickListener(v -> ((GeneralActivity) getActivity()).replaceWithCreatePostActivity());

        mNewCharacterBtn = view.findViewById(R.id.add_character);
        mNewCharacterBtn.setOnClickListener(v -> ((GeneralActivity) getActivity()).replaceWithCreateCharacterActivity());

        mProfileImage = view.findViewById(R.id.profile_image);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        StorageReference profileRef = mStorageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(mProfileImage));

        mProfileViewModel.getPostsData().observe(getViewLifecycleOwner(), new PostsDataObserver());
        mProfileViewModel.getCharactersData().observe(getViewLifecycleOwner(), new CharactersDataObserver());
        mProfileViewModel.download();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class PostsDataObserver implements Observer<ArrayList<Post>> {

        @Override
        public void onChanged(ArrayList<Post> posts) {
            mPostsData.clear();
            mPostsData.addAll(posts);
            mPostsAdapter.notifyDataSetChanged();
        }
    }

    private class CharactersDataObserver implements Observer<ArrayList<Characters>> {

        @Override
        public void onChanged(ArrayList<Characters> characters) {
            mCharactersData.clear();
            mCharactersData.addAll(characters);
            mCharacterAdapter.notifyDataSetChanged();
        }
    }

    private class NameObserver implements Observer<String> {

        @Override
        public void onChanged(String name) {
            mName.setText(name);
        }
    }

    private class StatusObserver implements Observer<String> {

        @Override
        public void onChanged(String status) {
            mStatus.setText(status);
        }
    }

    private class SubsObserver implements Observer<String> {

        @Override
        public void onChanged(String subs) {
            mSubs.setText(subs);
        }
    }

}
