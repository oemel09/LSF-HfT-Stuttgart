package de.oemel09.lsf.gradeinfo;

import java.util.ArrayList;

import de.oemel09.lsf.gradeinfo.grades.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class GradeInfo {

    private String average;
    private String cpBaseCourses;
    private String cpMainCourses;
    @Setter
    private ArrayList<Grade> grades;
}
