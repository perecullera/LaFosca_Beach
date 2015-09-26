package guinovart.joaquim.lafosca_beach;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import guinovart.joaquim.lafosca_beach.Models.Beach;
import guinovart.joaquim.lafosca_beach.Utilities.KidsAdapter;
import guinovart.joaquim.lafosca_beach.Utilities.putPostToken;


public class BeachActivity extends ActionBarActivity implements OnTaskCompleted {

    String baseUrl = "http://lafosca-beach.herokuapp.com/api/v1";

    TextView stateVW;
    TextView flagTW;
    TextView happinessTW;
    TextView dirtinessTW;
    TextView kidsTW;

    ListView list;
    KidsAdapter adapter;
    EditText inputSearch;


    Button openBttn;
    Button cleanBttn;
    Button niveaBttn;
    Button flagBttn;

    Beach beach;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        stateVW = (TextView) findViewById(R.id.statetTV);
        flagTW = (TextView) findViewById(R.id.flagTV);
        happinessTW = (TextView) findViewById(R.id.happinessTV);
        dirtinessTW = (TextView) findViewById(R.id.dirtinessTV);
        kidsTW = (TextView) findViewById(R.id.kidsTV);

        list = (ListView) findViewById(R.id.kid_list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        openBttn = (Button) findViewById(R.id.close_button);
        cleanBttn = (Button) findViewById(R.id.clean_button);
        niveaBttn = (Button) findViewById(R.id.nivea_button);
        flagBttn = (Button) findViewById(R.id.flag_button);


        String auth_token_string = getToken();

        if(auth_token_string.equals("")){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }else{
            putPostToken GSTTask = new putPostToken(auth_token_string,this,this,"state");
            GSTTask.execute(baseUrl+"/state");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beach, menu);
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
    public void Clean(View v){

    }
    public void CloseCleanNivea (View v){
        //TextView textV = (TextView) v;
        String text = ((Button)v).getText().toString();
        String token = getToken();
        putPostToken PCTTask = null;
        String url = null;
        if(text.equalsIgnoreCase("OPEN")){
            PCTTask = new putPostToken(token,this,this,"open");
            url = baseUrl+"/open";

        }else if(text.equalsIgnoreCase("CLOSE")){
            PCTTask = new putPostToken(token,this,this,"close");
            url = baseUrl+"/close";

        }else if(text.equalsIgnoreCase("CLEAN")){
            PCTTask = new putPostToken(token,this,this,"clean");
            url = baseUrl+"/clean";
        }else if(text.equalsIgnoreCase("NIVEA")){
            PCTTask = new putPostToken(token,this,this,"nivea");
            url = baseUrl+"/nivea-rain";
        }
        PCTTask.execute(url);
    }
    public void changeFlag(View V){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose flag colour");
        builder.setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String token = getToken();
                putPostToken PCTTask = new putPostToken(token,BeachActivity.this,BeachActivity.this,"flag",item);
                String url = null;
                url = baseUrl+"/flag";
                PCTTask.execute(url);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    public String getToken (){
        String token = null;

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        token = settings.getString("token", "");
        Log.d("token",token);

        return token;
    }
    public void openBeach(Beach beach){
        stateVW.setText(beach.state);
        stateVW.setVisibility(View.VISIBLE);
        flagTW.setText(Integer.toString(beach.flag));
        if(beach.flag==0){
            flagTW.setBackgroundColor(Color.GREEN);
        }else if (beach.flag==1){
            flagTW.setBackgroundColor(Color.YELLOW);
        }else if (beach.flag==2){
            flagTW.setBackgroundColor(Color.RED);
        }
        flagTW.setVisibility(View.VISIBLE);
        kidsTW.setText("Kids");
        kidsTW.setVisibility(View.VISIBLE);
        getKids(beach);
        list.setVisibility(View.VISIBLE);
        inputSearch.setVisibility(View.VISIBLE);
        happinessTW.setText("Happiness: " + Integer.toString(beach.happiness));
        happinessTW.setVisibility(View.VISIBLE);
        dirtinessTW.setText("Dirtiness: " + Integer.toString(beach.dirtiness));
        dirtinessTW.setVisibility(View.VISIBLE);
        openBttn.setText("close");
        cleanBttn.setVisibility(View.GONE);
        niveaBttn.setVisibility(View.VISIBLE);
    }
    public void closeBeach(Beach beach){
        stateVW.setText(beach.state);
        flagTW.setVisibility(View.GONE);
        happinessTW.setVisibility(View.GONE);
        dirtinessTW.setVisibility(View.GONE);
        kidsTW.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        inputSearch.setVisibility(View.GONE);
        openBttn.setText("OPEN");
        cleanBttn.setVisibility(View.VISIBLE);
        niveaBttn.setVisibility(View.GONE);
    }
    public void getKids(Beach beach){
        adapter = new KidsAdapter(this,beach.kids);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                //adapter.getFilter().filter(arg0.toString());
                BeachActivity.this.adapter.getFilter().filter(arg0);

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                //BeachActivity.this.adapter.getFilter().filter(arg0);
            }
        });
        list.setAdapter(adapter);
    }
    @Override
    public void onTaskCompleted(Beach beach) {
        this.beach=beach;
        if (beach.state.equals("open")) {
            openBeach(beach);
        } else {
            closeBeach(beach);
        }
    }
}
