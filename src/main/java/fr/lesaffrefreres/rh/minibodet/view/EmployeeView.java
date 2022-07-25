package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.EmployeeViewController;
import fr.lesaffrefreres.rh.minibodet.controller.LabelViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class represents the view of the employee view.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class EmployeeView extends VBox {


    private final EmployeeViewController controller;

    public EmployeeViewController getController() {
        return controller;
    }
    /**
     * Load the view from the FXML file.
     */
    public EmployeeView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/employee-view.fxml"), bundle);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        controller = fxmlLoader.getController();
    }
}
