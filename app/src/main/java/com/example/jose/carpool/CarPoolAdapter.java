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
 * Created by jose on 8/28/17.
 */

public class CarPoolAdapter extends ArrayAdapter<CarPool> {


    public CarPoolAdapter(Context context, ArrayList<CarPool> carpools){
        super(context, 0, carpools);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.pool_item, parent, false);
        }

        CarPool curCP = getItem(position);

        TextView costTV = (TextView) listItem.findViewById(R.id.Costo);
        costTV.setText("S/"+curCP.getCosto());

        TextView origTV = (TextView) listItem.findViewById(R.id.Origen);
        origTV.setText(curCP.getNomOrigen()+", "+curCP.getDistOrigen());

        TextView destTV = (TextView) listItem.findViewById(R.id.Destino);
        destTV.setText(curCP.getNomDestino()+", "+curCP.getDistDestino());

        TextView horaTV = (TextView) listItem.findViewById(R.id.Hora);
        horaTV.setText(curCP.getHsalida());


        return listItem;
    }
}
