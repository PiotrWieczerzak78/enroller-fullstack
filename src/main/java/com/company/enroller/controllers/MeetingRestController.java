package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {

        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting){
        Meeting foundMeeting = meetingService.findById(meeting.getId());
        if (foundMeeting != null) {
            return new ResponseEntity("Unable to create. A meeting with id " + meeting.getId() + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Not Found", HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participant/{id2}", method = RequestMethod.POST)
    public ResponseEntity<?> addMeetingParticipant(@PathVariable("id") long id,@PathVariable("id2") String login) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Not Found meeting",HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity("Not Found participant",HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = meeting.getParticipants();
        if (participants.contains(participant)){
            return new ResponseEntity("Participant already added",HttpStatus.ALREADY_REPORTED);
        }
        meeting.addParticipant(participant);
        meetingService.update(meeting);
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participant/{id2}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeetingParticipant(@PathVariable("id") long id,@PathVariable("id2") String login) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Not Found meeting",HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity("Not Found participant",HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = meeting.getParticipants();
        if (!participants.contains(participant)){
            return new ResponseEntity("Not Found participant attached to meeting",HttpStatus.NOT_FOUND);
        }
        meeting.removeParticipant(participant);
        meetingService.delete(meeting);
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }
}
