package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import guinovart.joaquim.lafosca_beach.Models.Beach;
import guinovart.joaquim.lafosca_beach.Models.Kid;
import guinovart.joaquim.lafosca_beach.OnTaskCompleted;

/**
 * Created by perecullera on 22/9/15.
 */
public class beachAsyncT extends AsyncTask<String, Integer, String[]> {

    //Base url to API
    String baseUrl = "http://lafosca-beach.herokuapp.com/api/v1";

    //Atributs
    String token;
    Context context;
    //request method to API
    String method;
    //0:green 1:yellow 2:red.
    int flag;
    //username ans password passed in Constructor for sigIn
    String username;
    String password;

    //listener to pass Object to Activity
    private OnTaskCompleted listener;

    //constructor for post with no auth and BasicAuth
    public beachAsyncT(Context c, OnTaskCompleted listener, String method, String user, String pwd) {
        this.context = c;
        this.listener=listener;
        this.method = method;
        this.username = user;
        this.password = pwd;
    }
    //constructor for clean/state/open/close/nivea requests
    public beachAsyncT(String token, Context c, OnTaskCompleted listener, String method) {
        this.token = token;
        this.context = c;
        this.listener=listener;
        this.method = method;
    }
    //constructor for flah request
    public beachAsyncT(String token, Context c, OnTaskCompleted listener, String method, int flag) {
        this.token = token;
        this.context = c;
        this.listener=listener;
        this.method = method;
        this.flag = flag;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection conn;
        String [] result = new String[2];
        /* check if device is connected to Internet, if it isn't we don't do connection */
        if (checkConnectivity()) {
            try {

                //common  request properties
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                //put method and token for open/close
                if (method.equalsIgnoreCase("open")||method.equalsIgnoreCase("close")) {
                    putToken(conn);
                    conn.setRequestMethod("PUT");
                    conn.setDoInput(true);
                //put method and token and user-pwd header with constructor passed values
                }else if(method.equalsIgnoreCase("signIn")){
                    JSONObject cred   = new JSONObject();
                    cred.put("username",username);
                    cred.put("password", password);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    putRequestHeader(conn, cred);
                //put method and BasicAuth with user-pwd passed by constructor
                }else if(method.equalsIgnoreCase("logIn")){
                    final String basicAuth = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.DEFAULT);
                    conn.setRequestProperty("Authorization", basicAuth);
                //put method and token to clean/nivea cases
                } else if (method.equalsIgnoreCase("clean")||method.equalsIgnoreCase("nivea")) {
                    putToken(conn);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                //put method and token for state case
                }else if(method.equalsIgnoreCase("state")){
                    putToken(conn);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                //put method and token and flag header for flag case
                }else if(method.equalsIgnoreCase("flag")){
                    putToken(conn);
                    conn.setRequestMethod("PUT");
                    JSONObject cred   = new JSONObject();
                    cred.put("flag",flag);
                    putRequestHeader(conn,cred);
                }
                // Starts the query
                conn.connect();
                //get response
                result = returnResponse(conn);
            //pass exceptions to OnPostExecute()
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            //if device not connected to Internet
            // we pass response 0 to control on OnPostExecute
            result[0] = "0";
            result[1] = null;
            return result;
        }
    }

    //check Connectivity: False not connected, True connected
    private boolean checkConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    @Override
    protected void onPostExecute(String[] result){
        super.onPostExecute(result);
        //Succes for all methods
        if(result[0].equalsIgnoreCase(Integer.toString(HttpURLConnection.HTTP_OK))||result[0].equalsIgnoreCase(Integer.toString(HttpURLConnection.HTTP_CREATED))||
                result[0].equalsIgnoreCase(Integer.toString(HttpURLConnection.HTTP_NO_CONTENT))
                ){
            //success for state request, parse Beach an pass to activity through listener
            if(method.equalsIgnoreCase("state")) {
                // Convert String to json object
                JSONObject json;
                String state;
                try {
                    json = new JSONObject(result[1]);
                    state = json.getString("state");

                    if (state.equals("open")) {
                        Beach beach = parseBeach(result[1]);
                        listener.onTaskCompleted(beach);
                        //Toast.makeText(context, "Beach opened", Toast.LENGTH_SHORT).show();
                    } else {
                        Beach beach = new Beach("close");
                        listener.onTaskCompleted(beach);
                        //Toast.makeText(context, "Beach closed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            //Succes on sigIn and logIn,, get the user and output from response
            }else if(method.equalsIgnoreCase("signIn")||method.equalsIgnoreCase("logIn")){
                // Convert String to json object
                JSONObject json;
                String user = "";
                String token;
                try {
                    json = new JSONObject(result[1]);
                    user = json.getString("username");
                    token = json.getString("authentication_token");
                    listener.onTaskCompleted(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(method.equalsIgnoreCase("signIn")){
                    Toast.makeText(context, "User created: " + user, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "User logged: " + user, Toast.LENGTH_SHORT).show();
                }
            //Success on clean method, Toast to inform user action done
            }else if (method.equalsIgnoreCase("clean")){
                Toast.makeText(context, "Beach Cleaned", Toast.LENGTH_SHORT).show();
            //Succes to nivea method, Toast to inform user and refresh Beach values with state request
            }else if(method.equalsIgnoreCase("nivea")){
                Toast.makeText(context, "Happiness spreaded", Toast.LENGTH_SHORT).show();
                runState();
            //Succes to open method, Toast to inform user and refresh Beach values with state request
            }else if (method.equalsIgnoreCase("open")){
                Toast.makeText(context, "Beach open", Toast.LENGTH_SHORT).show();
                runState();
            //Succes to close method, Toast to inform user and refresh Beach values with state request
            }else if (method.equalsIgnoreCase("close")){
                Beach beach = new Beach("close");
                listener.onTaskCompleted(beach);
                Toast.makeText(context, "Beach Closed", Toast.LENGTH_SHORT).show();
            //Succes to flag method, Toast to inform user and refresh Beach values with state request
            }else if (method.equalsIgnoreCase("flag")){
                Toast.makeText(context, "Flag changed", Toast.LENGTH_SHORT).show();
                runState();
            }
        //Toast for not connected
        }else if (result[0].equals("0")){
            Toast.makeText(context, "Device not connected to Internet ", Toast.LENGTH_SHORT).show();
        //
        }else{
            Toast.makeText(context, "Something went wrong with response:  "+ result[0], Toast.LENGTH_SHORT).show();
        }

    }
    //parsing JSON response for state method, return Beach object
    private Beach parseBeach(String s) {
        Beach beach = new Beach();
        JSONObject json;
        try{
            json = new JSONObject(s);
            beach.state = json.getString("state");
            beach.flag = json.getInt("flag");
            beach.happiness = json.getInt("happiness");
            beach.dirtiness = json.getInt("dirtiness");
            JSONArray kids = json.getJSONArray("kids");
            beach.kids = new ArrayList<>();
            for(int i =0;i<kids.length();i++){
                JSONObject kidObj = kids.getJSONObject(i);
                Kid kid = new Kid();
                kid.name = kidObj.getString("name");
                kid.age = kidObj.getInt("age");
                beach.kids.add(kid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beach;

    }
    //put JSON header to connection
    private void putRequestHeader(HttpURLConnection conn,JSONObject cred) {
        OutputStream os;
        try {
            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(cred.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get response to server and return it on String result
    private String[] returnResponse(HttpURLConnection conn) throws IOException {
        String[] result = new String[2];
        int response = conn.getResponseCode();
        StringBuilder sb;
        if (response == HttpURLConnection.HTTP_OK||response == HttpURLConnection.HTTP_CREATED) {
            //Log.d("AsyncTask " + params[0], " The response is: " + response);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            result[0]= String.valueOf(response);
            result[1]= sb.toString();
            return result;
        } else {
            //Log.d("AsyncTask " + params[0], " The BAD response is: " + response);
            result[0]= String.valueOf(response);
            result[1]= null;
            return result;
        }
    }

    //put token to request properties of connection
    private void putToken(HttpURLConnection conn){
        String tokenST = "Token token=" + "\"" + token + "\"";
        conn.setRequestProperty("Authorization", tokenST);
    }
    //run AsyncTask for refreshing Beach state
    private void runState(){
        beachAsyncT GSTTask = new beachAsyncT(token,context,listener,"state");
        GSTTask.execute(baseUrl+"/state");
    }

}
