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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

import static agh.Main.productionData;
import static agh.agents.TimeAgent.formatNanoTime;

public class LearningAgent extends Agent {

    private Classifier[] classifiers = new Classifier[]{productionData.getMlp(), productionData.getForest(), productionData.getM5p(), productionData.getVote()};
    private Agents agent = Agents.LEARNING_AGENT;

    private ITime time;

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

                ThreadMXBean bean = ManagementFactory.getThreadMXBean();

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
                                training("mlp", 0, args, bean,
                                        TimeAgent.StopwatchType.LEARNING_MLP_CPU,
                                        TimeAgent.StopwatchType.LEARNING_MLP_USER,
                                        TimeAgent.StopwatchType.LEARNING_MLP_REAL,
                                        AgentMessages.START_LEARNING_MLP_ACK
                                );
                                break;
                            case (AgentMessages.START_LEARNING_M5P):
                                training("m5p", 2, args, bean,
                                        TimeAgent.StopwatchType.LEARNING_M5P_CPU,
                                        TimeAgent.StopwatchType.LEARNING_M5P_USER,
                                        TimeAgent.StopwatchType.LEARNING_M5P_REAL,
                                        AgentMessages.START_LEARNING_M5P_ACK
                                );
                                break;
                            case (AgentMessages.START_LEARNING_FOREST):
                                training("forest", 1, args, bean,
                                        TimeAgent.StopwatchType.LEARNING_FOREST_CPU,
                                        TimeAgent.StopwatchType.LEARNING_FOREST_USER,
                                        TimeAgent.StopwatchType.LEARNING_FOREST_REAL,
                                        AgentMessages.START_LEARNING_FOREST_ACK
                                );
                                break;
                            case (AgentMessages.START_LEARNING_VOTE):
                                training("vote", 3, args, bean,
                                        TimeAgent.StopwatchType.LEARNING_VOTE_CPU,
                                        TimeAgent.StopwatchType.LEARNING_VOTE_USER,
                                        TimeAgent.StopwatchType.LEARNING_VOTE_REAL,
                                        AgentMessages.START_LEARNING_VOTE_ACK
                                );
                                break;
                        }
                    }
                }
                block();
            }
        });
    }

    private void training(String name, int classifier, Object[] args, ThreadMXBean bean,
                          TimeAgent.StopwatchType typeCpu,
                          TimeAgent.StopwatchType typeUser,
                          TimeAgent.StopwatchType typeReal,
                          int replyAck) {
        ACLMessage reply;
        send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training " + name + " start"));
        time.initializeMeasurement(typeCpu, bean::getCurrentThreadCpuTime);
        time.initializeMeasurement(typeUser, bean::getCurrentThreadUserTime);
        time.start(typeCpu);
        time.start(typeUser);
        time.start(typeReal);
        productionData.train("TrainingData.arff", classifiers[classifier]);
        reply = new ACLMessage(replyAck);
        long timeCpu = time.time(typeCpu);
        long timeUser = time.time(typeUser);
        long timeReal = time.time(typeReal);
        time.stop(typeCpu);
        time.stop(typeUser);
        time.stop(typeReal);
        send(LoggingAgent.prepareLog(LogLevel.INFO, agent, "Training " + name + " time: " +
                formatNanoTime(timeCpu - timeUser, timeUser, timeReal)));
        send(LoggingAgent.prepareLog(LogLevel.DEBUG, agent, "Training " + name + " end"));
        reply.setContent("success ");
        reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
        send(reply);
    }
}
