package com.lai;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MeetingRoomService extends Remote{
    String createNewRoom(String userId) throws RemoteException;
    boolean joinRoom(String roomId, String userId) throws RemoteException;
    boolean exitRoom(String roomId, String userId) throws RemoteException; 
    boolean kickUser(String roomId, String userId, String kickerId) throws RemoteException;
    boolean sendMessage(String msg, String roomId, String userId) throws RemoteException;
    boolean addShape(String s, String roomId, String userId) throws RemoteException;
    boolean addFreelines(String s, String roomId, String userId) throws RemoteException;
    boolean addFreeShape(List<String> list, String roomId, String userId) throws RemoteException;
    boolean addText(String str, String roomId, String userId) throws RemoteException;
    boolean clearDoc(String roomId, String userId) throws RemoteException;
    boolean setInitialisationStatus(String roomId, String userId) throws RemoteException;
    boolean setDocument(String roomId, String userId, String docJson) throws RemoteException;
    String getDoc(String roomId, String userId) throws RemoteException;
    int getUserRole(String roomId, String userId) throws RemoteException;
    List<String> getUserList(String roomId, String userId) throws RemoteException;
    List<String> initFreeShapesDoc(String roomId, String userId) throws RemoteException;
    List<String> initShapesDoc(String roomId, String userId) throws RemoteException;
    List<String> initTextDoc(String roomId, String userId) throws RemoteException;
}