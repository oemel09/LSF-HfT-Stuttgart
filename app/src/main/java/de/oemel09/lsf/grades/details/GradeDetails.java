package de.oemel09.lsf.grades.details;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GradeDetails implements Serializable {

    private Integer[] amountOfPeopleInRange;
    private int inRange;
    private String attendees;
    private String average;
}
