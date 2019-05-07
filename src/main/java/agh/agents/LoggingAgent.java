package agh.agents;

import agh.utils.LogLevel;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.beans.property.SimpleStringProperty;

public class LoggingAgent extends Agent implements ILogging {

    public SimpleStringProperty logs = new SimpleStringProperty("");

    protected void setup() {
        registerO2AInterface(ILogging.class, this);
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    String[] message = msg.getContent().split("##", 2);
                    System.out.println(message[0] + ": " + message[1]);
                    logs.setValue(logs.getValue() + "\n" + message[0] + ": " + message[1]);
                } else {
                    block();
                }
            }
        });
    }

    public static ACLMessage prepareLog(LogLevel level, String message) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Logging-agent", AID.ISLOCALNAME));
        msg.setContent(level.toString() + "##" + message);
        return msg;
    }

    @Override
    public SimpleStringProperty getLog() {
        return logs;
    }
}
