package fr.lesaffrefreres.rh.minibodet.model;

public interface WorkDay extends Day{
    public WorkSchedule getSchedule();

    public String getComment();

    public WorkWeek getWeek();

    public void setComment(String com);
}
