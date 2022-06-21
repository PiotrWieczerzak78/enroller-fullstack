package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @RequestMapping(value = "sortByTitle/{orderBy}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingsSortBy(@PathVariable("orderBy") String orderBy) {
        Collection<Meeting> meetings = meetingService.getAllSortedByTitle(orderBy);
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "contains/{word}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingsContains(@PathVariable("word") String word) {
        Collection<Meeting> meetings = meetingService.getAllContains(word);
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "containsUser/{login}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingsContainsUser(@PathVariable("login") String login) {
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity("Not Found participant", HttpStatus.NOT_FOUND);
        }
        Collection<Meeting> meetings = meetingService.getAll();

        ArrayList<Meeting> foundMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            Collection<Participant> participants = meeting.getParticipants();
            if (participants.contains(participant)) {
                foundMeetings.add(meeting);
            }
        }
        return new ResponseEntity<Collection<Meeting>>(foundMeetings, HttpStatus.OK);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Not Found",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Not Found",HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting){
        Collection<Meeting> foundMeetings = meetingService.findByTitle(meeting.getTitle());
        if (foundMeetings.size() >0) {
            return new ResponseEntity("Unable to create. A meeting with id " + meeting.getTitle() + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "getId/{title}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingId(@PathVariable("title") String title) {
        Collection<Meeting> foundMeetings = meetingService.findByTitle(title);
        if (foundMeetings.size() ==0) {
            return new ResponseEntity("Unable to find. A meeting with title " + title + " not exist.", HttpStatus.CONFLICT);
        }else if (foundMeetings.size() >1) {
            return new ResponseEntity("Unable to find id. A meeting with id " + title + "  exist more than one.", HttpStatus.CONFLICT);
        }
        long foundId = foundMeetings.iterator().next().getId();
        return new ResponseEntity<Long>(foundId, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT	)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
        Meeting foundMeeting = meetingService.findById(meeting.getId());
        if (foundMeeting == null) {
            return new ResponseEntity("Not Found",HttpStatus.NOT_FOUND);
        }
        foundMeeting.setTitle(meeting.getTitle());
        foundMeeting.setDescription(meeting.getDescription());
        foundMeeting.setDate(meeting.getDate());
        meetingService.update(foundMeeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Not Found",HttpStatus.NOT_FOUND);
        }
//        meeting = meetingService.getMeetingParticipants(id);
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
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
