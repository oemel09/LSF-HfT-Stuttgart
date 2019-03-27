package de.oemel09.lsf.gradeinfo.grades;

import java.io.Serializable;

import de.oemel09.lsf.gradeinfo.grades.details.GradeDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
public
class Grade implements Serializable {
    private String gradeNr;
    private String gradeName;
    private String gradeDetailsLink;
    private String semester;
    private String grade;
    private String state;
    private String ects;
    private String attempt;
    private String date;
    @Setter
    private GradeDetails gradeDetails;
}
