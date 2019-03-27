package de.oemel09.lsf.grades;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradeParser {

    private ArrayList<Grade> grades;

    public GradeParser() {
        grades = new ArrayList<>();
    }

    public ArrayList<Grade> parse(String html) {
        Document doc = Jsoup.parse(html);
        Element tableBody = doc.select("tbody").get(1);
        Elements tableRows = tableBody.select("tr");
        tableRows.subList(0, 5).clear();
        for (Element tableRow : tableRows) {
            List<Node> children = tableRow.childNodes();
            addGrade(children);
        }
        Collections.sort(grades, new SemesterComparator());
        return grades;
    }

    private void addGrade(List<Node> children) {
        try {
            Grade grade = Grade.builder()
                    .gradeNr(((Element) children.get(1)).text())
                    .gradeName(((Element) children.get(3)).text())
                    .semester(((Element) children.get(5)).text())
                    .grade(validateEntry(((Element) children.get(7)).text()))
                    .gradeDetailsLink(extractGradeDetails(children))
                    .state(((Element) children.get(9)).text())
                    .ects(((Element) children.get(11)).text())
                    .attempt(((Element) children.get(15)).text())
                    .date(validateEntry(((Element) children.get(17)).text()))
                    .build();
            grades.add(grade);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private String validateEntry(String text) {
        return (text.isEmpty()) ? " - " : text;
    }

    private String extractGradeDetails(List<Node> children) {
        Element gradeDetailsElement = ((Element) children.get(7)).select("a").first();
        return (gradeDetailsElement == null) ? null : gradeDetailsElement.attr("href");
    }
}
