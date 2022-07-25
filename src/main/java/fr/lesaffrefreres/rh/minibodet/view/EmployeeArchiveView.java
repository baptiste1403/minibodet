package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.EmployeeArchiveViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class represents the view of the employee archive view.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class EmployeeArchiveView extends VBox {

    /**
     * Load the view from the FXML file.
     */
    public EmployeeArchiveView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/employee-archive-view.fxml"), bundle);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
