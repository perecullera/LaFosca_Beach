package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import guinovart.joaquim.lafosca_beach.BeachActivity;

/**
 * Created by perecullera on 22/9/15.
 */
public class getUserBAuth extends AsyncTask<String, Void, String[]> {

    String username;
    String password;
    Context context;

    public getUserBAuth(String user, String pwd,Context c){
        this.username = user;
        this.password = pwd;
        this.context = c;
    }
    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection conn;
        InputStream is = null;
        String [] result = new String[2];
        try{
            Log.d("Entering ", "AsyncTask");
            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestMethod("GET");
            final String basicAuth = "Basic " + Base64.encodeToString((username+":"+password).getBytes(), Base64.DEFAULT);
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Log.d("Encoded ", basicAuth);

            // Starts the query
            //conn.connect();
            int response = conn.getResponseCode();
            StringBuilder sb;
            if (response == HttpURLConnection.HTTP_OK) {
                Log.d("AsyncTask " + params[0], " The response is: " + response);
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
                Log.d("AsyncTask " + params[0], " The BAD response is: " + response);
                result[0]= String.valueOf(response);
                result[1]= null;
                return result;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        if(result[0]!= null){

            Log.d("Postexecute resultn ", result[0]);
            // Convert String to json object
            JSONObject json = null;
            String token = "";
            try {
                json = new JSONObject(result[1]);
                // get LL json object
                //JSONObject json_LL = json.getJSONObject("authentication_token");

                // get value from LL Json Object
                token=json.getString("authentication_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("token", token);
            editor.commit();
            Toast.makeText(context,"response "+result[0]+" token "+ token,Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context,BeachActivity.class);
            context.startActivity(i);

        }else{
            Toast.makeText(context,"response "+result[0]+" result "+ result[1],Toast.LENGTH_SHORT).show();
            Log.d("Postexecute BAD result ", result[0]);
        }

    }

}
