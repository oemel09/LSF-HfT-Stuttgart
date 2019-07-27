package de.oemel09.lsf.gradeinfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static de.oemel09.lsf.gradeinfo.grades.GradeParser.TABLE_BODY;
import static de.oemel09.lsf.gradeinfo.grades.GradeParser.TABLE_DATA;
import static de.oemel09.lsf.gradeinfo.grades.GradeParser.TABLE_ROW;

public class GradeInfoParser {

    public GradeInfo parse(String html) {
        try {
            Document doc = Jsoup.parse(html);
            Element tableBody = doc.select(TABLE_BODY).get(1);

            String average = tableBody.select(TABLE_ROW).get(2).getElementsByTag(TABLE_DATA).get(2).text();
            String cpBaseCourses = tableBody.select(TABLE_ROW).get(3).getElementsByTag(TABLE_DATA).get(4).text();
            String cpMainCourses = tableBody.select(TABLE_ROW).get(4).getElementsByTag(TABLE_DATA).get(4).text();

            return GradeInfo.builder()
                    .average(average)
                    .cpBaseCourses(cpBaseCourses)
                    .cpMainCourses(cpMainCourses)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
