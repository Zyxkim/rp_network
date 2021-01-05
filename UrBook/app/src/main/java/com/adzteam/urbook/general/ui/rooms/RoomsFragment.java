package com.adzteam.urbook.general.ui.rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.adapters.RoomsAdapter;
import com.adzteam.urbook.general.GeneralActivity;
import java.util.ArrayList;

public class RoomsFragment extends Fragment {

    private RoomsViewModel mRoomsViewModel;

    private ActionMenuItemView mNewRoomBtn;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Room> mRoomsData = new ArrayList<>();
    private RoomsAdapter mAdapter;

    public RoomsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRoomsViewModel = new ViewModelProvider(this).get(RoomsViewModel.class);
        mAdapter = new RoomsAdapter(mRoomsData, mRoomsViewModel);
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        rv.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            mRoomsViewModel.downloadRooms();
        }

        mNewRoomBtn = view.findViewById(R.id.add_room);
        mNewRoomBtn.setOnClickListener(v -> ((GeneralActivity) getActivity()).replaceWithCreateRoomActivity());

        mSwipeRefreshLayout = view.findViewById(R.id.room_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mRoomsViewModel.refresh());

        mRoomsViewModel.getRefreshState().observe(getViewLifecycleOwner(), new RefreshProgressObserver());
        mRoomsViewModel.getRooms().observe(getViewLifecycleOwner(), new RoomsDataObserver());
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("isThereRoomArray", "true");
        super.onSaveInstanceState(outState);
    }

    private class RoomsDataObserver implements Observer<ArrayList<Room>> {

        @Override
        public void onChanged(ArrayList<Room> rooms) {
            mRoomsData.clear();
            mRoomsData.addAll(rooms);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class RefreshProgressObserver implements Observer<RoomsViewModel.RefreshState> {

        @Override
        public void onChanged(RoomsViewModel.RefreshState refreshState) {
            if (refreshState == RoomsViewModel.RefreshState.DONE) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}