package me.rossbower.suggesterapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.rossbower.suggesterapp.EditActivity;
import me.rossbower.suggesterapp.EditInterface;
import me.rossbower.suggesterapp.MainActivity;
import me.rossbower.suggesterapp.R;
import me.rossbower.suggesterapp.adapter.PlaceItemTouchHelperCallback;
import me.rossbower.suggesterapp.adapter.PlaceRecyclerAdapter;
import me.rossbower.suggesterapp.data.Place;

public class ListFragment extends Fragment implements EditInterface {

    public PlaceRecyclerAdapter placeRecyclerAdapter;
    public static final String KEY_PLACE_TO_EDIT = "KEY_PLACE_TO_EDIT";
    public static final int REQUEST_CODE_EDIT = 102;
    private RecyclerView recyclerPlace;
    private int positionToEdit = -1;
    private Long idToEdit = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, null);

        recyclerPlace = (RecyclerView) v.findViewById(R.id.recyclerPlace);
        recyclerPlace.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity());
        recyclerPlace.setLayoutManager(mLayoutManager);

        placeRecyclerAdapter = new PlaceRecyclerAdapter(this);

        ItemTouchHelper.Callback callback = new PlaceItemTouchHelperCallback(placeRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerPlace);

        recyclerPlace.setAdapter(placeRecyclerAdapter);

        return v;
    }

    @Override
    public void showEditDialog(Place placeToEdit, int position) {
        positionToEdit = position;
        idToEdit = placeToEdit.getId();
        Intent intentShowEdit = new Intent();
        intentShowEdit.setClass(getActivity(), EditActivity.class);
        intentShowEdit.putExtra(KEY_PLACE_TO_EDIT,placeToEdit);
        startActivityForResult(intentShowEdit, REQUEST_CODE_EDIT);
    }

    public void addPlaceToRecycler(com.google.android.gms.location.places.Place placeObj){
        Place place = new Place(placeObj);
        placeRecyclerAdapter.addPlace(place);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_EDIT && data != null &&
                    data.hasExtra(EditActivity.KEY_PLACE)) {

                me.rossbower.suggesterapp.data.Place changedPlace = (me.rossbower.suggesterapp.data.Place) data.getSerializableExtra(
                        EditActivity.KEY_PLACE);
                changedPlace.setId(idToEdit);

                placeRecyclerAdapter.edit(changedPlace, positionToEdit);
            }
    }

    public Place getPlaceFromAdapter(int position) {
        return placeRecyclerAdapter.getPlace(position);
    }

    public List<Place> getPlaceList() {
        return placeRecyclerAdapter.getPlaceList();
    }

}