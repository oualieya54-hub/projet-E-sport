package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Booking;
import org.example.Service.BookingService;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingController {
    private BookingService bookingService;

    @FXML private ListView<Booking> bookingListView;
    @FXML private TextField searchField;

    @FXML private TextField idSessionField;
    @FXML private TextField idEleveField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private ComboBox<String> statutPaiementCombo;
    @FXML private ComboBox<String> modePaiementCombo;

    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    public BookingController() {
        this.bookingService = new BookingService();
    }

    @FXML
    public void initialize() {
        // 1. Configure ListView CellFactory
        bookingListView.setCellFactory(param -> new ListCell<Booking>() {
            @Override
            protected void updateItem(Booking item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    VBox card = new VBox(5);
                    card.getStyleClass().add("list-card");

                    HBox header = new HBox(10);
                    Label title = new Label("Réservation #" + item.getIdBooking());
                    title.getStyleClass().add("item-title");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label mode = new Label(item.getModePaiement() != null ? item.getModePaiement().toUpperCase() : "N/A");
                    mode.getStyleClass().add("item-title");
                    mode.setStyle("-fx-text-fill: #e91e63;");

                    header.getChildren().addAll(title, spacer, mode);

                    String dateStr = item.getDateReservation() != null ? item.getDateReservation().toString().replace("T", " ") : "N/A";
                    Label detail = new Label("Session: " + item.getIdSession() + " | Elève: " + item.getIdEleve() + " | " + dateStr);
                    detail.getStyleClass().add("item-detail");

                    HBox footer = new HBox(10);
                    Label badge = new Label(item.getStatutPaiement() != null ? item.getStatutPaiement().toUpperCase() : "PENDING");
                    badge.getStyleClass().add("badge");
                    if ("confirmé".equalsIgnoreCase(item.getStatutPaiement())) badge.getStyleClass().add("badge-active");
                    else if ("annulé".equalsIgnoreCase(item.getStatutPaiement())) badge.getStyleClass().add("badge-danger");
                    else badge.getStyleClass().add("badge-warning");

                    footer.getChildren().add(badge);

                    card.getChildren().addAll(header, detail, footer);
                    setGraphic(card);
                }
            }
        });

        bookingListView.setItems(bookingList);

        // 2. Setup ListView selection listener to fill the form
        bookingListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookingDetails(newValue)
        );

        // 3. Initialize ComboBox items
        statutPaiementCombo.setItems(FXCollections.observableArrayList("en_attente", "confirmé", "annulé"));
        modePaiementCombo.setItems(FXCollections.observableArrayList("carte", "virement", "espèces", "gratuit"));

        // 4. Load data
        loadData();
    }

    private void loadData() {
        try {
            List<Booking> data = bookingService.getAll();
            bookingList.setAll(data);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les réservations", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showBookingDetails(Booking b) {
        if (b != null) {
            idSessionField.setText(String.valueOf(b.getIdSession()));
            idEleveField.setText(String.valueOf(b.getIdEleve()));
            if (b.getDateReservation() != null) {
                dateReservationPicker.setValue(b.getDateReservation().toLocalDate());
            }
            statutPaiementCombo.getSelectionModel().select(b.getStatutPaiement());
            modePaiementCombo.getSelectionModel().select(b.getModePaiement());
        } else {
            handleReset(null);
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            Booking b = new Booking(
                    0,
                    Integer.parseInt(idSessionField.getText()),
                    Integer.parseInt(idEleveField.getText()),
                    statutPaiementCombo.getValue()
            );
            if (dateReservationPicker.getValue() != null) {
                b.setDateReservation(dateReservationPicker.getValue().atTime(LocalTime.now()));
            }
            b.setModePaiement(modePaiementCombo.getValue());
            bookingService.book(b);
            loadData();
            handleReset(null);
            showAlert("Succès", "Réservation ajoutée", null, Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Erreur", "Saisie invalide", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Booking selected = bookingListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setIdSession(Integer.parseInt(idSessionField.getText()));
                selected.setIdEleve(Integer.parseInt(idEleveField.getText()));
                if (dateReservationPicker.getValue() != null) {
                    selected.setDateReservation(dateReservationPicker.getValue().atTime(LocalTime.now()));
                }
                selected.setStatutPaiement(statutPaiementCombo.getValue());
                selected.setModePaiement(modePaiementCombo.getValue());
                bookingService.update(selected);
                loadData();
                showAlert("Succès", "Réservation mise à jour", null, Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Erreur", "Mise à jour impossible", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Booking selected = bookingListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                bookingService.cancel(selected.getIdBooking());
                loadData();
                handleReset(null);
                showAlert("Succès", "Réservation supprimée", null, Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Erreur", "Suppression impossible", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleReset(ActionEvent event) {
        idSessionField.clear();
        idEleveField.clear();
        dateReservationPicker.setValue(null);
        statutPaiementCombo.getSelectionModel().clearSelection();
        modePaiementCombo.getSelectionModel().clearSelection();
        bookingListView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleFilter() {
        String keyword = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        try {
            List<Booking> all = bookingService.getAll();
            List<Booking> filtered = all.stream()
                .filter(b -> keyword.isEmpty() || String.valueOf(b.getIdBooking()).contains(keyword) || (b.getStatutPaiement() != null && b.getStatutPaiement().toLowerCase().contains(keyword)))
                .collect(Collectors.toList());
            bookingList.setAll(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void navigateToDashboard(ActionEvent event) { switchScene(event, "/AcademyMainView.fxml"); }

    @FXML
    void navigateToFormation(ActionEvent event) { switchScene(event, "/FormationView.fxml"); }

    @FXML
    void navigateToSession(ActionEvent event) { switchScene(event, "/SessionView.fxml"); }

    @FXML
    void navigateToBooking(ActionEvent event) { switchScene(event, "/BookingView.fxml"); }

    @FXML
    void navigateToEvaluation(ActionEvent event) { switchScene(event, "/EvaluationView.fxml"); }

    @FXML
    void navigateToCertification(ActionEvent event) { switchScene(event, "/CertificationView.fxml"); }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void book(Booking b) throws SQLException { bookingService.book(b); }
    public Booking getById(int idBooking) throws SQLException { return bookingService.getById(idBooking); }
    public List<Booking> getByEleve(int idEleve) throws SQLException { return bookingService.getByEleve(idEleve); }
    public List<Booking> getAll() throws SQLException { return bookingService.getAll(); }
    public void update(Booking b) throws SQLException { bookingService.update(b); }
    public void confirmPayment(int idBooking) throws SQLException { bookingService.confirmPayment(idBooking); }
    public void cancel(int idBooking) throws SQLException { bookingService.cancel(idBooking); }
    public List<Booking> getBookingsBySession(int idSession) throws SQLException { return bookingService.getBookingsBySession(idSession); }
    public boolean hasAlreadyBooked(int idEleve, int idSession) throws SQLException { return bookingService.hasAlreadyBooked(idEleve, idSession); }
    public void bookSafe(Booking b) throws SQLException { bookingService.bookSafe(b); }
    public int getNombreReservations(int idSession) throws SQLException { return bookingService.getNombreReservations(idSession); }
}
