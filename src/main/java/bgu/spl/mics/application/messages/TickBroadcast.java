package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    
    private final int duration;
    private final int tick;

    public TickBroadcast(int duration, int tick){
        this.duration = duration;
        this.tick = tick;
    }

    public int getDuration() {
        return duration;
    }

    public int getTick() {
        return tick;
    }
}