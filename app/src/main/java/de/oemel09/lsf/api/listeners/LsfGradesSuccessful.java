package de.oemel09.lsf.api.listeners;

import java.util.ArrayList;

import de.oemel09.lsf.grades.Grade;

public interface LsfGradesSuccessful {
    void onGradesSuccessful(ArrayList<Grade> grades);
}
