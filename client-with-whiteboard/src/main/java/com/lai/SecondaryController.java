package com.lai;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * The controller that controls the secondary view of client
 */
public class SecondaryController {
    private String roomId;
    private String userId;
    private MeetingRoomService server;
    private Stage stage;
    private FreeShape fs;
    private ClientCallbackHandler cbHandler;
    private int role;

    private static String operation = "cursor";
    private static double startX;
    private static double startY;
    private static String color = "black";
    private ObservableList<String> messageHistory = FXCollections.observableArrayList();
    private ObservableList<String> onlineUserList = FXCollections.observableArrayList();
    private ObservableList<String> colors = FXCollections.observableArrayList(
        "chocolate", "salmon", "gold", "coral", "darkorchid",
        "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
        "blueviolet", "brown", "yellow", "green", "red", "purple");

    @FXML
    private ListView<String> messageList;

    @FXML
    private TextField messageInput;

    @FXML
    private VBox chatBox;

    @FXML
    private Canvas canvas;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private ListView<String> userList;

    @FXML
    private Menu fileMenu;
    
    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    public SecondaryController(String rId, String uId, Stage stage) {
        this.roomId = rId;
        this.userId = uId;
        this.stage = stage;
        this.fs = new FreeShape();

        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            server = (MeetingRoomService) registry
                .lookup("MeetingRoomService");
            this.role = server.getUserRole(roomId, userId);
        } catch (RemoteException | NotBoundException e) {
            remoteExceptionAlert();
        }
        System.out.println(this.role);
    }

    /**
     * Initialise the components used by the secondary view
     */
    @FXML
    private void initialize() {
        // Initialise canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        try {
            initDocument();
        } catch (RemoteException e1) {
            remoteExceptionAlert();
        }

        // Add event handlers for drag operations
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e) {
                switch(operation) {
                    case "cursor":
                        gc.lineTo(e.getX(), e.getY());
                        gc.stroke();
                        processShape(startX, startY, e.getX(), e.getY(), "freeline");

                        gc.closePath();
                        gc.beginPath();
                        gc.moveTo(e.getX(), e.getY());
                        startX = e.getX();
                        startY = e.getY();
                        break;
                }
            }
        });

        // Add event handlers for press operations
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e) {
                switch(operation) {
                    case "cursor":
                        gc.setStroke(Color.web(color));
                        gc.beginPath();
                        gc.moveTo(e.getX(), e.getY());
                        gc.stroke();
                        setInitialPoint(e);
                        break;
                    case "line":
                        setInitialPoint(e);
                        break;
                    case "circle":
                        setInitialPoint(e);
                        break;
                    case "oval":
                        setInitialPoint(e);
                        break;
                    case "rectangle":
                        setInitialPoint(e);
                        break;
                    case "text":
                        setInitialPoint(e);
                        break;
                }
            }

            private void setInitialPoint(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }
        });

        // Add event handlers for release operations
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e) {
                switch(operation) {
                    case "cursor":
                        gc.lineTo(e.getX(), e.getY());
                        gc.stroke();
                        processShape(startX, startY, e.getX(), e.getY(), "freeline");
                        processFreeShape();
                        gc.closePath();
                        break;
                    case "line":
                        lineDrawing(e);
                        break;
                    case "circle":
                        circleDrawing(e);
                        break;
                    case "oval":
                        ovalDrawing(e);
                        break;
                    case "rectangle":
                        rectangleDrawing(e);
                        break;
                    case "text":
                        textDrawing(e);
                        break;

                }
            }

            private void textDrawing(MouseEvent e) {
                // Text dialog to set text
                TextInputDialog dialog = new TextInputDialog("walter");
                dialog.setTitle("Set Text");
                dialog.setHeaderText("Text Input");
                dialog.setContentText("Enter text: ");
                dialog.setResizable(true);
                dialog.onShowingProperty().addListener(event -> {
                    Platform.runLater(() -> dialog.setResizable(false));
                });

                String text = "";
                Optional<String>result = dialog.showAndWait();
                if (result.isPresent()) {
                    text = result.get();
                }

                gc.setStroke(Color.web(color));
                gc.strokeText(text, startX, startY);
                processText(text, startX, startY);
            }

            private void rectangleDrawing(MouseEvent e) {
                double width = e.getX() - startX;
                double height = e.getY() - startY;
                gc.setStroke(Color.web(color));
                gc.strokeRect(startX, startY, width, height);
                processShape(startX, startY, width, height, "rectangle");
            }

            private void ovalDrawing(MouseEvent e) {
                // Calculate the radius of the circle
                double width = e.getX() - startX;
                double height = e.getY() - startY;
                gc.setStroke(Color.web(color));
                gc.strokeOval(startX, startY, width, height);
                processShape(startX, startY, width, height, "oval");
            }

            private void circleDrawing(MouseEvent e) {
                // Calculate the radius of the circle
                double width = e.getX() - startX;
                gc.setStroke(Color.web(color));
                gc.strokeOval(startX, startY-width/2, width, width);
                processShape(startX, startY-width/2, width, width, "circle");
            }

            private void lineDrawing(MouseEvent e) {
                gc.setLineWidth(2);
                gc.setStroke(Color.web(color));
                gc.strokeLine(startX, startY, e.getX(), e.getY());
                processShape(startX, startY, e.getX(), e.getY(), "line");
            }
        });

        // Initialise combobox
        comboBox.setItems(colors);
        comboBox.valueProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                color = t1;
            }
        });

        // Initialise user list
        if (this.role == 0) {
            userList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> param) {
                    return new XCell(server, roomId, userId);
                }
            });
        }

        try {
            List<String>result = server.getUserList(roomId, userId);
            updateUserList(result);
        } catch (RemoteException e1) {
            remoteExceptionAlert();
        }

        // Disable the file enu for users
        if (this.role == 1) {
            fileMenu.setDisable(true);
        }
    }

    /**
     * Operation that returns the room number of the room.
     */
	@FXML
    private void getRoomNumber() {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setContentText(roomId);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setResizable(true);
        a.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> a.setResizable(false));
        });
        a.show();
    }

    /**
     * Operation when the user exits the room
     */
    @FXML
    private void exitRoom() {
        try {
            server.exitRoom(roomId, userId);
        } catch (RemoteException e) {
            remoteExceptionAlert();
        }

        // Stop the callback thread
        cbHandler.stop();

        // Move to the join screen
        FXMLLoader fxml = new FXMLLoader();

        fxml.setLocation(getClass().getResource("/com/lai/primary.fxml"));
        fxml.setController(new PrimaryController(stage));
        VBox root;
        try {
            root = fxml.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Whiteboard");
            stage.show();
        } catch (IOException e) {
            ioExceptionAlert();
        }
    }

    /**
     * Operation to remove user from the current room
     */
    @FXML
    public void removeUser() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Session ended by the manager");
        alert.setHeaderText("Session ended by the manager");
        alert.setContentText("Press OK to proceed");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(true);
        alert.onShowingProperty().addListener(e -> {
            Platform.runLater(() -> alert.setResizable(false));
        });
        alert.showAndWait();

        exitRoom();
    }

    @FXML
    private void onEnter(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            try {
                server.sendMessage(messageInput.getText(), roomId, userId);
                messageInput.setText("");
            } catch (RemoteException e1) {
                remoteExceptionAlert();
            }
            e.consume();
        }
    }

    @FXML
    private void setCursor() {
        operation = "cursor";
    }

    @FXML
    private void setLine() {
        operation = "line";
    }

    @FXML
    private void setCircle() {
        operation = "circle";
    }

    @FXML
    private void setOval() {
        operation = "oval";
    }

    @FXML
    private void setRectangle() {
        operation = "rectangle";
    }

    @FXML
    private void setText() {
        operation = "text";
    }

    public void setCallbackHandler(ClientCallbackHandler handler) {
        this.cbHandler = handler;
    }

    /**
     * Operation to add new messages to the message box
     * @param msg
     */
    public void addMessage(String msg) {
        messageHistory.add(msg);
        messageList.setItems(null);
        messageList.setItems(messageHistory);
    }

    /**
     * Updates the list of users using the shared whiteboard
     * @param userId
     */
    public void updateUserList(List<String> userId) {
        onlineUserList = FXCollections.observableArrayList(userId);
        userList.setItems(null);
        userList.setItems(onlineUserList);
    }

    public void addShape(Shape s) {
        String type = s.getType();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.web(s.getColor()));

        switch(type) {
            case "freeline":
            case "line":
                gc.strokeLine(s.getParam(0), s.getParam(1), s.getParam(2), s.getParam(3));
                break;
            case "circle":
                gc.strokeOval(s.getParam(0), s.getParam(1), s.getParam(2), s.getParam(3));
                break;
            case "oval":
                gc.strokeOval(s.getParam(0), s.getParam(1), s.getParam(2), s.getParam(3));
                break;
            case "rectangle":
                gc.strokeRect(s.getParam(0), s.getParam(1), s.getParam(2), s.getParam(3));
                break;
        }
    }

    /**
     * Adds a new free shape into the whiteboard
     * @param fs
     */
    public void addFreeShape(FreeShape fs) {
        List<Shape> points = fs.getAllPoints();
        for (Shape shape : points) {
            addShape(shape);
        }
    }

    /**
     * Adds a new text element into the whiteboard
     * @param t
     */
    public void addText(Text t) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.strokeText(t.getText(), t.getX(), t.getY());
    }

    /**
     * Operation to create new files 
     */
    public void createNewFile() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        try {
            server.clearDoc(roomId, userId);
        } catch (RemoteException e) {
            remoteExceptionAlert();
        }
    }

    /**
     * Operation to remove all drawings from the canvas
     */
    public void clearDoc() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Operation to save file 
     */
    public void saveFile() {
        FileChooser fileChooser = new FileChooser();

        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            saveDocToFile(selectedFile);
        }
    }

    private void saveDocToFile(File selectedFile) {
        try {
            String docString = server.getDoc(roomId, userId);
            Gson gson = new Gson();
            Document doc = gson.fromJson(docString, Document.class);
            FileWriter fileWriter = new FileWriter(selectedFile);

            gson.toJson(doc, fileWriter);
            fileWriter.close();
        } catch (RemoteException e) {
            remoteExceptionAlert();
        } catch (IOException e) {
            ioExceptionAlert();
        }
    }

    /**
     * Operation to open file
     */
    public void openFile() {
        FileChooser fileChooser = new FileChooser();

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            openDocToCanvas(selectedFile);
        }
    }

    private void openDocToCanvas(File selectedFile) {
        try {
            FileReader fileReader = new FileReader(selectedFile);
            Gson gson = new Gson();
            Document doc = gson.fromJson(fileReader, Document.class);
            initDocument(doc);

            String docJson = gson.toJson(doc);
            server.setDocument(roomId, userId, docJson);

            fileReader.close();
        } catch (IOException e) {
            ioExceptionAlert();
        }
    }

    /**
     * duplicate function for process shape
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param type
     */
    private void processShape(double p1, double p2, double p3, double p4, String type) {
        processShape(p1, p2, p3, p4, type, color);
    }

    /**
     * Sends the shape object back to the server
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param type
     * @param color
     */
    private void processShape(double p1, double p2, double p3, double p4, String type, String color) {
        Thread taskThread = new Thread(new Runnable(){
            @Override
            public void run() {
                ArrayList<Double> paramList = new ArrayList<Double>();
                paramList.add(p1);
                paramList.add(p2);
                paramList.add(p3);
                paramList.add(p4);
                
                Shape shape = new Shape();
                shape.setType(type);
                shape.setParamList(paramList);
                shape.setColor(color);
        
                if (type.equals("freeline")) {
                    fs.addPoints(shape);
                }
                
                Gson gson = new Gson();
                String shapeJson = gson.toJson(shape);
                try {
                    if (type.equals("freeline")) {
                        server.addFreelines(shapeJson, roomId, userId);
                    } else {
                        server.addShape(shapeJson, roomId, userId);
                    }
                } catch (RemoteException e) {
                    remoteExceptionAlert();
                }
            }
        });
        taskThread.start();
    }

    /**
     * Sends the text object back to the server
     * @param str
     * @param x
     * @param y
     */
    private void processText(String str, double x, double y) {
        Thread taskThread = new Thread(new Runnable(){
           @Override
           public void run() {
                Text t = new Text(str, x, y);

                Gson gson = new Gson();
                String textJson = gson.toJson(t);
                try {
                    server.addText(textJson, roomId, userId);
                } catch (RemoteException e) {
                    remoteExceptionAlert();
                }
           } 
        });
        taskThread.start();
    }

    /**
     * Sends a freeshapes to the server
     */
    private void processFreeShape() {
        List<String> result = new ArrayList<String>();
        List<Shape> points = fs.getAllPoints();
        fs.setNull();

        Gson gson = new Gson();
        for (Shape shape : points) {
            result.add(gson.toJson(shape));
        }
        
        try {
            server.addFreeShape(result, roomId, userId);
        } catch (RemoteException e) {
            remoteExceptionAlert();
        }
    }

    /**
     * Initialises the whiteboard after server query
     * @throws RemoteException
     */
    private void initDocument() throws RemoteException{
        clearDoc();
        List<String> docShapes = server.initShapesDoc(roomId, userId);
        List<String> docFreeShapes = server.initFreeShapesDoc(roomId, userId);
        List<String> docTexts = server.initTextDoc(roomId, userId);

        // Display all the shapes in the canvas from the document
        Gson gson = new Gson();
        for (String shapes : docShapes) {
            Shape s = gson.fromJson(shapes, Shape.class);
            addShape(s);
        }

        for (String freeshape : docFreeShapes) {
            FreeShape fs = gson.fromJson(freeshape, FreeShape.class);
            addFreeShape(fs);
        }

        for (String text : docTexts) {
            Text t = gson.fromJson(text, Text.class);
            addText(t);
        }
    }

    /**
     * Initialises the whiteboard after document object is supplied
     * @param doc
     */
    public void initDocument(Document doc) {
        clearDoc();
        List<Shape> docShapes = doc.getAllShapes();
        List<FreeShape> docFreeShapes = doc.getAllFreeShapes();
        List<Text> docTexts = doc.getAllTexts();

        for (Shape s : docShapes) {
            addShape(s);
        }

        for (FreeShape fs : docFreeShapes) {
            addFreeShape(fs);
        }

        for (Text t : docTexts) {
            addText(t);
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

    static class XCell extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("Remove");
        String lastItem;

        public XCell(MeetingRoomService server, String id, String userId) {
            super();

            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent e) {
                    try {
                        server.kickUser(id, lastItem, userId);
                    } catch (RemoteException e1) {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setHeaderText("Remote Exception Error");
                        a.setContentText("The server is unreachable. Please try again later.");
                        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        a.setResizable(true);
                        a.onShowingProperty().addListener(event -> {
                            Platform.runLater(() -> a.setResizable(false));
                        });
                        a.show();
                    }
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                lastItem = item;
                label.setText(item);
                setGraphic(hbox);
            }
        }
    }
}