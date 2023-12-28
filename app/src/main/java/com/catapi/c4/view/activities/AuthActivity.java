package com.catapi.c4.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.catapi.c4.R;
import com.catapi.c4.databinding.ActivityAuthBinding;
import com.catapi.c4.view.fragments.SignInFragment;
import com.catapi.c4.view.fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private final Fragment[] fragments = new Fragment[]{new SignUpFragment(), new SignInFragment()};
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        mAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_auth, fragments[1], "signInFragment")
                .commit();

        /*getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[0], "signUpFragment").hide(fragments[1])
                .commit();*/


    }
}