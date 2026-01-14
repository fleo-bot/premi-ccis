package com.premiccis.backend.controller;

import com.premiccis.backend.dao.RoomFinderDAO;
import com.premiccis.backend.dao.RequestInboxDAO;
import com.premiccis.backend.model.*;
import com.premiccis.backend.data.RoomLinkedList;
import com.premiccis.backend.service.RoomHashMap;
import com.premiccis.backend.service.SequentialSearch;
import com.premiccis.backend.service.ShellSort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @GetMapping("/scheduled-rooms")
    public ResponseEntity<List<Map<String, Object>>> getScheduledRooms() {
        List<Map<String, Object>> bookings = RequestInboxDAO.getAllCurrentBookings();
        return ResponseEntity.ok(bookings);
    }

    @Autowired
    private RoomHashMap roomHashMap;

    @PostMapping("/book-rooms")
    public ResponseEntity<?> bookRoom(@RequestBody RoomRequest roomBooked) {

        if (!roomHashMap.contains(roomBooked.getChosenRoom())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Unknown room: " + roomBooked.getChosenRoom()));
        }

        boolean overlap = RequestInboxDAO.isOverlappingBooking(roomBooked);
        if (overlap) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Room is already booked during this period."));
        }

        boolean success = RequestInboxDAO.bookRoomDirectly(roomBooked);
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "message", "Room successfully booked",
                    "room", roomBooked.getChosenRoom()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save booking."));
        }
    }



    @PostMapping("/reject-request")
    public ResponseEntity<Map<String, String>> rejectRequest(@RequestBody Map<String, Object> payload) {
        int requestId = (int) payload.get("requestId");

        boolean success = RequestInboxDAO.deleteRequest(requestId);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Request rejected."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to reject request."));
        }
    }


    @PostMapping("/approve-request")
    public ResponseEntity<Map<String, String>> approveRequest(@RequestBody Map<String, Object> payload) {
        Integer requestId = (Integer) payload.get("requestId");

        if (requestId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing requestId"));
        }

        RoomRequest request = RequestInboxDAO.getRequestById(requestId);
        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Request not found"));
        }

        boolean success = RequestInboxDAO.approveAndBook(request);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Request approved."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to approve request."));
        }
    }

    @GetMapping("/pending-requests")
    public List<Map<String, Object>> getPendingRequests() {
        List<RoomRequest> pending = RequestInboxDAO.getAllPending();

        return pending.stream().map(request -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", request.getUserId());
            map.put("id", request.getRequestId());
            map.put("roomName", request.getChosenRoom());
            map.put("date", request.getBookingDate().toString());
            map.put("time", request.getStartTime().toString());
            map.put("duration", request.getDurationMinutes());
            map.put("students", request.getNumberOfStudents());
            map.put("pcs", request.getRequiredPCs());
            map.put("roomType", RoomFinderDAO.getRoomType(request.getChosenRoom()));

            map.put("userName", RoomFinderDAO.getUserNameById(request.getUserId()));
            map.put("fullName", (request.getFullName() != null && !request.getFullName().trim().isEmpty()) ? request.getFullName() : "Not Provided");
            map.put("professorName", (request.getProfessorName() != null && !request.getProfessorName().trim().isEmpty()) ? request.getProfessorName() : "Not Provided");
            map.put("program", (request.getProgram() != null && !request.getProgram().trim().isEmpty()) ? request.getProgram() : "BSIT");
            map.put("section", (request.getSection() != null && !request.getSection().trim().isEmpty()) ? request.getSection() : "Unknown");
            map.put("year", request.getYear() != 0 ? request.getYear() : "N/A");

            return map;
        }).collect(Collectors.toList());
    }


    @PostMapping("/available-rooms")
    public List<?> getAvailableRooms(@RequestBody RoomRequestPayload payload) {
        RoomRequest request = new RoomRequest();

        request.setRoomType(payload.getRoomType());

        try {
            String formattedTime = payload.getTime().length() == 5 ? payload.getTime() + ":00" : payload.getTime();
            request.setBookingDate(Date.valueOf(payload.getDate()));
            request.setStartTime(Time.valueOf(formattedTime));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date or time format.");
        }

        request.setDurationMinutes(payload.getDuration());
        request.setRequiredPCs(payload.getPcs());
        request.setNumberOfStudents(payload.getStudents());

        RoomLinkedList allUnoccupiedRooms = RoomFinderDAO.getAllUnoccupiedRooms();

        RoomLinkedList typeFilteredRooms = new RoomLinkedList();
        for (Room room : allUnoccupiedRooms.toArray()) {
            if ("laboratory".equalsIgnoreCase(request.getRoomType()) && room instanceof LaboratoryRoom) {
                typeFilteredRooms.add(room);
            } else if ("lecture".equalsIgnoreCase(request.getRoomType()) && !(room instanceof LaboratoryRoom)) {
                typeFilteredRooms.add(room);
            }
        }

        RoomLinkedList available = SequentialSearch.filterMatchingRooms(typeFilteredRooms, request);

        if ("laboratory".equalsIgnoreCase(request.getRoomType())) {
            Room[] roomArray = available.toArray();
            ShellSort.sort(roomArray);
            return Arrays.asList(roomArray);
        }

        return available.toRoomNames();
    }



    @GetMapping("/bookings/faculty/{userId}")
    public List<BookingDTO> getFacultyInbox(@PathVariable int userId) {
        List<RoomRequest> requests = RequestInboxDAO.getUserInbox(userId);
        List<BookingDTO> dtos = new ArrayList<>();

        for (RoomRequest req : requests) {
            String room = req.getChosenRoom();
            String date = req.getBookingDate().toString();
            String timeIn = req.getStartTime().toString();
            String timeOut = req.calculateEndTime().toString();
            String status = req.getStatus();

            String roomType = RoomFinderDAO.getRoomType(room);

            dtos.add(new BookingDTO(room, roomType, date, timeIn, timeOut, status));
        }


        return dtos;
    }
    @PostMapping("/terminate-booking")
    public ResponseEntity<?> terminateBooking(@RequestBody Map<String, Integer> body) {
        int bookingId = body.get("bookingId");

        boolean success = RequestInboxDAO.terminateSpecificBooking(bookingId);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Terminated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not terminate booking."));
        }
    }




    @GetMapping
    public List<Room> getAllRooms() {
        return RoomFinderDAO.getAllUnoccupiedRooms().toList();
    }

    @GetMapping("/status/{roomName}")
    public RoomStatusResponse getRoomStatus(@PathVariable String roomName) {
        boolean occupied = RoomFinderDAO.isRoomCurrentlyOccupied(roomName);
        String rawType = RoomFinderDAO.getRoomType(roomName);

        Map<String, String> specialNames = new HashMap<>();
        specialNames.put("S507", "CCMIT Server Room");
        specialNames.put("N500", "College of Accountancy Faculty Room");
        specialNames.put("S516", "College of Science Accreditation Center");
        specialNames.put("S514", "College of Science Faculty Room");
        specialNames.put("S506", "Curriculum Planning and Development Office");
        specialNames.put("E500", "JPIA Office");
        specialNames.put("S512A", "Sci-Tech Research and Development Center");

        String type;
        String statusText = "";

        if (specialNames.containsKey(roomName)) {
            type = specialNames.get(roomName);
        } else if (rawType != null && !rawType.isEmpty()) {
            type = rawType.substring(0, 1).toUpperCase() + rawType.substring(1).toLowerCase() + " Room";
            statusText = occupied ? "Currently Occupied" : "Available";
        } else {
            type = "Unknown Room";
        }

        RoomStatusResponse response = new RoomStatusResponse();
        response.setRoomName(roomName);
        response.setRoomType(type);
        response.setOccupied(occupied);
        response.setStatusText(statusText);

        return response;
    }


    @PostMapping("/book-room")
    public Map<String, String> bookRoom(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new HashMap<>();

        try {
            boolean success = false;

            String role = (String) payload.get("role");

            if ("admin".equalsIgnoreCase(role)) {
                int userId = ((Number) payload.get("userId")).intValue();
                String room = (String) payload.get("room");
                String date = (String) payload.get("date");
                String time = (String) payload.get("time");
                int duration = ((Number) payload.get("duration")).intValue();
                success = RoomFinderDAO.bookRoomDirectly(userId, room, date, time, duration);
                if (success) {
                    response.put("message", "Room booked successfully.");
                } else {
                    response.put("error", "Booking failed.");
                }

            } else if ("faculty".equalsIgnoreCase(role)) {
                RoomRequest request = new RoomRequest();
                request.setUserId(((Number) payload.get("userId")).intValue());
                request.setChosenRoom((String) payload.get("room"));
                request.setBookingDate(Date.valueOf((String) payload.get("date")));
                String timeStr = (String) payload.get("time");
                String formattedTime = timeStr.length() == 5 ? timeStr + ":00" : timeStr;
                request.setStartTime(Time.valueOf(formattedTime));
                request.setDurationMinutes(((Number) payload.get("duration")).intValue());
                request.setNumberOfStudents(payload.get("students") != null ? ((Number) payload.get("students")).intValue() : 0);
                request.setRequiredPCs(payload.get("pcs") != null ? ((Number) payload.get("pcs")).intValue() : 0);
                request.setFullName((String) payload.get("fullName"));
                request.setProfessorName((String) payload.get("professorName"));
                request.setProgram((String) payload.get("program"));
                request.setSection((String) payload.get("section"));
                request.setYear(payload.get("year") != null ? ((Number) payload.get("year")).intValue() : 0);

                if (RequestInboxDAO.isOverlappingBooking(request)) {
                    response.put("error", "Time conflict with existing booking.");
                } else {
                    RequestInboxDAO.addToInbox(request);
                    response.put("message", "Booking request sent to admin for approval.");
                    success = true;
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error.");
        }

        return response;
    }

}
