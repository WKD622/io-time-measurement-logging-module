package agh.agents;

import agh.utils.LogLevel;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class LoggingAgent extends Agent {

    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    String[] message = msg.getContent().split("##",2);
                    System.out.println(message[0] + ": " + message[1]);
                } else {
                    block();
                }
            }
        });
    }

    public static ACLMessage prepareLog(LogLevel level, String message) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Logging-agent", AID.ISLOCALNAME));
        msg.setContent(level.toString()+"##"+message);
        return msg;
    }
}
