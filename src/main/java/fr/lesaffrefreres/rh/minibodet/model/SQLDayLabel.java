package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.helpers.ColorHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.sql.*;

public class SQLDayLabel implements DayLabel {

    private long idLabel;

    private StringProperty text;

    private Color color;

    public SQLDayLabel(String txt, Color c) {
        color = c;
        text = new SimpleStringProperty();
        text.set(txt);

        Connection conn= DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO LABEL(LABEL_COLOR, LABEL_TEXT) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ColorHelper.toRGBCode(c));
            ps.setString(2, txt);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.first();
            idLabel = rs.getLong(1);
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public SQLDayLabel(long idl) {
        idLabel = idl;
        text = new SimpleStringProperty();
        updateBuffer();
    }

    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT LABEL_TEXT, LABEL_COLOR FROM LABEL WHERE LABEL_ID = ?");
            ps.setLong(1, idLabel);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                text.set(rs.getString(1));
                color = Color.web(rs.getString(2));
            } else {
                ps.close();
                throw new IllegalArgumentException("given id doesn't exist in DB");
            }
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public long getId() {
        return idLabel;
    }

    public void setColor(Color c) {
        color = c;
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE LABEL SET LABEL_COLOR = ? WHERE LABEL_ID = ?;");
            ps.setString(1, ColorHelper.toRGBCode(c));
            ps.setLong(2, idLabel);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void setText(String txt) {
        text.set(txt);
        Connection conn= DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE LABEL SET LABEL_TEXT = ? WHERE LABEL_ID = ?;");
            ps.setString(1, txt);
            ps.setLong(2, idLabel);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public String getText() {
        return text.get();
    }

    @Override
    public StringProperty textProperty() {
        return text;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return text.get();
    }
}
