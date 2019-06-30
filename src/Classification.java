import java.util.ArrayList;
import java.util.List;



import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.trees.LMT;
import weka.core.FastVector;
import weka.core.Instances;

import javax.xml.crypto.Data;

public class Classification {
    static Classifier bestmodel = null;
    static double bestAcc = 0.0;

    public static void trainString(List<String> train,List<Integer> train_label, boolean verbose) throws Exception{
        Instances train_set = DataLoader.dataLoaderString(train,train_label);
        train_set.setClassIndex(train_set.numAttributes() - 1);

        Instances[][] split = crossValidationSplit(train_set,10);
        Instances[] trainingSplits = split[0];
        Instances[] testingSplits = split[1];

        Classifier[] models = {
//                new J48(),
//                new DecisionTable(),
//                new DecisionStump(),
//                new RandomTree(),
                new LMT()
//                new RandomForest(),
//                new HoeffdingTree()

        };

        for (int i = 0; i < models.length; i++) {
            FastVector predictions = new FastVector();

            for (int j = 0; j < trainingSplits.length; j++) {
                Evaluation validation = classify(models[i], trainingSplits[j], testingSplits[j]);
                predictions.appendElements(validation.predictions());
            }

            double accuracy = calculateAccuracy(predictions);
            if (accuracy > bestAcc){
                bestAcc = accuracy;
                bestmodel = models[i];

            }
            if (verbose){
                System.out.println("Accuracy of "   + models[i].getClass().getSimpleName() + ":"
                        + String.format("%.2f%%",accuracy)
                        + "\n--------------------------------------");
            }
        }

        if (verbose){
            System.out.println("Best Accuracy of "   + bestmodel.getClass().getSimpleName() + ":"
                    + String.format("%.2f%%",bestAcc)
                    + "\n--------------------------------------");

        }
        return;
    }
    public static void trainString(List<String> train,List<Integer>train_label)throws Exception{
        trainString(train,train_label,false);
        return;
    }
    public static List<Integer> predictionString(List<String> test,List<Integer> test_label) throws Exception{
        List<Integer> rst = new ArrayList<Integer>();
        Instances test_set = DataLoader.dataLoaderString(test,test_label);
        test_set.setClassIndex(test_set.numAttributes() - 1);

        for (int i = 0; i < test_set.size(); i++) {
            int this_rst = 0;
            double poss = 0.0;
            for (int j = 0; j < bestmodel.distributionForInstance(test_set.get(i)).length; j++) {
                if (bestmodel.distributionForInstance(test_set.get(i))[j] > poss){
                    poss = bestmodel.distributionForInstance(test_set.get(i))[j];
                    this_rst = j;
                }
            }
            rst.add(this_rst);
        }
        return rst;
    }

    public static void train(List<String> train,List<Integer> train_label, boolean verbose) throws Exception{
        //先得到list <feature>
        //再生成数据集
        Instances train_set = DataLoader.dataLoader(train,train_label);
//        System.out.println(train_set.size());
        train_set.setClassIndex(train_set.numAttributes() - 1);

        Instances[][] split = crossValidationSplit(train_set,train_set.size());
        Instances[] trainingSplits = split[0];
        Instances[] testingSplits = split[1];

        Classifier[] models = {
//                new J48(),
//                new DecisionTable(),
//                new DecisionStump(),
//                new RandomTree(),
                new LMT(),
                new RandomForest(),
                new HoeffdingTree()

        };

        for (int i = 0; i < models.length; i++) {
            FastVector predictions = new FastVector();

            for (int j = 0; j < trainingSplits.length; j++) {
                Evaluation validation = classify(models[i], trainingSplits[j], testingSplits[j]);
                predictions.appendElements(validation.predictions());
            }

            double accuracy = calculateAccuracy(predictions);
            if (accuracy > bestAcc){
                bestAcc = accuracy;
                bestmodel = models[i];

            }
            if (verbose){
                System.out.println("Accuracy of "   + models[i].getClass().getSimpleName() + ":"
                                                    + String.format("%.2f%%",accuracy)
                                                    + "\n--------------------------------------");
            }
        }

        if (verbose){
            System.out.println("Best Accuracy of "   + bestmodel.getClass().getSimpleName() + ":"
                    + String.format("%.2f%%",bestAcc)
                    + "\n--------------------------------------");

        }
        return;
    }
    public static void train(List<String> train,List<Integer>train_label)throws Exception{
        train(train,train_label,false);
        return;
    }
    public static List<Integer> prediction(List<String> test,List<Integer> test_label) throws Exception{
        List<Integer> rst = new ArrayList<Integer>();
        Instances test_set = DataLoader.dataLoader(test,test_label);
        test_set.setClassIndex(test_set.numAttributes() - 1);

        for (int i = 0; i < test_set.size(); i++) {
            int this_rst = 0;
            double poss = 0.0;
            for (int j = 0; j < bestmodel.distributionForInstance(test_set.get(i)).length; j++) {
                if (bestmodel.distributionForInstance(test_set.get(i))[j] > poss){
                    poss = bestmodel.distributionForInstance(test_set.get(i))[j];
                    this_rst = j;
                }
            }
            rst.add(this_rst);
        }
        return rst;
    }
    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds){
        Instances[][] split = new Instances[2][numberOfFolds];
        for (int i = 0;i < numberOfFolds; ++i){
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }
        return split;
    }
    public static Evaluation classify(Classifier model, Instances trainingSet, Instances testingSet)throws Exception{
        Evaluation evaluation = new Evaluation(trainingSet);
        model.buildClassifier(trainingSet);
        evaluation.evaluateModel(model, testingSet);
        return evaluation;
    }
    public static double calculateAccuracy(FastVector predictions){
        double correct = 0;
        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()){
                correct ++;
            }
        }
        return 100 * correct / predictions.size();
    }

}
