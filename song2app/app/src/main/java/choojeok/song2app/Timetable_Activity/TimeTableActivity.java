package choojeok.song2app.Timetable_Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import choojeok.song2app.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeTableActivity extends Activity {
    private GridView gridView;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_main);
        layout = (LinearLayout) findViewById(R.id.timeTableLayout);
        gridView = new TimeTableView(this);
        layout.addView(gridView);
    }
        //추가
        //Intent intent = null ;
        public void onClick(View arg) {
            switch (arg.getId()) {
                case R.id.lecturePlus:
                    final Intent intent = new Intent(TimeTableActivity.this, LectureAdderActivity.class);
                    startActivityForResult(intent, Utils.REQUEST_ADD_LECTURE);
                    break;

                case R.id.lectureReset:
                    final DatabaseWriteAccess databaseAccess = DatabaseWriteAccess.getInstance(TimeTableActivity.this);
                    databaseAccess.open();
                    databaseAccess.deleteAllLectures();
                    databaseAccess.close();
                    gridView.invalidate();
                    break;

                case R.id.lecturesettig:
                    final Intent intent2 = new Intent(TimeTableActivity.this, TimetableSetting.class);
                    startActivity(intent2);
                    break;
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Utils.RESULT_ADD_LECTURE:
            case Utils.RESULT_DELETE_LECTURE:
                gridView.invalidate();
                break;
        }
    }

    public class TimeTableView extends GridView {
        private static final int COLUMNSNUM = 6; //5
        private static final int ROWNUM = 13;
        private static final int PADDING_LEFT = -50; //-50
        private static final int PADDING_TOP = 50; //50
        private List<Utils.Lecture> lectureList;
        private final int[] colorArray;
        private Context context;

        public TimeTableView(Context context) {
            super(context);
            this.context = context;
            this.setWillNotDraw(false);
            this.setNumColumns(COLUMNSNUM);
            this.setGravity(Gravity.CENTER);
            this.setPadding(PADDING_LEFT, PADDING_TOP, 20, 0); //20
            this.setBackgroundColor(Color.WHITE);

            TypedArray typedArray = getResources().obtainTypedArray(R.array.table_colors);
            colorArray = new int[typedArray.length()];
            for(int i = 0; i < typedArray.length(); ++i)
                colorArray[i] = typedArray.getColor(i, 0);
            typedArray.recycle();

            List<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.timetable)));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.timetable_list, list);
            this.setAdapter(adapter);

            lectureList = readLecturesFromDatabase();
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if(ev.getAction() == MotionEvent.ACTION_DOWN) {
                for(Utils.Lecture lecture : lectureList) {
                    for(Utils.LectureTime time : lecture.timeList) {
                        if (time.rect.contains(ev.getX(), ev.getY())) {
                            Intent intent = new Intent(context, LectureViewActivity.class);
                            intent.putExtra("lectureId", lecture.id);
                            startActivityForResult(intent, Utils.REQUEST_VIEW_LECTURE);
                            break;
                        }
                    }
                }
            }
            return true;
        }

        private List<Utils.Lecture> readLecturesFromDatabase() {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            List<Utils.Lecture> lectureList = databaseAccess.getAllLectures();
            databaseAccess.close();

            return lectureList;
        }

        private static final int LECTURE_TEXT_SIZE = 50;
        private static final int PLACE_TEXT_SIZE = 40;
        private static final int PROFESSOR_TEXT_SIZE = 40;
        private static final int TIME_TABLE_HEIGHT = 180;
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            final float width = this.getColumnWidth();
            final float height = TIME_TABLE_HEIGHT;
            Paint paint = new Paint();

            int colorNumber = 0;
            for(Utils.Lecture lecture : lectureList) {
                for(Utils.LectureTime time : lecture.timeList) {
                    time.setRect(width, height, PADDING_LEFT, PADDING_TOP);

                    paint.setColor(colorArray[colorNumber]);
                    paint.setStrokeWidth(10);
                    canvas.drawRect(time.rect, paint);

                    paint.setColor(Color.BLACK);
                    paint.setTextSize(LECTURE_TEXT_SIZE);
                    canvas.drawText(lecture.name, time.dayId * width + PADDING_LEFT,
                            time.startPosition * height + LECTURE_TEXT_SIZE + PADDING_TOP, paint);

                    paint.setColor(Color.GRAY);
                    paint.setTextSize(PLACE_TEXT_SIZE);
                    canvas.drawText(lecture.place, time.dayId * width + PADDING_LEFT,
                            time.startPosition * height + LECTURE_TEXT_SIZE + PLACE_TEXT_SIZE + PADDING_TOP, paint);

                    paint.setColor(Color.GRAY);
                    paint.setTextSize(PROFESSOR_TEXT_SIZE);
                    canvas.drawText(lecture.professor, time.dayId * width + PADDING_LEFT,
                            time.startPosition * height + LECTURE_TEXT_SIZE + PLACE_TEXT_SIZE + PROFESSOR_TEXT_SIZE + PADDING_TOP, paint);
                }
                colorNumber++;
            }
        }

        @Override
        public void invalidate() {
            lectureList = readLecturesFromDatabase();
            super.invalidate();
        }
    }
}
