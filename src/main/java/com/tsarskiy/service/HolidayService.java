package com.tsarskiy.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class HolidayService {

    public Map<LocalDate, String> getHolidaysForMonth(YearMonth month) {
        Map<LocalDate, String> holidays = new HashMap<>();
        int year = month.getYear();
        addIfInMonth(holidays, LocalDate.of(year, 1, 1), "Новый год", month);

        addIfInMonth(holidays, LocalDate.of(year, 1, 2), "Новогодние каникулы", month);
        addIfInMonth(holidays, LocalDate.of(year, 1, 3), "Новогодние каникулы", month);
        addIfInMonth(holidays, LocalDate.of(year, 1, 4), "Новогодние каникулы", month);
        addIfInMonth(holidays, LocalDate.of(year, 1, 5), "Новогодние каникулы", month);
        addIfInMonth(holidays, LocalDate.of(year, 1, 6), "Новогодние каникулы", month);

        addIfInMonth(holidays, LocalDate.of(year, 1, 7), "Рождество Христово", month);

        addIfInMonth(holidays, LocalDate.of(year, 1, 8), "Новогодние каникулы", month);

        addIfInMonth(holidays, LocalDate.of(year, 2, 23), "День защитника Отечества", month);
        addIfInMonth(holidays, LocalDate.of(year, 3, 8), "Международный женский день", month);
        addIfInMonth(holidays, LocalDate.of(year, 5, 1), "Праздник Весны и Труда", month);
        addIfInMonth(holidays, LocalDate.of(year, 5, 9), "День Победы", month);
        addIfInMonth(holidays, LocalDate.of(year, 6, 12), "День России", month);
        addIfInMonth(holidays, LocalDate.of(year, 11, 4), "День народного единства", month);

        return holidays;
    }

    private void addIfInMonth(
            Map<LocalDate, String> map,
            LocalDate date,
            String name,
            YearMonth month
    ) {
        if (YearMonth.from(date).equals(month)) {
            map.put(date, name);
        }
    }
}
