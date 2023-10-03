package com.example.ires;

import com.example.ires.firebase.FireBaseConn;

import org.junit.Test;

import java.util.Date;

public class TestFirebaseConn {
    // test date
    Date newDate = new Date ( 2023, 8, 16, 6, 9, 42);
    @Test
    public void TestSendToDashboard ( ) {
        FireBaseConn conn = new FireBaseConn ();
        conn.SendToDashboard (1, "name", "09123456789", "content", newDate);
    }
    @Test
    public void TestSetStatus(){
        FireBaseConn conn = new FireBaseConn ();
        conn.SetStatus (1, "name", newDate, true);
    }
    @Test
    public void TestCheckDatabase(){
        FireBaseConn conn = new FireBaseConn ();
        conn.CheckDatabase ();
    }
}