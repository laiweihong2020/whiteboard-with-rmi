package com.lai;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackHandler implements Runnable{
    private String callBackIdentifier = "";
    private SecondaryController controller;
    private Registry registry;
    private Thread worker;
    private String userId;
    private String roomId;

    public ClientCallbackHandler(String roomId, String userId, SecondaryController controller) {
        callBackIdentifier = roomId + userId;
        this.controller = controller;
        this.userId = userId;
        this.roomId = roomId;
    }

    public void start() {
        worker = new Thread(this);
        worker.start();
        controller.setCallbackHandler(this);
    }

    public void stop() {
        worker.interrupt();
    }

    @Override
    public void run() {
        ClientCallbackService server = new ClientCallbackImpl(controller);
        try {
            ClientCallbackService stub = (ClientCallbackService) UnicastRemoteObject
                .exportObject((ClientCallbackService) server, 0);
            registry = LocateRegistry.getRegistry(1099);
            registry.rebind(callBackIdentifier, stub);

            MeetingRoomService mrs = (MeetingRoomService) registry
                .lookup("MeetingRoomService");
            mrs.setInitialisationStatus(roomId, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
