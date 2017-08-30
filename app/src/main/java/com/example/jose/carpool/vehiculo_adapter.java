package com.example.jose.carpool;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by Johnny on 29/08/2017.
 */

public class vehiculo_adapter extends ArrayAdapter<Vehiculo> {

    public vehiculo_adapter (Context context, ArrayList<Vehiculo> vehiculos){
        super(context, 0, vehiculos);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.vehiculo_item, parent, false);
        }

        Vehiculo curCP = getItem(position);

        TextView placa = (TextView) listItem.findViewById(R.id.lblPlaca);
        placa.setText(curCP.getPlaca());

        TextView modelo = (TextView) listItem.findViewById(R.id.lblModelo);
        modelo.setText(curCP.getModelo());

        TextView marca = (TextView) listItem.findViewById(R.id.lblMarca);
        marca.setText(curCP.getMarca());



        return listItem;
    }


}
