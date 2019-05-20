package agh.agents;

import agh.utils.Agents;
import agh.utils.LogLevel;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import weka.classifiers.Classifier;

import java.util.ArrayList;

import static agh.Main.productionData;

public class LearningAgent extends Agent {

    private Classifier[] classififiers = new Classifier[]{productionData.getMlp(), productionData.getForest(), productionData.getM5p(), productionData.getVote()};
    private Agents agent = Agents.LEARNING_AGENT;

    protected void setup() {
        Object[] args = getArguments();

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
                                productionData.train("TrainingData.arff", classififiers[0]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_MLP_ACK);
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training mlp end"));
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_M5P):
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training m5p start"));
                                productionData.train("TrainingData.arff", classififiers[2]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_M5P_ACK);
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent,"Training m5p end"));
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_FOREST):
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training forest start"));
                                productionData.train("TrainingData.arff", classififiers[1]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_FOREST_ACK);
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent,"Training forest end"));
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_VOTE):
                                send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training vote start"));
                                productionData.train("TrainingData.arff", classififiers[3]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_VOTE_ACK);
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
