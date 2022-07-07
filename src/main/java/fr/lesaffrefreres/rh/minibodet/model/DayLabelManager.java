package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;

public interface DayLabelManager {
    public long createDayLabel(String txt, Color c);

    public long getDayLabelIdByName(String txt);

    public DayLabel getDayLabelById(long id);

    public void removeDayLabelById(long id);

    public void setDayLabelText(int id, String txt);

    public void setDayLabelColor(int id, Color c);

    public boolean labelExist(long id);

    public List<DayLabel> getAllDayLabels();

    public long getUndefinedDayLabelId();
}
