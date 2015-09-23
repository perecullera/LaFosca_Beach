package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by perecullera on 21/9/15.
 */
public class UserPostNoAuth extends AsyncTask<String, String[], String[]> {

    String username;
    String password;
    Context context;

    public  UserPostNoAuth (String user, String pwd,Context c){

        this.context = c;
        this.username = user;
        this.password = pwd;

    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection conn;
        InputStream is = null;
        String [] result = new String[2];
        try {
            Log.d("Entering ", "AsyncTask");
            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject cred   = new JSONObject();
            cred.put("username",username);

            cred.put("password", password);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(cred.toString());
            writer.flush();


            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            StringBuilder sb;
            if (response == HttpURLConnection.HTTP_CREATED) {
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

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        if(result[0]!= null){
            Log.d("Postexecute resultn ", result[0]);
            Toast.makeText(context, "response " + result[0] + " result " + result[1], Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(context,"response "+result[0]+" result "+ result[1],Toast.LENGTH_SHORT).show();
            Log.d("Postexecute BAD result ", result[0]);
        }

    }


}
