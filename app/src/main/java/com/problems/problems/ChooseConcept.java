package com.problems.problems;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ChooseConcept extends Activity {
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_concept);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Defined Array values to show in ListView
        String[] values = new String[] {
                "Big O Notation",
                "Element Mass",
                "Biology Taxonomy"
        };

        final String[] conceptCodes = {
                "BigONotation",
                "ElementMass",
                "BiologyTaxonomy"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                Intent conceptIntent = new Intent(ChooseConcept.this, MainActivity.class);
                conceptIntent.putExtra("conceptCode", conceptCodes[itemPosition]);
                startActivity(conceptIntent);
                //Log.i("conceptCode",conceptCodes[itemPosition]);

            }

        });
    }

}