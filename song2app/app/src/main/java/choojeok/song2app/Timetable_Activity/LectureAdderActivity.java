package choojeok.song2app.Timetable_Activity;

import android.app.Activity;
import android.content.Context;
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

public class LectureAdderActivity extends Activity {
    private LinearLayout container;
    private EditText name, professor;
    private List<Utils.TimeViewInfo> timeViewInfoList;
    private Utils.Lecture lecture;
    private Spinner place;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_adder);

        container = (LinearLayout) findViewById(R.id.timeLinearLayout);
        final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        lecture = new Utils.Lecture();

        name = (EditText)findViewById(R.id.nameEditText);
        professor = (EditText) findViewById(R.id.profEditText);
        place = (Spinner) findViewById(R.id.placeEditText);

        final Utils.TimeViewInfo first = new Utils.TimeViewInfo();
        first.day = (Spinner) findViewById(R.id.daySpinner);
        first.startHour = (EditText) findViewById(R.id.startHour);
        first.startMin = (EditText) findViewById(R.id.startMin);
        first.endHour = (EditText) findViewById(R.id.endHour);
        first.endMin = (EditText) findViewById(R.id.endMin);

        timeViewInfoList = new ArrayList<>();
        timeViewInfoList.add(first);

        Button addFinishButton = (Button) findViewById(R.id.addFinishButton);
        addFinishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final DatabaseWriteAccess databaseAccess = DatabaseWriteAccess.getInstance(LectureAdderActivity.this);
                databaseAccess.open();

                if (first.startHour.getText().toString() == "") {
                    return;
                }

                lecture.name = name.getText().toString();
                lecture.professor = professor.getText().toString();
                lecture.place = place.getSelectedItem().toString();

                final String lectureBid = changePlacenameToBid(lecture.place);

                lecture.bfid = Integer.parseInt(lectureBid);

                for (Utils.TimeViewInfo timeView : timeViewInfoList) {
                    Utils.LectureTime time = new Utils.LectureTime();
                    time.setDay(timeView.day.getSelectedItem().toString());

                    try{
                    int start = parseTimeInt(timeView.startHour.getText().toString(), timeView.startMin.getText().toString());
                    int end = parseTimeInt(timeView.endHour.getText().toString(), timeView.endMin.getText().toString());
                    time.setTime(start, end);
                    lecture.timeList.add(time);}

                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                databaseAccess.addLectureToDatabase(lecture);
                databaseAccess.close();

                setResult(Utils.RESULT_ADD_LECTURE);
                finish();
            }
        });

        Button addLectureTime = (Button) findViewById(R.id.additionalTimeButton);
        addLectureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = layoutInflater.inflate(R.layout.timetable_additional_time, null);
                container.addView(view);

                final Utils.TimeViewInfo additional = new Utils.TimeViewInfo();
                additional.day = (Spinner) view.findViewById(R.id.daySpinner);
                additional.startHour = (EditText) view.findViewById(R.id.startHour);
                additional.startMin = (EditText) view.findViewById(R.id.startMin);
                additional.endHour = (EditText) view.findViewById(R.id.endHour);
                additional.endMin = (EditText) view.findViewById(R.id.endMin);

                additional.deleteLectureButton = (Button) view.findViewById(R.id.deleteLectureButton);
                additional.deleteLectureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        container.removeView(view);
                        timeViewInfoList.remove(additional);
                    }
                });

                timeViewInfoList.add(additional);
            }
        });
    }

    private int parseTimeInt(String hour, String min) {
        int time = 0;

        int hourInt = Integer.parseInt(hour);
        int minInt = Integer.parseInt(min);

        if(hourInt < 9) {
            hourInt += 12;
        }

        time += hourInt * 100;
        time +=  minInt;

        return time;
    }

    public String changePlacenameToBid(String placename){

        String placeToBid = null;

        switch (placename) {
            case "명신관 308호":
                placeToBid = "10443";
                break;
            case "명신관 309호":
                placeToBid = "234";
                break;
            case "명신관 414호":
                placeToBid = "10443";
                break;
            case "명신관 413호":
                placeToBid = "10444";
                break;
        }

            return placeToBid ;
    }
}
