package com.example.jose.carpool;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Johnny on 26/08/2017.
 */

public class tus_vehiculos extends Fragment {
    private static final String TAG = "VehiculoFragment";
    private static final String _baseVehiculo = "http://200.16.7.170/api/vehiculos/obtener_vehiculos/";
    private static String _baseDelVehi = "http://200.16.7.170/api/vehiculos/eliminar_vehiculo/";
    private ArrayList<Vehiculo> lstVehi;
    private RecyclerView listView;
    private SwipeRefreshLayout swipeView;
    private int auxI;

    private Uri photoURI;
    private static final int REQUEST_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1234;
    private static final int MY_PERMISSIONS_REQUEST_FILE = 4321;

    private User mUser;

    String mCurrentPhotoPath;
    TextView flagLoadImage;
    ImageView prueba;
    Bitmap bitmap;

    TextInputLayout textInputplaca;
    TextInputLayout textInputmodelo;
    TextInputLayout textInputmarca;
    TextInputLayout textInputcolor;
    TextInputLayout textInputnasientos;


    EditText txtPlaca;
    EditText txtModelo;
    EditText txtMarca;
    EditText txtColor;
    EditText txtNasientos;
    private OkHttpClient mHttpClient;
    private vehiculo_adapter mVehicleAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // returning our layout file
        // UI setup
        View view = inflater.inflate(R.layout.fragment_vehiculos, container, false);
        listView = (RecyclerView) view.findViewById(R.id.listViewV);

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.strlayoutVehi);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        //Toast.makeText(getActivity(), "Refresh",
                        //Toast.LENGTH_LONG).show();
                        lstVehi.clear();
                        VehiculosAT task = new VehiculosAT();
                        task.execute();
                    }
                }, 3000);
            }
        });
        bindUser();

        // Data setup
        if(lstVehi == null) {
            lstVehi = new ArrayList<>();
        }
        mVehicleAdapter = new vehiculo_adapter(getActivity(), lstVehi);
        if(lstVehi.isEmpty()){
            //asyncTask para obtener pools
            VehiculosAT task = new VehiculosAT();
            task.execute();
        }

        mVehicleAdapter.setOnItemLongClickListener(new vehiculo_adapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                createAndShowAlertDialog(position);
                return true;
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false
        );
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mVehicleAdapter);

        mHttpClient = new OkHttpClient();

        return view;
    }

    private void bindUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");

        if(!userJSON.isEmpty()) {
            Gson gson = new Gson();
            mUser = gson.fromJson(userJSON, User.class);
        } else {
            Log.d(TAG, "No se recibio el usuario");
        }
    }

    private void createAndShowAlertDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar vehiculo");
        builder.setMessage("¿Seguro que quieres eliminar el vehiculo de placa ?" + lstVehi.get(i).getPlaca());
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                EliminarVehiculo ee = new EliminarVehiculo();
                auxI = i;
                ee.execute();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class EliminarVehiculo extends   AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls){
            _baseDelVehi = _baseDelVehi + lstVehi.get(auxI).getID();

            String formedurl = _baseDelVehi;

            URL url = UrlUtils.createUrl(formedurl);
            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestGet(url); ///////
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }
            Gson gson = new Gson();
            User user = gson.fromJson(jsonResponse, User.class);

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String userinfoJSON){
            if (TextUtils.isEmpty(userinfoJSON)) {
                Log.d(TAG, "Json vacio");
                return;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(userinfoJSON);
                int codigo = baseJsonResponse.getInt("codigo");
                if (codigo == 1) {
                    String mensaje = baseJsonResponse.getString("mensaje");
                    Log.d(TAG, mensaje);
                    UpdateUI();
                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void UpdateUI() {
        mVehicleAdapter.notifyDataSetChanged();
        swipeView.setRefreshing(false);
    }


    private class VehiculosAT extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls){
            URL CPurl = UrlUtils.createUrl(_baseVehiculo + mUser.getID());
            Log.d(TAG, CPurl.toString());
            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestGet(CPurl);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            //Toast.makeText(getActivity(), jsonResponse,
            //Toast.LENGTH_LONG).show();

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String CPinfoJSON){
            //aca lo convierto a un objeto json y verifico el codigo

            if (TextUtils.isEmpty(CPinfoJSON)) {
                //failmessage(1);
                Log.d(TAG, "Json vacio");
                return;
            }

            try {
                JSONArray vehiArray = new JSONArray(CPinfoJSON);
                Log.d(TAG, "se obtuviero los vehiculos de la db");
                for(int i=0; i<vehiArray.length(); i++){
                    JSONObject pool = vehiArray.getJSONObject(i);

                    int IDusuario = pool.getInt("idUsuario");
                    String Placa = pool.getString("placa");
                    String Modelo = pool.getString("modelo");
                    String Marca = pool.getString("marca");
                    String Color = pool.getString("color");
                    int Numasientos = pool.getInt("numAsientos");
                    boolean Estado = pool.getBoolean("estado");
                    String imageUrl = pool.getString("img");
                    if (!imageUrl.startsWith("http://")) { // Really hardcoded, should use Uri checks
                        imageUrl = "http://".concat(imageUrl);
                    }
                    int IDvehiculo = pool.getInt("idVehiculo");

                    Vehiculo auxVehiculo = new Vehiculo(
                            Integer.toString(IDvehiculo),
                            Placa,
                            Modelo,
                            Marca,
                            Color,
                            Numasientos,
                            imageUrl
                    );
                    Log.d(TAG, imageUrl);
                    lstVehi.add(auxVehiculo);
                }

                UpdateUI();
                return;
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)  {
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

    /*private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }*/

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
                Bitmap dstBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
                prueba.setImageBitmap(dstBitmap);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void launchCamera() {
        Intent intent  = new Intent  (MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null) {

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

                        launchCamera();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        //getActivity().setTitle("Menu 1");   <----Johnny bien huevon eres no?
        ImageView addCar = (ImageView) view.findViewById(R.id.btnAgregarVehiculo);
        addCar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_addcar);
                dialog.setTitle("Perfil");
                dialog.show();
                final AppCompatButton btnAdd = (AppCompatButton)dialog.findViewById(R.id.btn_add_car);
                final ImageButton btnAddPhoto = (ImageButton)dialog.findViewById(R.id.btn_vehicle_select_photo);

                /*Carga la imagen para mostrartla es una prueba*/
                prueba = dialog.findViewById(R.id.vehicle_img_preview);

                /*To pick car image*/
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
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {

                                requestPermissions(
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_CAMERA);
                            }else {
                                launchCamera();
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

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textInputplaca = dialog.findViewById(R.id.txt_input_plate);
                        textInputmodelo = dialog.findViewById(R.id.txt_input_model);
                        textInputmarca = dialog.findViewById(R.id.txt_input_brand);
                        textInputcolor = dialog.findViewById(R.id.txt_input_color);
                        textInputnasientos = dialog.findViewById(R.id.txt_input_seat_number);

                        txtPlaca = textInputplaca.getEditText();
                        txtModelo = textInputmodelo.getEditText();
                        txtMarca = textInputmarca.getEditText();
                        txtColor = textInputcolor.getEditText();
                        txtNasientos = textInputnasientos.getEditText();

                        if (validate()) {
                            AsyncTask<Void, Void, Void> addVehicle = new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    saveVehicle();
                                    return null;
                                }
                            };
                            addVehicle.execute();
                            dialog.dismiss();
                        }
                    }
                });
            }
            });
    }

    private boolean validate() {
        boolean valid = true;

        String placa;
        String modelo;
        String marca;
        String color;
        String nasientos;

        if (txtPlaca.length() == 0) {
            txtPlaca.setError("Ingrese una placa");
            valid = false;
        } else {
            placa = txtPlaca.getText().toString();
            if (placa.length() != 7){// || placa.indexOf("-") != 3) { // a veces el - no esta en el 3
                txtPlaca.setError("Placa invalida");
                valid = false;
            } else {
                txtPlaca.setError(null);
            }
        }

        if (txtModelo.length() == 0) {
            txtModelo.setError("Ingrese un modelo");
            valid = false;
        } else {
            modelo = txtModelo.getText().toString();
            if (modelo.isEmpty() || modelo.length() < 2 || modelo.length() > 15) {
                txtModelo.setError("Modelo invalido");
                valid = false;
            } else {
                txtModelo.setError(null);
            }
        }

        if (txtMarca.length() == 0) {
            txtMarca.setError("Ingrese un modelo");
            valid = false;
        } else {
            marca = txtMarca.getText().toString();
            if (marca.isEmpty() || marca.length() < 2 || marca.length() > 15) {
                txtMarca.setError("Marca invalida");
                valid = false;
            } else {
                txtMarca.setError(null);
            }
        }


        if (txtColor.length() == 0) {
            txtColor.setError("Ingrese un modelo");
            valid = false;
        } else {
            color = txtColor.getText().toString();
            if (color.isEmpty() || color.length() < 2 || color.length() > 15) {
                txtColor.setError("Color invalido");
                valid = false;
            } else {
                txtColor.setError(null);
            }
        }


        if (txtNasientos.length() == 0) {
            txtNasientos.setError("Ingrese un valor");
            valid = false;
        } else {
            nasientos = txtNasientos.getText().toString();
            if (nasientos.isEmpty()) {
                txtNasientos.setError("Numero asientos invalido");
                valid = false;
            } else {
                txtNasientos.setError(null);
            }
        }

        if (prueba == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Debes elegir una foto, nunca deberia salir este dialog");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return valid;
    }

    private void saveVehicle() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idUsuario", Integer.parseInt(mUser.getID()));
            jsonObject.put("placa", txtPlaca.getText());
            jsonObject.put("modelo", txtModelo.getText());
            jsonObject.put("marca", txtMarca.getText());
            jsonObject.put("color", txtColor.getText());
            jsonObject.put("nasientos", Integer.parseInt(txtNasientos.getText().toString()));
            // This tends to take a long time
            jsonObject.put("img", BitmapUtils.bitmapToBase64(bitmap));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String BaseURL = "http://200.16.7.170/api/vehiculos/agregar_vehiculo";
        URL url = null;
        try {
            url = new URL(BaseURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return;
        }

        final Vehiculo newVehicle = deserializeVehicle(jsonObject);
        if (newVehicle == null) {
            // Esto no debería pasar
            Log.e(TAG, "Error al procesar el nuevo vehículo");
            return;
        }
        String message = jsonObject.toString();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                message
        );
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d(TAG, e.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                getActivity(),
                                "Ocurrió un error. Intente nuevamente",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, response.body().string());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (photoURI != null) {
                            newVehicle.setUrlPic(photoURI.toString());
                        }
                        lstVehi.add(newVehicle);
                        UpdateUI();
                        Toast.makeText(
                                getActivity(),
                                "Vehículo agregado con éxito",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });
    }

    private Vehiculo deserializeVehicle(JSONObject vehicleData) {
        try {
            String Placa = vehicleData.getString("placa");
            String Modelo = vehicleData.getString("modelo");
            String Marca = vehicleData.getString("marca");
            String Color = vehicleData.getString("color");
            int Numasientos = vehicleData.getInt("nasientos");
            String imageUrl = vehicleData.getString("img");

            if (!imageUrl.startsWith("http://")) { // Really hardcoded, should use Uri checks
                imageUrl = "http://".concat(imageUrl);
            }
            int IDvehiculo = -1;
            if (vehicleData.has("idVehiculo")) {
                IDvehiculo = vehicleData.getInt("idVehiculo");
            }

            Vehiculo vehicle = new Vehiculo(
                    Integer.toString(IDvehiculo),
                    Placa,
                    Modelo,
                    Marca,
                    Color,
                    Numasientos,
                    imageUrl
            );
            return vehicle;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
