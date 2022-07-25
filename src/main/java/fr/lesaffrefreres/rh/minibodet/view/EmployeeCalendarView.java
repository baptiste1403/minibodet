package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.*;
import fr.lesaffrefreres.rh.minibodet.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class represents the view of the employee calendar view.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 * @see EmployeeArchiveView
 */
public class EmployeeCalendarView extends HBox {

    /**
     * Load the view from the FXML file.
     */
    public EmployeeCalendarView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/employee-calendar-view.fxml"), bundle);
        fxmlLoader.setRoot(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
