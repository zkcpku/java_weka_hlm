import java.util.ArrayList;
import java.util.List;

public class Feature {
//    用于保存单条数据各个feature的数据结构
    List<Double> feature = new ArrayList<Double>();
    int score;
    static String [] attributes = {"之", "其", "或", "亦", "方", "于", "即", "皆", "因", "仍", "故", "尚", "呢", "了", "的", "着", "一", "不", "乃", "呀", "吗", "咧", "啊", "把", "让", "向", "往", "是", "在", "越", "再", "更", "比", "很", "偏", "别", "好", "可", "便", "就", "但", "儿", "又", "也", "都", "要", "这", "那", "你", "我", "他", "来", "去", "道", "说","class"};
    static int attrNum = attributes.length;
    public Feature(List<Double> _features, int _score){
        feature.addAll(_features);
        feature.add((double)score);
        score = _score;
//        异常处理

        if (feature.size() < attrNum){
            System.err.print("feature num lack\n");
            while(feature.size() < attrNum) {
                feature.add(0.0);
            }
        }
        else if(feature.size() > attrNum){
            System.err.print("feature num overflow\n");
            while(feature.size() > attrNum){
                feature.remove(feature.size() - 1);
            }
        }
    }
    public List<Double> getFeature(){
        return feature;
    }
    public int getScore(){
        return score;
    }
//    public int getAttrNum(){
//        return attrNum;
//    }
}
