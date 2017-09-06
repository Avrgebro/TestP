package com.example.jose.carpool;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Johnny on 29/08/2017.
 */

public class vehiculo_adapter extends RecyclerView.Adapter<vehiculo_adapter.VehicleViewHolder> {

    private Context mContext;
    private List<Vehiculo> mVehicleList;
    private OnItemLongClickListener mLongClickListener;

    public vehiculo_adapter(Context context, List<Vehiculo> vehicles) {
        mContext = context;
        this.setVehicleList(vehicles);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position);
    }

    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.vehiculo_item,
                parent,
                false
        );
        return new VehicleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, int position) {
        if (mVehicleList == null || mVehicleList.isEmpty()) {
            return;
        }
        Vehiculo vehicle = mVehicleList.get(position);
        holder.bind(vehicle, position);
    }

    @Override
    public int getItemCount() {
        if (mVehicleList == null) {
            return 0;
        }
        return mVehicleList.size();
    }

    public List<Vehiculo> getVehicleList() {
        return mVehicleList;
    }

    public void setVehicleList(List<Vehiculo> mVehicleList) {
        this.mVehicleList = mVehicleList;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    public class VehicleViewHolder
            extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private static final int DEFAULT_POSITION = -1;
        ImageView carImage;
        TextView brandText;
        TextView modelText;
        TextView plateText;

        int mPosition;

        public VehicleViewHolder(View itemView) {
            super(itemView);
            mPosition = DEFAULT_POSITION;

            if (itemView != null) {
                itemView.setOnLongClickListener(this);
                carImage = itemView.findViewById(R.id.item_car_image);
                brandText = itemView.findViewById(R.id.lblMarca);
                modelText = itemView.findViewById(R.id.lblModelo);
                plateText = itemView.findViewById(R.id.lblPlaca);
            }
        }

        public void bind(Vehiculo vehicle, int position) {
            mPosition = position;

            plateText.setText(vehicle.getPlaca());

            modelText.setText(vehicle.getModelo());

            brandText.setText(vehicle.getMarca());

            String imageUrl = vehicle.getUrlPic();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                (new Picasso.Builder(mContext)).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        exception.printStackTrace();
                        Log.e("vehiculo_adapter", uri.toString());
                    }
                }).build()
                        .load(imageUrl)
                        .placeholder(R.drawable.pucp_logo)
                        .fit()
                        .into(carImage);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener == null || mPosition == DEFAULT_POSITION) {
                return false;
            }
            return mLongClickListener.onItemLongClick(mPosition);
        }
    }
}
