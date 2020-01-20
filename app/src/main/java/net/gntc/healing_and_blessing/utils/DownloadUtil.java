package net.gntc.healing_and_blessing.utils;

import android.os.AsyncTask;

import net.gntc.healing_and_blessing.room.async.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

public class DownloadUtil {

    public void DownloadAsync( String server , String local ,Command d , Command r)
    {
//        new DownloadThread(server,local ,c).start();
        new DownloadTask(d,r).execute(server,local);
    }

    class DownloadThread extends Thread{
        String serverUrl;
        String localUrl;
        Command command;
        public DownloadThread(String serverUrl , String localUrl , Command command){
            this.serverUrl = serverUrl;
            this.localUrl = localUrl;
            this.command = command;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int read = 0;
                int len = conn.getContentLength();
                byte[] bytes = new byte[len];
                InputStream is = conn.getInputStream();

                File file = new File(localUrl);
                FileOutputStream fos = new FileOutputStream(file);
                while (true){
                    read = is.read(bytes);
                    if(read < 0) {
                        break;
                    }
                    fos.write(bytes,0, read);
                }
                is.close();
                fos.close();
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                command.execute(false);
            } catch (IOException e) {
                e.printStackTrace();
                command.execute(false);
            }
            command.execute(true);
        }
    }


    class DownloadTask extends AsyncTask<String, Void, Boolean>{
        Command<Boolean> onDownloading;
        Command<Boolean> onResult;
        public DownloadTask(Command<Boolean> onDownloading,Command<Boolean> onResult){
            this.onDownloading = onDownloading;
            this.onResult = onResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onDownloading.execute(true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            String serverUrl = strings[0];
            String localUrl = strings[1];

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int read = 0;
                int len = conn.getContentLength();
                byte[] bytes = new byte[len];
                InputStream is = conn.getInputStream();

                File file = new File(localUrl);
                FileOutputStream fos = new FileOutputStream(file);
                while (true){
                    read = is.read(bytes);
                    if(read < 0) {
                        break;
                    }
                    fos.write(bytes,0, read);
                }
                is.close();
                fos.close();
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return  false;
            } catch (IOException e) {
                e.printStackTrace();
                return  false;
            }
            return  true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            onDownloading.execute(false);
            onResult.execute(aBoolean);
        }
    }
}
