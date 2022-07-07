package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;

public interface WorkWeek {

    public WorkDay getDay(DayOfWeek dow);

    public int getNbHours();
}
