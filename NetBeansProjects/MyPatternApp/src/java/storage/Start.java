package storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import proxy.DBProxy;
import serializer.ObjectSerializer;

public class Start {

    public static void main(String[] args) {
        DBProxy vp = new DBProxy();
        MyClass prox = (MyClass) vp.newInstance(
                MyClass.class, new Class[]{MyClass.class});

        prox.query("INSERT INTO model (`data`) VALUES (\"test\");");
        ResultSet res = prox.query("SELECT * FROM model;");

        try {
            while (res.next()) {
                System.out.println(res.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            URL url = new URL(
                    "http://localhost:8080/ObjectSaver/services/db/save");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = ObjectSerializer.serialize("Model i");

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
            int id = -1;

            while ((output = br.readLine()) != null) {
                id = Integer.parseInt(output);
            }
            System.out.println(id);

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void get() {
        try {
            URL url = new URL(
                    "http://localhost:8080/ObjectSaver/services/db/restore");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = ObjectSerializer.serialize(5);

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

            String test = "";
            while ((output = br.readLine()) != null) {
                test += output;
            }
            if (test != "") {
                System.out.println((String) ObjectSerializer.deserialize(test));
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
