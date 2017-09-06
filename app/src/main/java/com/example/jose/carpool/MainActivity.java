package com.example.jose.carpool;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static User user;

    private static final String TAG = "MainActivity";
    private static final int Editar_ACTIVITY_RESULT_CODE = 0;

    final Context context = this;

    public static TextView _nomnavbar;
    public static TextView _cornavbar;
    public static ImageView imgPerfil;
    private View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header=navigationView.getHeaderView(0);

        TextView _logout = (TextView) findViewById(R.id.logout);
        _logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();
            }
        });


        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        int SessionState = prefs.getInt("SessionState", 0);
        if(SessionState == 0){
            Log.d(TAG, "No session active");
            Intent loginintent = new Intent(this, activity_login.class);
            startActivity(loginintent);
        }



        //displaySelectedScreen(R.id.idVehiculos);

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");

        if(!userJSON.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJSON, User.class);
            Log.d(TAG, user.getNombre()+" "+user.getCorreo());

            _nomnavbar = (TextView) findViewById(R.id.nombre_navbar);
            _cornavbar = (TextView) findViewById(R.id.correo_navbar);
            imgPerfil = (ImageView) findViewById(R.id.lblFOTOPERFIL);
            if(_nomnavbar!=null && _cornavbar!=null){
                _nomnavbar.setText(user.getNombre() + " " + user.getApellido());
                _cornavbar.setText(user.getCorreo());
            }

            if(imgPerfil == null){
                try{
                    Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imgPerfil);
                }catch (Exception ex){
                    Log.e(TAG,"error carga imagen");
                }

            }

        }else{
            Log.e(TAG, "No se recibio el usuario");
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");

        if(!userJSON.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJSON, User.class);
            Log.d(TAG, user.getNombre()+" "+user.getCorreo());
            _nomnavbar = (TextView) findViewById(R.id.nombre_navbar);
            _cornavbar = (TextView) findViewById(R.id.correo_navbar);
            if(_nomnavbar!=null && _cornavbar!=null){
                _nomnavbar.setText(user.getNombre() + " " + user.getApellido());
                _cornavbar.setText(user.getCorreo());
            }


        }else{
            Log.e(TAG, "No se recibio el usuario");
        }

        return true;

    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;




        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.idCrearPool:
                fragment = new fragment_crearpool();
                break;
            case R.id.idVehiculos:
                fragment = new tus_vehiculos();
                break;
            case R.id.idVer_pendiente:
                //fragment = new Menu2();
                break;
            case R.id.idVer_historial:
                fragment = new historial_fragment();
                break;
            case R.id.idEditarPerfil:
                fragment = new editar_Perfil(user);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.MainFrameLayout, fragment);
            ft.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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


    public void Ver_Perfil(View view){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_verperfil);
        dialog.setTitle("Perfil");
        cargarVerPerfil(dialog);
        dialog.show();

    }

    public static void cargarVerPerfil(Dialog dialog){
        TextView nombre = (TextView) dialog.findViewById(R.id.txtNombre);
        TextView telefono = (TextView) dialog.findViewById(R.id.txtTelefono);
        TextView correo = (TextView) dialog.findViewById(R.id.txtCorreo);
        ImageView foto = (ImageView) dialog.findViewById(R.id.lblFoto);

        nombre.setText(user.getNombre().toString() +" "+ user.getApellido().toString());
        telefono.setText(user.getTelefono());
        correo.setText(user.getCorreo().toString());

        if(imgPerfil != null){
            foto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            foto.setImageDrawable(imgPerfil.getDrawable());
        }
    }


}