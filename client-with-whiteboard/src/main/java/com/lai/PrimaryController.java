package com.lai;

import java.io.IOException;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PrimaryController {
    private Stage stage;

    @FXML
    private TextField createRoomUID;

    @FXML
    private TextField joinRoomId;

    @FXML
    private TextField joinUserId;

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    public PrimaryController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {

    }

    /**
     * Controls the actions when the user ceates a new meeting room
     */
    @FXML
    private void createNewMeetingRoom() {
        boolean hasError = false;

        MeetingRoomService server = null;
        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            server = (MeetingRoomService) registry
                .lookup("MeetingRoomService");
        } catch (AccessException e) {
            accessExceptionAlert();
            hasError = true;
        } catch (RemoteException e) {
            remoteExceptionAlert();
            hasError = true;
        } catch (NotBoundException e) {
            notBoundException();
            hasError = true;
        } 

        String userId = createRoomUID.getText();
        String roomId = "";
        try {
            roomId = server.createNewRoom(userId);
        } catch (RemoteException e) {
            remoteExceptionAlert();
            hasError = true;
        } catch (NullPointerException e) {
            // This is covered by the remote exception
            hasError = true;
        }

        if (!hasError) {
            SecondaryController controller = new SecondaryController(roomId, userId, stage);

            ClientCallbackHandler callBackThread = new ClientCallbackHandler(roomId, userId, controller);
            callBackThread.start();  

            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource("/com/lai/secondary.fxml"));
            fxml.setController(controller);
            VBox interfaceScene = new VBox();
            try {
                interfaceScene =fxml.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Scene scene = new Scene(interfaceScene);
            stage.setScene(scene);  
            
            final String rId = roomId;
            final String uId = userId;
            final MeetingRoomService s = server;

            stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                @Override
                public void handle(WindowEvent t) {
                    t.consume();
                    exitRoom(rId, uId, s);
                    System.exit(0);
                }
            });
            stage.show();
        }
    }

    @FXML
    private void remoteExceptionAlert() {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("Remote Exception Error");
        a.setContentText("The server is unreachable. Please try again later.");
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setResizable(true);
        a.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> a.setResizable(false));
        });
        a.show();
    }

    @FXML
    private void notBoundException() {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("Not Bound Exception Error");
        a.setContentText("The server may be sleeping now. Please try again later.");
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setResizable(true);
        a.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> a.setResizable(false));
        });
        a.show();
    }

    @FXML
    private void ioExceptionAlert() {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("IO Exception Error");
        a.setContentText("The FXML cannot be accessed. It may have been removed or modified from its initial location");
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setResizable(true);
        a.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> a.setResizable(false));
        });
        a.show();
    }

    @FXML
    private void accessExceptionAlert() {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("Access Exception Error");
        a.setContentText("You may not have permission to access the server. Please contact the service provider.");
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setResizable(true);
        a.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> a.setResizable(false));
        });
        a.show();
    }

    @FXML
    private void duplicateUserAlert() {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("Incorrect details Error");
        a.setContentText("The user name or room number is incorrect. Please check and try again");
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setResizable(true);
        a.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> a.setResizable(false));
        });
        a.show();
    }

    @FXML
    private void generalErrorExeption() {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("General Error");
        a.setContentText("Something went wrong. Please try again later.");
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setResizable(true);
        a.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> a.setResizable(false));
        });
        a.show();
    }

    protected void exitRoom(String roomId, String userId, MeetingRoomService server) {
        try {
            server.exitRoom(roomId, userId);
        } catch (RemoteException e) {
            remoteExceptionAlert();
        }
    }

    /**
     * Controls the action that allows users to join meeting rooms
     */
    @FXML
    private void joinMeetingRoom() {
        boolean hasError = false;

        MeetingRoomService server = null;
        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            server = (MeetingRoomService) registry
                .lookup("MeetingRoomService");
        } catch (RemoteException | NotBoundException e2) {
            remoteExceptionAlert();
            hasError = true;
        }

        String roomId = joinRoomId.getText();
        String userId = joinUserId.getText();

        try {
            int r = Integer.parseInt(roomId);
            System.out.println(r);
        } catch (Exception e) {
            duplicateUserAlert();
            return;
        }

        try {
            boolean operation = server.joinRoom(roomId, userId);
            if (!operation) {
                hasError = true;
                duplicateUserAlert();
            }
        } catch (RemoteException e1) {
            remoteExceptionAlert();
            hasError = true;
        } catch (NullPointerException e2) {
            hasError = true;
        }

        if (!hasError) {
            SecondaryController controller = new SecondaryController(roomId, userId, stage);

            // Start a new callback sequence
            ClientCallbackHandler callBackThread = new ClientCallbackHandler(roomId, userId, controller);
            callBackThread.start();  

            FXMLLoader fxml = new FXMLLoader();
            fxml.setLocation(getClass().getResource("/com/lai/secondary.fxml"));
            fxml.setController(controller);
            VBox interfaceScene = new VBox();
            try {
                interfaceScene =fxml.load();
            } catch (IOException e) {
                ioExceptionAlert();
            }

            Scene scene = new Scene(interfaceScene);
            stage.setScene(scene);

            final String rId = roomId;
            final String uId = userId;
            final MeetingRoomService s = server;

            stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                @Override
                public void handle(WindowEvent t) {
                    t.consume();
                    exitRoom(rId, uId, s);
                    System.exit(0);
                }
            });
            stage.show();
        }
    }
}
