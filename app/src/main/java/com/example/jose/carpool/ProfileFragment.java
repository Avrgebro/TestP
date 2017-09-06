package com.example.jose.carpool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private static String TAG = ProfileFragment.class.getSimpleName();
    private static String USER_UPDATE_URL = "http://200.16.7.170/api/users/actualizar_perfil";

    @Bind(R.id.profile_img) ImageView profileImg;
    @Bind(R.id.profile_name) TextView profileName;
    @Bind(R.id.profile_email) TextView profileEmail;
    @Bind(R.id.profile_phone_number) TextView profilePhone;
//    @Bind(R.id.profile_summary_name) TextView profileSummaryName;
//    @Bind(R.id.profile_summary_email) TextView profileSummaryEmail;
//    @Bind(R.id.profile_summary) TextView getProfileSummary;
    @Bind(R.id.fab_edit_profile) FloatingActionButton fabEditToggle;
    @Bind(R.id.fab_save_profile) FloatingActionButton fabSaveProfile;

    // Inputs
//    @Bind(R.id.profile_input_email_wrapper) TextInputLayout emailWrapper;
//    @Bind(R.id.profile_input_email) TextInputEditText emailInput;
    @Bind(R.id.profile_input_phone_wrapper) TextInputLayout phoneWrapper;
    @Bind(R.id.profile_input_phone) TextInputEditText phoneInput;
    @Bind(R.id.profile_input_first_name_wrapper) TextInputLayout firstNameWrapper;
    @Bind(R.id.profile_input_first_name) TextInputEditText firstNameInput;
    @Bind(R.id.profile_input_last_name_wrapper) TextInputLayout lastNameWrapper;
    @Bind(R.id.profile_input_last_name) TextInputEditText lastNameInput;

    private boolean editEnabled = false;
    private User mUser;
    private OkHttpClient mHttpClient;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, mainView);

        fabEditToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleProfileEdit();
            }
        });

        fabSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        bindUser();

        mHttpClient = new OkHttpClient();

        return mainView;
    }

    private void updateProfile() {
        if (!validateInput() || mUser == null) {
            return;
        }

//        mUser.setCorreo(emailInput.getText().toString());
        mUser.setTelefono(phoneInput.getText().toString());
        mUser.setNombre(firstNameInput.getText().toString());
        mUser.setApellido(lastNameInput.getText().toString());

        updateUser();
    }

    private void updateUser() {
        if (mUser == null) {
            return;
        }

        String userJsonBody = serializeUser(mUser);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json;charset=utf-8"),
                userJsonBody
        );
        Request request = (new Request.Builder())
                .url(USER_UPDATE_URL)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .post(body)
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API error: " + e.toString());
                Snackbar.make(
                        fabEditToggle,
                        "Ocurrió un error. Intente nuevamente",
                        Snackbar.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "Response code & body: " + response.code() + " " + response.body().string());

                switch (response.code()) {
                    case 500: { // Server error
                        Snackbar.make(
                                fabEditToggle,
                                "Ocurrió un error. Intente nuevamente",
                                Snackbar.LENGTH_SHORT
                        ).show();
                        return;
                    }
                }

                String userJson = new Gson().toJson(mUser, User.class);
                SharedPreferences.Editor prefEditor = getActivity().getSharedPreferences(
                        "SessionToken",
                        MODE_PRIVATE
                ).edit();

                prefEditor.putString("SessionUser", userJson);
                prefEditor.apply();

                bindUser();
                disableProfileEdit();

                Snackbar.make(
                        fabEditToggle,
                        "Perfil actualizado con éxito",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        });
    }

    // Shouldn't be here, user serialization should be standard
    // And probably centralized in User class
    private String serializeUser(User user) {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("_id", user.getID());
            jsonObject.put("_nombre", user.getNombre());
            jsonObject.put("_apellido", user.getApellido());
            jsonObject.put("_telefono", user.getTelefono());
            jsonObject.put("_imgPerfil", "");
        } catch (JSONException e) {
            Log.e(TAG, "Problem creating JsonObject", e);
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private boolean validateInput() {
        boolean valid = true;
//        String emailText = emailInput.getText().toString();
//        if (emailText.isEmpty()) {
//            emailText = mUser.getCorreo();
//        }

//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
//                || !emailUtils.correoPucp(emailText)) {
//            emailInput.setError("Correo inválido");
//            valid = false;
//        }

        String phone = phoneInput.getText().toString();
        if (phone.isEmpty()) {
            phone = mUser.getTelefono();
        }

//        if (!Patterns.PHONE.matcher(phone).matches()) {
//            phoneInput.setError("Teléfono inválido");
//            valid = false;
//        }

        // No name validation
        String firstName = firstNameInput.getText().toString();
        if (firstName.isEmpty()) {
            firstName = mUser.getNombre();
        }

        String lastName = lastNameInput.getText().toString();
        if (lastName.isEmpty()) {
            lastName = mUser.getApellido();
        }

        // Set validated input values
        firstNameInput.setText(firstName);
        lastNameInput.setText(lastName);
        phoneInput.setText(phone);
//        emailInput.setText(emailText);

        return valid;
    }

    private void toggleProfileEdit() {
        if (isEditEnabled()) {
            disableProfileEdit();
        } else {
            enableProfileEdit();
        }
    }

    private void disableProfileEdit() {
        setEditEnabled(false);
        fabEditToggle.setImageResource(R.drawable.ic_mode_edit_black_24px);
        fabSaveProfile.setVisibility(View.GONE);

//        emailWrapper.setVisibility(View.GONE);
        phoneWrapper.setVisibility(View.GONE);
        firstNameWrapper.setVisibility(View.GONE);
        lastNameWrapper.setVisibility(View.GONE);
        profilePhone.setVisibility(View.VISIBLE);
//        profileEmail.setVisibility(View.VISIBLE);
        profileName.setVisibility(View.VISIBLE);
    }

    private void enableProfileEdit() {
        setEditEnabled(true);
        fabEditToggle.setImageResource(R.drawable.ic_clear_black_24dp);
        fabSaveProfile.setVisibility(View.VISIBLE);

        phoneWrapper.setVisibility(View.VISIBLE);
//        emailWrapper.setVisibility(View.VISIBLE);
//        profileEmail.setVisibility(View.GONE);
        profilePhone.setVisibility(View.GONE);
        profileName.setVisibility(View.GONE);

        firstNameWrapper.setVisibility(View.VISIBLE);
        lastNameWrapper.setVisibility(View.VISIBLE);

        // Bind values to inputs
//        emailWrapper.setHint(mUser.getCorreo());
        phoneWrapper.setHint(mUser.getTelefono());
        firstNameWrapper.setHint(mUser.getNombre());
        lastNameWrapper.setHint(mUser.getApellido());
    }

    private void bindUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");

        if(!userJSON.isEmpty()) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJSON, User.class);
            bindUser(user);
        }else{
            Log.d(TAG, "No se recibio el usuario");
        }
    }

    private void bindUser(@NonNull User user) {
        mUser = user;
        Log.d(TAG, mUser.getNombre() + " " + mUser.getCorreo());

        profileName.setText(mUser.getFullName());
        profilePhone.setText(mUser.getTelefono());
        profileEmail.setText(mUser.getCorreo());
    }

    public boolean isEditEnabled() {
        return editEnabled;
    }

    synchronized public void setEditEnabled(boolean editEnabled) {
        this.editEnabled = editEnabled;
    }
}
