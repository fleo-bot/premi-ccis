package com.premiccis.backend.service;

import com.premiccis.backend.model.LaboratoryRoom;
import com.premiccis.backend.model.Room;
import com.premiccis.backend.data.RoomLinkedList;
import com.premiccis.backend.data.RoomNode;
import com.premiccis.backend.dao.RequestInboxDAO;
import com.premiccis.backend.model.RoomRequest;

public class SequentialSearch {

    public static RoomLinkedList filterMatchingRooms(RoomLinkedList allRooms, RoomRequest request) {
        RoomLinkedList matched = new RoomLinkedList();
        RoomNode current = allRooms.getHead();

        while (current != null) {
            Room room = current.data;

            RoomRequest tempRequest = new RoomRequest();
            tempRequest.setChosenRoom(room.getRoomName());
            tempRequest.setBookingDate(request.getBookingDate());
            tempRequest.setStartTime(request.getStartTime());
            tempRequest.setDurationMinutes(request.getDurationMinutes());

            boolean hasConflict = RequestInboxDAO.isOverlappingBooking(tempRequest);
            if (hasConflict) {
                current = current.next;
                continue;
            }

            if (request.getRoomType().equalsIgnoreCase("laboratory")) {
                if (room instanceof LaboratoryRoom labRoom) {

                    boolean chairsOkay = labRoom.getCapacity() >= request.getNumberOfStudents();
                    if (chairsOkay) {
                        matched.add(labRoom);
                    }
                }
            } else if (request.getRoomType().equalsIgnoreCase("lecture")) {
                if (!(room instanceof LaboratoryRoom)) {
                    matched.add(room);
                }
            }


            current = current.next;
        }

        return matched;
    }

}
