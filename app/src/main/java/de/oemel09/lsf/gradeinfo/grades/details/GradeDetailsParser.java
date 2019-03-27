package de.oemel09.lsf.gradeinfo.grades.details;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.oemel09.lsf.gradeinfo.grades.GradeParser.TABLE_BODY;
import static de.oemel09.lsf.gradeinfo.grades.GradeParser.TABLE_DATA;
import static de.oemel09.lsf.gradeinfo.grades.GradeParser.TABLE_ROW;

public class GradeDetailsParser {

    private static final String INCLUSIVE = "inklusive";

    public GradeDetails parse(String html) throws NoDetailsException {
        Document doc = Jsoup.parse(html);
        try {
            Element tableBody = doc.select(TABLE_BODY).get(2);
            Elements tableRows = tableBody.select(TABLE_ROW);
            tableRows.subList(0, 3).clear();
            Integer[] amountOfPeopleInRange = extractAmountOfPeopleInRange(tableRows);
            int rangeOfGrade = getRangeOfGrade(tableRows);
            String attendees = ((Element) tableRows.get(5).childNode(2)).text();
            String average = ((Element) tableRows.get(6).childNode(2)).text();
            return GradeDetails.builder().amountOfPeopleInRange(amountOfPeopleInRange)
                    .inRange(rangeOfGrade).attendees(attendees).average(average).build();
        } catch (IndexOutOfBoundsException e) {
            throw new NoDetailsException();
        }
    }

    private Integer[] extractAmountOfPeopleInRange(Elements tableRows) {
        Integer[] amountOfPeopleInRange = new Integer[5];
        for (int i = 0; i < 5; i++) {
            String amountString = tableRows.get(i).getElementsByTag(TABLE_DATA).get(1).text();
            amountOfPeopleInRange[i] = getAmount(amountString);
        }
        return amountOfPeopleInRange;
    }

    private Integer getAmount(String amountString) {
        Pattern pattern = Pattern.compile("(\\d*)");
        Matcher matcher = pattern.matcher(amountString);
        Integer amount = null;
        if (matcher.find()) {
            amount = Integer.valueOf(matcher.group(1));
        }
        return amount;
    }

    private int getRangeOfGrade(Elements tableRows) {
        for (int i = 0; i < 5; i++) {
            String amountString = tableRows.get(i).getElementsByTag(TABLE_DATA).get(1).text();
            if (amountString.contains(INCLUSIVE)) {
                return i;
            }
        }
        return 0;
    }
}
