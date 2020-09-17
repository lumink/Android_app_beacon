package choojeok.song2app.Timetable_Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import choojeok.song2app.R;

import java.util.ArrayList;
import java.util.List;

public class LectureViewActivity extends Activity{
    private LinearLayout container;
    private EditText name, professor, place;
    private List<Utils.TimeViewInfo> timeViewInfoList;

    private DatabaseAccess databaseAccess;
    private Utils.Lecture lecture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_view);

        final Intent intent = getIntent();
        final int lectureId = intent.getIntExtra("lectureId", 0);

        databaseAccess = DatabaseAccess.getInstance(this);
        lecture = readLectureFromDatabase(lectureId);

        fillLectureView();

        final Button alarmSetButton = (Button) findViewById(R.id.alarmSetButton);
        if(lecture.alarmFlag == 0) {
            alarmSetButton.setText("알람 설정");
        }
        else if(lecture.alarmFlag == 1) {
            alarmSetButton.setText("알람 해제");
        }

        alarmSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseWriteAccess databaseAccess2 = DatabaseWriteAccess.getInstance(LectureViewActivity.this);
                databaseAccess2.open();
                if(lecture.alarmFlag == 0) {
                    databaseAccess2.setLectureAlarmFlag(lecture.id, 1);
                    alarmSetButton.setText("알람 해제");
                }
                else if(lecture.alarmFlag == 1) {
                    databaseAccess2.setLectureAlarmFlag(lecture.id, 0);
                    alarmSetButton.setText("알람 설정");
                }
                databaseAccess2.close();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteLectureButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseWriteAccess databaseAccess2 = DatabaseWriteAccess.getInstance(LectureViewActivity.this);
                databaseAccess2.open();
                databaseAccess2.deleteLectureById(lectureId);
                databaseAccess2.close();

                setResult(Utils.RESULT_DELETE_LECTURE);
                finish();
            }
        });

        Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Utils.Lecture readLectureFromDatabase(int id) {
        databaseAccess.open();
        Utils.Lecture lecture = databaseAccess.getLectureById(id);
        databaseAccess.close();

        return lecture;
    }

    private void fillLectureView() {
        final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = (LinearLayout) findViewById(R.id.timeLinearLayout);
        timeViewInfoList = new ArrayList<>();

        name = (EditText) findViewById(R.id.nameEditText);
        name.setText(lecture.name);
        professor = (EditText) findViewById(R.id.profEditText);
        professor.setText(lecture.professor);
        place = (EditText) findViewById(R.id.placeEditText);
        place.setText(lecture.place);

        for(Utils.LectureTime time : lecture.timeList) {
            View view = layoutInflater.inflate(R.layout.timetable_additional_time, null);
            container.addView(view);

            Utils.TimeViewInfo timeView = new Utils.TimeViewInfo();
            timeView.day = (Spinner) view.findViewById(R.id.daySpinner);
            timeView.day.setFocusable(false);
            timeView.day.setClickable(false);
            timeView.day.setSelection((time.dayId - 1));

            timeView.startHour = (EditText) view.findViewById(R.id.startHour);
            timeView.startHour.setFocusable(false);
            timeView.startHour.setClickable(false);
            timeView.startHour.setText("" + (time.startTime / 100));

            timeView.startMin = (EditText) view.findViewById(R.id.startMin);
            timeView.startMin.setFocusable(false);
            timeView.startMin.setClickable(false);
            timeView.startMin.setText(setMinText(time.startTime % 100));

            timeView.endHour = (EditText) view.findViewById(R.id.endHour);
            timeView.endHour.setFocusable(false);
            timeView.endHour.setClickable(false);
            timeView.endHour.setText("" + (time.endTime / 100));

            timeView.endMin = (EditText) view.findViewById(R.id.endMin);
            timeView.endMin.setFocusable(false);
            timeView.endMin.setClickable(false);
            timeView.endMin.setText(setMinText(time.endTime % 100));

            timeView.deleteLectureButton = (Button) view.findViewById(R.id.deleteLectureButton);
            timeView.deleteLectureButton.setText("");

            timeViewInfoList.add(timeView);
        }
    }

    private String setMinText(int min) {
        if(min < 10)
            return "0" + min;
        else
            return "" + min;
    }
}
