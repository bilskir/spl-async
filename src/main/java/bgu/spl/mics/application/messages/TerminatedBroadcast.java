package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TerminatedBroadcast is a broadcast message sent by a sensor to notify all other services
 * that the sender service is terminating.
 */
public class TerminatedBroadcast implements Broadcast {

    private final String senderName;

    /**
     * Constructor for TerminatedBroadcast.
     *
     * @param senderName The name of the service that is terminating.
     */
    public TerminatedBroadcast(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Gets the name of the service that sent this broadcast.
     *
     * @return The sender's name.
     */
    public String getSenderName() {
        return senderName;
    }
}