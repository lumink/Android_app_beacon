package choojeok.song2app.Timetable_Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseWriteAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseWriteAccess instance;

    private DatabaseWriteAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseWriteAccess getInstance(Context context) {
        if(instance == null)
            instance = new DatabaseWriteAccess(context);

        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if(database != null)
            this.database.close();
    }

    public void addLectureToDatabase(Utils.Lecture lecture) {
        ContentValues values = new ContentValues();
        values.put("name", lecture.name);
        values.put("professor", lecture.professor);
        values.put("place", lecture.place);
        values.put("bfid", lecture.bfid);
        values.put("alarmFlag", lecture.alarmFlag);
        int id = (int) database.insert("lectures", null, values);

        for(Utils.LectureTime time : lecture.timeList) {
            ContentValues timeValues = new ContentValues();
            timeValues.put("lectureId", id);
            timeValues.put("day", time.day);
            timeValues.put("startTime", time.startTime);
            timeValues.put("endTime", time.endTime);
            timeValues.put("alarmFlag", time.alarmFlag);
            database.insert("lectureTime", null, timeValues);
        }
    }

    public void deleteAllLectures() {
        database.delete("lectureTime", null, null);
        database.delete("lectures", null, null);
    }

    public void deleteLectureById(int id) {
        String deleteLectureTimes = "DELETE FROM lectureTime WHERE lectureId="+id;
        String deleteLecture = "DELETE FROM lectures WHERE id=" + id;

        database.execSQL(deleteLectureTimes);
        database.execSQL(deleteLecture);
    }

    public void setLectureAlarmFlag(int lectureId, int alarmFlag) {
        String updateLectureAlarmFlag = "UPDATE lectures SET alarmFlag = " + alarmFlag
                + " WHERE id="+lectureId+";";
        String updatLectureTimeeAlarmFlag = "UPDATE lectureTime SET alarmFlag = " + alarmFlag
                + " WHERE lectureId="+lectureId+";";

        database.execSQL(updateLectureAlarmFlag);
        database.execSQL(updatLectureTimeeAlarmFlag);
    }

    private class DatabaseOpenHelper extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "timetable.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }
}
