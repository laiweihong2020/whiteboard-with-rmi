package com.lai;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface allows the server to send updates back to the 
 * client upon the completion / update of server operations
 */
public interface ClientCallbackService extends Remote{
    void updateMessage(String message) throws RemoteException;
    void updateShapes(String shapeJson) throws RemoteException;
    void updateText(String textJson) throws RemoteException;
    void updateUserList(List<String> userId) throws RemoteException;
    void removeUser() throws RemoteException;
    void createnewDocument() throws RemoteException;
    void updateDocument(String docJson) throws RemoteException;
}
