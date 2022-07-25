package fr.lesaffrefreres.rh.minibodet.controller;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import net.harawata.appdirs.AppDirsFactory;

import java.net.URISyntaxException;

public class AboutViewController {
    private HostServices services;

    public void setHostServices(HostServices hs) {
        services = hs;
    }

    @FXML
    private void onHyperLinkClick() {
        services.showDocument("https://baptiste1403.github.io/manuel-bodet.pdf");
    }
}
