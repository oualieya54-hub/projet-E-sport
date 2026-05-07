package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Booking;
import org.example.Service.BookingService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BookingController {
    private BookingService bookingService;

    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> colIdBooking;
    @FXML private TableColumn<Booking, Integer> colIdSession;
    @FXML private TableColumn<Booking, Integer> colIdEleve;
    @FXML private TableColumn<Booking, String> colStatutPaiement;
    @FXML private TableColumn<Booking, LocalDateTime> colDateReservation;
    @FXML private TableColumn<Booking, String> colModePaiement;

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
        colIdBooking.setCellValueFactory(new PropertyValueFactory<>("idBooking"));
        colIdSession.setCellValueFactory(new PropertyValueFactory<>("idSession"));
        colIdEleve.setCellValueFactory(new PropertyValueFactory<>("idEleve"));
        colStatutPaiement.setCellValueFactory(new PropertyValueFactory<>("statutPaiement"));
        colDateReservation.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        colModePaiement.setCellValueFactory(new PropertyValueFactory<>("modePaiement"));

        bookingTable.setItems(bookingList);

        bookingTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookingDetails(newValue)
        );

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
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
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
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
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
        bookingTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Backend delegates ---
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
