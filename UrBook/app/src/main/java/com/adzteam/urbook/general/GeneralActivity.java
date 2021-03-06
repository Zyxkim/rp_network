package com.adzteam.urbook.general;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.adzteam.urbook.R;
import com.adzteam.urbook.general.ui.profile.CreateCharacterActivity;
import com.adzteam.urbook.general.ui.profile.CreatePostActivity;
import com.adzteam.urbook.general.ui.rooms.CreateRoomActivity;
import com.adzteam.urbook.authentification.AuthActivity;
import com.adzteam.urbook.room.RoomActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class GeneralActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        Intent intent = getIntent();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_friends, R.id.navigation_rooms, R.id.navigation_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void roomClickListener() {
        Intent intent = new Intent(this, RoomActivity.class);
        startActivity(intent);
    }

    public void replaceWithCreateRoomActivity() {
        Intent intent = new Intent(this, CreateRoomActivity.class);
        startActivity(intent);
    }

    public void replaceWithCreatePostActivity() {
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent);
    }

    public void replaceWithCreateCharacterActivity() {
        Intent intent = new Intent(this, CreateCharacterActivity.class);
        startActivity(intent);
    }

    public void replaceWithAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}