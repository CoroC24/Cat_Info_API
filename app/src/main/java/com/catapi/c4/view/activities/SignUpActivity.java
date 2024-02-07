package com.catapi.c4.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import com.catapi.c4.R;
import com.catapi.c4.databinding.ActivitySignUpBinding;
import com.catapi.c4.model.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

import kotlin.collections.ArraysKt;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private String username, pass, rPass, email;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRefDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        usersRefDB = firebaseDB.getReference("users");

        getDataEntered();

        binding.signInClickableText.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        binding.signUpButtonSP.setOnClickListener(view -> {
            if (validateData()) {
                createAccount(email, pass);
            }
        });
    }

    private void getDataEntered() {
        email = Objects.requireNonNull(binding.InputEmailSP.getEditText()).getText().toString();
        username = Objects.requireNonNull(binding.InputUserNameSP.getEditText()).getText().toString();
        pass = Objects.requireNonNull(binding.InputPasswordSP.getEditText()).getText().toString();
        rPass = Objects.requireNonNull(binding.InputRPasswordSP.getEditText()).getText().toString();
    }

    /*----------------------------- Methods to validate data entered -----------------------------*/

    private boolean emailCorrect() {
        if (email.isEmpty()) {
            binding.InputEmailSP.setError("Field cannot be empty");
            Snackbar.make(findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.InputEmailSP.setError("Please enter a valid email address");
            Snackbar.make(findViewById(android.R.id.content), R.string.valid_email, Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            binding.InputEmailSP.setError(null);
            return true;
        }
    }

    private boolean userCorrect() {
        Pattern userRegex = Pattern.compile("^.{4,}$");

        if (username.isEmpty()) {
            binding.InputUserNameSP.setError("Field cannot be empty");
            Snackbar.make(findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!userRegex.matcher(username).matches()) {
            binding.InputUserNameSP.setError("Please enter a valid Username");
            Snackbar.make(findViewById(android.R.id.content), R.string.user_length, Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            binding.InputUserNameSP.setError(null);
            return true;
        }
    }

    private boolean passCorrect() {
        Pattern passRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_,.:!¡?¿<>])(?=\\S+$).{8,}$");

        if (pass.isEmpty()) {
            binding.InputPasswordSP.setError("Field cannot be empty");
            Snackbar.make(findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!passRegex.matcher(pass).matches()) {
            binding.InputPasswordSP.setError("Password is too weak");
            passRegex();
            return false;
        } else {
            binding.InputPasswordSP.setError(null);
            return true;
        }
    }

    private boolean rPassCorrect() {
        if (rPass.isEmpty()) {
            binding.InputRPasswordSP.setError("Field cannot be empty");
            return false;
        } else if (!rPass.equals(pass)) {
            binding.InputRPasswordSP.setError("The password is not the same");
            Snackbar.make(findViewById(android.R.id.content), R.string.password_same, Snackbar.LENGTH_SHORT).show();
            Objects.requireNonNull(binding.InputRPasswordSP.getEditText()).setText("");
            return false;
        } else {
            binding.InputRPasswordSP.setError(null);
            return true;
        }
    }

    private void passRegex() {
        if (!pass.matches(".*[a-z].*")) {
            Snackbar.make(findViewById(android.R.id.content), R.string.pass_regex_minus, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[A-Z].*")) {
            Snackbar.make(findViewById(android.R.id.content), R.string.pass_regex_mayus, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[0-9].*")) {
            Snackbar.make(findViewById(android.R.id.content), R.string.pass_regex_number, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[@#$%^&+=_,.:!¡?¿<>].*")) {
            Snackbar.make(findViewById(android.R.id.content), R.string.pass_regex_special_char, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!pass.matches(".*[{8,}].*")) {
            Snackbar.make(findViewById(android.R.id.content), R.string.pass_regex_length, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (pass.matches(".*[(?=\\S+$)].*")) {
            Snackbar.make(findViewById(android.R.id.content), R.string.pass_regex_spaces, Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean validateData() {
        getDataEntered();
        Boolean[] result = new Boolean[]{this.emailCorrect(), this.passCorrect(), this.userCorrect(), this.rPassCorrect()};
        return !ArraysKt.contains(result, false);
    }

    /*--------------------------------------------------------------------------------------------*/

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    saveUserDataToDB(user.getUid(), username);
                    Utils.loggedUser = user;
                }

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Toast.makeText(SignUpActivity.this, R.string.register_successfully, Toast.LENGTH_SHORT).show();

                Objects.requireNonNull(binding.InputUserNameSP.getEditText()).setText("");
                Objects.requireNonNull(binding.InputPasswordSP.getEditText()).setText("");
                Objects.requireNonNull(binding.InputRPasswordSP.getEditText()).setText("");
                Objects.requireNonNull(binding.InputEmailSP.getEditText()).setText("");
            } else {
                Utils.loggedUser = null;
                Toast.makeText(SignUpActivity.this, R.string.register_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataToDB(String userId, String username) {
        usersRefDB.child(userId).child("username").setValue(username);
    }
}