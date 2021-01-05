package com.adzteam.urbook.authentification.registration;

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
import com.adzteam.urbook.authentification.AuthActivity;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;

    private RegistrationViewModel mRegistrationViewModel;

    EditText mUserName, mEmail, mPassword, mConfirmPassword;
    TextInputLayout mEmailBox, mPasswordBox, mConfirmPasswordBox, mUserBox;
    Button mRegisterBtn;
    TextView mGoLogin;
    ImageView mGoogleRegisterBtn;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRegistrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);

        mRegistrationViewModel.getRegistrationLiveData().observe(getViewLifecycleOwner(), new RegistrationDataObserver());
        mRegistrationViewModel.getRegistrationState().observe(getViewLifecycleOwner(), new RegisterProgressObserver());
        mRegistrationViewModel.getLoginState().observe(getViewLifecycleOwner(), new LoginProgressObserver());
        mRegistrationViewModel.getAddUserToDatabaseState().observe(getViewLifecycleOwner(), new AddToDatabaseObserver());
        mRegistrationViewModel.getGoogleSignInIntent().observe(getViewLifecycleOwner(), new IntentObserver());

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
        mGoLogin = view.findViewById(R.id.goLogin);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String username = mUserName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                mRegistrationViewModel.registerWithEmail(email, username, password, confirmPassword);
            }
        });

        mGoogleRegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mRegistrationViewModel.registerWithGoogle();
            }
        });

        mGoLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((AuthActivity) getActivity()).replaceWithLoginFragment();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            mRegistrationViewModel.catchGoogleResult(data);
        }
    }

    private class RegistrationDataObserver implements Observer<RegistrationViewModel.RegistrationData> {

        @Override
        public void onChanged(RegistrationViewModel.RegistrationData registrationData) {
            mEmailBox.setErrorEnabled(false);
            mUserBox.setErrorEnabled(false);
            mPasswordBox.setErrorEnabled(false);
            mConfirmPasswordBox.setErrorEnabled(false);

            if (!registrationData.isEmailValidate()) {
                mEmailBox.setError("Not a valid email address");
            }

            if(!registrationData.isNameValidate()) {
                mUserBox.setError("Enter username");
            }

            if (!registrationData.isPasswordValidate()) {
                mPasswordBox.setError("Password's length must be > 5");
            } else if (!registrationData.isConfirmPasswordValidate()) {
                mConfirmPasswordBox.setError("Passwords didn't match");
            }
        }
    }

    private class LoginProgressObserver implements Observer<RegistrationViewModel.LoginState> {

        @Override
        public void onChanged(RegistrationViewModel.LoginState loginState) {
            if (loginState == RegistrationViewModel.LoginState.SUCCESS) {
                ((AuthActivity) getActivity()).replaceWithGeneralActivity();
            } else if (loginState == RegistrationViewModel.LoginState.FAILED) {

            } if (loginState == RegistrationViewModel.LoginState.IN_PROGRESS) {

            }
        }
    }

    private class RegisterProgressObserver implements Observer<RegistrationViewModel.RegistrationState> {

        @Override
        public void onChanged(RegistrationViewModel.RegistrationState registrationState) {
            if (registrationState == RegistrationViewModel.RegistrationState.FAILED) {
                Toast.makeText(getActivity(), "FAILED: ", Toast.LENGTH_SHORT).show();
            } else if (registrationState == RegistrationViewModel.RegistrationState.IN_PROGRESS) {
                Toast.makeText(getActivity(), "IN_PROGRESS: ", Toast.LENGTH_SHORT).show();
            } else if (registrationState == RegistrationViewModel.RegistrationState.SUCCESS) {
                if (getActivity() != null) {
                    /*((AuthActivity) getActivity()).finish();
                    Intent intent = new Intent(((AuthActivity) getActivity()), AuthActivity.class);
                    startActivity(intent);*/
                    ((AuthActivity) getActivity()).replaceWithLoginFragment();
                }
                Toast.makeText(getActivity(), "register successfully", Toast.LENGTH_SHORT).show();
            } else if (registrationState == RegistrationViewModel.RegistrationState.SEND_EMAIL){
                Toast.makeText(getActivity(), "Verification email has been sent", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private class AddToDatabaseObserver implements Observer<RegistrationViewModel.AddUserToDatabaseState> {

        @Override
        public void onChanged(RegistrationViewModel.AddUserToDatabaseState addUserToDatabaseState) {
            if (addUserToDatabaseState == RegistrationViewModel.AddUserToDatabaseState.FAILED) {
                Toast.makeText(getActivity(), "can't add user in database", Toast.LENGTH_SHORT).show();
            } else if (addUserToDatabaseState == RegistrationViewModel.AddUserToDatabaseState.SUCCESS) {
                Toast.makeText(getActivity(), "add user in database", Toast.LENGTH_SHORT).show();
                ((AuthActivity) getActivity()).replaceWithLoginFragment();
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
