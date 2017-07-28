package com.example.jose.carpool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class activity_emailverification extends AppCompatActivity {

    @Bind(R.id.btn_signup) Button _signupbutton;
    @Bind(R.id.ver_code) EditText _vercode;

    private final int _covv = 123456;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailverification);
        ButterKnife.bind(this);


        _signupbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                verify();
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    public void verify(){

        //Integer.parseInt(_vercode.getText().toString())).
        String auxcode = _vercode.getText().toString();

        if(auxcode.isEmpty()){
            _vercode.setError("Campo vacio");
            return;
        }

        int codeinput = Integer.parseInt(auxcode);



        if(_covv == codeinput){

            //TODO: se envia el registro a la base de datos,
            //TODO: se quita infode shared preferences;
            //TODO: se guarda info de la sesion actual;
            setResult(RESULT_OK, null);
            finish();
        }
        else{
            _vercode.setError("Codigo invalido");
            return;
        }
    }
}
