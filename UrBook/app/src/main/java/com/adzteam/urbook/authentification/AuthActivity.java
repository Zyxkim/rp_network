package com.adzteam.urbook.authentification;

import com.adzteam.urbook.R;
import com.adzteam.urbook.general.GeneralActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import java.io.FileOutputStream;

public class AuthActivity extends AppCompatActivity {

    private AuthActivityViewModel mAuthActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuthActivityViewModel = new ViewModelProvider(this).get(AuthActivityViewModel.class);

        if (savedInstanceState == null) {
            if (mAuthActivityViewModel.isLoggedIn()) {
                Intent intent = new Intent(this, GeneralActivity.class);
                startActivity(intent);
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_auth_container, new LoginFragment())
                        .commit();
            }
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