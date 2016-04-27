package storage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import serializer.ObjectSerializer;

public class DBWSHelper {

    public static int SaveToDB(String param) {
        int id = -1;
        try {
            URL url = new URL(
                    "http://localhost:6080/ObjectSaver/services/db/save");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = ObjectSerializer.serialize(param);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            String res = "";

            while ((output = br.readLine()) != null) {
                res += output;
            }

            if (!res.equalsIgnoreCase("")) {
                id = Integer.parseInt(output);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static Object GetFromDB(String id) {
        Object o = null;
        try {
            URL url = new URL(
                    "http://localhost:6080/ObjectSaver/services/db/restore");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = ObjectSerializer.serialize(id);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            String res = "";

            while ((output = br.readLine()) != null) {
                res += output;
            }

            if (!res.equalsIgnoreCase("")) {
                o = ObjectSerializer.deserialize(res);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
}
