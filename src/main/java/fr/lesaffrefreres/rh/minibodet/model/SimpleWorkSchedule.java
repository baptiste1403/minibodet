package fr.lesaffrefreres.rh.minibodet.model;

public class SimpleWorkSchedule implements WorkSchedule{

    private int totalHours;
    private int nightHours;
    private boolean isNormalSchedule;


    public SimpleWorkSchedule() {
        totalHours = 0;
        nightHours = 0;
        isNormalSchedule = true;
    }

    public SimpleWorkSchedule(int th, int nh) {
        if(th < 0 || nh < 0 || th < nh) {
            throw new IllegalArgumentException("hour's number must be a positive or null integer");
        }
        totalHours = th;
        nightHours = nh;
        isNormalSchedule = true;
    }

    public int getNightHours() {
        return nightHours;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setNightHours(int nh) {
        if(nh < 0 || nh > totalHours) {
            throw new IllegalStateException("the number of night's hours must be less or equal than the total number of hours and a positive or null integer");
        }
        nightHours = nh;
    }

    public void setTotalHours(int th) {
        if(th < 0 || th < nightHours) {
            throw new IllegalStateException("the number of night's hours must be less or equal than the total number of hours and a positive or null integer");
        }
        totalHours = th;
    }

    public void setAnormalSchedule() {
        isNormalSchedule = false;
    }

    public boolean isNormalSchedule() {
        return isNormalSchedule;
    }
}
