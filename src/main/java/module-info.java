module com.github.tinplayscode.slang {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.collections4;


    opens com.github.tinplayscode.slang to javafx.fxml;
    exports com.github.tinplayscode.slang;
}