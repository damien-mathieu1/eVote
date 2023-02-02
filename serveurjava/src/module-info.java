module saevote {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires cyptageVote;
    requires mysql.connector.java;

    opens fr.umontpellier.controller;
    exports fr.umontpellier.controller;
    opens fr.umontpellier;
    exports fr.umontpellier;
}
