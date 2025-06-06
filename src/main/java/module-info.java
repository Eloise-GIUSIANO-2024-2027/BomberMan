module org.bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.xml.dom;
    requires jdk.compiler;


    opens org.bomberman to javafx.fxml;
    exports org.bomberman;
    exports org.bomberman.entite;
    opens org.bomberman.entite to javafx.fxml;
}