package de.oemel09.lsf;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lukas on 11.02.2017.
 * adapter for main view
 */

public class GradeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "PlaylistAdapter";

    private final Activity context;
    private ArrayList<Grade> grades;

    private OnClickListener onClickListener;

    GradeAdapter(Activity context, ArrayList<Grade> grades) {
        this.context = context;
        this.grades = grades;
    }

    //ViewHolder inner classes
    private class GradeViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvSemester, tvState, tvGrade, tvDate, tvEcts, tvAttempt, tvGradeNr;

        GradeViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            tvSemester = v.findViewById(R.id.tv_semester);
            tvState = v.findViewById(R.id.tv_state);
            tvGrade = v.findViewById(R.id.tv_grade);
            tvDate = v.findViewById(R.id.tv_date);
            tvEcts = v.findViewById(R.id.tv_ects);
            tvAttempt = v.findViewById(R.id.tv_attempt);
            tvGradeNr = v.findViewById(R.id.tv_grade_nr);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grade_item, parent, false);
        return new GradeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Grade grade = grades.get(position);
        GradeViewHolder gvh = (GradeViewHolder) holder;

        gvh.tvName.setText(grade.getGradeName());
        gvh.tvSemester.setText(grade.getSemester());
        gvh.tvState.setText(grade.getState());
        gvh.tvGrade.setText(grade.getGrade());
        gvh.tvDate.setText(grade.getDate());
        gvh.tvEcts.setText(grade.getEcts());
        gvh.tvAttempt.setText(grade.getAttempt());
        gvh.tvGradeNr.setText(grade.getGradeNr());
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    // the onclickListener interface and it's set method
    public interface OnClickListener {
        void onClick(View v, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
