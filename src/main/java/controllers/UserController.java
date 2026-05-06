package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.User;
import service.UserService;

import java.util.List;

public class UserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPseudo;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Integer> colPoints;
    @FXML private TableColumn<User, Boolean> colBanned;

    @FXML private TextField txtNom;
    @FXML private TextField txtPseudo;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtRole;
    @FXML private TextField txtAvatarUrl;

    private UserService userService;
    private ObservableList<User> userList;

    @FXML
    public void initialize() {
        userService = new UserService();
        userList = FXCollections.observableArrayList();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPseudo.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colBanned.setCellValueFactory(new PropertyValueFactory<>("isBanned"));

        loadUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void loadUsers() {
        userList.clear();
        List<User> users = userService.getAllUsers();
        userList.addAll(users);
        userTable.setItems(userList);
    }

    private void populateFields(User user) {
        txtNom.setText(user.getNom());
        txtPseudo.setText(user.getPseudo());
        txtEmail.setText(user.getEmail());
        txtRole.setText(user.getRole());
        txtAvatarUrl.setText(user.getAvatarUrl());
        // Do not populate password for security
        txtPassword.clear();
    }

    @FXML
    public void handleAddUser() {
        if (txtNom.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Name, Email, and Password are required fields.");
            return;
        }

        User user = new User(
            txtNom.getText(),
            txtPseudo.getText(),
            txtEmail.getText(),
            txtPassword.getText(),
            txtAvatarUrl.getText(),
            txtRole.getText().isEmpty() ? "player" : txtRole.getText()
        );
        
        if (userService.addUser(user)) {
            loadUsers();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user.");
        }
    }

    @FXML
    public void handleUpdateUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to update.");
            return;
        }

        selected.setNom(txtNom.getText());
        selected.setPseudo(txtPseudo.getText());
        selected.setEmail(txtEmail.getText());
        selected.setAvatarUrl(txtAvatarUrl.getText());
        if (!txtRole.getText().isEmpty()) {
            selected.setRole(txtRole.getText());
        }

        if (userService.updateUser(selected)) {
            // Check if password update is requested
            if (!txtPassword.getText().isEmpty()) {
                userService.updatePassword(selected.getId(), txtPassword.getText());
            }
            loadUsers();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user.");
        }
    }

    @FXML
    public void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete user " + selected.getPseudo() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            if (userService.deleteUser(selected.getId())) {
                loadUsers();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
            }
        }
    }

    @FXML
    public void handleClear() {
        clearFields();
        userTable.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        txtNom.clear();
        txtPseudo.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtRole.clear();
        txtAvatarUrl.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
