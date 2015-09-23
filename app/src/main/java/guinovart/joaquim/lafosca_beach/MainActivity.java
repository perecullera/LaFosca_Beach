package guinovart.joaquim.lafosca_beach;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import guinovart.joaquim.lafosca_beach.Utilities.UserPostNoAuth;
import guinovart.joaquim.lafosca_beach.Utilities.getUserBAuth;


public class MainActivity extends ActionBarActivity {

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
            UserPostNoAuth USPNATask = new UserPostNoAuth(user, pwd, MainActivity.this);
            String userUrl = baseUrl + "/users";
            USPNATask.execute(userUrl);
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
            getUserBAuth GUBA = new getUserBAuth(user, pwd, MainActivity.this);
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
}
