package me.rossbower.suggesterapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import me.rossbower.suggesterapp.data.Place;
import me.rossbower.suggesterapp.fragments.ListFragment;

public class EditActivity extends AppCompatActivity {

    public static final String KEY_PLACE = "KEY_PLACE";

    private Place placeToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (getIntent() != null
                && getIntent().hasExtra(ListFragment.KEY_PLACE_TO_EDIT)) {
            placeToEdit = (Place) getIntent().getSerializableExtra(ListFragment.KEY_PLACE_TO_EDIT);

            if (placeToEdit.getType() == null) {
                spinner.setSelection(0);
            }
            else if (placeToEdit.getType().equals(getString(R.string.food))) {
                spinner.setSelection(0);
            }
            else if (placeToEdit.getType().equals(getString(R.string.activities))) {
                spinner.setSelection(1);
            }
        }

        Button btnSave = (Button) findViewById(R.id.btnSavePlace);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();

                placeToEdit.setType(spinner.getSelectedItem().toString());

                result.putExtra(KEY_PLACE, placeToEdit);

                setResult(RESULT_OK, result);
                finish();
            }
        });

    }
}