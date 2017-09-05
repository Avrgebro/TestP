package com.example.jose.carpool;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private static String TAG = ProfileFragment.class.getSimpleName();

    @Bind(R.id.profile_img) ImageView profileImg;
    @Bind(R.id.profile_name) TextView profileName;
    @Bind(R.id.profile_email) TextView profileEmail;
    @Bind(R.id.profile_phone_number) TextView profilePhone;
    @Bind(R.id.profile_summary_name) TextView profileSummaryName;
    @Bind(R.id.profile_summary_email) TextView profileSummaryEmail;
    @Bind(R.id.profile_summary) TextView getProfileSummary;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, mainView);

        FloatingActionButton fabEditProfile = mainView.findViewById(R.id.fab_edit_profile);
        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Hola", Toast.LENGTH_SHORT).show();
            }
        });

        bindUser();

        return mainView;
    }

    private void bindUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");

        if(!userJSON.isEmpty()) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJSON, User.class);
            Log.d(TAG, user.getNombre() + " " + user.getCorreo());

            profileName.setText(user.getFullName());
            profileSummaryName.setText(user.getFullName());
            profilePhone.setText(user.getTelefono());
            profileEmail.setText(user.getCorreo());
            profileSummaryEmail.setText(user.getCorreo());
        }else{
            Log.d(TAG, "No se recibio el usuario");
        }
    }
}
