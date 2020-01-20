package net.gntc.healing_and_blessing.file;

import android.util.Log;

import net.gntc.healing_and_blessing.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipManager {

    FileInputStream fin = null;
    ZipInputStream zin = null;

    public void unzip(String zip , String toDir) {
        try  {
            fin = new FileInputStream(zip);
            zin = new ZipInputStream(fin);
            ZipEntry ze = null;

            while ((ze = zin.getNextEntry()) != null) {
                String zePath = ze.getName();
                if(ze.isDirectory()) {
                    FileUtil.dirChecker(toDir +"/"+ze.getName());
                }
                else if(zePath.contains("/")) {
                    String[] dirs =zePath.split("/");
                    for(int i=0;i < dirs.length-1 ; i++){
                        FileUtil.dirChecker(toDir +"/" + dirs[i]);
                    }
                    File outFIle = new File(toDir , ze.getName());
                    copyFile(zin, outFIle);
                }
                else {
                    File outFIle = new File(toDir , ze.getName());
                    copyFile(zin, outFIle);
                }

            }
            zin.close();
        } catch(Exception e) {
            Log.e("Decompress", "unzip", e);
        }

    }

    private void copyFile(InputStream in , File outFile){
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(outFile);
            FileUtil.copyFile(zin , fout);
            zin.closeEntry();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
