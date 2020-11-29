package com.adzteam.urbook.general.ui.rooms;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adzteam.urbook.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;

import java.util.ArrayList;

import com.adzteam.urbook.general.ui.RoomsDataSource;

public class RoomsFragment extends Fragment {

    Button mNewRoomBtn;

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
        list.setAdapter(ADAPTER);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            list.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        } else {
            list.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
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
                        mCounter++;
                        LIST_DATA.add(new RoomsDataSource(flatDialog.getFirstTextField(), flatDialog.getSecondTextField()));
                        ADAPTER.notifyItemInserted(mCounter - 1);
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