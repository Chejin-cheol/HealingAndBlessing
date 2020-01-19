package net.gntc.healing_and_blessing.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history",primaryKeys = {"seq"})
public class AudioHistory {

    @ColumnInfo(name = "url")
    private String url = null;

    @ColumnInfo(name = "position")
    private int position = 0;

    @NonNull
    @ColumnInfo(name="seq")
    private int seq = 0;

    @ColumnInfo(name = "state")
    private  int state = 1;

    public  AudioHistory(int seq ,String url, int position ,int on){
        this.seq = seq;
        this.url = url;
        this.position = position;
        this.state = on;
    }
    public  AudioHistory(){}


    public int getSeq() {
        return seq;
    }

    public int getPosition() {
        return position;
    }

    public String getUrl() {
        return url;
    }

    public int getState(){
        return  state;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setState(int state) {
        this.state = state;
    }
}
