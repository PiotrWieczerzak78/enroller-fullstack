package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {
    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    DatabaseConnector connector;

    public MeetingService() {
        connector = DatabaseConnector.getInstance();
    }

    public Collection<Meeting> getAll() {
        String hql = "FROM Meeting";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }
    public Collection<Meeting> getAllSortedByTitle(String order) {
        String hql="";
        if (order.equals("ASC")){
            hql = "FROM Meeting m ORDER BY m.title ASC";
        } else {
            hql = "FROM Meeting m ORDER BY m.title DESC";
        }
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

    public Collection<Meeting> getAllContains(String word) {
        String hql = "FROM Meeting m where m.title like :word or m.description like :word";
        Query query = connector.getSession().createQuery(hql);
        query.setParameter("word","%"+word+"%");
        return query.list();
    }
    public Meeting findById(long id){
        return  (Meeting)  connector.getSession().get(Meeting.class, id);
    }
    public Collection<Meeting> findByTitle(String title){

        String hql = "FROM Meeting m where m.title = :word";
        Query query = connector.getSession().createQuery(hql);
        query.setParameter("word",title);
        return query.list();
    }
    public void delete(Meeting meeting){
        Transaction transaction= connector.getSession().beginTransaction();
        connector.getSession().delete(meeting);
        transaction.commit();
    }
    public void add(Meeting meeting){
        Transaction transaction= connector.getSession().beginTransaction();
        connector.getSession().save(meeting);
        transaction.commit();
    }
    public void update(Meeting meeting) {
        Transaction transaction= connector.getSession().beginTransaction();
        connector.getSession().update(meeting);
        transaction.commit();
    }
}
