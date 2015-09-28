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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import guinovart.joaquim.lafosca_beach.Models.Beach;
import guinovart.joaquim.lafosca_beach.Utilities.KidsAdapter;
import guinovart.joaquim.lafosca_beach.Utilities.beachAsyncT;


public class BeachActivity extends ActionBarActivity implements OnTaskCompleted {

    String baseUrl = "http://lafosca-beach.herokuapp.com/api/v1";

    //Views for the Beach
    TextView stateVW;
    TextView flagTW;
    TextView happinessTW;
    TextView dirtinessTW;
    TextView kidsTW;

    //Views for ListView
    ListView list;
    KidsAdapter adapter;
    EditText inputSearch;

    //Buttons
    Button openBttn;
    Button cleanBttn;
    Button niveaBttn;
    Button flagBttn;

    //Beach object
    Beach beach;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        //inject views to activity
        stateVW = (TextView) findViewById(R.id.statetTV);
        flagTW = (TextView) findViewById(R.id.flagTV);
        happinessTW = (TextView) findViewById(R.id.happinessTV);
        dirtinessTW = (TextView) findViewById(R.id.dirtinessTV);
        kidsTW = (TextView) findViewById(R.id.kidsTV);

        //listview injection to activity
        list = (ListView) findViewById(R.id.kid_list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        //buttons injection to activity
        openBttn = (Button) findViewById(R.id.close_button);
        cleanBttn = (Button) findViewById(R.id.clean_button);
        niveaBttn = (Button) findViewById(R.id.nivea_button);
        flagBttn = (Button) findViewById(R.id.flag_button);

        //getting the token from SahredPrefrences
        String auth_token_string = getToken();

        //if there's no token we come back to login/sigin MainActivity
        if(auth_token_string.equals("")){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        //otherwise we start asynctask to get Beach object from API
        }else{
            beachAsyncT GSTTask = new beachAsyncT(auth_token_string,this,this,"state");
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

    //same OnClick method for Open/Close/Nivea
    public void CloseCleanNivea (View v){
        String text = ((Button)v).getText().toString();
        String token = getToken();
        beachAsyncT ATask = null;
        String url = null;
        //case Button text is OPEN, correspondent constructor with "open" tag
        if(text.equalsIgnoreCase("OPEN")){
            ATask = new beachAsyncT(token,this,this,"open");
            url = baseUrl+"/open";
        //case Button text is CLOSE, correspondent constructor with "close" tag
        }else if(text.equalsIgnoreCase("CLOSE")){
            ATask = new beachAsyncT(token,this,this,"close");
            url = baseUrl+"/close";
        //case Button text is CLEAN, correspondent constructor with "clean" tag
        }else if(text.equalsIgnoreCase("CLEAN")){
            ATask = new beachAsyncT(token,this,this,"clean");
            url = baseUrl+"/clean";
        //case Button text is NIVEA, correspondent constructor with "nivea" tag
        }else if(text.equalsIgnoreCase("NIVEA")){
            ATask = new beachAsyncT(token,this,this,"nivea");
            url = baseUrl+"/nivea-rain";
        }
        //execute Asyn
        ATask.execute(url);
    }

    //method onClick to change Flag
    public void changeFlag(View V){
        //Alert List Dialog to choose flag colour
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose flag colour");
        builder.setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String token = getToken();
                beachAsyncT ATask = new beachAsyncT(token,BeachActivity.this,BeachActivity.this,"flag",item);
                String url = null;
                url = baseUrl+"/flag";
                ATask.execute(url);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    //method to getToken from SharedPreferences
    public String getToken (){
        String token = null;
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        token = settings.getString("token", "");
        return token;
    }

    //modify UI with Beach object values when this is open
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
    //modify UI with Beach object values when this is open
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

    //set adapter to listView and ontextchanged to launch filter
    public void getKids(Beach beach){
        adapter = new KidsAdapter(this,beach.kids);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                BeachActivity.this.adapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });
        list.setAdapter(adapter);
    }

    //listener for AsyncTask completed with success
    //update UI
    @Override
    public void onTaskCompleted(Beach beach) {
        this.beach=beach;
        if (beach.state.equals("open")) {
            openBeach(beach);
        } else {
            closeBeach(beach);
        }
    }

    //empty method, we're not using this in this activity
    @Override
    public void onTaskCompleted(String token) {

    }
}
