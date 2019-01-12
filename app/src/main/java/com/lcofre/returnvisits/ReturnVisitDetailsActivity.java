package com.lcofre.returnvisits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ReturnVisitDetailsActivity extends AppCompatActivity {
    private ReturnVisit returnVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_visit_details);


        returnVisit = (ReturnVisit) getIntent().getExtras().getSerializable(ReturnVisit.CLASS_ID);
        updateInterface();

        Button button = (Button) findViewById(R.id.updateButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMarkerData();

                Intent returnIntent = new Intent();
                returnIntent.putExtra(ReturnVisit.CLASS_ID, returnVisit);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void updateInterface() {
        setEditText(R.id.nameText, returnVisit.name);
        setEditText(R.id.addressText, returnVisit.address);
        setEditText(R.id.otherDetailsText, returnVisit.details);
    }

    private void updateMarkerData() {
        returnVisit.name = getEditText(R.id.nameText);
        returnVisit.address = getEditText(R.id.addressText);
        returnVisit.details = getEditText(R.id.otherDetailsText);
    }

    private void setEditText(int editTextId, String title) {
        EditText editText = (EditText) findViewById(editTextId);
        editText.setText(title);
    }

    private String getEditText(int editTextId) {
        EditText editText = (EditText) findViewById(editTextId);

        return editText.getText().toString();
    }
}