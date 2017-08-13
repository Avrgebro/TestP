package com.example.jose.carpool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        public User usuario;
        User user;
    private static final String TAG = "MainActivity";
    private static final int Editar_ACTIVITY_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setear o recibir usuario
        usuario = new User();
        usuario.setNombre("Flash");
        usuario.setApellido("Campos");
        usuario.setCorreo("flash@pucp.pe");
        usuario.setTelefono("978461250");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        int SessionState = prefs.getInt("SessionState", 0);
        if(SessionState == 0){
            Log.d(TAG, "No session active");
            Intent loginintent = new Intent(this, activity_login.class);
            startActivity(loginintent);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
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

        TextView _nomnavbar = (TextView) findViewById(R.id.nombre_navbar);
        TextView _cornavbar = (TextView) findViewById(R.id.correo_navbar);

        _nomnavbar.setText(usuario.getNombre() + " " + usuario.getApellido());
        _cornavbar.setText(usuario.getCorreo());


 /*
        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");

        if(!userJSON.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJSON, User.class);
            Log.d(TAG, user.getNombre()+" "+user.getCorreo());
            TextView _nomnavbar = (TextView) findViewById(R.id.nombre_navbar);
            TextView _cornavbar = (TextView) findViewById(R.id.correo_navbar);

            if(_nomnavbar!=null && _cornavbar!=null){
                _nomnavbar.setText(user.getNombre() + " " + user.getApellido());
                _cornavbar.setText(user.getCorreo());
            }


        }else{
            Log.e(TAG, "No se recibio el usuario");
        }
*/
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

        if (id == R.id.idPedir_pool) {
            // Handle the camera action
        } else if (id == R.id.idVer_pendiente) {

        } else if (id == R.id.idVer_historial) {

        } else if (id == R.id.idConfiguraciones) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Ver_Perfil(View view){
        Intent intent = new Intent(this, frmEditarPerfil.class);
        //intent.putExtra("sampleObject", user);
        intent.putExtra("sampleObject", usuario);
        setResult(frmEditarPerfil.RESULT_OK, intent);
        startActivityForResult(intent, Editar_ACTIVITY_RESULT_CODE); //suppose resultCode == 2;
        //finish();

    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == Editar_ACTIVITY_RESULT_CODE) {
                if (resultCode == RESULT_OK) {
                    User usuarioAux = (User)data.getSerializableExtra("UsuarioRet");

                    usuario.setNombre(usuarioAux.getNombre());
                    usuario.setApellido(usuarioAux.getApellido());
                    usuario.setTelefono(usuarioAux.getTelefono());
                    TextView _nomnavbar = (TextView) findViewById(R.id.nombre_navbar);
                    TextView _cornavbar = (TextView) findViewById(R.id.correo_navbar);

                    _nomnavbar.setText(usuario.getNombre() + " " + usuario.getApellido());
                    _cornavbar.setText(usuario.getCorreo());

                }
            }





    }

}
