package net.gntc.healing_and_blessing.file;

import android.content.Context;
import android.content.res.AssetManager;

import net.gntc.healing_and_blessing.room.HnB;
import net.gntc.healing_and_blessing.room.async.Promise;
import net.gntc.healing_and_blessing.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceManager {

    Context context;
    public ResourceManager(Context context){
        this.context = context;
    }

    public void decompressFromAssets(String from , String to) {
        AssetManager assetManager = context.getResources().getAssets();
        ZipManager zm = new ZipManager();
        InputStream is = null;
        OutputStream out = null;

            try {

                is = assetManager.open(from);
                String toDir = context.getFilesDir().getAbsolutePath();
                File outFile = new File(toDir , to);
                out = new FileOutputStream(outFile);
                FileUtil.copyFile(is, out);
                zm.unzip( outFile.getAbsolutePath() , toDir);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    if (is != null) {
                        is.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void getDataFile(String file){
        String toDir = context.getFilesDir().getAbsolutePath();
        File f = new File(toDir,file);
    }
}


