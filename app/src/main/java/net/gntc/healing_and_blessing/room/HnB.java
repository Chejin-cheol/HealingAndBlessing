package net.gntc.healing_and_blessing.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "HnB",primaryKeys = {"seq","gubun"})
public class HnB {
    public static final int HEALING = 1;
    public static final int BLESSING = 2;

    @NonNull
    @ColumnInfo(name = "seq")
    int seq = 0;

    @NonNull
    @ColumnInfo(name = "gubun")
    private String gubun="_";

    @ColumnInfo(name = "path")
    private String path;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getPath(){
        return path;
}
    public  String getGubun(){
        return gubun;
    }

    public void setPath(String value){
        path = value;
    }
    public void setGubun(String value){
        gubun = value;
    }
}
