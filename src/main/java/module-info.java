module org.bomberman {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.bomberman to javafx.fxml;
    exports org.bomberman;
}