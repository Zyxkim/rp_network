package com.adzteam.urbook.general.ui.rooms;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.adzteam.urbook.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;

public class RoomsFragment extends Fragment {

    Button mNewRoomBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        mNewRoomBtn = view.findViewById(R.id.addRoom);

        mNewRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    private void showEditDialog() {
        final FlatDialog flatDialog = new FlatDialog(getActivity());
        flatDialog.setTitle("NewRoom")
                .setBackgroundColor(Color.parseColor("#442D68"))
                .setFirstButtonColor(Color.parseColor("#F97794"))
                .setSecondButtonColor(Color.WHITE)
                .setSecondButtonTextColor(Color.parseColor("#F97794"))
                .setSubtitle("Write your new room name here, please")
                .setFirstTextFieldHint("Room Name")
                .setFirstButtonText("CREATE")
                .setSecondButtonText("CANCEL")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "The Room " + flatDialog.getFirstTextField() + " was created", Toast.LENGTH_SHORT).show();
                        flatDialog.dismiss();
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
}