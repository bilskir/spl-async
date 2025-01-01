package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;


/**
 * CrashedBroadcast is a broadcast message sent by a sensor to notify all other services
 * that the sender service has crashed.
 */
public class CrashedBroadcast implements Broadcast {

    private final String senderName;
    private int crashTime;

    /**
     * Constructor for CrashedBroadcast.
     *
     * @param senderName The name of the service that has crashed.
     */
    public CrashedBroadcast(String senderName, int crashTime) {
        this.senderName = senderName;
        this.crashTime = crashTime;
    }

    /**
     * Gets the name of the service that sent this broadcast.
     *
     * @return The sender's name.
     */
    public String getSenderName() {
        return senderName;
    }

    public int getCrashTime() {
        return crashTime;
    }
}