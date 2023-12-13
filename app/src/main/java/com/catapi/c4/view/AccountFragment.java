package com.catapi.c4.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.catapi.c4.R;

public class AccountFragment extends Fragment {

    public AccountFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}
