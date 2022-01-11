module com.example.pantera {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.pantera to javafx.fxml;
    opens com.example.pantera.controller to javafx.fxml;
    opens com.example.pantera.domain to javafx.fxml;
    opens com.example.pantera.service to javafx.fxml;
    opens com.example.pantera.events to javafx.fxml;
    opens com.example.pantera.repository to javafx.fxml;
    opens com.example.pantera.ui to javafx.fxml;
    opens com.example.pantera.utils to javafx.fxml;
    opens com.example.pantera.domain.validators to javafx.fxml;
    exports com.example.pantera;
    exports com.example.pantera.controller;
    exports com.example.pantera.domain;
    exports com.example.pantera.service;
    exports com.example.pantera.events;
    exports com.example.pantera.repository;
    exports com.example.pantera.ui;
    exports com.example.pantera.utils;
    exports com.example.pantera.domain.validators;
    requires org.apache.pdfbox;
}