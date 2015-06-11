package appewtc.masterung.pbrurestaurant;

import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OrderListView extends AppCompatActivity {

    //Explicit
    private TextView officerTextView;
    private Spinner deskSpinner;
    private ListView foodListView;
    private String officerString, deskString, foodString, itemString;
    private FoodTABLE objFoodTABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_view);

        objFoodTABLE = new FoodTABLE(this);

        //Bind Widget
        bindWidget();

        //Show Officer
        showOfficer();

        //Create Spinner
        createSpinner();

        //Syn JSON to foodTABLE
        synJSONtoSQLte();



    }   // onCreate

    private void synJSONtoSQLte() {

        //Setup Policy
        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(myPolicy);
        }


        InputStream objInputStream = null;
        String strJSON = "";

        //1. Create InputStream
        try {

            HttpClient objHttpClient = new DefaultHttpClient();
            HttpPost objHttpPost = new HttpPost("http://www.applesguesthouse.com.203.151.157.79.no-domain.name/pbru/get_data_food.php");
            //HttpPost objHttpPost = new HttpPost("http://10.0.3.2:8888/get_data_master.php");
            HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
            HttpEntity objHttpEntity = objHttpResponse.getEntity();
            objInputStream = objHttpEntity.getContent();

        } catch (Exception e) {
            Log.d("pbru", "InputStream ==> " + e.toString());
        }



        //2. Create strJSON
        try {

            BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
            StringBuilder objStringBuilder = new StringBuilder();
            String strLine = null;

            while ((strLine = objBufferedReader.readLine()) != null) {

                objStringBuilder.append(strLine);

            }   // while

            objInputStream.close();
            strJSON = objStringBuilder.toString();

        } catch (Exception e) {
            Log.d("pbru", "strJSON ==> " + e.toString());
        }


        //3. Update to SQLite
        try {

            final JSONArray objJsonArray = new JSONArray(strJSON);
            for (int i = 0; i < objJsonArray.length(); i++) {

                JSONObject objJSONObject = objJsonArray.getJSONObject(i);
                String strFood = objJSONObject.getString("Food");
                String strPrice = objJSONObject.getString("Price");

                objFoodTABLE.addFood(strFood, strPrice);

            }   // for

        } catch (Exception e) {
            Log.d("pbru", "Update ==> " + e.toString());
        }


    }   // synJSON

    private void createSpinner() {

        final String showDesk[] = getResources().getStringArray(R.array.desk);
        ArrayAdapter<String> deskAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, showDesk);
        deskSpinner.setAdapter(deskAdapter);

        deskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deskString = showDesk[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                deskString = showDesk[0];
            }
        });

    }   // createSpinner

    private void showOfficer() {

        officerString = getIntent().getExtras().getString("Officer");
        officerTextView.setText(officerString);

    }   //showOfficer

    private void bindWidget() {

        officerTextView = (TextView) findViewById(R.id.txtShowOfficer);
        deskSpinner = (Spinner) findViewById(R.id.spinner);
        foodListView = (ListView) findViewById(R.id.listView);

    }   // bindWidget

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}   // Main Class
