module MyStore {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.json;
    requires java.sql;
    requires java.desktop;


    opens Controller to javafx.fxml;
    opens View to javafx.fxml;
    opens Controller.menuController to javafx.fxml;
    opens View.menuView to javafx.fxml;

    exports Controller;
    exports Start;
    exports Controller.menuController;


}