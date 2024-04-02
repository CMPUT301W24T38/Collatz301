package com.example.collatzcheckin.admin.controls;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.collatzcheckin.AdminMainActivity;
import com.example.collatzcheckin.R;

import com.example.collatzcheckin.admin.controls.events.AdminEventListFragment;
import com.example.collatzcheckin.admin.controls.profile.UserListFragment;
import com.example.collatzcheckin.admin.controls.profile.UserViewFragment;


public class AdminBrowseFragment extends Fragment {

    private View view;
    Button eventButton;
    Button profilesButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.admin_browse, container, false);

        eventButton = view.findViewById(R.id.events_button);
        profilesButton = view.findViewById(R.id.profiles_button);

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEventListFragment adminEventListFragment = new AdminEventListFragment();
                ((AdminMainActivity) requireActivity()).replaceFragment(adminEventListFragment);
            }
        });
        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListFragment userListFragment = new UserListFragment();
                ((AdminMainActivity) requireActivity()).replaceFragment(userListFragment);
            }
        });
        return view;
    }
}

