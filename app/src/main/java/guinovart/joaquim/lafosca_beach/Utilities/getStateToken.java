package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import guinovart.joaquim.lafosca_beach.Models.Beach;
import guinovart.joaquim.lafosca_beach.Models.Kid;
import guinovart.joaquim.lafosca_beach.OnTaskCompleted;

/**
 * Created by perecullera on 22/9/15.
 */
public class getStateToken extends AsyncTask<String, Void, String[]> {

    String token;
    Context context;
    private OnTaskCompleted listener;

    public getStateToken (String token, Context c, OnTaskCompleted listener) {
        this.token = token;
        this.context = c;
        this.listener = listener;
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
            String tokenST = "Token token="+"\""+token+"\"";
            Log.d("token string :",tokenST);
            conn.setRequestProperty("Authorization", tokenST);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);




            // Starts the query
            conn.connect();
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
            Toast.makeText(context, "response " + result[0] + " result " + result[1], Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Beach Opened", Toast.LENGTH_SHORT).show();
            // Convert String to json object
            JSONObject json = null;
            String state = "";
            try {
                json = new JSONObject(result[1]);
                state=json.getString("state");
                if (state.equals("open")){
                    Beach beach = parseBeach(result[1]);
                    beachToUi(beach);
                }else{
                    Beach beach = new Beach("close");
                    listener.onTaskCompleted(beach);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(context,"response "+result[0]+" result "+ result[1],Toast.LENGTH_SHORT).show();
            Log.d("Postexecute BAD result ", result[0]);
        }

    }

    private void beachToUi(Beach beach) {

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

}
