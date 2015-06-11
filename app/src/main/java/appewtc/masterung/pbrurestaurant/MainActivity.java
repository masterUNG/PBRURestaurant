package appewtc.masterung.pbrurestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {

    //Explicit
    private UserTABLE objUserTABLE;
    private FoodTABLE objFoodTABLE;
    private EditText userEditText, passwordEditText;
    private String userString, passwordString, truePasswordString, nameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        bindWidget();

        //Request Database
        objUserTABLE = new UserTABLE(this);
        objFoodTABLE = new FoodTABLE(this);

        //Tester Add Value
        //testAddValue();

        //Delete All Data
        deleteAllData();

        //Synchronize JSON to SQLite
        synJSONtoSQLite();

    }   // onCreate

    public void clickLogin(View view) {

        userString = userEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();

        if (userString.equals("") || passwordString.equals("") ) {

            //Show Negative Dialog
            negDialog("มีช่องว่าง", "กรุณากรอกทุกช่องคะ");

        } else {

            //Check User
            checkUser();

        }

    }   // clickLogin

    private void checkUser() {

        try {

            String myResult[] = objUserTABLE.searchUser(userString);
            truePasswordString = myResult[2];
            nameString = myResult[3];

            Log.d("pbru", "Welcome ==> " + nameString);

            //Check Password
            checkPassword();

        } catch (Exception e) {
            negDialog("ไม่มี User", "ไม่มี " + userString + " บนฐานข้อมูล");
        }

    }   // checkUser

    private void checkPassword() {

        if (passwordString.equals(truePasswordString)) {

            welcome();

        } else {
            negDialog("Password False", "Please Try Again Password False");
        }

    }   // checkPassword

    private void welcome() {

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.restaurant);
        objBuilder.setTitle("ยินดีต้อนรับ");
        objBuilder.setMessage("ยินดีต้อนรับ คุณ " + nameString + "\n" + "สู่ร้านของเรา");
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("ขอบคุณ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                Intent objIntent = new Intent(MainActivity.this, OrderListView.class);
                objIntent.putExtra("Officer", nameString);
                startActivity(objIntent);
                finish();

            }
        });
        objBuilder.show();
    }

    private void negDialog(String strTitle, String strMessage) {

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_question);
        objBuilder.setTitle(strTitle);
        objBuilder.setMessage(strMessage);
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                userEditText.setText("");
                passwordEditText.setText("");
            }
        });
        objBuilder.show();


    }   // negDialog


    private void bindWidget() {
        userEditText = (EditText) findViewById(R.id.edtUser);
        passwordEditText = (EditText) findViewById(R.id.edtPassword);
    }

    private void deleteAllData() {

        SQLiteDatabase objDatabase = openOrCreateDatabase("pbru.db", MODE_PRIVATE, null);
        objDatabase.delete("userTABLE", null, null);
        objDatabase.delete("foodTABLE", null, null);

    }


    private void synJSONtoSQLite() {

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
            HttpPost objHttpPost = new HttpPost("http://www.applesguesthouse.com.203.151.157.79.no-domain.name/pbru/get_data_master.php");
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
                String strUser = objJSONObject.getString("User");
                String strPassword = objJSONObject.getString("Password");
                String strName = objJSONObject.getString("Name");

                objUserTABLE.addNewUser(strUser, strPassword, strName);

            }   // for

        } catch (Exception e) {
            Log.d("pbru", "Update ==> " + e.toString());
        }



    }   //synJSONtoSQLite

    private void testAddValue() {

        objUserTABLE.addNewUser("testUser", "testPass", "ชื่อภาษาไทย");
        objFoodTABLE.addFood("ผัดกะเพรา", "65");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
