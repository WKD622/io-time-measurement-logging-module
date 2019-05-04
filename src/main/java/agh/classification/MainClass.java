package agh.classification;
import org.apache.log4j.net.*;

public class MainClass {

    public static void main(String[] args) {

        ProductionData prod = new ProductionData();
        //prod.train("TrainingData1.arff");
        System.out.println(prod.test("TrainingData1.arff", "TrainingData1.arff", 0));

    }
}
