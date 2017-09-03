package com.example.jose.carpool;

/**
 * Created by Johnny on 14/08/2017.
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class editar_Perfil extends Fragment {

    EditText txtNombre;
    EditText txtApellido;
    EditText txtTelefono;
    TextInputLayout textinputNombre;
    TextInputLayout textinputApellido;
    TextInputLayout textinputTelefono;
    public User usuario;


    private Uri photoURI;
    private static final int REQUEST_CAMERA = 1;
    private static final int PICK_FROM_FILE =2;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1234;
    private static final int MY_PERMISSIONS_REQUEST_FILE =4321;
    private  Bitmap dstBitmap;


    String mCurrentPhotoPath;
    TextView flagLoadImage;
    ImageView prueba;
    Bitmap bitmap;
    public editar_Perfil(){}

    public editar_Perfil(User usuario2){
        usuario = usuario2;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file

        return inflater.inflate(R.layout.fragment_editarperfil, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");
        ImageView perfil2 = (ImageView) MainActivity.imgPerfil;
        /*if(perfil2!=null){
            prueba.setScaleType(ImageView.ScaleType.CENTER_CROP);
            prueba.setImageDrawable(perfil2.getDrawable());
        }*/

        textinputNombre = (TextInputLayout) getView().findViewById(R.id.textNombre);
        textinputApellido = (TextInputLayout) getView().findViewById(R.id.textApellido);
        textinputTelefono = (TextInputLayout) getView().findViewById(R.id.textTelefono);


        textinputNombre.setHint(usuario.getNombre().toString());
        textinputApellido.setHint(usuario.getApellido().toString());
        textinputTelefono.setHint(usuario.getTelefono());

        ImageView editarAll = (ImageView) getView().findViewById(R.id.btnEditar);
        editarAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtNombre = textinputNombre.getEditText();
                txtApellido = textinputApellido.getEditText();
                txtTelefono = textinputTelefono.getEditText();
                if(txtNombre.length() != 0) {
                    MainActivity.user.setNombre(txtNombre.getText().toString());
                }

                if(txtApellido.length() != 0) {
                    MainActivity.user.setApellido(txtApellido.getText().toString());
                }

                if(txtTelefono.length() != 0) {
                    MainActivity.user.setTelefono(txtTelefono.getText().toString());
                }


                TextView nombreMain = (TextView) MainActivity._nomnavbar;
                nombreMain.setText(MainActivity.user.getNombre() + " " + MainActivity.user.getApellido());

                if(prueba != null){
                    ImageView perfil = (ImageView) MainActivity.imgPerfil;
                    perfil.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    perfil.setImageDrawable(prueba.getDrawable());
                }



                //LLamar a la funcion actualizar BD
                actualizarUsuario();
                modificarCache();

               // FragmentTransaction ft = getFragmentManager().beginTransaction();
               // ft.detach(getTargetFragment()).attach(getTargetFragment()).commit();
            }
        });

        final ImageButton btnAddPhoto = (ImageButton) getView().findViewById(R.id.btn_addPhotoPerfil);
                /*Carga la imagen para mostrartla es una prueba*/
        prueba = getView().findViewById(R.id.lblFotoEditPerfil);
                /*To pick car image*/
        final String [] items = { "Cámara","Galería"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity()  , android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona una imagen");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }else {
                        launchCam();
                    }
                }
                else{

                    Intent intent  = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });

                /*Build options dialog*/
        final AlertDialog dialogPhoto = builder.create();
        btnAddPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                dialogPhoto.show();
            }

        });

    }

    private void modificarCache(){

        Gson gson = new Gson();
        String json = gson.toJson(usuario);

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("SessionToken", 0).edit();
        editor.putString("SessionUser", json);
        editor.commit();
    }

    public static void actualizarUsuario() {
        //subir user
        //String rspt = postJSONObject(BaseURL, jsonObject);


        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("idUsuario", MainActivity.user.getID());
                    jsonObject.put("nombre", MainActivity.user.getNombre());
                    jsonObject.put("apellido", MainActivity.user.getApellido());
                    jsonObject.put("telefono", MainActivity.user.getTelefono());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String BaseURL = "http://200.16.7.170/api/users/actualizar_perfil";//solo se agrega "correo/pass"
                OutputStream os = null;
                InputStream is = null;
                HttpURLConnection conn = null;
                try {
                    //constants
                    URL url = new URL(BaseURL);
                    String message = jsonObject.toString();
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /*milliseconds*/);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(message.getBytes().length);

                    //make some HTTP header nicety
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                    //open
                    conn.connect();

                    //setup send
                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(message.getBytes());
                    //clean up
                    os.flush();

                    //do somehting with response
                    is = conn.getInputStream();
                    //String contentAsString = readIt(is,len);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //clean up
                    try {
                        os.close();
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    conn.disconnect();
                }
            }
        }).start();

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        bitmap= null;
        String path = "";
        if (requestCode == PICK_FROM_FILE){
            photoURI =data.getData();
            try {
                // path = getPathFromURI(photoURI);
                InputStream is = getActivity().getContentResolver().openInputStream(photoURI);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();


                int srcWidth = bitmap.getWidth();
                int srcHeight = bitmap.getHeight();
                int dstWidth = (int)(srcWidth*0.8f);
                int dstHeight = (int)(srcHeight*0.8f);
                Bitmap dstBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
                prueba.setImageBitmap(dstBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_CAMERA){
            try{
                InputStream is = getActivity().getContentResolver().openInputStream(photoURI);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
                int srcWidth = bitmap.getWidth();
                int srcHeight = bitmap.getHeight();
                int dstWidth = (int)(srcWidth*0.8f);
                int dstHeight = (int)(srcHeight*0.8f);
                dstBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
                prueba.setImageBitmap(dstBitmap);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null){
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }else
            return "";
    }

    public void launchCam(){

        Intent intent  = new Intent  (MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager())!=null) {


            File photoFile = null;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(getActivity(), "com.example.jose.carpool.fileprovider"
                            , photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_CAMERA);

                }
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    launchCam();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }case MY_PERMISSIONS_REQUEST_FILE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {//nada
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}

