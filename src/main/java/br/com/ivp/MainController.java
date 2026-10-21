package br.com.ivp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.nio.file.Path;

public class MainController {
    @FXML private TableView<Host> hostsTable;
    @FXML private TableColumn<Host, String> hostnameColumn;
    @FXML private TableColumn<Host, String> ipColumn;
    @FXML private TableColumn<Host, String> locationColumn;
    @FXML private Label statusLabel;

    private final Path excelPath = Path.of(System.getenv().getOrDefault("APPDATA",
            Path.of(System.getProperty("user.home"), "AppData", "Roaming").toString()),
            "Ivp_data", "data_hosts.xlsx");

    @FXML
    private void initialize() {
        hostnameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().hostname()));
        ipColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().ipAddress()));
        locationColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().location()));
        reload();
    }

    @FXML
    private void reload() {
        try {
            var hosts = new ExcelService().readHosts(excelPath);
            hostsTable.setItems(FXCollections.observableArrayList(hosts));
            statusLabel.setText(hosts.size() + " registro(s) carregado(s) — " + excelPath);
        } catch (Exception ex) {
            hostsTable.getItems().clear();
            statusLabel.setText("Erro: " + ex.getMessage());
            System.err.println("Falha ao ler a planilha: " + excelPath);
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
        }
    }
}
