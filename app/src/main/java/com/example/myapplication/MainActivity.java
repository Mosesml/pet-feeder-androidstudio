package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Base URL for the server
    String server_url = "";

    // Button for triggering the HTTP request
    private Button buttonFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the button
        buttonFeed = findViewById(R.id.buttonFeed);

        // Set up a click listener for the button
        buttonFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log message indicating that the button is clicked and a request is being sent
                Log.d("tag", "Button clicked. Sending request.");

                // Execute AsyncTask to perform the HTTP request in the background
                new ParseTask().execute("/move/?direction=feed");
            }
        });
    }

    // AsyncTask for performing network operations in the background
    private class ParseTask extends AsyncTask<String, Void, Void> {

        // Variables for handling HTTP connection and response
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        String data;

        @Override
        protected Void doInBackground(String... params) {
            try {
                // Extract the parameter (URL path) from the AsyncTask input
                data = params[0];

                // Log message indicating the URL path being processed
                Log.d("tag", "doInBackground: " + data);

                // Construct the full URL by appending the URL path to the base URL
                String site_url_json = server_url + data;
                Log.d("tag", "doInBackground: " + site_url_json);

                // Create a URL object from the constructed URL
                URL url = new URL(site_url_json);

                // Open an HTTP connection to the URL
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the response from the input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                // Read each line of the response and append it to the buffer
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                // Convert the buffer to a string (the result JSON)
                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Disconnect the HTTP connection and close the BufferedReader in the finally block
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
