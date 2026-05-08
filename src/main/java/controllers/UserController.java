package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import models.User;
import service.UserService;

import java.sql.Timestamp;
import java.util.List;

public class UserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPseudo;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Boolean> colBanned;
    @FXML private TableColumn<User, Timestamp> colLastActive;
    @FXML private TableColumn<User, Void> colBanAction;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbRoleFilter;

    @FXML private TextField txtNom;
    @FXML private TextField txtPseudo;
    @FXML private TextField txtEmail;
    @FXML private TextField txtRole;
    @FXML private TextField txtAvatarUrl;

    private UserService userService;
    private ObservableList<User> userList;
    private FilteredList<User> filteredData;

    @FXML
    public void initialize() {
        userService = new UserService();
        userList = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(userList, p -> true);

        cbRoleFilter.setItems(FXCollections.observableArrayList("All", "Player", "Captain", "Coach", "Guest", "Admin"));
        cbRoleFilter.setValue("All");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPseudo.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colBanned.setCellValueFactory(new PropertyValueFactory<>("isBanned"));
        colLastActive.setCellValueFactory(new PropertyValueFactory<>("lastActive"));

        addBanButtonToTable();

        loadUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cbRoleFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void addBanButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button();

                    {
                        btn.setOnAction(event -> {
                            User data = getTableView().getItems().get(getIndex());
                            boolean newBanStatus = !data.getIsBanned();
                            if (userService.setBanned(data.getId(), newBanStatus)) {
                                data.setIsBanned(newBanStatus);
                                userTable.refresh();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update ban status.");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User data = getTableView().getItems().get(getIndex());
                            if (data != null) {
                                if (data.getIsBanned()) {
                                    btn.setText("Unban");
                                    btn.getStyleClass().setAll("button", "button-accent");
                                } else {
                                    btn.setText("Ban");
                                    btn.getStyleClass().setAll("button", "button-danger");
                                }
                                setGraphic(btn);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        };
        colBanAction.setCellFactory(cellFactory);
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedRole = cbRoleFilter.getValue();

        filteredData.setPredicate(user -> {
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = (user.getNom() != null && user.getNom().toLowerCase().contains(searchText))
                        || (user.getPseudo() != null && user.getPseudo().toLowerCase().contains(searchText));
            }

            boolean matchesRole = true;
            if (selectedRole != null && !"All".equals(selectedRole)) {
                matchesRole = user.getRole() != null && user.getRole().equalsIgnoreCase(selectedRole);
            }

            return matchesSearch && matchesRole;
        });
        
        userTable.setItems(filteredData);
    }

    private void loadUsers() {
        userList.clear();
        List<User> users = userService.getAllUsers();
        userList.addAll(users);
        userTable.setItems(filteredData);
    }

    private void populateFields(User user) {
        txtNom.setText(user.getNom());
        txtPseudo.setText(user.getPseudo());
        txtEmail.setText(user.getEmail());
        txtRole.setText(user.getRole());
        txtAvatarUrl.setText(user.getAvatarUrl());
    }

    @FXML
    public void handleAddUser() {
        if (txtNom.getText().isEmpty() || txtPseudo.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill out at least Name, Pseudo, and Email.");
            return;
        }

        String role = txtRole.getText();
        if (role == null || role.isEmpty()) {
            role = "Guest";
        }

        User newUser;
        if ("Player".equalsIgnoreCase(role)) {
            newUser = new models.Player(txtNom.getText(), txtPseudo.getText(), txtEmail.getText(), "default123", "FIFA", "Bronze");
        } else if ("Coach".equalsIgnoreCase(role)) {
            newUser = new models.Coach(txtNom.getText(), txtPseudo.getText(), txtEmail.getText(), "default123", "FIFA");
        } else if ("Captain".equalsIgnoreCase(role)) {
            newUser = new models.Captain(txtNom.getText(), txtPseudo.getText(), txtEmail.getText(), "default123", "FIFA", "Bronze");
        } else {
            newUser = new models.Guest(txtNom.getText(), txtPseudo.getText(), txtEmail.getText(), "default123");
        }

        newUser.setAvatarUrl(txtAvatarUrl.getText());
        newUser.setRole(role);

        if (userService.addUser(newUser)) {
            loadUsers();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully. Default password is 'default123'.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user. Email or pseudo might already exist.");
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
