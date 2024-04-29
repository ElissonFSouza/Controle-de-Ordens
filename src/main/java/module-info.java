module br.elissonsouza.controleordens2 {
    requires transitive java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens br.elissonsouza.controleordens2 to javafx.fxml;
    exports br.elissonsouza.controleordens2;
    
    opens br.elissonsouza.controleordens2.controller to javafx.fxml;
    exports br.elissonsouza.controleordens2.controller;

    opens br.elissonsouza.controleordens2.model to javafx.fxml;
    exports br.elissonsouza.controleordens2.model;

    opens br.elissonsouza.controleordens2.dao to javafx.fxml;
    exports br.elissonsouza.controleordens2.dao;
}