package com.adzteam.urbook.authentification;

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
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out
                )
                .replace(R.id.activity_auth_container, new RegistrationFragment())
                .addToBackStack(null)
                .commit();
    }

    public void replaceWithLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out
                )
                .replace(R.id.activity_auth_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    public void replaceWithResetPasswordFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out
                )
                .replace(R.id.activity_auth_container, new ResetPasswordFragment())
                .addToBackStack(null)
                .commit();
    }
}