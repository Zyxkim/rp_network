package com.adzteam.urbook.general.ui.rooms;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RoomsFragment extends Fragment {

    private ActionMenuItemView mNewRoomBtn;
    private RoomsViewModel mRoomsViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final ArrayList<Room> mRoomsData = new ArrayList<>();
    private final RoomsAdapter mAdapter = new RoomsAdapter(mRoomsData);

    public RoomsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRoomsViewModel = new ViewModelProvider(this).get(RoomsViewModel.class);

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
        
        mNewRoomBtn = view.findViewById(R.id.add_room);
        mNewRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GeneralActivity) getActivity()).replaceWithCreateRoomActivity();
            }
        });

        if(savedInstanceState == null) {
            downloadRooms(null);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.room_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                mRoomsData.clear();
                mAdapter.notifyDataSetChanged();
                downloadRooms(new RefreshCallBack());
            }
        });
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("isThereRoomArray", "true");
        super.onSaveInstanceState(outState);
    }

    public void downloadRooms(final RefreshCallBack callBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("rooms");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("aaa", "task suc");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = (String) document.get("name");
                        String description = (String) document.get("description");
                        String creator = (String) document.get("creator");
                        String date = (String) document.get("date");
                        Boolean isThereImage = document.getBoolean("thereImage");
                        if (isThereImage == null) isThereImage =false;
                        Log.i("eee", String.valueOf(isThereImage));
                        String uri = (String) document.get("roomImg");
                        Room newRoom = new Room(document.getId(), name, description, creator, date, isThereImage, uri);
                        mRoomsData.add(newRoom);
                        Log.i("aaa", String.valueOf(mRoomsData.size()));
                    }
                    mAdapter.notifyDataSetChanged();
                    if (callBack != null) {
                        callBack.stopResreshing();
                    }
                    Log.i("aaa", "bbbb");
                } else {
                    Log.i("aaa", "not suc");
                }
            }
        });
    }

    public class RefreshCallBack {
        public void stopResreshing() {
            mSwipeRefreshLayout.setRefreshing(false);
        };
    }
}