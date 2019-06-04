package agh.agents;

import agh.utils.Agents;
import agh.utils.LogLevel;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import weka.classifiers.Classifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static agh.Main.productionData;

public class LearningAgent extends Agent {

    private Classifier[] classififiers = new Classifier[]{productionData.getMlp(), productionData.getForest(), productionData.getM5p(), productionData.getVote()};
    private Agents agent = Agents.LEARNING_AGENT;

    private ITime time;

    private String formatNanoTime(long time) {
//        long nanos = 1000000000;
//        Long s = time / nanos;
//        Long ns = time % nanos;
//        return s.toString() + ":" + ns.toString() + " s"
        String format = "ss.SSSSSS's'";
//        Duration duration = Duration.ofNanos(time);
//

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(LocalTime.ofNanoOfDay(time));
    }

    protected void setup() {
        Object[] args = getArguments();

        try {
            time = MainContainer.cc.getAgent("time").getO2AInterface(ITime.class);
        } catch (ControllerException e) {
            e.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage reply;

                ArrayList<MessageTemplate> templates = new ArrayList<>();
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.CHECK_AGENT));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_MLP));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_M5P));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_FOREST));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_VOTE));

                ACLMessage[] checkMsg = new ACLMessage[templates.size()];
                int counter = 0;
                for (MessageTemplate checkState : templates) {
                    checkMsg[counter++] = receive(checkState);
                }
                for (ACLMessage msg : checkMsg) {
                    long t;
                    if (msg != null) {
                        switch (msg.getPerformative()) {
                            case (AgentMessages.CHECK_AGENT):
                                reply = new ACLMessage(AgentMessages.CHECK_AGENT);
                                reply.setContent("success");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_MLP):
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training mlp start"));
                                time.start(TimeAgent.StopwatchType.LEARNING_MLP);
                                productionData.train("TrainingData.arff", classififiers[0]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_MLP_ACK);
                                t = time.time(TimeAgent.StopwatchType.LEARNING_MLP);
                                time.stop(TimeAgent.StopwatchType.LEARNING_MLP);
                                send(LoggingAgent.prepareLog(LogLevel.INFO, agent, "Training mlp time: " + formatNanoTime(t)));
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training mlp end"));
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_M5P):
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training m5p start"));
                                time.start(TimeAgent.StopwatchType.LEARNING_M5P);
                                productionData.train("TrainingData.arff", classififiers[2]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_M5P_ACK);
                                t = time.time(TimeAgent.StopwatchType.LEARNING_M5P);
                                time.stop(TimeAgent.StopwatchType.LEARNING_M5P);
                                send(LoggingAgent.prepareLog(LogLevel.INFO, agent, "Training m5p time: " + formatNanoTime(t)));
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent,"Training m5p end"));
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_FOREST):
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training forest start"));
                                time.start(TimeAgent.StopwatchType.LEARNING_FOREST);
                                productionData.train("TrainingData.arff", classififiers[1]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_FOREST_ACK);
                                t = time.time(TimeAgent.StopwatchType.LEARNING_FOREST);
                                time.stop(TimeAgent.StopwatchType.LEARNING_FOREST);
                                send(LoggingAgent.prepareLog(LogLevel.INFO, agent, "Training forest time: " + formatNanoTime(t)));
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent,"Training forest end"));
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_VOTE):
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training vote start"));
                                time.start(TimeAgent.StopwatchType.LEARNING_VOTE);
                                productionData.train("TrainingData.arff", classififiers[3]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_VOTE_ACK);
                                t = time.time(TimeAgent.StopwatchType.LEARNING_VOTE);
                                time.stop(TimeAgent.StopwatchType.LEARNING_VOTE);
                                send(LoggingAgent.prepareLog(LogLevel.INFO, agent, "Training vote time: " + formatNanoTime(t)));
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent,"Training vote end"));
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                        }
                    }
                }
                block();
            }
        });
    }
}
