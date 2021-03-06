package com.example.jose.carpool;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 51242;
    private static final int REQUEST_PICK_FROM_FILE = 51231;
    private static final int REQUEST_CAMERA = 52322;
    private static final int MY_PERMISSIONS_REQUEST_FILE = 12312;
    private static String TAG = ProfileFragment.class.getSimpleName();
    private static final String USER_UPDATE_URL = "http://200.16.7.170/api/users/actualizar_perfil";
    private static final String BASE_CARPOOL_URL = "http://200.16.7.170/api/pools/";
    private static final String DRIVER_CARPOOL_URL = BASE_CARPOOL_URL + "obtener_pools_conductor/";
    private static final String PASSENGER_CARPOOL_URL = BASE_CARPOOL_URL + "obtener_pools_pasajero/";

    @Bind(R.id.profile_img) ImageView profileImg;
    @Bind(R.id.profile_name) TextView profileName;
    @Bind(R.id.profile_email) TextView profileEmail;
    @Bind(R.id.profile_phone_number) TextView profilePhone;
    @Bind(R.id.profile_carpool_driver_count) TextView profileDriverCarpools;
    @Bind(R.id.profile_carpool_passenger_count) TextView profilePassangerCarpools;
    @Bind(R.id.fab_edit_profile) FloatingActionButton fabEditToggle;
    @Bind(R.id.fab_save_profile) FloatingActionButton fabSaveProfile;
    @Bind(R.id.btn_profile_edit_picture) ImageButton selectImageButton;

    // Inputs
    @Bind(R.id.profile_input_phone_wrapper) TextInputLayout phoneWrapper;
    @Bind(R.id.profile_input_phone) TextInputEditText phoneInput;
    @Bind(R.id.profile_input_first_name_wrapper) TextInputLayout firstNameWrapper;
    @Bind(R.id.profile_input_first_name) TextInputEditText firstNameInput;
    @Bind(R.id.profile_input_last_name_wrapper) TextInputLayout lastNameWrapper;
    @Bind(R.id.profile_input_last_name) TextInputEditText lastNameInput;

    private boolean editEnabled = false;
    private User mUser;
    private OkHttpClient mHttpClient;
    private Dialog mPhotoDialog;
    private String mCurrentPhotoPath;
    private Uri mCurrentPhotoUri;
    private Bitmap mProfileBitmap;
    private Target mCustomTarget;
    private ProfileImgListener mProfileImgListener;

    public interface ProfileImgListener {
        void updateProfileImg(Bitmap bitmap);
        void updateProfileImg(Bitmap bitmap, boolean serializeImg);
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mProfileImgListener = (ProfileImgListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
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
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoSelectionDialog();
            }
        });
        mPhotoDialog = buildPhotoSelectionDialog();
        if (MainActivity.profilePictureBitmap != null) {
            profileImg.setImageBitmap(MainActivity.profilePictureBitmap);
        }

        mCustomTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (mProfileImgListener != null) {
                    mProfileImgListener.updateProfileImg(bitmap, true);
                }
                profileImg.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(TAG, "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        bindUser();
        mHttpClient = new OkHttpClient();

        return mainView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_PICK_FROM_FILE: {
                mCurrentPhotoUri = data.getData();
                setProfileImg(mCurrentPhotoUri);
                break;
            }
            case REQUEST_CAMERA: {
                setProfileImg(mCurrentPhotoUri);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults
    ) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    launchCamera();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }case MY_PERMISSIONS_REQUEST_FILE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                break;
            }
        }
    }

    private void setProfileImg(Uri photoUri) {
        try {
            // path = getPathFromURI(mCurrentPhotoUri);
            InputStream is = getActivity().getContentResolver().openInputStream(photoUri);
            mProfileBitmap = BitmapFactory.decodeStream(is);
            is.close();

            int srcWidth = mProfileBitmap.getWidth();
            int srcHeight = mProfileBitmap.getHeight();
            int dstWidth = (int)(srcWidth*0.8f);
            int dstHeight = (int)(srcHeight*0.8f);

            Bitmap dstBitmap = Bitmap.createScaledBitmap(mProfileBitmap, dstWidth, dstHeight, true);
            profileImg.setImageBitmap(dstBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void launchCamera() {
        Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    mCurrentPhotoUri = FileProvider.getUriForFile(
                            getActivity(),
                            "com.example.jose.carpool.fileprovider",
                            photoFile
                    );
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Dialog buildPhotoSelectionDialog() {
        final String [] items = { "Cámara","Galería" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item,
                items
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona una imagen");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                if (index == 0) {
                    if (ActivityCompat.checkSelfPermission(
                            getActivity(),
                            android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{android.Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA
                        );
                    } else {
                        launchCamera();
                    }
                } else {
                    Intent intent  = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Complete action using"),
                            REQUEST_PICK_FROM_FILE
                    );
                }
            }
        });

        AlertDialog dialogPhoto = builder.create();
        return dialogPhoto;
    }

    private void showPhotoSelectionDialog() {
        if (mPhotoDialog != null) {
            mPhotoDialog.show();
        }
    }

    private void updateProfile() {
        if (!validateInput() || mUser == null) {
            return;
        }

//        mUser.setCorreo(emailInput.getText().toString());
        mUser.setTelefono(phoneInput.getText().toString());
        mUser.setNombre(firstNameInput.getText().toString());
        mUser.setApellido(lastNameInput.getText().toString());

        // Run in background
        AsyncTask<Void, Void, Void> updateUser = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                updateUser();
                return null;
            }
        };
        updateUser.execute();
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
                Log.e(TAG, "User update success. Before serialization to shared preferences");
                String userJson = (new Gson()).toJson(mUser);
                SharedPreferences.Editor prefEditor = getActivity().getSharedPreferences(
                        "SessionToken",
                        MODE_PRIVATE
                ).edit();

                prefEditor.putString("SessionUser", userJson);
                prefEditor.apply();
                Log.e(TAG, "After serialization to shared preferences");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getActivity()).load(mCurrentPhotoUri).into(mCustomTarget);
                        disableProfileEdit();
                        bindUser();
                    }
                });

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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idUsuario", user.getID());
            jsonObject.put("nombre", user.getNombre());
            jsonObject.put("apellido", user.getApellido());
            jsonObject.put("telefono", user.getTelefono());
            jsonObject.put("img", BitmapUtils.bitmapToBase64(mProfileBitmap));
        } catch (JSONException e) {
            Log.e(TAG, "Problem creating JsonObject", e);
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private boolean validateInput() {
        boolean valid = true;

        String phone = phoneInput.getText().toString();
        if (phone.isEmpty()) {
            phone = mUser.getTelefono();
        }

        if (phone.length() < 8) {
            phoneWrapper.setError("Numero Invalido");
            valid = false;
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

        phoneWrapper.setVisibility(View.GONE);
        firstNameWrapper.setVisibility(View.GONE);
        lastNameWrapper.setVisibility(View.GONE);
        selectImageButton.setVisibility(View.GONE);

        profilePhone.setVisibility(View.VISIBLE);
        profileName.setVisibility(View.VISIBLE);
    }

    private void enableProfileEdit() {
        setEditEnabled(true);
        fabEditToggle.setImageResource(R.drawable.ic_clear_black_24dp);
        fabSaveProfile.setVisibility(View.VISIBLE);

        phoneWrapper.setVisibility(View.VISIBLE);
        profilePhone.setVisibility(View.GONE);
        profileName.setVisibility(View.GONE);

        firstNameWrapper.setVisibility(View.VISIBLE);
        lastNameWrapper.setVisibility(View.VISIBLE);
        selectImageButton.setVisibility(View.VISIBLE);

        // Bind values to inputs
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
            Log.d(TAG, "Before bindUser(User)");
            bindUser(user);
            Log.d(TAG, "After bindUser(User)");
        } else {
            Log.d(TAG, "No se recibio el usuario");
        }
    }

    private void bindUser(@NonNull User user) {
        mUser = user;
        Log.d(TAG, mUser.getNombre() + " " + mUser.getCorreo());

        profileName.setText(mUser.getFullName());
        profilePhone.setText(mUser.getTelefono());
        profileEmail.setText(mUser.getCorreo());

        final AsyncTask<User, Void, String[]> carpoolCount = new AsyncTask<User, Void, String[]>() {
            @Override
            protected String[] doInBackground(User... users) {
                if (users == null || users.length == 0) {
                    return null;
                }
                User user = users[0];
                final int requestedCarpoolState = 1; // En camino
                try {
                    String driverPoolResponse = UrlUtils.makeHttpRequestGet(new URL(
                            DRIVER_CARPOOL_URL + user.getID() + "/" + requestedCarpoolState
                    ));
                    String passengerPoolResponse = UrlUtils.makeHttpRequestGet(new URL(
                            PASSENGER_CARPOOL_URL + user.getID() + "/" + requestedCarpoolState
                    ));

                    String[] results = new String[] { driverPoolResponse, passengerPoolResponse };
                    return results;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String[] strings) {
                super.onPostExecute(strings);
                if (strings == null) {
                    return;
                }
                Gson gson = new Gson();
                CarPool[] driverCarpools = gson.fromJson(strings[0], CarPool[].class);
                CarPool[] passengerCarpools = gson.fromJson(strings[1], CarPool[].class);

                if (driverCarpools == null) {
                    profileDriverCarpools.setText("0");
                } else {
                    profileDriverCarpools.setText("" + driverCarpools.length);
                }

                if (passengerCarpools == null) {
                    profilePassangerCarpools.setText("0");
                } else {
                    profilePassangerCarpools.setText("" + passengerCarpools.length);
                }
            }
        };
        carpoolCount.execute(user);
    }

    public boolean isEditEnabled() {
        return editEnabled;
    }

    synchronized public void setEditEnabled(boolean editEnabled) {
        this.editEnabled = editEnabled;
    }
}
