package com.example.my.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.my.entity.Event;
import com.example.my.entity.Member;

import java.util.List;

@Dao
public interface DaoClass {

    @Insert
    void addMember(Member member);
    @Insert
    void addEvent(Event event);

    @Update
    void updateMember(Member member);
    @Update
    void updateEvent(Event event);

    @Delete
    void deleteMember(Member member);
    @Delete
    void deleteEvent(Event event);

    @Query("select * from members")
    List<Member> getMembers();

    @Query("select * from events")
    List<Event> getEvents();
}
