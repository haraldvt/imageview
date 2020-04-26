package nl.slideview.screensaver;



import org.junit.jupiter.api.Test;

import java.text.DateFormatSymbols;


class TimedActionTest {

    @Test
    public void testMonths() {

        System.out.println(getMonth(1));
        System.out.println(getMonth(11));
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}