package com.adzteam.urbook.general.ui.rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.adzteam.urbook.R;

public class RoomsFragment extends Fragment {

    private RoomsViewModel roomsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        roomsViewModel = new ViewModelProvider(this).get(RoomsViewModel.class);
        //View root = inflater.inflate(R.layout.fragment_rooms, container, false);
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }
}