package com.catapi.c4.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.PatternsCompat;
import androidx.fragment.app.Fragment;

import com.catapi.c4.R;
import com.catapi.c4.databinding.FragmentSignupBinding;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

import kotlin.collections.ArraysKt;

public class SignUpFragment extends Fragment {

    private FragmentSignupBinding binding;
    private String username, pass, rpass, email;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference usersRefDB;

    public SignUpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        usersRefDB = firebaseDB.getReference("users");

        setDataEntered();

        binding.signInClickableText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_auth, new SignInFragment(), "signInFragment")
                        .commit();
            }
        });

        binding.signUpButtonSP.setOnClickListener(view -> {
            if (validateData()) {
                createAccount(email, pass);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setDataEntered() {
        email = Objects.requireNonNull(binding.InputEmailSP.getEditText()).getText().toString();
        username = Objects.requireNonNull(binding.InputUserNameSP.getEditText()).getText().toString();
        pass = Objects.requireNonNull(binding.InputPasswordSP.getEditText()).getText().toString();
        rpass = Objects.requireNonNull(binding.InputRPasswordSP.getEditText()).getText().toString();
    }

    /*----------------------------- Methods to validate data entered -----------------------------*/

    private boolean emailCorrect() {
        if (email.isEmpty()) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            binding.InputEmailSP.setError("Field cannot be empty");
            return false;
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.InputEmailSP.setError("Please enter a valid email address");
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.valid_email, Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            binding.InputEmailSP.setError(null);
            return true;
        }
    }

    private boolean userCorrect() {
        Pattern userRegex = Pattern.compile("^.{4,}$");

        if (username.isEmpty()) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            binding.InputUserNameSP.setError("Field cannot be empty");
            return false;
        } else if (!userRegex.matcher(username).matches()) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.user_length, Snackbar.LENGTH_SHORT).show();
            binding.InputUserNameSP.setError("Please enter a valid Username");
            return false;
        } else {
            binding.InputUserNameSP.setError(null);
            return true;
        }
    }

    private boolean passCorrect() {
        Pattern passRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_,.:!¡?¿<>])(?=\\S+$).{8,}$");

        if (pass.isEmpty()) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            binding.InputPasswordSP.setError("Field cannot be empty");
            return false;
        } else if (!passRegex.matcher(pass).matches()) {
            passRegex();
            binding.InputPasswordSP.setError("Password is too weak");
            return false;
        } else {
            binding.InputPasswordSP.setError(null);
            return true;
        }
    }

    private boolean rPassCorrect() {
        if (rpass.isEmpty()) {
            binding.InputRPasswordSP.setError("Field cannot be empty");
            return false;
        } else if (!rpass.equals(pass)) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.password_same, Snackbar.LENGTH_SHORT).show();
            Objects.requireNonNull(binding.InputRPasswordSP.getEditText()).setText("");
            binding.InputRPasswordSP.setError("The password is not the same");
            return false;
        } else {
            binding.InputRPasswordSP.setError(null);
            return true;
        }
    }

    private void passRegex() {
        if (!pass.matches(".*[a-z].*")) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.pass_regex_minus, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[A-Z].*")) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.pass_regex_mayus, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[0-9].*")) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.pass_regex_number, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[@#$%^&+=_,.:!¡?¿<>].*")) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.pass_regex_special_char, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[{8,}].*")) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.pass_regex_length, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (pass.matches(".*[(?=\\S+$)].*")) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.pass_regex_spaces, Snackbar.LENGTH_SHORT).show();
            return;
        }
    }

    private boolean validateData() {
        setDataEntered();
        Boolean[] result = new Boolean[]{this.emailCorrect(), this.passCorrect(), this.userCorrect(), this.rPassCorrect()};

        return !ArraysKt.contains(result, false);
    }

    /*--------------------------------------------------------------------------------------------*/

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        saveUserDataToDB(user.getUid(), username);
                        Utils.loggedUser = user;
                    }

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    Toast.makeText(getActivity(), R.string.register_successfully, Toast.LENGTH_SHORT).show();

                    Objects.requireNonNull(binding.InputUserNameSP.getEditText()).setText("");
                    Objects.requireNonNull(binding.InputPasswordSP.getEditText()).setText("");
                    Objects.requireNonNull(binding.InputRPasswordSP.getEditText()).setText("");
                    Objects.requireNonNull(binding.InputEmailSP.getEditText()).setText("");
                } else {
                    Utils.loggedUser = null;
                    Toast.makeText(getActivity(), R.string.register_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserDataToDB(String userId, String username) {
        usersRefDB.child(userId).child("username").setValue(username);
    }
}
