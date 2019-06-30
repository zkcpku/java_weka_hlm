import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.*;


import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.seg.*;
import com.hankcs.hanlp.seg.NShort.*;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import weka.core.DenseInstance;

public class DataLoader {

    public static Instances dataLoaderString(List<String> train,List<Integer> train_label){
        List<Feature> entities = new ArrayList<Feature>();
        for (int i = 0; i < train.size(); i++) {
            entities.add(string2featureWithHanLP(train.get(i),train_label.get(i)));
        }

        Instances tmp = generatePopularInstance(entities);
//        System.out.println(tmp.toString());
        return tmp;

    }

    public static Instances dataLoader(List<String> train,List<Integer> train_label){
        List<Feature> entities = new ArrayList<Feature>();
        for (int i = 0; i < train.size(); i++) {
            entities.add(text2featureWithHanLP(train.get(i),train_label.get(i)));
        }

        Instances tmp = generatePopularInstance(entities);
//        System.out.println(tmp.toString());
        return tmp;
    }

    public static Instances generatePopularInstance(List<Feature> entities) {
//        用于生成数据集
        //    https://blog.csdn.net/cuiods/article/details/52483823
        //set
        ArrayList<String> classVal = new ArrayList<String>();
        classVal.add("0");
        classVal.add("1");
        classVal.add("2");
        ArrayList<Attribute> attributes = new ArrayList<>();

        for (int i = 0; i < Feature.attrNum; i++) {
            if (i != Feature.attrNum - 1){
                attributes.add(new Attribute(Feature.attributes[i]));
            }
            else{
                attributes.add(new Attribute(Feature.attributes[i],classVal));
            }
        }
        //set instances
        Instances instances = new Instances("text_feature",attributes,0);
        instances.setClassIndex(instances.numAttributes() - 1);
        //add instance
        for (Feature secRepoEntity: entities) {
            Instance instance = new DenseInstance(attributes.size());

            instance.setDataset(instances);
            for (int i = 0;i < Feature.attrNum - 1; ++i) {
                instance.setValue(i,secRepoEntity.getFeature().get(i));
//                System.out.println();
            }
            String score = String.valueOf(secRepoEntity.getScore());
//            System.out.print(score);
//            int score = secRepoEntity.getScore();
            int i = Feature.attrNum - 1;
            instance.setValue(i,score);
            instances.add(instance);
        }


        return instances;
    }
    public static Feature text2featureWithHanLP(String filename, int score){
        int[] count = new int[Feature.attrNum - 1];
        int sumcount = 0;
        for (int i = 0; i < count.length; i++)
            count[i] = 0;

       BufferedReader datafile = null;
        try{
            datafile = new BufferedReader(new FileReader(filename));
//            datafile = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"GB2312"));
        } catch (FileNotFoundException ex){
            System.err.println("File not found: " + filename);
        }
        Segment segment = new NShortSegment();
        try{
            List<Term> termList = null;
            String str = null;
            do {
                str = datafile.readLine();
                termList = segment.seg(str);
                boolean printFlag = false;
                sumcount += termList.size();
                for (int i = 0; i < termList.size(); ++i){
                    for (int j = 0; j < Feature.attrNum - 1; j++) {
                        if (termList.get(i).word.equals(Feature.attributes[j])){
                            count[j] += 1;
                        }
                    }
                }
            } while (str != null);

        } catch (Exception e){
        }

        List<Double> percent = new ArrayList<Double>(Feature.attrNum - 1); // 最后一个attr是分类
        for (int i = 0; i < Feature.attrNum - 1; i++) {
            percent.add((double)count[i] / (double) sumcount * 100);
        }

        Feature rst = new Feature(percent,score);

        return rst;
    }

    public static Feature string2featureWithHanLP(String str, int score){
        int[] count = new int[Feature.attrNum - 1];
        int sumcount = 0;
        for (int i = 0; i < count.length; i++)
            count[i] = 0;

        Segment segment = new NShortSegment();
        try{
            List<Term> termList = segment.seg(str);

            sumcount += termList.size();
            for (int i = 0; i < termList.size(); ++i){
                for (int j = 0; j < Feature.attrNum - 1; j++) {
                    if (termList.get(i).word.equals(Feature.attributes[j])){
                        count[j] += 1;
                    }
                }
            }
        } catch (Exception e){
        }

        List<Double> percent = new ArrayList<Double>(Feature.attrNum - 1); // 最后一个attr是分类
        for (int i = 0; i < Feature.attrNum - 1; i++) {
            percent.add((double)count[i] / (double) sumcount * 100);
        }

        Feature rst = new Feature(percent,score);

        return rst;
    }
}
