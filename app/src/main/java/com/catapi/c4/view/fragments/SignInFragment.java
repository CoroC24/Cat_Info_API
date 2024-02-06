package com.catapi.c4.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.PatternsCompat;
import androidx.fragment.app.Fragment;

import com.catapi.c4.R;
import com.catapi.c4.databinding.FragmentSigninBinding;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import kotlin.collections.ArraysKt;

public class SignInFragment extends Fragment {

    private FragmentSigninBinding binding;
    private FirebaseAuth mAuth;
    private String pass, email;

    public SignInFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();

        setDataEntered();

        binding.signInButton.setOnClickListener(view -> {
            if (validateData()) {
                signInAccount(email, pass);
            }
        });

        /*binding.signUpClickableText.setOnClickListener(view -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_auth, new SignUpFragment(), "signUpFragment")
                .commit());*/

        return binding.getRoot();
    }

    private void setDataEntered() {
        email = Objects.requireNonNull(binding.InputEmailSignIn.getEditText()).getText().toString();
        pass = Objects.requireNonNull(binding.InputPasswordSignIn.getEditText()).getText().toString();
    }

    private boolean emailCorrect() {
        if(email.isEmpty()) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            binding.InputEmailSignIn.setError("Field cannot be empty");
            return false;

        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.InputEmailSignIn.setError("Please enter a valid email address");
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.valid_email, Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            binding.InputEmailSignIn.setError(null);
            return true;
        }
    }

    private boolean passCorrect() {
        if(pass.isEmpty()) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            binding.InputPasswordSignIn.setError("Field cannot be empty");
            return false;

        } else {
            binding.InputPasswordSignIn.setError(null);
            return true;
        }
    }

    private boolean validateData() {
        setDataEntered();
        Boolean[] result = new Boolean[] {this.emailCorrect(), this.passCorrect()};

        return !ArraysKt.contains(result, false);
    }

    private void signInAccount(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Utils.loggedUser = user;
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    Toast.makeText(getActivity(), R.string.login_successfully, Toast.LENGTH_SHORT).show();

                    Objects.requireNonNull(binding.InputEmailSignIn.getEditText()).setText("");
                    Objects.requireNonNull(binding.InputPasswordSignIn.getEditText()).setText("");
                } else {
                    Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
