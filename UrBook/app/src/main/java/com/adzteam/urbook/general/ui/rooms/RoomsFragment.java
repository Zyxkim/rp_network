package com.adzteam.urbook.general.ui.rooms;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.adzteam.urbook.adapters.Room;
import com.adzteam.urbook.adapters.RoomsAdapter;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class RoomsFragment extends Fragment {

    private ActionMenuItemView mNewRoomBtn;
    private RoomsViewModel mRoomsViewModel;

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
                showEditDialog();
            }
        });
        if(savedInstanceState == null) {
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

                            Room newRoom = new Room(name, description, creator, date);
                            mRoomsData.add(newRoom);
                            mAdapter.notifyItemInserted(mRoomsData.size() - 1);
                            Log.i("aaa", String.valueOf(mRoomsData.size()));
                        }
                    } else {
                        Log.i("aaa", "not suc");
                    }
                }
            });
        }
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
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference collectionReference = db.collection("rooms");
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            Room newRoom = new Room(flatDialog.getFirstTextField(), flatDialog.getSecondTextField(), mAuth.getCurrentUser().getUid(), (new Date()).toString());
                            collectionReference.add(newRoom);
                            mRoomsData.add(newRoom);
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
        outState.putString("isThereRoomArray", "true");
        super.onSaveInstanceState(outState);
    }
}