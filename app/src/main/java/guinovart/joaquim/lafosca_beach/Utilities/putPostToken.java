package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
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
public class putPostToken extends AsyncTask<String, Integer, String[]> {

    Beach beachUI;
    String baseUrl = "http://lafosca-beach.herokuapp.com/api/v1";

    String token;
    Context context;
    String method;
    int flag;

    private OnTaskCompleted listener;

    public putPostToken(String token, Context c, OnTaskCompleted listener, String method) {
        this.token = token;
        this.context = c;
        this.listener=listener;
        this.method = method;
    }

    public putPostToken(String token, Context c, OnTaskCompleted listener, String method,int flag) {
        this.token = token;
        this.context = c;
        this.listener=listener;
        this.method = method;
        this.flag = flag;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection conn;
        InputStream is = null;
        String [] result = new String[2];

        if (checkConnectivity()) {
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
                    conn.setDoInput(true);
                } else if (method.equalsIgnoreCase("clean")||method.equalsIgnoreCase("nivea")) {
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                }else if(method.equalsIgnoreCase("state")){
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                }else if(method.equalsIgnoreCase("flag")){
                    conn.setRequestMethod("PUT");
                    JSONObject cred   = new JSONObject();
                    cred.put("flag",flag);
                    putRequestHeader(conn,cred);
                }



                // Starts the query
                conn.connect();
                //result = conn.getResponseCode();
                result = returnResponse(conn);

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
            result[0] = "0";
            result[1] = null;
            return result;
        }
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    @Override
    protected void onPostExecute(String[] result){
        super.onPostExecute(result);
        if(result[0].equalsIgnoreCase(Integer.toString(HttpURLConnection.HTTP_OK))||result[0].equalsIgnoreCase(Integer.toString(HttpURLConnection.HTTP_CREATED))||
                result[0].equalsIgnoreCase(Integer.toString(HttpURLConnection.HTTP_NO_CONTENT))
                ){
            if(method.equalsIgnoreCase("state")){
            // Convert String to json object
                JSONObject json = null;
                String state = "";
                try {
                    json = new JSONObject(result[1]);
                    state=json.getString("state");
                    if (state.equals("open")){
                        Beach beach = parseBeach(result[1]);
                        Toast.makeText(context, "Beach opened", Toast.LENGTH_SHORT).show();
                        //beachToUi(beach);
                    }else{
                        Beach beach = new Beach("close");
                        listener.onTaskCompleted(beach);
                        Toast.makeText(context, "Beach closed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (method.equalsIgnoreCase("clean")){
                Toast.makeText(context, "Beach Cleaned", Toast.LENGTH_SHORT).show();
            }else if(method.equalsIgnoreCase("nivea")){
                Toast.makeText(context, "Happiness spreaded", Toast.LENGTH_SHORT).show();
                runState();
            }else if (method.equalsIgnoreCase("open")){
                runState();
            }else if (method.equalsIgnoreCase("close")){
                Beach beach = new Beach("close");
                listener.onTaskCompleted(beach);
            }else if (method.equalsIgnoreCase("flag")){
                Toast.makeText(context, "Beach cleaned", Toast.LENGTH_SHORT).show();
                runState();
            }
        }else if (result[0].equals("0")){
            Toast.makeText(context, "Device not connected to Internet ", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Something went wrong with response:  "+ result, Toast.LENGTH_SHORT).show();
        }

    }

    private Beach parseBeach(String s) {
        Beach beach = new Beach();
        JSONObject json = null;
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
            listener.onTaskCompleted(beach);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beach;

    }
    private void putRequestHeader(HttpURLConnection conn,JSONObject cred) {
        OutputStream os = null;
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

    private String[] returnResponse(HttpURLConnection conn) throws IOException {
        String[] result = new String[2];
        int response = conn.getResponseCode();
        StringBuilder sb;
        if (response == HttpURLConnection.HTTP_OK) {
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
    private void runState(){
        putPostToken GSTTask = new putPostToken(token,context,listener,"state");
        GSTTask.execute(baseUrl+"/state");
    }

}
