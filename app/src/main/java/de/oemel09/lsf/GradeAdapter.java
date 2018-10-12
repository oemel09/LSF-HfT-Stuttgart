package de.oemel09.lsf;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class GradeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Grade> grades;

    GradeAdapter(ArrayList<Grade> grades) {
        this.grades = grades;
    }

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
}
