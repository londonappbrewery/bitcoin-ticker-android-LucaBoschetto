package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    private final String TAG = "Bitcoin"; // For Log.d
    // Create the base URL and API key
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";
    private final String API_KEY = "YzBjOWJiMWUxYjFmNDhiYWI3MjAzN2EwYzdjY2RhOWM";

    // From apiv2.bitcoinaverage.com documentation:
    //
    // All of our request are get request and they must contain our x-ba-key header:
    //
    // Example: 'x-ba-key': 'NzI0MjM4Njc1NGQ3NDU4Mzg1NWU3YYmU4MTdiMaA'

    // GET request should be:
    //
    // GET https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC<OTHER>
    //
    // JSON result for the last ticker value is in 'last'

    // Member Variables:
    TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = findViewById(R.id.priceLabel);
        Spinner spinner = findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set an OnItemSelected listener on the spinner
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedSymbol;

                // Write "Searching..." instead of exchange rate
                mPriceTextView.setText(R.string.searching);

                selectedSymbol = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, selectedSymbol);
                letsDoSomeNetworking(BASE_URL + selectedSymbol);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "Nothing selected");
            }
        });

    }

    private void letsDoSomeNetworking(String url) {

        Log.d(TAG, "Entered letsDoSomeNetworking");

        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "client created");
        client.addHeader("x-ba-key", API_KEY);
        Log.d(TAG, "header added");
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "JSON: " + response.toString());
                TickerDataModel tickerData = TickerDataModel.fromJson(response);
                updateUI(tickerData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, "Call to onFailure(int, Header[], String, Throwable)");
                Log.d(TAG, "Request fail! Status code: " + statusCode);
                Log.d(TAG, "Fail response: " + s);
                Log.e(TAG, e.toString());
                mPriceTextView.setText(R.string.error);
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, "Call to onFailure(int, Header[], Throwable, JSONObject)");
                Log.d(TAG, "Request fail! Status code: " + statusCode);
                Log.d(TAG, "Fail response: " + response);
                Log.e(TAG, e.toString());
                mPriceTextView.setText(R.string.error);
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG, "async get started, with JsonHttpResponseHandler()");

    }

    private void updateUI(TickerDataModel tickerData) {

        mPriceTextView.setText((tickerData.getExchangeRate()));

    }


}
