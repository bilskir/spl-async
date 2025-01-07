package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class StartSimulationEvent implements Event<Void>{
    /*Start simulation event is sent to the TimeService to indicate that it should start to tick and send broadcasts to the other microservices */
}
