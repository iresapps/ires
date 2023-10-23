package com.example.ires;

import com.example.ires.firebase.FireBaseConn;

import org.junit.Test;

import java.util.Calendar;

public class TestFirebaseConn {
    @Test
    public void TestSendToDashboard ( ) {
        FireBaseConn conn = new FireBaseConn ();
        conn.SendToDashboard (1, "name", "09123456789", Calendar.getInstance());
    }
    @Test
    public void TestSetStatus(){
        FireBaseConn conn = new FireBaseConn ();
        conn.SetStatus (1, "name", Calendar.getInstance(), true);
    }
    @Test
    public void TestCheckDatabase(){
        MainActivity mainActivity = new MainActivity();
        mainActivity.onCreate(null);
        FireBaseConn conn = new FireBaseConn ();
        conn.CheckDatabase ();
    }
    @Test
    public void TestFirebaseActivity(){

    }
}