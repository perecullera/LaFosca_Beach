package guinovart.joaquim.lafosca_beach;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import guinovart.joaquim.lafosca_beach.Models.Beach;
import guinovart.joaquim.lafosca_beach.Utilities.putPostToken;


public class MainActivity extends ActionBarActivity implements OnTaskCompleted{

    String baseUrl = "http://lafosca-beach.herokuapp.com/api/v1";

    EditText userTV;
    EditText pwdTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userTV = (EditText) findViewById(R.id.usuariTV);
        pwdTV = (EditText) findViewById(R.id.pwdTV);
    }

    public void signIn(View v) throws IOException, JSONException {
        String user = userTV.getText().toString();
        String pwd = pwdTV.getText().toString();

        if (!user.equals("") && !pwd.equals("")) {
            Log.d("User text ", String.valueOf(userTV.getText()));
            Log.d("PWD text ", String.valueOf(pwdTV.getText()));
            putPostToken GUBA = new putPostToken(MainActivity.this,
                    this, "signIn",user,pwd);
            String userUrl = baseUrl + "/users";
            GUBA.execute(userUrl);
        } else {
            Toast.makeText(this, "Please, add a user and a pwd", Toast.LENGTH_SHORT).show();
        }
        //return response;

    }

    public void logIn(View v) {
        String user = userTV.getText().toString();
        String pwd = pwdTV.getText().toString();
        if (!user.equals("") && !pwd.equals("")) {
            Log.d("User login text ", user);
            Log.d("PWD login text ", pwd);
            putPostToken GUBA = new putPostToken(MainActivity.this,
                    this, "logIn",user,pwd);
            String userUrl = baseUrl + "/user";
            GUBA.execute(userUrl);
        } else {
            Toast.makeText(this, "Please, add a user and a pwd", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onTaskCompleted(Beach beach) {

    }

    @Override
    public void onTaskCompleted(String token) {
        putToken(token);
        Intent i = new Intent(this,BeachActivity.class);
        this.startActivity(i);
    }

    private void putToken(String token) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", token);
        editor.commit();
    }
}
