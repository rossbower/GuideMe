package me.rossbower.suggesterapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import me.rossbower.suggesterapp.EditInterface;
import me.rossbower.suggesterapp.MainActivity;
import me.rossbower.suggesterapp.R;

import java.util.List;

import me.rossbower.suggesterapp.data.Place;
import me.rossbower.suggesterapp.fragments.MapFragment;

public class PlaceRecyclerAdapter extends
        RecyclerView.Adapter<PlaceRecyclerAdapter.ViewHolder>
        implements PlaceTouchHelperAdapter {

    public List<Place> placeList;
    private EditInterface editInterface;
    private Context context;

    public PlaceRecyclerAdapter(EditInterface editInterface) {
        this.editInterface = editInterface;

        placeList = Place.listAll(Place.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cityRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.place_row,parent,false);
        context = parent.getContext();

        return new ViewHolder(cityRow);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tvCity.setText(placeList.get(position).getPlaceName());
        holder.tvType.setText(placeList.get(position).getType());

        holder.icDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                placeList.get(pos).delete();
                placeList.remove(pos);
                notifyItemRemoved(pos);

                MapFragment mapFragment = (MapFragment) ((AppCompatActivity)context).getSupportFragmentManager().findFragmentByTag(
                        "android:switcher:"+R.id.pager+":1"
                );

                mapFragment.updateMap();
            }
        });


        holder.viewRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editInterface.showEditDialog(
                        placeList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        placeList.get(position).delete();
        placeList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        placeList.add(toPosition, placeList.get(fromPosition));
        placeList.remove(fromPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvCity;
        private ImageView icDelete;
        private TextView tvType;
        private View viewRow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCity = (TextView) itemView.findViewById(R.id.tvPlace);
            viewRow = (View) itemView.findViewById(R.id.row);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            icDelete = (ImageView) itemView.findViewById(R.id.icDelete);
        }
    }

    public void addPlace(Place place) {
        place.save();
        placeList.add(0, place);

        notifyItemInserted(0);

        MapFragment mapFragment = (MapFragment) ((AppCompatActivity)context).getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":1"
        );

        LatLng latlng = new LatLng(place.getLatitude(), place.getLongitude());
        mapFragment.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
    }

    public void edit(Place place, int position) {
        place.save();
        placeList.set(position, place);
        notifyItemChanged(position);
    }


    public Place getPlace(int position) {
        return placeList.get(position);
    }

    public List<Place> getPlaceList() {
        return placeList;
    }

    public void deleteAllItems() {
        Place.deleteAll(Place.class);
        placeList.removeAll(placeList);
        notifyDataSetChanged();

    }

}