package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a toolbox used to compute values for human resources.
 *  !!! This class is very specific to the entreprise "LessafreFreres"
 *  some result might not correspond to other enterprises system. !!!
 * @param <T>
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 */
public class WorkCalendarOperation<T extends WorkDay> {

    private SQLWorkCalendar calendar;

    /**
     * Set the calendar to use for the computation.
     * @param cal
     */
    public WorkCalendarOperation(Calendar<T> cal) {
        calendar = (SQLWorkCalendar) cal;
    }

    /**
     * Return the week where the given date is.
     * the week is just a map between DayOfWeek and WorkDay of the calendar.
     * @param ld the date to use
     * @return the week as a map between DayOfWeek and WorkDay
     */
    private Map<DayOfWeek, WorkDay> getWeek(LocalDate ld) {
        LocalDate cur = ld.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Map<DayOfWeek, WorkDay> res = new EnumMap<DayOfWeek, WorkDay>(DayOfWeek.class);

        for(DayOfWeek dow : DayOfWeek.values()) {
            res.put(dow, calendar.getDay(cur));
            cur = cur.plusDays(1);
        }

        return res;
    }

    /**
     * Return the number of hours worked in the week where the given date is,
     * from monday to sunday.
     * @param ld the date to use
     * @return the number of hours worked in the week from monday to sunday
     */
    public double getWeekNbHours(LocalDate ld) {
        double res = 0;
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        for(WorkDay wd : week.values()) {
                res += wd.getSchedule().getTotalHours();
        }

        return res;
    }

    public double getWeekNbHoursNight(LocalDate ld) {
        double res = 0;
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        for(WorkDay wd : week.values()) {
            res += wd.getSchedule().getNightHours();
        }

        return res;
    }

    public double getWeekNbHoursWithSpecialAsPlanned(LocalDate ld, Employee emp) {
        double res = 0;
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        SQLEmployeeManager employeeManager = SQLEmployeeManager.getInstance();

        for(WorkDay wd : week.values()) {
            if(justifiedAbsenceDate(wd.getDate(), emp)) {
                res += emp.getPlanningTotalHours(wd.getDate().getDayOfWeek());
            } else {
                res += wd.getSchedule().getTotalHours();
            }
        }

        return res;
    }

    /**
     * Return the number of hours worked in the week where the given date is,
     * from monday to saturday.
     * The sunday is not included.
     * @param ld the date to use
     * @return the number of hours worked in the week from monday to saturday
     */
    private double getWeekNbHoursNoSunday(LocalDate ld) {
        double res = 0;
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        for(WorkDay wd : week.values()) {
            if(!wd.getDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                res += wd.getSchedule().getTotalHours();
            }
        }

        return res;
    }

    /**
     * Return the number of hours worked in the whole month of the given date.
     * @param ld the date to use
     * @return the number of hours worked in the whole month of the given date
     */
    public double getMonthNbHours(LocalDate ld) {
        LocalDate start = ld.with(TemporalAdjusters.firstDayOfMonth());
        start = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<LocalDate> month = start.datesUntil(ld.with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusDays(1)).toList(); // plus day 1 because the last date is not on the list


        double res = 0;

        for (LocalDate cur : month) {
            res += calendar.getDay(cur).getSchedule().getTotalHours();
        }

        return res;
    }

    public double getMonthNbHoursNight(LocalDate ld) {
        LocalDate start = ld.with(TemporalAdjusters.firstDayOfMonth());
        start = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<LocalDate> month = start.datesUntil(ld.with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusDays(1)).toList(); // plus day 1 because the last date is not on the list

        double res = 0;

        for (LocalDate cur : month) {
            res += calendar.getDay(cur).getSchedule().getNightHours();
        }

        return res;
    }

    /**
     * Return the theoretical number of hours worked in the week according to the planning of the given employee.
     * @param emp the employee to use
     * @return the theoretical number of hours worked in the week according to the planning of the given employee
     */
    private double getPlanningWeekHours(Employee emp) {
        double res = 0;

        for(DayOfWeek dow : DayOfWeek.values()) {
                res += emp.getPlanningTotalHours(dow);
        }

        return res;
    }


    /**
     * Return the number of overtime hours worked in the week where the given date is, according to the planning of the given employee. and the calendar.
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime hours worked in the week where the given date is.
     */
    private double getOvertimeWeek(LocalDate ld, Employee emp) {

        double res = getWeekNbHours(ld) - getPlanningWeekHours(emp);

        if(res < 0) {
            res = 0;
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime hours inflated by 25 %
     * the algorithm is based on the following assumptions:
     * the 8 first overtime hours are 25 % ones
     * if there is more than 8 overtime hours, the "overflow" transform to 50 % inflated hours
     * ex : 12 overtime hours => 8 overtime 25 % + 4 overtime 50 %
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime hours inflated by 25 %
     */
    public double getOvertimeWeekLevel1(LocalDate ld, Employee emp) {

        double res = getOvertimeWeek(ld, emp);

        if(res > 8) {
            res = 8;
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime hours inflated by 50 %
     * the algorithm is based on the following assumptions:
     * the 8 first overtime hours are 25 % ones
     * if there is more than 8 overtime hours, the "overflow" transform to 50 % inflated hours
     * ex : 12 overtime hours => 8 overtime 25 % + 4 overtime 50 %
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime hours inflated by 50 %
     */
    public double getOvertimeWeekLevel2(LocalDate ld, Employee emp) {
        double res = getOvertimeWeek(ld, emp);

        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        res -= 8;

        if(res < 0) {
            res = 0;
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime worked on sunday inflated by 25 %
     * the algorithm is based on the following assumptions:
     * sunday worked hours are always inflated by 50 %
     *
     * it mean that the part of this hours corresponding to the number of overtime hours are inflated by 25 % because they are already counted in the 25 % overtime hours.
     * the rest of the hours are inflated by 50 % because they are not counted in the 25 % overtime hours (they are a sort of complementary hours)
     *
     * exemple : if there is 3 overtimes hours in total and 5 hours worked on sunday,
     * it mean that the first 3 hours are inflated by 25 % and the last 2 hours are inflated by 50 %
     *
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime worked on sunday inflated by 25 %
     */
    public double getOvertimeSundayLevel1(LocalDate ld, Employee emp) {

        double overtime = getOvertimeWeek(ld, emp);
        double res = 0;
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        if(overtime > 0) {
            if(overtime < week.get(DayOfWeek.SUNDAY).getSchedule().getTotalHours()) {
                res = overtime;
            } else {
                res = week.get(DayOfWeek.SUNDAY).getSchedule().getTotalHours() - getOvertimeWeekLevel2(ld, emp);
            }
        }

        if(res < 0) {
            res = 0;
        }
        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime worked on sunday inflated by 50 %
     * the algorithm is based on the following assumptions:
     * sunday worked hours are always inflated by 50 %
     *
     * it mean that the part of this hours corresponding to the number of overtime hours are inflated by 25 % because they are already counted in the 25 % overtime hours.
     * the rest of the hours are inflated by 50 % because they are not counted in the 25 % overtime hours (they are a sort of complementary hours)
     *
     * exemple : if there is 3 overtimes hours in total and 5 hours worked on sunday,
     * it mean that the first 3 hours are inflated by 25 % and the last 2 hours are inflated by 50 %
     *
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime worked on sunday inflated by 50 %
     */
    public double getOvertimeSundayLevel2(LocalDate ld, Employee emp) {
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        double res = week.get(DayOfWeek.SUNDAY).getSchedule().getTotalHours() - getOvertimeWeek(ld, emp);
        if(res < 0) {
            res = 0;

        }
        return res;
    }

    private boolean justifiedAbsenceDate(LocalDate ld, Employee emp) {

        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        SQLEmployeeManager employeeManager = SQLEmployeeManager.getInstance();

        if(employeeManager.getCalendar().getDay(ld).getLabelId() == dlm.getBankHolidayDayLabelId()
                || (calendar.getDay(ld).getSchedule().getTotalHours() == 0.0
                && emp.getPlanningTotalHours(ld.getDayOfWeek()) > 0.0
                && calendar.getDay(ld).getLabelId() != dlm.getUnjustifiedAbsenceLabelId())) {
            return true;
        }
        return false;
    }

    private boolean justifiedAbsenceInWeek(LocalDate ld, Employee emp) {
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        SQLEmployeeManager employeeManager = SQLEmployeeManager.getInstance();

        for (WorkDay wd : week.values()) {
            if(employeeManager.getCalendar().getDay(wd.getDate()).getLabelId() == dlm.getBankHolidayDayLabelId()
                    || (wd.getSchedule().getTotalHours() == 0.0
                    && emp.getPlanningTotalHours(wd.getDate().getDayOfWeek()) > 0.0
                    && wd.getLabelId() != dlm.getUnjustifiedAbsenceLabelId())) {
                return true;
            }
            WorkDay curwd;
        }
        return false;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of compensatory hours worked in the week
     * the algorithm is based on the following assumptions:
     * compensatory hours are overtime hours on week where the employee worked less than planned by is planning
     * they are counted only on this condition and are just the sum overtime for each days of the week
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of compensatory hours worked in the week
     */
    public double getCompensatoryHoursWeek(LocalDate ld, Employee emp) {
        Map<DayOfWeek, WorkDay> week = getWeek(ld);

        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        double res = 0;

        res = getWeekNbHoursWithSpecialAsPlanned(ld, emp) - getPlanningWeekHours(emp) - getOvertimeWeek(ld, emp);

        if(!justifiedAbsenceInWeek(ld, emp) || res < 0.0) { // todo
            res = 0;
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime hours inflated by 25% worked in the month
     * the algorithm is based on the following assumptions:
     * all the week wich are fully contained in the month are counted and the week wich are not fully contained in the month are counted for the next month
     * ex : if the week start on 28 of january and end on 3 of february, the overtime hours of this week are counted for the month of february.
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime hours inflated by 25% worked in the month
     */
    public double getMonthOvertimeLevel1(LocalDate ld, Employee emp) {
        LocalDate start = ld.with(TemporalAdjusters.firstDayOfMonth()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate end = ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);

        double res = 0;

        for(LocalDate cur = start; cur.isBefore(end); cur = cur.plusWeeks(1)) {
            res += getOvertimeWeekLevel1(cur, emp);
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime hours inflated by 50% worked in the month
     * the algorithm is based on the following assumptions:
     * all the week wich are fully contained in the month are counted and the week wich are not fully contained in the month are counted for the next month
     * ex : if the week start on 28 of january and end on 3 of february, the overtime hours of this week are counted for the month of february.
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime hours inflated by 50% worked in the month
     */
    public double getMonthOvertimeLevel2(LocalDate ld, Employee emp) {
        LocalDate start = ld.with(TemporalAdjusters.firstDayOfMonth()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate end = ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);

        double res = 0;

        for(LocalDate cur = start; cur.isBefore(end); cur = cur.plusWeeks(1)) {
            res += getOvertimeWeekLevel2(cur, emp);
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime hours inflated by 25% worked on sunday in the month
     * the algorithm is based on the following assumptions:
     * all the week wich are fully contained in the month are counted and the week wich are not fully contained in the month are counted for the next month
     * ex : if the week start on 28 of january and end on 3 of february, the overtime hours of this week are counted for the month of february.
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime hours inflated by 25% worked on sunday in the month
     */
    public double getMonthSundayOvertimeLevel1(LocalDate ld, Employee emp) {
        LocalDate start = ld.with(TemporalAdjusters.firstDayOfMonth()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate end = ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);

        double res = 0;

        for(LocalDate cur = start; cur.isBefore(end); cur = cur.plusWeeks(1)) {
            res += getOvertimeSundayLevel1(cur, emp);
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of overtime hours inflated by 50% worked on sunday in the month
     * the algorithm is based on the following assumptions:
     * all the week wich are fully contained in the month are counted and the week wich are not fully contained in the month are counted for the next month
     * ex : if the week start on 28 of january and end on 3 of february, the overtime hours of this week are counted for the month of february.
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of overtime hours inflated by 25% worked in the month
     */
    public double getMonthSundayOvertimeLevel2(LocalDate ld, Employee emp) {
        LocalDate start = ld.with(TemporalAdjusters.firstDayOfMonth()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate end = ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);

        double res = 0;

        for(LocalDate cur = start; cur.isBefore(end); cur = cur.plusWeeks(1)) {
            res += getOvertimeSundayLevel2(cur, emp);
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of compensatory hours worked in the month
     * the algorithm is based on the following assumptions:
     * all the week wich are fully contained in the month are counted and the week wich are not fully contained in the month are counted for the next month
     * ex : if the week start on 28 of january and end on 3 of february, the overtime hours of this week are counted for the month of february.
     * @param ld the date to use
     * @param emp the employee to use
     * @return the number of compensatory hours worked in the month
     */
    public double getMonthCompensatoryHours(LocalDate ld, Employee emp) {
        LocalDate start = ld.with(TemporalAdjusters.firstDayOfMonth()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate end = ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);

        double res = 0;

        for(LocalDate cur = start; cur.isBefore(end); cur = cur.plusWeeks(1)) {
            res += getCompensatoryHoursWeek(cur, emp);
        }

        return res;
    }

    /**
     * Return the number of worked bank holidays in the month
     * @param ld the date to use
     * @return the number of worked bank holidays in the month
     */
    public double getMonthNbWorkedBankHolidayHours(LocalDate ld) {
        List<LocalDate> month = ld.with(TemporalAdjusters.firstDayOfMonth()).datesUntil(ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)).toList(); // all days of month
        double res = 0.0;

        SQLDayLabelManager sdlm = SQLDayLabelManager.getInstance();
        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();


        for(LocalDate cur : month) {
            if(emp.getCalendar().getDay(cur).getLabelId() == sdlm.getBankHolidayDayLabelId() &&
            calendar.getDay(cur).getSchedule().getTotalHours() > 0.0) {
                res += calendar.getDay(cur).getSchedule().getTotalHours();
            }
        }

        return res;
    }

    public double getNbWorkedBankHolidayHours(LocalDate ld) {
        List<LocalDate> week = ld.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).datesUntil(ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)).toList();

        double res = 0.0;

        SQLDayLabelManager sdlm = SQLDayLabelManager.getInstance();
        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();

        for(LocalDate cur : week) {
            if(emp.getCalendar().getDay(cur).getLabelId() == sdlm.getBankHolidayDayLabelId() &&
                    calendar.getDay(cur).getSchedule().getTotalHours() > 0.0) {
                res += calendar.getDay(cur).getSchedule().getTotalHours();
            }
        }

        return res;
    }

    /**
     * return the number of holidays in the month
     * @param ld the date to use
     * @return the number of holidays in the month
     */
    public int getMonthNbHoliday(LocalDate ld) {
        List<LocalDate> month = ld.with(TemporalAdjusters.firstDayOfMonth()).datesUntil(ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)).toList(); // all days of month
        int res = 0;

        SQLDayLabelManager sdlm = SQLDayLabelManager.getInstance();
        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();

        int bufYear = calendar.getYear();
        if(calendar.getYear() != ld.getYear()) {
            calendar.setYear(ld.getYear());
        }

        for(LocalDate cur : month) {
            if(calendar.getDay(cur).getLabelId() == sdlm.getHolidayDayLabelId() &&
                    !cur.getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                    emp.getCalendar().getDay(cur).getLabelId() != sdlm.getBankHolidayDayLabelId()) {
                res++;
            }
        }

        if(calendar.getYear() != bufYear) {
            calendar.setYear(bufYear);
        }

        return res;
    }

    /**
     * Return the number of sick leaves in the month
     * @param ld the date to use
     * @return the number of sick leaves in the month
     */
    public int getMonthSickLeave(LocalDate ld) {
        List<LocalDate> month = ld.with(TemporalAdjusters.firstDayOfMonth()).datesUntil(ld.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)).toList(); // all days of month
        int res = 0;

        SQLDayLabelManager sdlm = SQLDayLabelManager.getInstance();

        for(LocalDate cur : month) {
            if(calendar.getDay(cur).getLabelId() == sdlm.getSickLeaveDayLabelId() && !cur.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                res++;
            }
        }

        return res;
    }

    /**
     * This part is specific to the entreprise "LesaffreFreres".
     * Return the number of holiday between june and june of the next year where the given date is in between.
     * @param ld the date to use
     * @return the number of holiday between june and june of the next year where the given date is in between.
     */
    public int getYearNbDayHoliday(LocalDate ld) {
        LocalDate start;
        LocalDate end;

        if(ld.isBefore(LocalDate.of(ld.getYear(), 6, 1))) { // all days between first of june and 31 of may (containing the day)
            start = LocalDate.of(ld.getYear()-1, 6, 1);
            end = LocalDate.of(ld.getYear(), 6, 1);
        } else {
            start = LocalDate.of(ld.getYear(), 6, 1);
            end = LocalDate.of(ld.getYear()+1, 6, 1);
        }

        int res = 0;

        for(LocalDate cur = start; cur.isBefore(end); cur = cur.plusMonths(1)) {
            res += getMonthNbHoliday(cur);
        }

        return res;
    }
}
