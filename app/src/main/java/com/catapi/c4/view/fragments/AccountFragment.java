package com.catapi.c4.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.catapi.c4.R;
import com.catapi.c4.view.activities.AuthActivity;

public class AccountFragment extends Fragment {

    private TextView textView;

    public AccountFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewLayout = inflater.inflate(R.layout.fragment_account, container, false);

        textView = viewLayout.findViewById(R.id.textview_account);

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            startActivity(intent);
        });

        return viewLayout;
    }
}
