package br.com.ivp;

import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.awt.Desktop;
import java.io.IOException;
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
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void ping() {
        Host selected = hostsTable.getSelectionModel().getSelectedItem();
        TextInputDialog dialog = new TextInputDialog(selected == null ? "" : selected.ipAddress());
        dialog.initOwner(hostsTable.getScene().getWindow());
        dialog.setTitle("Ping");
        dialog.setHeaderText("Informe o IP ou hostname para executar o ping.");
        dialog.setContentText("Destino:");
        dialog.showAndWait().ifPresent(value -> {
            String destination = value.trim();
            if (!destination.matches("[A-Za-z0-9][A-Za-z0-9.:_-]*")) {
                showMessage(Alert.AlertType.WARNING, "Ping", "Informe um IP ou hostname válido.");
                return;
            }
            try {
                new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", "ping.exe", "-t", destination).start();
            } catch (IOException ex) {
                showMessage(Alert.AlertType.ERROR, "Ping", "Não foi possível iniciar o ping: " + ex.getMessage());
            }
        });
    }

    @FXML
    private void openDataFolder() {
        try {
            Desktop.getDesktop().open(excelPath.getParent().toFile());
        } catch (IOException | UnsupportedOperationException | IllegalArgumentException | SecurityException ex) {
            showMessage(Alert.AlertType.ERROR, "Abrir pasta", "Não foi possível abrir a pasta "
                    + excelPath.getParent() + ": " + ex.getMessage());
        }
    }

    @FXML
    private void showAbout() {
        showMessage(Alert.AlertType.INFORMATION, "Sobre", "IVP Hosts\nInventário de hosts a partir da planilha data_hosts.xlsx.");
    }

    private void showMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(hostsTable.getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
