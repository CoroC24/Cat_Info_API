package com.catapi.c4.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import com.catapi.c4.R;
import com.catapi.c4.databinding.ActivitySignInBinding;
import com.catapi.c4.model.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import kotlin.collections.ArraysKt;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    private String pass, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        getDataEntered();

        binding.signUpClickableText.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        binding.signInButton.setOnClickListener(view -> {
            if (validateData()) {
                signInAccount(email, pass);
            }
        });
    }

    private void getDataEntered() {
        email = Objects.requireNonNull(binding.InputEmailSignIn.getEditText()).getText().toString();
        pass = Objects.requireNonNull(binding.InputPasswordSignIn.getEditText()).getText().toString();
    }

    private boolean emailCorrect() {
        if (email.isEmpty()) {
            binding.InputEmailSignIn.setError("Field cannot be empty");
            Snackbar.make(findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            return false;

        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.InputEmailSignIn.setError("Please enter a valid email address");
            Snackbar.make(findViewById(android.R.id.content), R.string.valid_email, Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            binding.InputEmailSignIn.setError(null);
            return true;
        }
    }

    private boolean passCorrect() {
        if (pass.isEmpty()) {
            binding.InputPasswordSignIn.setError("Field cannot be empty");
            Snackbar.make(findViewById(android.R.id.content), R.string.field_empty, Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            binding.InputPasswordSignIn.setError(null);
            return true;
        }
    }

    private boolean validateData() {
        getDataEntered();
        Boolean[] result = new Boolean[]{this.emailCorrect(), this.passCorrect()};
        return !ArraysKt.contains(result, false);
    }

    private void signInAccount(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, task -> {
            if (task.isSuccessful()) {
                Utils.loggedUser = mAuth.getCurrentUser();

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Toast.makeText(SignInActivity.this, R.string.login_successfully, Toast.LENGTH_SHORT).show();

                Objects.requireNonNull(binding.InputEmailSignIn.getEditText()).setText("");
                Objects.requireNonNull(binding.InputPasswordSignIn.getEditText()).setText("");
            } else {
                Toast.makeText(SignInActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}