package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.helpers.ColorHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.sql.*;

/**
 * This class represents a persistent implementation of a day label.
 * This class is usually and must be used only by the {@link SQLDayLabelManager}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class SQLDayLabel implements DayLabel {

    private long idLabel;

    private StringProperty text;

    private Color color;

    /**
     * Creates a new SQLDayLabel that does not exist in the database, and inserts it in the database.
     * the id of the label is set to the id of the newly created label.
     * @param txt the text of the label
     * @param c the color of the label
     */
    public SQLDayLabel(String txt, Color c) {
        color = c;
        text = new SimpleStringProperty();
        text.set(txt);

        Connection conn= DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO LABEL(COLOR, TEXT) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
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

    /**
     * Creates a new SQLDayLabel that exists in the database, and loads it from the database.
     * @param idl the id of the label in the database
     */
    public SQLDayLabel(long idl) {
        idLabel = idl;
        text = new SimpleStringProperty();
        updateBuffer();
    }

    /**
     * Updates the buffer of the label with the data from the database.
     */
    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT TEXT, COLOR FROM LABEL WHERE ID_LABEL = ?");
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

    /**
     * Return the id of the label in the database.
     * @return the id of the label in the database.
     */
    public long getId() {
        return idLabel;
    }

    /**
     * Set the color of the label.
     * @param c the color of the label
     */
    public void setColor(Color c) {
        color = c;
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE LABEL SET COLOR = ? WHERE ID_LABEL = ?;");
            ps.setString(1, ColorHelper.toRGBCode(c));
            ps.setLong(2, idLabel);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Set the text of the label.
     * @param txt the text of the label
     */
    public void setText(String txt) {
        text.set(txt);
        Connection conn= DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE LABEL SET TEXT = ? WHERE ID_LABEL = ?;");
            ps.setString(1, txt);
            ps.setLong(2, idLabel);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Return the text of the label.
     * @return the text of the label.
     */
    @Override
    public String getText() {
        return text.get();
    }

    /**
     * Return the textProperty of the label.
     * @return the textProperty of the label.
     */
    @Override
    public StringProperty textProperty() {
        return text;
    }

    /**
     * Return the color of the label.
     * @return the color of the label.
     */
    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return text.get();
    }
}
