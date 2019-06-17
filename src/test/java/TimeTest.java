import agh.agents.ITime;
import agh.agents.MainContainer;
import agh.agents.TimeAgent;
import agh.classification.ProductionData;
import jade.wrapper.AgentController;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import weka.classifiers.Classifier;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import static agh.agents.TimeAgent.formatNanoTime;
import static org.junit.Assert.assertTrue;

public class TimeTest{
    private ProductionData productionData;
    private Classifier[] classifiers;
    private ThreadMXBean bean;
    private ITime time;
    private JFXPanel fxPanel;

    @Before
    public void setUp () throws Exception {
        try {
            AgentController time = MainContainer.cc.createNewAgent("time", "agh.agents.TimeAgent", null);
            time.start();
        } catch (Exception e) { // Who cares?
        }

        productionData = new ProductionData();
        classifiers = new Classifier[]{
                productionData.getMlp(),
                productionData.getForest(),
                productionData.getM5p(),
                productionData.getVote()};
        bean = ManagementFactory.getThreadMXBean();
        time = MainContainer.cc.getAgent("time").getO2AInterface(ITime.class);
        fxPanel = new JFXPanel(); // Suppress JUnit errors
    }

    private TestResults testClassifier(int id){
        int n = 3;
        TestResults results = new TestResults();

        for(int i = 0; i < n; i++) {
            time.initializeMeasurement(TimeAgent.StopwatchType.LEARNING_MLP_CPU, bean::getCurrentThreadCpuTime);
            time.initializeMeasurement(TimeAgent.StopwatchType.LEARNING_MLP_USER, bean::getCurrentThreadUserTime);
            time.start(TimeAgent.StopwatchType.LEARNING_MLP_CPU);
            time.start(TimeAgent.StopwatchType.LEARNING_MLP_USER);
            time.start(TimeAgent.StopwatchType.LEARNING_MLP_REAL);

            productionData.train("TrainingData.arff", classifiers[id]);
            results.getCpuTimes().add(time.time(TimeAgent.StopwatchType.LEARNING_MLP_CPU));
            results.getUserTimes().add(time.time(TimeAgent.StopwatchType.LEARNING_MLP_USER));
            results.getRealTimes().add(time.time(TimeAgent.StopwatchType.LEARNING_MLP_REAL));
            time.stop(TimeAgent.StopwatchType.LEARNING_MLP_CPU);
            time.stop(TimeAgent.StopwatchType.LEARNING_MLP_USER);
            time.stop(TimeAgent.StopwatchType.LEARNING_MLP_REAL);
            time.reset(TimeAgent.StopwatchType.LEARNING_MLP_CPU);
            time.reset(TimeAgent.StopwatchType.LEARNING_MLP_USER);
            time.reset(TimeAgent.StopwatchType.LEARNING_MLP_REAL);

        }
        return results;
    }

    @Test
    public void testMlp() {
        TestResults results = testClassifier(0);
        printResults("MLP", results);
        assertTrue(true);
    }

    @Test
    public void testForest() {
        TestResults results = testClassifier(1);
        printResults("Forest", results);
        assertTrue(true);
    }

    @Test
    public void testM5P() {
        TestResults results = testClassifier(2);
        printResults("M5P", results);
        assertTrue(true);
    }

    @Test
    public void testVote() {
        TestResults results = testClassifier(3);
        printResults("Vote", results);
        assertTrue(true);
    }

    private void printResults(String model, TestResults results){
        System.out.println(model.toUpperCase() + " test:");
        System.out.println("CPU times: " + results.getCpuTimes());
        System.out.println("User times: " + results.getUserTimes());
        System.out.println("Real times: " + results.getRealTimes());
        System.out.println("Average: " + formatNanoTime(results.getAvgCpuTime(),
                results.getAvgUserTime(),
                results.getAvgRealTime()));
    }

    class TestResults {
        private List<Long> cpuTimes = new ArrayList<>();
        private List<Long> userTimes = new ArrayList<>();
        private List<Long> realTimes = new ArrayList<>();

        List<Long> getCpuTimes() { return cpuTimes; }
        List<Long> getUserTimes() { return userTimes; }
        List<Long> getRealTimes() { return realTimes; }

        Long getAvgCpuTime() { return calculateAverage(cpuTimes); }
        Long getAvgUserTime() { return calculateAverage(userTimes); }
        Long getAvgRealTime() { return calculateAverage(realTimes); }

        private long calculateAverage(List<Long> list) {
            Long sum = 0L;
            if(!list.isEmpty()) {
                for (Long entry : list) {
                    sum += entry;
                }
                return sum / list.size();
            }
            return sum;
        }
    }
}