module com.capacita.irede {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    exports com.capacita.irede.app;

    opens com.capacita.irede.controller to javafx.fxml;
    opens com.capacita.irede.model to javafx.base;
}