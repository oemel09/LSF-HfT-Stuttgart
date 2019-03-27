package de.oemel09.lsf.grades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.oemel09.lsf.R;

public class GradeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Grade> grades;
    private ArrayList<Grade> gradesCopy;
    private OnGradeClickListener onGradeClickListener;

    public GradeAdapter(ArrayList<Grade> grades) {
        this.grades = grades;
        this.gradesCopy = new ArrayList<>();
        gradesCopy.addAll(grades);
    }

    public void filter(String query) {
        grades.clear();
        if(query.isEmpty()){
            grades.addAll(gradesCopy);
        } else{
            query = query.toLowerCase();
            for(Grade g : gradesCopy){
                if(g.getGradeName().toLowerCase().contains(query)){
                    grades.add(g);
                }
            }
        }
        notifyDataSetChanged();
    }

    private class GradeViewHolder extends RecyclerView.ViewHolder {

        private View view;

        GradeViewHolder(View view) {
            super(view);
            this.view = view;
        }

        void setText(Grade grade) {
            ((TextView) view.findViewById(R.id.tv_name)).setText(grade.getGradeName());
            ((TextView) view.findViewById(R.id.tv_semester)).setText(grade.getSemester());
            ((TextView) view.findViewById(R.id.tv_state)).setText(grade.getState());
            ((TextView) view.findViewById(R.id.tv_grade)).setText(grade.getGrade());
            ((TextView) view.findViewById(R.id.tv_date)).setText(grade.getDate());
            ((TextView) view.findViewById(R.id.tv_ects)).setText(grade.getEcts());
            ((TextView) view.findViewById(R.id.tv_attempt)).setText(grade.getAttempt());
            ((TextView) view.findViewById(R.id.tv_grade_nr)).setText(grade.getGradeNr());
            view.findViewById(R.id.cv_root).setOnClickListener(
                    view -> onGradeClickListener.onGradeClick(grade));
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
        gvh.setText(grade);
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    public interface OnGradeClickListener {
        void onGradeClick(Grade grade);
    }

    public void setOnGradeClickListener(OnGradeClickListener onGradeClickListener) {
        this.onGradeClickListener = onGradeClickListener;
    }
}
