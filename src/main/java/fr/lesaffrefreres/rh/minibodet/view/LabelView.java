package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.EmployeeViewController;
import fr.lesaffrefreres.rh.minibodet.controller.LabelViewController;
import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class represents the view of the label view.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class LabelView extends VBox {

    private final LabelViewController controller;

    /**
     * Load the view from the FXML file.
     */
    public LabelView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/label-view.fxml"), bundle);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        controller = fxmlLoader.getController();
    }

    public void setEmployeeController(EmployeeViewController evc) {
        controller.setEmployeeController(evc);
    }
}
