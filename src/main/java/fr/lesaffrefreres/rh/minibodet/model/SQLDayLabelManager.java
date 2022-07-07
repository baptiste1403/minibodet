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

public class SQLDayLabelManager implements DayLabelManager {

    private static SQLDayLabelManager instance;

    private static ObservableList<DayLabel> labels;

    private static long idUndefined;

    protected SQLDayLabelManager() {
        labels = FXCollections.observableArrayList(new DayLabelExtractor());
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT LABEL_ID FROM LABEL;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                labels.add(new SQLDayLabel(rs.getLong(1)));
            }
            ps.close();
            if(labels.isEmpty()) {
                SQLDayLabel undefinedLabel = new SQLDayLabel("indéfini", Color.LIGHTGREY);
                idUndefined = undefinedLabel.getId();
                labels.add(undefinedLabel);
            } else {
                idUndefined = getDayLabelIdByName("indéfini");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static SQLDayLabelManager getInstance() {
        if(instance == null) {
            instance = new SQLDayLabelManager();
        }
        return instance;
    }

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

    @Override
    public DayLabel getDayLabelById(long id) {
        for(DayLabel cur : labels) {
            if(((SQLDayLabel)cur).getId() == id) {
                return cur;
            }
        }
        throw new IllegalArgumentException("label with id " + id + " do not exist");
    }

    @Override
    public void removeDayLabelById(long id) {
        if(!containsDayLabel(id)) {
            return;
        }
        if(id == getUndefinedDayLabelId()) {
            throw new IllegalArgumentException("cannot remove default undefined label");
        }
        DayLabel dl = getDayLabelById(id);
        labels.remove(labels.indexOf(dl));
    }

    @Override
    public void setDayLabelText(long id, String txt) {
        Objects.requireNonNull(txt);
        SQLDayLabel sdl = (SQLDayLabel) getDayLabelById(id);
        sdl.setText(txt);
    }

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

    @Override
    public boolean labelExist(long id) {
        return containsDayLabel(id);
    }

    public boolean labelExist(String name) {
        return containsDayLabel(name);
    }

    @Override
    public List<DayLabel> getAllDayLabels() {
        return Collections.unmodifiableList(labels);
    }

    public ObservableList<DayLabel> getLabelsObservableList() {
        return labels;
    }

    @Override
    public long getUndefinedDayLabelId() {
        return idUndefined;
    }
}
