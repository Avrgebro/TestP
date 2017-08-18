package com.example.jose.carpool;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static User usuario;
    User user;

    private static final String TAG = "MainActivity";
    private static final int Editar_ACTIVITY_RESULT_CODE = 0;
    ImageView img;
    final Context context = this;

    public static TextView _nomNavbar;
    public static TextView _corNavbar;


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

        /*SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        int SessionState = prefs.getInt("SessionState", 0);
        if(SessionState == 0){
            Log.d(TAG, "No session active");
            Intent loginintent = new Intent(this, activity_login.class);
            startActivity(loginintent);
        }*/

        //displaySelectedScreen(R.id.idPedir_pool);




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

        _nomNavbar = (TextView) findViewById(R.id.nombre_navbar);
        _corNavbar = (TextView) findViewById(R.id.correo_navbar);

        _nomNavbar.setText(usuario.getNombre() + " " + usuario.getApellido());
        _corNavbar.setText(usuario.getCorreo());


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
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.idPedir_pool:

                break;
            case R.id.idVer_pendiente:
                //fragment = new Menu2();
                break;
            case R.id.idVer_historial:
                //fragment = new Menu3();
                break;
            case R.id.idEditarPerfil:
                fragment = new editar_Perfil(usuario);
                break;
            case R.id.idLogout:
                //fragment = new Menu3();
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

        nombre.setText(usuario.getNombre().toString() + usuario.getApellido().toString());
        telefono.setText(usuario.getTelefono());
        correo.setText(usuario.getCorreo().toString());
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