package com.example.jose.carpool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    //Se instancia la clase User con los datos obtenidos del login y PHP


    public User usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usuario = new User();
        usuario.setNombre("Flash");
        usuario.setApPat("Campos");
        usuario.setApMat("Fabian");
        usuario.setmTelefono("957894520");
        usuario.setCorreo("Flash@pucp.pe");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
/*
        TextView lblNombre =  navigationView.findViewById(R.id.lblNombreUser);
        lblNombre.setText(usuario.getNombre());
        TextView lblCorreo = (TextView) findViewById(R.id.lblCorreoUser);
        lblCorreo.setText(usuario.getCorreo());
*/
        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        int SessionState = prefs.getInt("SessionState", 0);
        if(SessionState == 0){
            Log.d(TAG, "No session active");
            Intent loginintent = new Intent(this, activity_login.class);
            startActivity(loginintent);
        }

        //TODO:AsyncTask para descargar los datos de los carpool y popular la ventana principal
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pool) {
            // Handle the camera action
        } else if (id == R.id.nav_pendiente) {

        } else if (id == R.id.nav_historial) {

        } else if (id == R.id.nav_configuraciones) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Ver_Perfil(View view){
        Intent intent = new Intent(this, activity_PerfilUsuario.class);
        intent.putExtra("sampleObject", usuario);
        startActivity(intent);
    }

}
