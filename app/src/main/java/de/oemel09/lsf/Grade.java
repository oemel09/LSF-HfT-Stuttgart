package de.oemel09.lsf;

class Grade {

    private String gradeNr, gradeName, semester, grade, state, ects, attempt, date;

    String getGradeNr() {
        return gradeNr;
    }

    void setGradeNr(String gradeNr) {
        this.gradeNr = gradeNr;
    }

    String getGradeName() {
        return gradeName;
    }

    void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    String getSemester() {
        return semester;
    }

    void setSemester(String semester) {
        this.semester = semester;
    }

    String getGrade() {
        return grade;
    }

    void setGrade(String grade) {
        this.grade = grade;
    }

    String getState() {
        return state;
    }

    void setState(String state) {
        this.state = state;
    }

    String getEcts() {
        return ects;
    }

    void setEcts(String ects) {
        this.ects = ects;
    }

    String getAttempt() {
        return attempt;
    }

    void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }
}
