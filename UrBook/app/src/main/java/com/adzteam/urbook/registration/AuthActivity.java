package com.adzteam.urbook.registration;

import com.adzteam.urbook.R;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_auth_container, new LoginFragment())
                    .commit();
        }
    }

    public void replaceWithRegistrationFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_auth_container, new RegistrationFragment())
                .addToBackStack(null)
                .commit();
    }

    public void replaceWithLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_auth_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }
}