package fr.lesaffrefreres.rh.minibodet.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.apache.ibatis.jdbc.SQL;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SQLDayLabelManager implements DayLabelManager {

    private static final class SQLDayLabel extends DayLabel {

        private long idLabel;

        public SQLDayLabel(String txt, Color c) {
            super(txt, c);
            Connection conn= DataBase.getInstance().getConnection();
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO LABEL(LABEL_COLOR, LABEL_TEXT) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, SQLDayLabelManager.toRGBCode(c));
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
            super("", Color.ALICEBLUE); // default value, will never be
            idLabel = idl;
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
                    text = rs.getString(1);
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
                ps.setString(1, SQLDayLabelManager.toRGBCode(c));
                ps.setLong(2, idLabel);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        public void setText(String txt) {
            text = txt;
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


    }

    private static String toRGBCode( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    private static SQLDayLabelManager instance;

    private static ObservableList<DayLabel> labels;

    private static long idUndefined;

    protected SQLDayLabelManager() {
        labels = FXCollections.observableArrayList();
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
    public void setDayLabelText(int id, String txt) {
        Objects.requireNonNull(txt);
        SQLDayLabel sdl = (SQLDayLabel) getDayLabelById(id);
        sdl.setText(txt);
    }

    @Override
    public void setDayLabelColor(int id, Color c) {
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

    @Override
    public List<DayLabel> getAllDayLabels() {
        return Collections.unmodifiableList(labels);
    }

    public ObservableList<DayLabel> getLabelsObservableList() {
        return FXCollections.unmodifiableObservableList(labels);
    }

    @Override
    public long getUndefinedDayLabelId() {
        return idUndefined;
    }
}
