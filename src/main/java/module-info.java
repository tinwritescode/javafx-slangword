module com.github.tinplayscode.slang {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.tinplayscode.slang to javafx.fxml;
    exports com.github.tinplayscode.slang;
}