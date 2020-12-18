package com.adzteam.urbook.general.ui.friends;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;

import java.util.ArrayList;

import com.adzteam.urbook.adapters.Friend;
import com.adzteam.urbook.adapters.FriendsAdapter;
import com.adzteam.urbook.general.ui.profile.ProfileViewModel;
import com.example.flatdialoglibrary.dialog.FlatDialog;

public class FriendsFragment extends Fragment {

    private ProfileViewModel mFriendsViewModel;

    private ActionMenuItemView mNewFriendBtn;

    private final ArrayList<Friend> mFriendsData = new ArrayList<>();
    private final FriendsAdapter mAdapter = new FriendsAdapter(mFriendsData);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFriendsViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        rv.setAdapter(mAdapter);

        mNewFriendBtn = view.findViewById(R.id.add_friend);
        mNewFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {
        final FlatDialog flatDialog = new FlatDialog(getActivity());
        flatDialog.setTitle("New Friend")
                .setBackgroundColor(Color.parseColor("#442D68"))
                .setFirstButtonColor(Color.parseColor("#F97794"))
                .setSecondButtonColor(Color.WHITE)
                .setSecondButtonTextColor(Color.parseColor("#F97794"))
                .setFirstTextFieldHint("Friend Name")
                .setSecondTextFieldHint("Friend Status")
                .setFirstButtonText("CREATE")
                .setSecondButtonText("CANCEL")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(flatDialog.getFirstTextField())) {
                            Toast.makeText(getActivity(), "Add friend name please", Toast.LENGTH_SHORT).show();
                        } else {
                            mFriendsData.add(new Friend(flatDialog.getFirstTextField(), flatDialog.getSecondTextField()));
                            Toast.makeText(getActivity(), "Wow! You have friends!", Toast.LENGTH_SHORT).show();
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