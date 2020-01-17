package com.example.my.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.my.dao.DaoClass;
import com.example.my.entity.Event;
import com.example.my.entity.Member;

@Database(entities = {Member.class,Event.class},version = 1)
public abstract class DatabaseClass extends RoomDatabase {
    public abstract DaoClass daoClass();
}
