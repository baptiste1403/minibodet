module fr.lesaffrefreres.rh.minibodet {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.localedata;
    requires com.h2database;
    requires net.harawata.appdirs;
    requires org.mybatis;
    requires java.sql;

    opens fr.lesaffrefreres.rh.minibodet to javafx.fxml;
    opens fr.lesaffrefreres.rh.minibodet.view to javafx.fxml;
    exports fr.lesaffrefreres.rh.minibodet;
    exports fr.lesaffrefreres.rh.minibodet.view;
}