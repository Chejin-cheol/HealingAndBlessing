package net.gntc.healing_and_blessing.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DataDao {
    @Query("SELECT seq,gubun," +
            "(CASE WHEN gubun = 1 THEN 'shinyu/' || path ELSE '10min/' || path END) AS path" +
            " FROM HnB WHERE seq = :seq AND gubun = :gubun")
    HnB getAudioData(int seq , int gubun);

    @Query("SELECT COUNT(*) FROM HnB WHERE gubun = :gubun")
    int getCount(int gubun);


    // Audio history
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertHistory(AudioHistory history);

    @Query("UPDATE history SET state = :state WHERE url LIKE '%'|| :url ||'%' ")
    void updateItemState(String url ,int state);

    @Query("UPDATE history SET state = :state ")
    void updateState(int state);

    @Query("DELETE FROM history WHERE seq = :seq")
    void clearHistory(int seq);

    @Query("SELECT * FROM history WHERE url=:url")
    AudioHistory getHistory(String url);
}
