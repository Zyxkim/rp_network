package com.adzteam.urbook.authentification;

import com.adzteam.urbook.R;
import com.adzteam.urbook.authentification.login.LoginFragment;
import com.adzteam.urbook.authentification.registration.RegistrationFragment;
import com.adzteam.urbook.authentification.reset_password.ResetPasswordFragment;
import com.adzteam.urbook.general.GeneralActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class AuthActivity extends AppCompatActivity {

    private AuthActivityViewModel mAuthActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuthActivityViewModel = new ViewModelProvider(this).get(AuthActivityViewModel.class);

        if (savedInstanceState == null) {
            if (mAuthActivityViewModel.isLoggedIn()) {
                replaceWithGeneralActivity();
            } else {
                replaceWithLoginFragment();
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

    public void replaceWithGeneralActivity() {
        Intent intent = new Intent(this, GeneralActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}