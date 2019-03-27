package de.oemel09.lsf.grades.details;

import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.oemel09.lsf.R;
import de.oemel09.lsf.grades.Grade;

import static de.oemel09.lsf.MainActivity.GRADE;

public class GradeDetailsActivity extends AppCompatActivity {

    private Grade grade;
    private GradeDetails details;
    private Integer[] amountOfPeopleInRange;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_details);
        extractGrade();

        setTitle(grade.getGradeName());
        fillOutDetails();
        createBarChart();
    }

    private void fillOutDetails() {
        ((TextView) findViewById(R.id.grade_details_tv_average)).setText(details.getAverage());
        ((TextView) findViewById(R.id.grade_details_tv_attendees)).setText(details.getAttendees());
        ((TextView) findViewById(R.id.grade_details_tv_grade)).setText(grade.getGrade());
    }

    private void createBarChart() {
        barChart = findViewById(R.id.grade_details_bc_amount_of_people_in_range);

        BarData barData = createBarData();
        barChart.setData(barData);
        barChart.disableScroll();
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
        barChart.setHighlightPerDragEnabled(false);
        barChart.getLegend().setEnabled(false);
        setDescription();
        customizeXAxis();
        customizeYAxis();
    }

    private void setDescription() {
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
    }

    @NonNull
    private BarData createBarData() {
        BarDataSet barDataSet = new BarDataSet(createDataEntries(), "Grades");
        barDataSet.setValueTextSize(18);
        barDataSet.setValueFormatter(new LargeValueFormatter());
        barDataSet.setColors(barColors());
        return new BarData(barDataSet);
    }

    @NonNull
    private List<BarEntry> createDataEntries() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < amountOfPeopleInRange.length; i++) {
            entries.add(createBar(i));
        }
        return entries;
    }

    private int[] barColors() {
        int[] colors = new int[5];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = getResources().getColor(
                    i == details.getInRange() ? R.color.colorPrimary : R.color.colorSecondary);
        }
        return colors;
    }

    private BarEntry createBar(int i) {
        return new BarEntry(i, amountOfPeopleInRange[i]);
    }

    private void customizeXAxis() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new RangeFormatter());
        xAxis.setTextSize(12);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
    }

    private void customizeYAxis() {
        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawLabels(false);
        axisLeft.setDrawAxisLine(false);
        barChart.getAxisRight().setEnabled(false);
    }

    private void extractGrade() {
        grade = (Grade) getIntent().getSerializableExtra(GRADE);
        details = grade.getGradeDetails();
        amountOfPeopleInRange = details.getAmountOfPeopleInRange();
    }
}
