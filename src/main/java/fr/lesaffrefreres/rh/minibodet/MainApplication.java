package fr.lesaffrefreres.rh.minibodet;

import fr.lesaffrefreres.rh.minibodet.controller.*;
import fr.lesaffrefreres.rh.minibodet.model.*;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        TabPane tp = (TabPane)scene.lookup("#tabs");

        TabChangedListener tcl = new TabChangedListener(tp);
        for(Tab cur : tp.getTabs()) {
            cur.setOnSelectionChanged(tcl);
        }

        EmployeeManager<Employee> em = SQLEmployeeManager.getInstance();

        CalendarView cv = (CalendarView) scene.getRoot().lookup("#calendar");

        cv.setCalendar(em.getCalendar());

        cv.setSelectedDate(LocalDate.now());

        stage.setTitle("Mini Bodet");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if(DataBase.getInstance() != null) {
            DataBase.getInstance().getConnection().close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}