package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.controller.extractor.DayLabelExtractor;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.ibatis.jdbc.SQL;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a manager for the persistent day labels. {@link SQLDayLabel}
 * it must be used to create, delete and update all the SQLDayLabel.
 * This class is a singleton.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class SQLDayLabelManager implements DayLabelManager {

    private static SQLDayLabelManager instance;

    private static ObservableList<DayLabel> labels;

    private static long idUndefined;
    private static long idBankHoliday;
    private static long idHoliday;
    private static long idSickLeave;

    private static long idWork;

    private static long idRest;

    private static long idUnjustifiedAbsence;

    protected SQLDayLabelManager() {
        labels = FXCollections.observableArrayList(new DayLabelExtractor());
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_LABEL FROM LABEL;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                labels.add(new SQLDayLabel(rs.getLong(1)));
            }
            ps.close();
            idUndefined = createIfNotExist("indéfini", Color.LIGHTGREY);
            idBankHoliday = createIfNotExist("férié", Color.LIGHTGREEN);
            idHoliday = createIfNotExist("CP", Color.LIGHTYELLOW);
            idSickLeave = createIfNotExist("AM", Color.SALMON);
            idWork = createIfNotExist("travail", Color.LIGHTBLUE);
            idRest = createIfNotExist("repos", Color.LIMEGREEN);
            idUnjustifiedAbsence = createIfNotExist("abs inj", Color.ALICEBLUE);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private long createIfNotExist(String text, Color color) {
        long id = 0;
        try {
            id = getDayLabelIdByName(text);
        } catch (IllegalArgumentException iae) { // label doesn't exist
            id = createDayLabel(text, color);
        }
        return id;
    }

    /**
     * Returns the instance of the SQLDayLabelManager.
     * @return the instance of the SQLDayLabelManager.
     */
    public static SQLDayLabelManager getInstance() {
        if(instance == null) {
            instance = new SQLDayLabelManager();
        }
        return instance;
    }

    /**
     * Creates a new day label. and returns the id of the new day label.
     * the id can be used to get the day label by calling {@link #getDayLabelById(long)}.
     * @param txt the text of the label.
     * @param c the color of the label.
     * @return the id of the new day label.
     */
    @Override
    public long createDayLabel(String txt, Color c) {
        Objects.requireNonNull(txt);
        Objects.requireNonNull(c);
        if(containsDayLabel(txt)) {
            throw new IllegalArgumentException("label " + txt + " already exist");
        }

        SQLDayLabel res = new SQLDayLabel(txt, c);
        labels.add(res);
        return res.getId();
    }

    /**
     * returns the id of the day label with the given name.
     * @param txt the text of the label to find.
     * @return the id of the day label with the given name.
     * @throws IllegalArgumentException if the label does not exist.
     */
    @Override
    public long getDayLabelIdByName(String txt) {
        Objects.requireNonNull(txt);
        for(DayLabel cur : labels) {
            if(cur.getText().equals(txt)) {
                return ((SQLDayLabel)cur).getId();
            }
        }
        throw new IllegalArgumentException("label " + txt + " do not exist");
    }

    /**
     * returns the day label with the given id.
     * @param id the id of the label to find.
     * @return the day label with the given id.
     * @throws IllegalArgumentException if the label does not exist.
     */
    @Override
    public DayLabel getDayLabelById(long id) {
        for(DayLabel cur : labels) {
            if(((SQLDayLabel)cur).getId() == id) {
                return cur;
            }
        }
        throw new IllegalArgumentException("label with id " + id + " do not exist");
    }

    /**
     * delete the day label with the given id from the database.
     * day in the database with the given id will be updated with the undefined label.
     * @param id the id of the label to remove.
     * if no label with the given id is found, nothing will change.
     */
    @Override
    public void removeDayLabelById(long id) {
        if(!containsDayLabel(id)) {
            return;
        }
        if(isReserved(id))
        {
            throw new IllegalArgumentException("cannot remove defaults labels");
        }

        DayLabel dl = getDayLabelById(id);

        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement psLabelDay = conn.prepareStatement("UPDATE LABELDAY SET ID_LABEL = ? WHERE ID_LABEL = ?;");
            psLabelDay.setLong(1, getUndefinedDayLabelId());
            psLabelDay.setLong(2, id);
            psLabelDay.executeUpdate();

            PreparedStatement psSchedule = conn.prepareStatement("UPDATE SCHEDULE SET ID_LABEL = ? WHERE  ID_LABEL = ?");
            psSchedule.setLong(1, getUndefinedDayLabelId());
            psSchedule.setLong(2, id);
            psSchedule.executeUpdate();

            PreparedStatement psLabel = conn.prepareStatement("DELETE FROM LABEL WHERE ID_LABEL = ?;");
            psLabel.setLong(1, id);
            psLabel.execute();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        labels.remove(dl);
    }

    /**
     * Check if the given label id is reserved.
     * the reserved labels list is :
     * - "undefined"
     * - "bank holiday"
     * - "holiday"
     * - "sick leave"
     * - "work"
     * - "rest"
     * @param id the id of the label to check.
     * @return true if the label is reserved, false otherwise.
     */
    public boolean isReserved(long id) {
        return id == getUndefinedDayLabelId() ||
                id == getWorkDayLabelId() ||
                id == getRestDayLabelId() ||
                id == getSickLeaveDayLabelId() ||
                id == getBankHolidayDayLabelId() ||
                id == getHolidayDayLabelId() ||
                id == getUnjustifiedAbsenceLabelId();
    }

    /**
     * Set the text of the label with the given id.
     * @param id the id of the label to set.
     * @param txt the text to set.
     * @throws IllegalArgumentException if the label does not exist.
     * do nothing if the label is reserved.
     */
    @Override
    public void setDayLabelText(long id, String txt) {
        Objects.requireNonNull(txt);
        if(isReserved(id)) {
            return;
        }
        SQLDayLabel sdl = (SQLDayLabel) getDayLabelById(id);
        sdl.setText(txt);
    }

    /**
     * Set the color of the label with the given id.
     * @param id the id of the label to set.
     * @param c the color to set.
     * @throws IllegalArgumentException if the label does not exist.
     */
    @Override
    public void setDayLabelColor(long id, Color c) {
        Objects.requireNonNull(c);
        SQLDayLabel sdl = (SQLDayLabel) getDayLabelById(id);
        sdl.setColor(c);
    }


    private boolean containsDayLabel(String txt) {
        Objects.requireNonNull(txt);
        for(DayLabel cur : labels) {
            if(cur.getText().equals(txt)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDayLabel(long id) {
        for(DayLabel cur : labels) {
            if(((SQLDayLabel)cur).getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the label with the given id exists.
     * @param id the id of the label to check.
     * @return true if the label exists, false otherwise.
     */
    @Override
    public boolean labelExist(long id) {
        return containsDayLabel(id);
    }

    /**
     * Check if the label with the given text exists.
     * @param name the text of the label to check.
     * @return true if the label exists, false otherwise.
     */
    public boolean labelExist(String name) {
        return containsDayLabel(name);
    }

    /**
     * Return the unmodifiable list of all the labels.
     * @return the unmodifiable list of all the labels.
     */
    @Override
    public List<DayLabel> getAllDayLabels() {
        return Collections.unmodifiableList(labels);
    }

    /**
     * Return the unmodifiable ObservableList of all the labels.
     * @return the unmodifiable ObservableList of all the labels.
     */
    public ObservableList<DayLabel> getLabelsObservableList() {
        return FXCollections.unmodifiableObservableList(labels);
    }

    /**
     * Return the id of the "undefined" label.
     * @return the id of the "undefined" label.
     */
    @Override
    public long getUndefinedDayLabelId() {
        return idUndefined;
    }

    /**
     * Return the id of the "bank holiday" label.
     * @return the id of the "bank holiday" label.
     */
    public long getBankHolidayDayLabelId() {
        return idBankHoliday;
    }

    /**
     * Return the id of the "holiday" label.
     * @return the id of the "holiday" label.
     */
    public long getHolidayDayLabelId() {
        return idHoliday;
    }

    /**
     * Return the id of the "sick leave" label.
     * @return the id of the "sick leave" label.
     */
    public long getSickLeaveDayLabelId() {
        return idSickLeave;
    }

    /**
     * Return the id of the "work" label.
     * @return the id of the "work" label.
     */
    public long getWorkDayLabelId() {
        return idWork;
    }

    /**
     * Return the id of the "rest" label.
     * @return the id of the "rest" label.
     */
    public long getRestDayLabelId() {
        return idRest;
    }

    public long getUnjustifiedAbsenceLabelId() {
        return idUnjustifiedAbsence;
    }
}
