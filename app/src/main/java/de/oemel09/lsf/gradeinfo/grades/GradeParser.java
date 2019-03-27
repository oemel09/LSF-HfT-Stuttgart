package de.oemel09.lsf.gradeinfo.grades;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;

public class GradeParser {

    public static final String TABLE_BODY = "tbody";
    public static final String TABLE_ROW = "tr";
    public static final String TABLE_DATA = "td";
    private static final String ANCHOR = "a";
    private static final String H_REF = "href";

    private ArrayList<Grade> grades;

    public GradeParser() {
        grades = new ArrayList<>();
    }

    public ArrayList<Grade> parse(String html) {
        Document doc = Jsoup.parse(html);
        Element tableBody = doc.select(TABLE_BODY).get(1);
        Elements tableRows = tableBody.select(TABLE_ROW);
        tableRows.subList(0, 5).clear();
        for (Element tableRow : tableRows) {
            Elements children = tableRow.getElementsByTag(TABLE_DATA);
            addGrade(children);
        }
        Collections.sort(grades, new SemesterComparator());
        return grades;
    }

    private void addGrade(Elements children) {
        try {
            Grade grade = Grade.builder()
                    .gradeNr(children.get(0).text())
                    .gradeName(children.get(1).text())
                    .semester(children.get(2).text())
                    .grade(validateEntry(children.get(3)))
                    .gradeDetailsLink(extractGradeDetails(children.get(3)))
                    .state(children.get(4).text())
                    .ects(children.get(5).text())
                    .attempt(children.get(7).text())
                    .date(validateEntry(children.get(8)))
                    .build();
            grades.add(grade);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private String validateEntry(Element element) {
        String text = element.text();
        return (text.isEmpty()) ? " - " : text;
    }

    private String extractGradeDetails(Element element) {
        Element gradeDetailsElement = element.select(ANCHOR).first();
        return (gradeDetailsElement == null) ? null : gradeDetailsElement.attr(H_REF);
    }
}
