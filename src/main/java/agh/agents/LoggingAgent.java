package agh.agents;

import agh.utils.LogLevel;
import agh.utils.LogMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class LoggingAgent extends Agent implements ILogging {

    private ObservableList<LogMessage> observableList = FXCollections.observableArrayList(new ArrayList<>());
    private ListProperty<LogMessage> logs = new SimpleListProperty<>(observableList);

    protected void setup() {
        registerO2AInterface(ILogging.class, this);
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    LogMessage message;
                    try {
                        message = (LogMessage) msg.getContentObject();
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                        return;
                    }
                    System.out.println(message.getLog());
                    logs.add(message);
                } else {
                    block();
                }
            }
        });
    }

    public static ACLMessage prepareLog(LogLevel level, String message) {
        return prepareLog(level, "", LocalTime.now().toString(), message);
    }

    public static ACLMessage prepareLog(LogLevel level, String agent, String time, String message) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Logging-agent", AID.ISLOCALNAME));
        try {
            msg.setContentObject(new LogMessage(level, agent, time, message));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }


    @Override
    public ListProperty<LogMessage> getLog() {
        return logs;
    }
}
