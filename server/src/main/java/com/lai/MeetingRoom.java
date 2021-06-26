package com.lai;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;


public class MeetingRoom {
    private int id;
    private HashMap<String, User> userList = new HashMap<String, User>();
    private ArrayList<String> messageList = new ArrayList<String>();
    private Document doc = new Document();

    public MeetingRoom(int roomId) {
        this.id = roomId;
    }

    public int roomSize() {
        return userList.size();
    }

    public Document getDoc() {
        return this.doc;
    }

    public void setDoc(Document d) {
        this.doc = d;

        Gson gson = new Gson();
        String docJson = gson.toJson(this.doc);

        // Notify all users about the new document
        for (User user : userList.values()) {
            String callbackIdentifier = id + user.getId();

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.updateDocument(docJson);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addUser(User user) {
        userList.put(user.getId(), user);

        // List conversion
        List<User> list = new ArrayList<User>(userList.values());
        List<String> result = new ArrayList<String>();

        for (User u : list) {
            result.add(u.getId());
        }

        for (User u : userList.values()) {
            String callbackIdentifier = id + u.getId();

            if(!u.getInitStatus()) {
                continue;
            }

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.updateUserList(result);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeUser(User user) {
        userList.remove(user.getId());
    }

    public void removeAllUsers() {
        for (User user : userList.values()) {
            String callbackIdentifier = id + user.getId();

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.removeUser();
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessage(String msg) {
        messageList.add(msg);

        // Notify all users about the new message
        for (User user : userList.values()) {
            String callbackIdentifier = id + user.getId();

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.updateMessage(msg);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void kickUser(String userId) {
        String callbackIdentifier = id + userId;
        userList.remove(userId);

        try {
            Registry registry = LocateRegistry.getRegistry();
            ClientCallbackService client = (ClientCallbackService) registry
                .lookup(callbackIdentifier);
            client.removeUser();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        // List conversion
        List<User> list = new ArrayList<User>(userList.values());
        List<String> result = new ArrayList<String>();

        for (User u : list) {
            result.add(u.getId());
        }

        for (User u : userList.values()) {
            String cbIdentifier = id + u.getId();

            if(!u.getInitStatus()) {
                continue;
            }

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(cbIdentifier);
                client.updateUserList(result);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void printMessageHistory() {
        for (String msg : messageList) {
            System.out.println(msg);
        }
    }

    public boolean checkUser(User user) {
        if(userList.containsKey(user.getId())) {
            return true;
        }
        return false;
    }

    public User getUser(User u) {
        return userList.get(u.getId());
    }

    public List<User> getUserList() {
        return new ArrayList<User>(userList.values()); 
    }

    public void addShape(String s) {
        Gson gson = new Gson();
        Shape shape = gson.fromJson(s, Shape.class);

        doc.addShape(shape);    

        // Notify all users about the new message
        for (User user : userList.values()) {
            String callbackIdentifier = id + user.getId();

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.updateShapes(s);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFreelines(String s) {
        // Notify all users about the new line
        for (User user : userList.values()) {
            String callbackIdentifier = id + user.getId();

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.updateShapes(s);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFreeShape(List<String> list) {
        // Create the free shape object
        FreeShape fs = new FreeShape();

        Gson gson = new Gson();
        for (String s : list) {
            Shape shape = gson.fromJson(s, Shape.class);
            fs.addPoints(shape);
        }

        doc.addFreeShape(fs);
    }

    public void addText(String s) {
        // Create the text object
        Gson gson = new Gson();
        Text t = gson.fromJson(s, Text.class);

        doc.addText(t);

        // Notify all users about the new message
        for (User user : userList.values()) {
            String callbackIdentifier = id + user.getId();

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.updateText(s);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void newDoc() {
        this.doc = new Document();

        // Notify all users about the new document
        for (User user : userList.values()) {
            String callbackIdentifier = id + user.getId();

            try {
                Registry registry = LocateRegistry.getRegistry();
                ClientCallbackService client = (ClientCallbackService) registry
                    .lookup(callbackIdentifier);
                client.createnewDocument();
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }
}
