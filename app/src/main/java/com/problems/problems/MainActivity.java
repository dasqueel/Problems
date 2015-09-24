package com.problems.problems;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity
{
    DragSortListView listView;
    ArrayAdapter<String> adapter;
    Button checkBtn;
    TextView questionTxt;
    String concept;
    //String concept = "BigONotation";

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
    {
        @Override
        public void drop(int from, int to)
        {
            if (from != to)
            {
                String item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener()
    {
        @Override
        public void remove(int which)
        {
            adapter.remove(adapter.getItem(which));
        }
    };

    protected int getItemLayout() {
        return R.layout.list_item;
    }

    public void displayQuestion(final String conceptCode) {
        String url = "http://52.24.226.232/khanGetProb?concept="+conceptCode;
        try {

            String res = new GetQuestion().execute(url).get();

            JSONObject json = new JSONObject(res);

            /* set the question */
            String question = json.getString("question");
            questionTxt.setText(question);

            /* set the drag sort list view */
            //turn json object to array with function
            JSONArray jsonChoices = json.getJSONArray("choices");
            String[] choices = new String[jsonChoices.length()];
            JSONArray jsonAnswer = json.getJSONArray("answer");
            final String[] answer = new String[jsonAnswer.length()];
            //turn array to list
            //answer array
            for (int i=0;i<jsonAnswer.length();i++){
                answer[i] = jsonAnswer.get(i).toString();
            }
            //choices array
            for (int i=0;i<jsonChoices.length();i++){
                choices[i] = jsonChoices.get(i).toString();
            }
            List<String> list = new ArrayList<String>(Arrays.asList(choices));
            adapter = new ArrayAdapter<String>(this, getItemLayout(), R.id.text, list);
            listView.setAdapter(adapter);

            listView.setDropListener(onDrop);
            listView.setRemoveListener(onRemove);

            DragSortController controller = new DragSortController(listView);
            controller.setDragHandleId(R.id.drag);
            controller.setRemoveEnabled(false);
            controller.setSortEnabled(true);
            controller.setDragInitMode(1);

            listView.setFloatViewManager(controller);
            listView.setOnTouchListener(controller);
            listView.setDragEnabled(true);

            checkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("clicked","yes");
                    String[] userAnswer = new String[listView.getAdapter().getCount()];
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        Object obj = listView.getAdapter().getItem(i);
                        userAnswer[i] = obj.toString();
                    }

                    if (Arrays.deepEquals(answer, userAnswer)) {
                        //Log.i("checkAnswer","yaaay");
                        Toast toast = Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_LONG);
                        View toastView = toast.getView();
                        toastView.setBackgroundColor(Color.GREEN);
                        toast.show();

                        displayQuestion(conceptCode);
                    }
                    else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_LONG);
                        View toastView = toast.getView();
                        toastView.setBackgroundColor(Color.RED);
                        toast.show();
                    }

                }
            });

        }
        catch (Exception e) {
            //Log.d("stuff", "nope");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //intent variables -- contest
        Intent intent = getIntent();
        String conceptCode = intent.getStringExtra("conceptCode");
        concept = conceptCode;

        listView = (DragSortListView) findViewById(R.id.list);
        checkBtn = (Button)findViewById(R.id.checkBtn);
        questionTxt = (TextView)findViewById(R.id.questionTxt);
        displayQuestion(conceptCode);

    }

    class GetQuestion extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            // Log.i("async", responseString);
            return responseString;
        }
    }
    /* implement shuffling later */
    // Implementing Fisher–Yates shuffle
    static void shuffleArray(int[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }


}
