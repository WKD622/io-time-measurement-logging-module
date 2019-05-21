package agh.agents;

import agh.calculation.Calculator;
import agh.classification.WekaManager;
import agh.utils.Agents;
import agh.utils.LogLevel;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class ProductionProcess {

    private String[] data;
    private int classifier;
    private Calculator calculator;
    private ILogging logInterface;
    private Agents agent = Agents.PRODUCTION_AGENT;


    public ProductionProcess(String[] data, int classifier) {
        this.data = data;
        this.classifier = classifier;
        calculator = new Calculator();
        try {
            logInterface = MainContainer.cc.getAgent("Logging-agent").getO2AInterface(ILogging.class);
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private String firstStep() {    //wyliczenie rzeczywistego składu
        return calculator.calculateInput(data);
    }

    private String secondStep() {
        return calculator.calculateCost(data);
    }

    private String thirdStep() {     //wyznaczenie jakości
        return WekaManager.makePrediction(data, classifier);
    }

    public String[] runProcess() {
        String[] result = new String[3];

        logInterface.sendMessage(LogLevel.INFO, agent, "Start first step");
        result[0] = firstStep();
        logInterface.sendMessage(LogLevel.INFO, agent, "End first step");
        logInterface.sendMessage(LogLevel.INFO, agent, "Start second step");
        result[1] = secondStep();
        logInterface.sendMessage(LogLevel.INFO, agent, "End second step");
        logInterface.sendMessage(LogLevel.INFO, agent, "Start third step");
        result[2] = thirdStep();
        logInterface.sendMessage(LogLevel.INFO, agent, "End third step");

        return result;
    }
}
