module br.elissonsouza.controleordens {
    requires transitive java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;

    opens br.elissonsouza.controleordens to javafx.fxml;
    exports br.elissonsouza.controleordens;
    
    opens br.elissonsouza.controleordens.controller to javafx.fxml;
    exports br.elissonsouza.controleordens.controller;

    opens br.elissonsouza.controleordens.model to javafx.fxml;
    exports br.elissonsouza.controleordens.model;

    opens br.elissonsouza.controleordens.dao to javafx.fxml;
    exports br.elissonsouza.controleordens.dao;
}