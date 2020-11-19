package com.adzteam.urbook.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationFragment extends Fragment {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    EditText mUserName, mEmail, mPassword, mConfirmPassword;
    TextInputLayout mEmailBox, mPasswordBox, mConfirmPasswordBox, mUserBox;
    Button mRegisterBtn;
    ImageView mGoogleRegisterBtn;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    boolean mIsVerified = false;
    RegistrationFragment THIS = this;

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

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        mUserName = view.findViewById(R.id.name);
        mEmail = view.findViewById(R.id.email);
        mPassword = view.findViewById(R.id.password);
        mConfirmPassword = view.findViewById(R.id.confirmPassword);
        mRegisterBtn = view.findViewById(R.id.registerBtn);
        mEmailBox = view.findViewById(R.id.email_text);
        mPasswordBox = view.findViewById(R.id.password_text);
        mConfirmPasswordBox = view.findViewById(R.id.confirm_password_text);
        mUserBox = view.findViewById(R.id.name_text);
        mGoogleRegisterBtn = view.findViewById(R.id.googleAuth);

        TextView goLogin = view.findViewById(R.id.goLogin);

        mAuth = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //валидация данных

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                String username = mUserName.getText().toString().trim();

                if (username.isEmpty()) {
                    mUserBox.setError("Enter username");
                    mIsVerified = false;
                } else {
                    mUserBox.setErrorEnabled(false);
                    mIsVerified = true;
                }
                if (!validateEmail(email)) {
                    mEmailBox.setError("Not a valid email address");
                    mIsVerified = false;
                } else {
                    mEmailBox.setErrorEnabled(false);
                    mIsVerified = true;
                }
                if (!validatePassword(password)) {
                    mPasswordBox.setError("Password's length must be > 5");
                    mIsVerified = false;
                } else {
                    mPasswordBox.setErrorEnabled(false);
                    mIsVerified = true;
                    if (!password.equals(confirmPassword)) {
                        mConfirmPasswordBox.setError("Passwords didn't match");
                        mIsVerified = false;
                    } else {
                        mConfirmPasswordBox.setErrorEnabled(false);
                        mIsVerified = true;
                    }
                }

                if (mIsVerified) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "new account created", Toast.LENGTH_SHORT).show();
                                    ((AuthActivity) getActivity()).replaceWithLoginFragment();
                                }
                            } else {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        mGoogleRegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoogleSignInClient = GoogleAuth.signInWithGoogle(getActivity(), RegistrationFragment.this);
            }
        });

        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((AuthActivity) getActivity()).replaceWithLoginFragment();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getActivity(), "firebaseAuthWithGoogle:" + account.getId(), Toast.LENGTH_SHORT).show();
                GoogleAuth.firebaseAuthWithGoogle(account.getIdToken(), mAuth, getActivity());
            } catch (ApiException e) {
                Toast.makeText(getActivity(), "Google sign in failed" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
