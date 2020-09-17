package choojeok.song2app.Timetable_Activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if(instance == null)
            instance = new DatabaseAccess(context);

        return instance;
    }

    public void open() {
        this.database = openHelper.getReadableDatabase();
    }

    public void close() {
        if(database != null)
            this.database.close();
    }

    public String[] getDay() {
        String day[] = new String[10];
        int i = 0;
        Cursor cursor = database.rawQuery("SELECT day FROM lectureTime where alarmFlag = '" + 1 + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            day[i] = ((cursor.getString(0)));
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return day;
    }

    public int[] getAlarmTime() {
        int[] startTime = new int[10];
        int i = 0;
        Cursor cursor = database.rawQuery("SELECT startTime FROM lectureTime where alarmFlag = '" + 1 + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            startTime[i] = Integer.parseInt((cursor.getString(0)));
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return startTime;
    }

    public String[] getClassBid(){
        String classBid[] = new String[10];
        int i = 0;
        Cursor cursor = database.rawQuery("SELECT bfid FROM lectures where alarmFlag ='" + 1 + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            classBid[i] = String.valueOf(cursor.getInt(0));
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return classBid;
    }

    public int getAlarmNum(){
        int num = 0;
        Cursor cursor = database.rawQuery("SELECT startTime FROM lectureTime where alarmFlag = '" + 1 + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            cursor.moveToNext();
            num++;
        }
        cursor.close();
        return num;
    }

    public List<String> getAlarmClass2(){
        List<String> classes = new ArrayList<>();
        int i = 0;
        Cursor cursor = database.rawQuery("SELECT name FROM lectures  where alarmFlag = '" + 1 + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            classes.add(cursor.getString(0));
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return classes;
    }

    public String getClassNo(String className){
        String classNo = null;
        Cursor cursor = database.rawQuery("SELECT place FROM lectures where name ='" + className + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            classNo = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return classNo;
    }

    public int getClassid(String className){
        int classNo = 0;
        Cursor cursor = database.rawQuery("SELECT id FROM lectures where name ='" + className + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            classNo = Integer.parseInt(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return classNo;
    }

    public List<Utils.Lecture> getAllLectures() {
        List<Utils.Lecture> lectureList = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT id, name, professor, place, alarmFlag FROM lectures", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Utils.Lecture lecture = new Utils.Lecture();

            lecture.id = ((cursor.getInt(0)));
            lecture.name = ((cursor.getString(1)));
            lecture.professor = ((cursor.getString(2)));
            lecture.place = ((cursor.getString(3)));
            //lecture.bfid = ((cursor.getInt(4)));
            lecture.alarmFlag = ((cursor.getInt(4)));
            lecture.timeList = getLectureTimesById(lecture.id);

            lectureList.add(lecture);

            cursor.moveToNext();
        }
        cursor.close();
        return lectureList;
    }

    public Utils.Lecture getLectureById(int id) {
        Cursor cursor = database.rawQuery("SELECT id, name, professor, place, alarmFlag FROM lectures WHERE id=" + id, null);
        cursor.moveToFirst();

        Utils.Lecture lecture = new Utils.Lecture();

        lecture.id = ((cursor.getInt(0)));
        lecture.name = ((cursor.getString(1)));
        lecture.professor = ((cursor.getString(2)));
        lecture.place = ((cursor.getString(3)));
        //lecture.bfid = ((cursor.getInt(4)));
        lecture.alarmFlag = ((cursor.getInt(4)));
        lecture.timeList = getLectureTimesById(lecture.id);

        cursor.close();
        return lecture;
    }

    private List<Utils.LectureTime> getLectureTimesById(int id) {
        List<Utils.LectureTime> timeList = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT day, startTime, endTime, alarmFlag FROM lectureTime where lectureID="+id, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Utils.LectureTime time = new Utils.LectureTime();

            time.lectureId = id;
            time.setDay(((cursor.getString(0))));
            time.setTime((cursor.getInt(1)), (cursor.getInt(2)));
            time.alarmFlag = ((cursor.getInt(3)));

            timeList.add(time);
            Log.i("lecture", "day " + time.day);

            cursor.moveToNext();
        }
        cursor.close();
        return timeList;
    }


    private class DatabaseOpenHelper extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "timetable.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }
}
