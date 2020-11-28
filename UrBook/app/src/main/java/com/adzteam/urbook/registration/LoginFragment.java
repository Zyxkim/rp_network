package com.adzteam.urbook.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;

    private LoginViewModel mLoginViewModel;

    private EditText mEmail, mPassword;
    TextView mResetPassword;
    private TextInputLayout mEmailBox, mPasswordBox;
    private Button mLoginBtn;
    private ImageView mGoogleSignIn;
    private TextView mGoRegister;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);

        mLoginViewModel.getLoginState()
                .observe(getViewLifecycleOwner(), new ProgressObserver());

        mLoginViewModel.getGoogleSignInIntent().observe(getViewLifecycleOwner(), new IntentObserver());

        mEmail = view.findViewById(R.id.email);
        mPassword = view.findViewById(R.id.password);
        mResetPassword = view.findViewById(R.id.goResetPassword);
        mLoginBtn = view.findViewById(R.id.loginBtn);
        mGoRegister = view.findViewById(R.id.goRegister);
        mEmailBox = view.findViewById(R.id.email_text);
        mPasswordBox = view.findViewById(R.id.password_text);
        mGoogleSignIn = view.findViewById(R.id.googleSignIn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginViewModel.loginWithEmail(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
            }
        });

        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLoginViewModel.loginWithGoogle();
            }
        });

        mGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((AuthActivity) getActivity()).replaceWithRegistrationFragment();
                }
            }
        });

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((AuthActivity) getActivity()).replaceWithResetPasswordFragment();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            mLoginViewModel.catchGoogleResult(data);
        }
    }

    private class ProgressObserver implements Observer<LoginViewModel.LoginState> {

        @Override
        public void onChanged(LoginViewModel.LoginState loginState) {
            mEmailBox.setErrorEnabled(false);
            mPasswordBox.setErrorEnabled(false);
            if (loginState == LoginViewModel.LoginState.FAILED) {
                Toast.makeText(getActivity(), "FAILED: ", Toast.LENGTH_SHORT).show();
            } else if (loginState == LoginViewModel.LoginState.NOT_VALID_EMAIL) {
                mEmailBox.setError("Not a valid email address");
            } else if (loginState == LoginViewModel.LoginState.NOT_VALID_PASSWORD) {
                mPasswordBox.setError("Not a valid password");
            } else if (loginState == LoginViewModel.LoginState.NOT_VALID_EMAIL_AND_PASSWORD) {
                mEmailBox.setError("Not a valid email address");
                mPasswordBox.setError("Not a valid password");
            } else if (loginState == LoginViewModel.LoginState.ERROR) {
                Toast.makeText(getActivity(), "ERROR: ", Toast.LENGTH_SHORT).show();
            } else if (loginState == LoginViewModel.LoginState.IN_PROGRESS) {
                Toast.makeText(getActivity(), "IN_PROGRESS: ", Toast.LENGTH_SHORT).show();
            } else if (loginState == LoginViewModel.LoginState.SUCCESS) {
                Toast.makeText(getActivity(), "logged in successfully", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }

    private class IntentObserver implements Observer<Intent> {

        @Override
        public void onChanged(Intent googleSignInIntent) {
            if (googleSignInIntent != null) startActivityForResult(googleSignInIntent, RC_SIGN_IN);
        }
    }
}