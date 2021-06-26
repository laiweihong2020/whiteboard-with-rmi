package com.lai;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        MeetingRoomService server = new MeetingRoomImpl();
        try {
            MeetingRoomService stub = (MeetingRoomService) UnicastRemoteObject
                .exportObject((MeetingRoomService) server, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MeetingRoomService", stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
