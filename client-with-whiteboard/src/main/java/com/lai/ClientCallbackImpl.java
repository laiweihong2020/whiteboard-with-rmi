package com.lai;

import java.util.List;

import com.google.gson.Gson;

import javafx.application.Platform;

public class ClientCallbackImpl implements ClientCallbackService{
    private SecondaryController controller;

    public ClientCallbackImpl(SecondaryController controller) {
        this.controller = controller;
    }

    @Override
    public void updateMessage(String message) {
        Thread taskThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        controller.addMessage(message);
                    }
                });
            }
        });

        taskThread.start();
    }

    @Override
    public void updateShapes(String stringJson) {
        Thread taskThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Shape s = gson.fromJson(stringJson, Shape.class);
                        controller.addShape(s);
                    }
                });
            }
        });

        taskThread.start();
    }

    @Override
    public void updateText(String stringJson) {
        Thread taskThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Text s = gson.fromJson(stringJson, Text.class);
                        controller.addText(s);
                    }
                });
            }
        });

        taskThread.start();
    }

    @Override
    public void createnewDocument() {
        Thread taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        controller.clearDoc();
                    }
                });
            }
        });

        taskThread.start();
    }

    @Override
    public void updateUserList(List<String> userId) {
        Thread taskThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        controller.updateUserList(userId);
                    }
                });
            }
        });
        taskThread.start();
    }

    @Override
    public void removeUser() {
        Thread taskThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        controller.removeUser();
                    }
                });
            }
        });
        taskThread.start();
    }

    @Override
    public void updateDocument(String docJson) {
        Thread taskThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Document doc = gson.fromJson(docJson, Document.class);
                        controller.initDocument(doc);
                    }
                });
            }
        });

        taskThread.start();
    }
}
