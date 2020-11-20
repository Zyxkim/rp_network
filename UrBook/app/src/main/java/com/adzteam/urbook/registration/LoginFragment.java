package com.adzteam.urbook.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;

    EditText mEmail, mPassword;
    TextInputLayout mEmailBox, mPasswordBox;
    Button mLoginBtn;
    FirebaseAuth mAuth;
    ImageView mGoogleSignIn;
    GoogleSignInClient mGoogleSignInClient;

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final Pattern mPattern = Pattern.compile(EMAIL_PATTERN);

    public boolean validateEmail(String email) {
        Matcher matcher;
        matcher = mPattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mEmail = view.findViewById(R.id.email);
        mPassword = view.findViewById(R.id.password);
        mLoginBtn = view.findViewById(R.id.loginBtn);
        TextView goRegister = view.findViewById(R.id.goRegister);
        mEmailBox = view.findViewById(R.id.email_text);
        mPasswordBox = view.findViewById(R.id.password_text);
        mGoogleSignIn = view.findViewById(R.id.googleSignIn);

        mAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Валидация

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (!validateEmail(email)) {
                    mEmailBox.setError("Not a valid email address");
                } else {
                    mEmailBox.setErrorEnabled(false);
                }
                if (!validatePassword(password)) {
                    mPasswordBox.setError("Not a valid password");
                } else {
                    mPasswordBox.setErrorEnabled(false);
                }

                if (validateEmail(email) && validatePassword(password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "logged in successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoogleSignInClient = GoogleAuth.signInWithGoogle(getActivity(), LoginFragment.this);
            }
        });

        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((AuthActivity) getActivity()).replaceWithRegistrationFragment();
                }
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleAuth.catchResult(requestCode, resultCode, data, getActivity(), mAuth);
        }
    }
}