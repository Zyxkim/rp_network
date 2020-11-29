package com.adzteam.urbook.authentification;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adzteam.urbook.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ResetPasswordViewModel mResetPasswordViewModel;

    TextInputEditText mEmail;
    MaterialButton mLogInBtn;
    FirebaseAuth mAuth;
    TextView mGoLogin;
    TextInputLayout mEmailBox;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    public static ResetPasswordFragment newInstance(String param1, String param2) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mResetPasswordViewModel = new ViewModelProvider(getActivity()).get(ResetPasswordViewModel.class);

        mEmail = view.findViewById(R.id.email);
        mLogInBtn = view.findViewById(R.id.loginBtn);
        mAuth = FirebaseAuth.getInstance();
        mGoLogin = view.findViewById(R.id.goRegister);
        mEmailBox = view.findViewById(R.id.email_text);

        mResetPasswordViewModel.getResetPasswordState()
                .observe(getViewLifecycleOwner(), new ProgressObserver());

        mLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mEmail.getText().toString().trim();
                mResetPasswordViewModel.resetPassword(mail);
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

    private class ProgressObserver implements Observer<ResetPasswordViewModel.ResetPasswordState> {

        @Override
        public void onChanged(ResetPasswordViewModel.ResetPasswordState resetPasswordState) {
            mEmailBox.setErrorEnabled(false);
            if (resetPasswordState == ResetPasswordViewModel.ResetPasswordState.FAILED) {
                Toast.makeText(getActivity(), "FAILED", Toast.LENGTH_SHORT).show();
            } else if (resetPasswordState == ResetPasswordViewModel.ResetPasswordState.NOT_VALID_EMAIL) {
                mEmailBox.setError("Not a valid email address");
            } else if (resetPasswordState == ResetPasswordViewModel.ResetPasswordState.ERROR) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            } else if (resetPasswordState == ResetPasswordViewModel.ResetPasswordState.IN_PROGRESS) {
                Toast.makeText(getActivity(), "IN_PROGRESS", Toast.LENGTH_SHORT).show();
            } else if (resetPasswordState == ResetPasswordViewModel.ResetPasswordState.SUCCESS) {
                Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    ((AuthActivity) getActivity()).replaceWithLoginFragment();
                }
            } else {
            }
        }
    }
}
