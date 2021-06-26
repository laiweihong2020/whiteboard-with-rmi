package com.lai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

public class MeetingRoomImpl implements MeetingRoomService {
    private HashMap<Integer, MeetingRoom> meetingRooms = new HashMap<Integer, MeetingRoom>();
    private int min = 0;
    private int max = 9999;

    @Override
    public String createNewRoom(String userId) {
        int roomId;
        do {
            // Generate a random four digit room id
            roomId = ThreadLocalRandom.current().nextInt(min, max+1);
        } while (meetingRooms.containsKey(roomId));

        // Create a manager level user
        User initialUser = new User(userId, 0);
        MeetingRoom meetingRoom = new MeetingRoom(roomId);
        meetingRoom.addUser(initialUser);
        meetingRooms.put(roomId, meetingRoom);
        return Integer.toString(roomId);
    }

    @Override
    public boolean joinRoom(String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user has a unique user name
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(room.checkUser(user)) {
            return false;
        }

        // Add the user into the room
        room.addUser(user);
        return true;
    }

    @Override
    public boolean exitRoom(String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        // Retrieve user from the room data
        user = room.getUser(user);

        // If the roomsize is greater than 1, just leave
        if(user.getRole() != 0) {
            room.removeUser(user);
        } else {
            room.removeUser(user);

            // Remove all users from the session
            room.removeAllUsers();
            
            meetingRooms.remove(roomId);
        }
        return true;
    }

    @Override
    public boolean sendMessage(String msg, String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        // Add the message into the messagehistory
        room.addMessage(msg);
        return true;
    }

    @Override
    public boolean addShape(String s, String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        room.addShape(s);
        return true;
    }

    @Override
    public boolean addFreelines(String s, String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }
        room.addFreelines(s);
        return true;
    }

    /**
     * Send the list of shapes saved in the server to the client
     */
    @Override
    public List<String> initShapesDoc(String id, String userId) {
        List<String> result = new ArrayList<String>();
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return result;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return result;
        }

        List<Shape> shapes = room.getDoc().getAllShapes();
        Gson gson = new Gson();
        for (Shape shape : shapes) {
            result.add(gson.toJson(shape));
        }
        return result;
    }

    /**
     * Send the list of free shapes saved on the server to the client
     */
    @Override
    public List<String> initFreeShapesDoc(String id, String userId) {
        List<String> result = new ArrayList<String>();
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return result;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return result;
        }

        List<FreeShape> shapes = room.getDoc().getAllFreeShapes();
        Gson gson = new Gson();
        for (FreeShape shape : shapes) {
            result.add(gson.toJson(shape));
        }
        return result;
    }

    /**
     * Send the list of text object on the server to the client
     */
    @Override
    public List<String> initTextDoc(String id, String userId) {
        List<String> result = new ArrayList<String>();
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return result;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return result;
        }

        List<Text> texts = room.getDoc().getAllTexts();
        Gson gson = new Gson();
        for (Text text : texts) {
            result.add(gson.toJson(text));
        }
        return result;
    }

    @Override
    public boolean addFreeShape(List<String> list, String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        room.addFreeShape(list);
        return true;
    }

    @Override
    public boolean addText(String s, String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        room.addText(s);
        return true;
    }

    @Override
    public boolean clearDoc(String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        // Set the doc to empty and send a callback to reset all canvases
        room.newDoc();
        return true;
    }

    @Override
    public String getDoc(String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return "";
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return "";
        }

        // Get the document from the room
        Document doc = room.getDoc();
        Gson gson = new Gson();
        return gson.toJson(doc);
    }

    @Override
    public List<String> getUserList(String id, String userId) {
        List<String> result = new ArrayList<String>();
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return result;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return result;
        }

        List<User> userList = room.getUserList();
        for (User u : userList) {
            result.add(u.getId());
        }
        return result;
    }

    /**
     * Update the initialisation status of the client
     * This is here because there will be times where the client is added
     * to the list of active users but have not intialised its callback handler
     */
    @Override
    public boolean setInitialisationStatus(String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        room.getUser(user).setInitStatus(true);
        return true;
    }

    @Override
    public boolean kickUser(String id, String userId, String kickerId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        User kicker = new User(kickerId);
        if(!room.checkUser(kicker)) {
            return false;
        }

        // ACtion of kicking the user
        room.kickUser(userId);
        return true;
    }

    @Override
    public int getUserRole(String id, String userId) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return 1;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return 1;
        }

        return room.getUser(user).getRole();
    }

    @Override
    public boolean setDocument(String id, String userId, String docJson) {
        // Check if the room exists
        int roomId = Integer.parseInt(id);
        if(!meetingRooms.containsKey(roomId)) {
            return false;
        }

        // Check if the user is in the room
        MeetingRoom room = meetingRooms.get(roomId);
        User user = new User(userId);
        if(!room.checkUser(user)) {
            return false;
        }

        Gson gson = new Gson();
        Document doc = gson.fromJson(docJson, Document.class);
        room.setDoc(doc);
        return true;
    }
}
