package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import guinovart.joaquim.lafosca_beach.OnTaskCompleted;

/**
 * Created by perecullera on 22/9/15.
 */
public class putPostToken extends AsyncTask<String, Integer, Integer> {

    String baseUrl = "http://lafosca-beach.herokuapp.com/api/v1";

    String token;
    Context context;
    String method;

    private OnTaskCompleted listener;

    public putPostToken(String token, Context c, OnTaskCompleted listener, String method) {
        this.token = token;
        this.context = c;
        this.listener=listener;
        this.method = method;
    }

    @Override
    protected Integer doInBackground(String... params) {
        HttpURLConnection conn;
        InputStream is = null;
        String [] result = new String[2];
        int response = 0;
        try {
            Log.d("Entering ", "AsyncTask");
            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            String tokenST = "Token token=" + "\"" + token + "\"";
            Log.d("token string :", tokenST);
            conn.setRequestProperty("Authorization", tokenST);

            if (method.equalsIgnoreCase("open")||method.equalsIgnoreCase("close")) {
                conn.setRequestMethod("PUT");
            } else if (method.equalsIgnoreCase("clean")||method.equalsIgnoreCase("nivea")) {
                conn.setRequestMethod("POST");
            }
            conn.setDoInput(true);


            // Starts the query
            conn.connect();
            response = conn.getResponseCode();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(Integer result){
        if(result==HttpURLConnection.HTTP_OK||result==HttpURLConnection.HTTP_CREATED){
            getStateToken GSTTask = new getStateToken(token,context,listener);
            GSTTask.execute(baseUrl+"/state");
            if (method.equalsIgnoreCase("clean")) {
                Toast.makeText(context, "Beach Cleaned", Toast.LENGTH_SHORT).show();
            } else if (method.equalsIgnoreCase("nivea")){
                Toast.makeText(context, "Happiness spreaded", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(context, "response "+ result, Toast.LENGTH_SHORT).show();
        }

    }

}
