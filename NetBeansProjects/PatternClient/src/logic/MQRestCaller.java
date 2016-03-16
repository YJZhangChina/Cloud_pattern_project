package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import serializer.ObjectSerializer;

/**
 *
 * @author benjamin
 */
public class MQRestCaller {    
        
    public static String SelectSimple(String mode, int id) {
        try {
            URL url = new URL(String.format(
                    "http://localhost:8080/MyPatternApp/services/mq/%s", mode));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = ""+id;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                return null;
            } else if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;

            String result = "";
            while ((output = br.readLine()) != null) {
                result += output;
            }
            conn.disconnect();
            
            return result;
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static UUID PlaceRequest(String proxyMode){
        try {
            URL url = new URL(String.format("http://localhost:8080/MyPatternApp/services/mq/%s", proxyMode));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            
            if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                return null;
            } else if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            } 
            
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;

            String result = "";
            while ((output = br.readLine()) != null) {
                result += output;
            }
            conn.disconnect();
            return UUID.fromString(result);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object GetFromMessageQueue(UUID uid){
        try {
            URL url = new URL("http://localhost:8080/MyPatternApp/services/mq/getFromMessageQueue");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = ObjectSerializer.serialize(uid);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                return null;
            } else if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;

            String result = "";
            while ((output = br.readLine()) != null) {
                result += output;
            }
            conn.disconnect();
            return result;
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
