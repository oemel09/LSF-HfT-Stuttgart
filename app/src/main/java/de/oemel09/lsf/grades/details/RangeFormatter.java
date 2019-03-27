package de.oemel09.lsf.grades.details;

import com.github.mikephil.charting.formatter.ValueFormatter;

class RangeFormatter extends ValueFormatter {

    private String[] rangeList = new String[]{
            "(1,0 - 1,5)",
            "(1,6 - 2,5)",
            "(2,6 - 3,5)",
            "(3,6 - 4,0)",
            "(4,1 - 6,0)"
    };

    @Override
    public String getFormattedValue(float value) {
        return rangeList[(int) value];
    }
}
