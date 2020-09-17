package choojeok.song2app.Timetable_Activity;

import android.graphics.RectF;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final int REQUEST_ADD_LECTURE = 1;
    public static final int REQUEST_VIEW_LECTURE = 2;
    public static final int RESULT_ADD_LECTURE = 3;
    public static final int RESULT_DELETE_LECTURE = 4;

    public static class Lecture {
        public String name, professor, place;
        public int id;
        public int bfid = 0;
        public int alarmFlag = 0;
        public List<LectureTime> timeList;

        public Lecture() {
            timeList = new ArrayList<>();
        }
    }

    public static class LectureTime {
        public int lectureId;
        public String day;
        public float time, startPosition;
        public int dayId, startTime, endTime;
        public int alarmFlag = 0;
        public RectF rect;

        public void setDay(String day) {
            this.day = day;
            switch (day){
                case "월": dayId = 1; break;
                case "화": dayId = 2; break;
                case "수": dayId = 3; break;
                case "목": dayId = 4; break;
                case "금": dayId = 5; break;
                case "토": dayId = 6; break;
                case "일": dayId = 7; break;
                default: dayId = 9;
            }
        }

        public void setTime(int startTime, int endTime) {
            this.startTime = startTime;
            this.endTime = endTime;

            time = calculateTime(startTime, endTime);
            startPosition = calculateStartPosition(startTime);
        }

        public void setRect(float width, float height , int paddingLeft, int paddingTop) {
            rect = new RectF(dayId * width + paddingLeft, startPosition * height + paddingTop, (dayId + 1) * width + paddingLeft, (startPosition + time) * height + paddingTop);
        }

        private static float calculateTime(int startTime, int endTime) {
            int startHour = startTime / 100;
            float startMin = startTime % 100;
            int endHour = endTime / 100;
            float endMin = endTime % 100;

            if (startMin > endMin) {
                return (endHour - startHour - 1) + ((endMin + 60 - startMin) / 60);
            } else {
                return (endHour - startHour) + ((endMin - startMin) / 60);
            }
        }

        private static float calculateStartPosition(int startTime) {
            final int DAY_START_TIME = 900;
            int startHour = (startTime - DAY_START_TIME + 100) / 100;
            int startMin = startTime % 100;

            return startHour + ((float) startMin / 60);
        }
    }

    public static class TimeViewInfo {
        public Spinner day;
        public EditText startHour;
        public EditText startMin;
        public EditText endHour;
        public EditText endMin;
        public Button deleteLectureButton;
    }
}
