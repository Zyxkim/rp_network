package com.adzteam.urbook.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationFragment extends Fragment {
    EditText mUserName, mEmail, mPassword, mConfirmPassword;
    Button mRegisterBtn;
    FirebaseAuth fAuth;

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
        TextView goLogin = view.findViewById(R.id.goLogin);

        fAuth = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //валидация данных

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
}
