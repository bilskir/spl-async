package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    
    private final int duration;

    public TickBroadcast(int duration){
        this.duration = duration;
    }


    public int getDuration() {
        return duration;
    }
}