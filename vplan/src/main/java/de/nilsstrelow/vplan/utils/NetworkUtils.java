package de.nilsstrelow.vplan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by djnilse on 10.04.2014.
 */
public class NetworkUtils {

    public static boolean isInternetAvailable(Context context) {

        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (info == null || !info.isConnected() || info.isRoaming()) {
            return false;
        }
        return true;
    }

    /**
     * @param sUrl file's url to download from
     * @return string of downloaded file
     */
    public static String getFile(String sUrl) {
        //if (exists(sUrl))
        //    Log.e("saveFile()", "exists on server");
        try {
            if (exists(sUrl)) {
                URL url = new URL(sUrl);
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.connect();

                InputStream in = connection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(in));

                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                String s = sb.toString();
                return s;
            }
        } catch (FileNotFoundException e) {
            Log.w("getFile()", "FileNotFound");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param sUrl file's url to download from
     * @param path save file to path
     * @return
     */
    public static void saveFile(String sUrl, String path) {
        try {
            if (exists(sUrl)) {
                URL url = new URL(sUrl);
                URLConnection connection = null;
                connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.connect();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());

                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                try {
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e5) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
