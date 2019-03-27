package de.oemel09.lsf.grades;

import java.util.Comparator;

public class SemesterComparator implements Comparator<Grade> {

    @Override
    public int compare(Grade o1, Grade o2) {
        String one = getNumberToCompare(o1.getSemester());
        String two = getNumberToCompare(o2.getSemester());

        if (one.compareTo(two) == 0) {
            return Integer.parseInt(o2.getGradeNr()) - Integer.parseInt(o1.getGradeNr());
        } else {
            return one.compareTo(two) * -1;
        }
    }

    private String getNumberToCompare(String semester) {
        if (semester.startsWith("SoSe")) {
            String[] pre1 = semester.split("SoSe ");
            return pre1[1] + "1";
        } else {
            String[] pre3 = semester.split("WiSe ");
            String[] pre4 = pre3[1].split("/");
            return pre4[0] + "2";
        }
    }
}
