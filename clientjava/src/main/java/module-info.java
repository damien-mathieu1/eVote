module fr.umontpellier.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.base;

    requires javafx.graphics;


    requires java.desktop;
    requires java.sql;
    requires cyptageVote;
    requires google.http.client.jackson2;
    requires com.google.api.client;
    requires google.api.client;
    requires com.google.api.client.auth;
    requires mail;
    requires commons.lang3;
    //requires mysql.connector.java;


    exports fr.umontpellier.Model;
    opens fr.umontpellier.Model;
    exports fr.umontpellier.client;
    opens fr.umontpellier.client;
    exports fr.umontpellier.View;
    opens fr.umontpellier.View;

    exports fr.umontpellier.Controller;
    opens fr.umontpellier.Controller;


}