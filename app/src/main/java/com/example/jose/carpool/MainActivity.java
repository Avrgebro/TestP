package com.example.jose.carpool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.ProfileImgListener {

    public static User user;
    public static Bitmap profilePictureBitmap;

    private static final String TAG = "MainActivity";
    private View header;
    private static final int RESULT_CODE = 0;
    private boolean mReturningWithResult;

    private static final int Editar_ACTIVITY_RESULT_CODE = 0;
    ImageView img;
    final Context context = this;

    public static TextView _nomnavbar;
    public static TextView _cornavbar;
    private Target customProfileImgTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.btn_all_pools);

        header = navigationView.getHeaderView(0);

        TextView _logout = (TextView) findViewById(R.id.logout);
        _logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        final ImageView navImage = header.findViewById(R.id.nav_profile_img);
        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationView.setCheckedItem(R.id.menu_none);
                inflateFragment(new ProfileFragment());
            }
        });

        customProfileImgTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                profilePictureBitmap = bitmap;
                navImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(TAG, "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        int SessionState = prefs.getInt("SessionState", 0);
        if(SessionState == 0){
            Log.d(TAG, "No session active");
            Intent loginintent = new Intent(this, activity_login.class);
            startActivityForResult(loginintent, RESULT_CODE);
        } else {
            //inflate first fragment
            displaySelectedScreen(R.id.btn_all_pools);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_CODE: {
                if (resultCode == RESULT_OK) {
                    super.onActivityResult(requestCode, resultCode, data);
                    mReturningWithResult = true;
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mReturningWithResult) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            pool_fragment cpF = new pool_fragment();
            fragmentTransaction.replace(R.id.MainFrameLayout, cpF);
            fragmentTransaction.commit();
        }
        // Reset the boolean flag back to false for next time.
        mReturningWithResult = false;
    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");
        String bitmapBase64 = prefs.getString("SessionProfileImg", "");
        Log.e(TAG, userJSON);
        if(!userJSON.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJSON, User.class);
            Log.d(TAG, user.getNombre()+" "+user.getCorreo());
            TextView _nomnavbar = (TextView) header.findViewById(R.id.nombre_navbar);
            TextView _cornavbar = (TextView) header.findViewById(R.id.correo_navbar);

            if(_nomnavbar!=null && _cornavbar!=null){
                _nomnavbar.setText(user.getNombre() + " " + user.getApellido());
                _cornavbar.setText(user.getCorreo());
            }

            AsyncTask<String, Void, Bitmap> getProfileImg = new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... strings) {
                    if (strings == null || strings.length == 0) {
                        return null;
                    }
                    Bitmap bitmap = BitmapUtils.decodeBase64(strings[0]);
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    if (bitmap == null && user.getPicUri() != null && !user.getPicUri().isEmpty()) {
                        Picasso.with(MainActivity.this)
                                .load(user.getPicUri())
                                .into(customProfileImgTarget);
                    } else if (bitmap != null) {
                        updateProfileImg(bitmap);
                    }
                }
            };
            getProfileImg.execute(bitmapBase64);
        } else {
            Log.d(TAG, "No se recibio el usuario");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.btn_all_pools: {
                fragment = new pool_fragment();
                break;
            }
//            case R.id.idCrearPool: {
//                fragment = new fragment_crearpool();
//                break;
//            }
            case R.id.idVehiculos: {
                fragment = new tus_vehiculos();
                break;
            }
            case R.id.idVer_pendiente: {
                fragment = new fragment_pendiente();
                break;
            }
            case R.id.idVer_historial: {
                //fragment = new Menu3();
                break;
            }
        }

        //replacing the fragment
        inflateFragment(fragment);
    }

    private void inflateFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.MainFrameLayout, fragment);
            ft.commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("SessionToken", MODE_PRIVATE).edit();
        editor.remove("SessionUser");
        editor.putInt("SessionState", 0);
        editor.apply();

        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    @Override
    public void updateProfileImg(final Bitmap bitmap, boolean serializeImg) {
        profilePictureBitmap = bitmap;
        final ImageView navImage = header.findViewById(R.id.nav_profile_img);
        navImage.setImageBitmap(bitmap);

        if (serializeImg) {
            // Update user image url
            AsyncTask<User, Void, String> updateImgUrl = new AsyncTask<User, Void, String>() {
                @Override
                protected String doInBackground(User... users) {
                    if (users == null || users.length == 0) {
                        return null;
                    }
                    String bitmapBase64 = BitmapUtils.bitmapToBase64(bitmap);
                    return bitmapBase64;
                }

                @Override
                protected void onPostExecute(String profileImgBas64) {
                    super.onPostExecute(profileImgBas64);
                    SharedPreferences.Editor editor = getSharedPreferences("SessionToken", MODE_PRIVATE).edit();
                    editor.putString("SessionProfileImg", profileImgBas64);
                    editor.apply();
                }
            };
            updateImgUrl.execute(user);
        }
    }

    @Override
    public void updateProfileImg(Bitmap bitmap) {
        updateProfileImg(bitmap, false);
    }
}