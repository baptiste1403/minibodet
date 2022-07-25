package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.helpers.ColorHelper;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * This class represents a simple persistent implementation of {@link Day}
 * It represents a day with a date and a label
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class SQLDay implements Day, SQLObject, CalendarElement{

    protected long idDay;
    private long bufferedIdCalendar;
    private LocalDate bufferedDate;
    private long bufferedIdLabel;

    /**
     * creates a  new SQLDay matching an existing day in the database
     * @param idd the id of the day in the database
     */
    public SQLDay(long idd) {
        if(idd < 0) {
            throw new IllegalArgumentException("giveen id must be greater or equals to 0, and this object must exist in database");
        }
        idDay = idd;
        updateBuffer();
    }

    /**
     * creates a new SQLDay with a given date and calendar database id, it represents a new day not already in the database
     * @param date the date of the day
     * @param idc the id of the calendar in the database
     */
    public SQLDay(LocalDate date, long idc) {
        idDay = -1;
        bufferedDate = date;
        bufferedIdCalendar = idc;
        bufferedIdLabel = SQLDayLabelManager.getInstance().getUndefinedDayLabelId();
    }

    /**
     * update the buffered values of the day with the values in the database
     * does nothing if the day is not already in the database
     * @throws SQLException if an error occurs while accessing the database (usually when the given id doesn't match an existing day in database)
     */
    public void updateBuffer() {
        if(idDay < 0) {
            return;
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_LABEL, DAY_DATE, ID_CALENDAR FROM LABELDAY WHERE ID_LABELDAY = ?;");
            ps.setLong(1, idDay);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedIdLabel = rs.getInt(1);
                bufferedDate = rs.getDate(2).toLocalDate();
                bufferedIdCalendar = rs.getLong(3);
            } else {
                ps.close();
                throw new IllegalArgumentException("the given id doesn't exist in DB");
            }
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * return the id of the label id of the day
     * @return the id of the label id of the day
     */
    @Override
    public long getLabelId() {
        return bufferedIdLabel;
    }

    /**
     * return the date of the day
     * @return the date of the day
     */
    @Override
    public LocalDate getDate() {
        return bufferedDate;
    }

    /**
     * Set the label id of the day, if the day is not in the database
     * and the given id is different from the buffered one,
     * it will be created by calling {@link #create()}
     * @param id the label to set.
     */
    @Override
    public void setLabelId(long id) { // TODO test if given id exist
        if(bufferedIdLabel == id) {
            return;
        }
        bufferedIdLabel = id;
        if(idDay < 0){
            create();
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE LABELDAY SET ID_LABEL = ? WHERE ID_LABELDAY = ?;");
            ps.setLong(1, id);
            ps.setLong(2, idDay);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Set the label id of the day, if the day is not in the database
     * and the given id is different from the buffered one,
     * it will be created by calling <strong>only if mustsave is true</strong> {@link #create()}
     * @param id the label id to set.
     * @param mustSave if true, the day will be created in the database if it doesn't exist yet
     */
    public void setLabelId(long id, boolean mustSave) {
        if(mustSave || idDay >= 0) {
            setLabelId(id);
        } else {
            bufferedIdLabel = id;
        }
    }

    /**
     * Return the id of the day in the database, or -1 if the day is not in the database
     * @return
     */
    @Override
    public long getId() {
        return idDay;
    }

    /**
     * This method is used to create the day in the database if it doesn't exist yet, it can be called by {@link #setLabelId(long, boolean)}
     * if the day doesn't exist yet in the database or by other classes if they need this day to be in the database before creating themselfs.
     * if the day is already in the database, this method only return the id of this day in the database.
     * @throws SQLException if an error occurs while accessing the database (usually when the given id doesn't match an existing day in database)
     * @return the id of the day in the database
     */
    @Override
    public long create() {
        if(idDay >= 0) {
            return idDay;
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO LABELDAY (DAY_DATE, ID_CALENDAR, ID_LABEL) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(bufferedDate));
            ps.setLong(2, bufferedIdCalendar);
            ps.setLong(3, bufferedIdLabel);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.first();
            idDay = rs.getLong(1);
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return idDay;
    }

    @Override
    public String toString() {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        return dlm.getDayLabelById(getLabelId()).getText();
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void setView(Label label) {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        label.setBackground(new Background(new BackgroundFill(dlm.getDayLabelById(getLabelId()).getColor(), null, null)));
        label.setText(getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()) + " " + dlm.getDayLabelById(getLabelId()).getText());
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void setViewOnSelected(Label label) {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        label.setBackground(new Background(new BackgroundFill(dlm.getDayLabelById(getLabelId()).getColor().darker(), null, null)));
        label.setText(getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()) + " " + dlm.getDayLabelById(getLabelId()).getText());
    }
}
