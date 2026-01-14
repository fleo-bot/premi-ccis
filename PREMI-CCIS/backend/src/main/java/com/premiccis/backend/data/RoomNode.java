package com.premiccis.backend.data;
import com.premiccis.backend.model.Room;

public class RoomNode {
    public Room data;
    public RoomNode next;

    public RoomNode(Room data) {
        this.data = data;
        this.next = null;
    }
} 
