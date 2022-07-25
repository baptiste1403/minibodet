package fr.lesaffrefreres.rh.minibodet;

import fr.lesaffrefreres.rh.minibodet.controller.TabChangedListener;
import fr.lesaffrefreres.rh.minibodet.model.DataBase;
import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.model.EmployeeManager;
import fr.lesaffrefreres.rh.minibodet.model.SQLEmployeeManager;
import fr.lesaffrefreres.rh.minibodet.view.AboutView;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeView;
import fr.lesaffrefreres.rh.minibodet.view.LabelView;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("Mini Bodet");
        stage.setMaximized(true);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        TabPane tp = (TabPane) scene.lookup("#tabs");

        TabChangedListener tcl = new TabChangedListener(tp);
        for (Tab cur : tp.getTabs()) {
            cur.setOnSelectionChanged(tcl);
        }

        //injection du controller de employeeView dans LabelView
        LabelView lv = (LabelView)scene.lookup("#labelView");

        EmployeeView ev = (EmployeeView)scene.lookup("#parent");

        lv.setEmployeeController(ev.getController());

        EmployeeManager<Employee> em = SQLEmployeeManager.getInstance();

        CalendarView cv = (CalendarView) scene.getRoot().lookup("#calendar");

        cv.setCalendar(em.getCalendar());

        cv.setSelected(LocalDate.now());

        AboutView aboutView = (AboutView) scene.lookup("#aboutView");

        aboutView.getController().setHostServices(getHostServices());

        stage.setScene(scene);
        stage.show();

        // -------------------------- test part must be cleaned before export --------------------------
    }

    @Override
    public void stop() throws Exception {
        if (DataBase.getInstance() != null) {
            DataBase.getInstance().getConnection().close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}