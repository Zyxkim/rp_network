package com.adzteam.urbook.general.ui.rooms;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.adzteam.urbook.general.GeneralActivity;
import com.example.flatdialoglibrary.dialog.FlatDialog;

import java.util.ArrayList;

import com.adzteam.urbook.general.ui.RoomsDataSource;

public class RoomsFragment extends Fragment {

    private ActionMenuItemView mNewRoomBtn;
    private RoomsViewModel mRoomsViewModel;

    private static final int START_LIST = 100;
    private final String EXTRA = "EXTRA";

    private int mCounter = START_LIST;
    private final ArrayList<RoomsDataSource> LIST_DATA = new ArrayList<>();
    private final MyAdapter ADAPTER = new MyAdapter(LIST_DATA);

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

        if(savedInstanceState != null)
            mCounter = savedInstanceState.getInt(EXTRA);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.recyclerView);
        list.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        list.setAdapter(ADAPTER);

        mNewRoomBtn = view.findViewById(R.id.add_room);
        mNewRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
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
                            mCounter++;
                            LIST_DATA.add(new RoomsDataSource(flatDialog.getFirstTextField(), flatDialog.getSecondTextField()));
                            ADAPTER.notifyItemInserted(mCounter - 1);
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
        outState.putInt(EXTRA, mCounter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {
        private final ArrayList<RoomsDataSource> LIST_DATA_ADAPTER;

        public MyAdapter(ArrayList<RoomsDataSource> listDataAdapter) {
            this.LIST_DATA_ADAPTER = listDataAdapter;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rooms_item, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
            holder.TEXT_VIEW.setText(LIST_DATA_ADAPTER.get(position).NUMBER);
            holder.mRoomDescription.setText(LIST_DATA_ADAPTER.get(position).roomDescription);

            holder.TEXT_VIEW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() instanceof GeneralActivity) {
                        ((GeneralActivity) getActivity()).roomClickListener();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return LIST_DATA_ADAPTER.size();
        }
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        final TextView TEXT_VIEW;
        final TextView mRoomDescription;
        //final CircleImageView mRoomImage;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            TEXT_VIEW = itemView.findViewById(R.id.number);
            mRoomDescription = itemView.findViewById(R.id.room_description);
            //mRoomImage = itemView.findViewById(R.id.room_image);
        }
    }
}