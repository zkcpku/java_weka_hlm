//https://www.cnblogs.com/yulia/p/6824058.html
// 打包和导入包的教程
import java.util.ArrayList;
import java.util.List;
import java.io.*;


public class Main {
//    处理输入，建立模型，完成输出

    public static void main(String[] args)throws Exception{
//        System.out.print(args.length);
//        System.out.print('\n');
        if (args.length != 2){
            System.out.print("ERROR！需要输入两个文件名");
            return;
        }
        String trainFileName = args[0];
        String testFileName = args[1];

        List<String> train = new ArrayList<String>();
        List<Integer> train_label = new ArrayList<Integer>();

        List<String> test = new ArrayList<String>();
//https://blog.csdn.net/some_times/article/details/44406803
        try{
            FileInputStream trainFile = new FileInputStream(trainFileName);
            InputStreamReader inReader = new InputStreamReader(trainFile,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader((inReader));
            String line = null;
            while((line = bufferedReader.readLine()) != null){
//                System.out.print(line);
//                System.out.print('\n');

                if (line.charAt(1) == '|'){
                    train.add(line.substring(2));
                    int score = line.charAt(0) - '0';
                    train_label.add(score);
                }
                else{
                    System.out.println("WARNING!格式不规范：" + line);
                }
            }
            bufferedReader.close();
            inReader.close();
            trainFile.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("ERROR！读取" + trainFileName + "错误");
            return;
        }

        try{
            FileInputStream testFile = new FileInputStream(testFileName);
            InputStreamReader inReader = new InputStreamReader(testFile,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader((inReader));
            String line = null;
            while((line = bufferedReader.readLine()) != null){
//                System.out.print(line);
//                System.out.print('\n');

                test.add(line);
            }
            bufferedReader.close();
            inReader.close();
            testFile.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("ERROR！读取" + testFileName + "错误");
            return;
        }
//  早期版本：这里采用命令行读写
//        for (int i = 0;i < args.length; ++i){
//            System.out.print(args[i]);
//            System.out.print('\n');
//            if (args[i].charAt(1) == '|') {
//                train.add(args[i].substring(2));
//                int score = args[i].charAt(0) - '0';
//                train_label.add(score);
//            }
//            else{
//                test.add(args[i]);
//            }
//        }
//        System.out.print("output:\n");
//        for(int i = 0;i < train.size(); ++i){
//            System.out.printf(train.get(i)+"  |  "+train_label.get(i));
//            System.out.print('\n');
//        }
        Classification clf = new Classification();
        clf.train(train, train_label,true);

        List<Integer> test_label = new ArrayList<Integer>();
        for (int i = 0;i < test.size(); ++i){
            test_label.add(0);
        }

        List<Integer> rst = clf.prediction(test,test_label);

        String myOutName = "[java19]HW2_student_id.txt";
        try{
            FileOutputStream out = new FileOutputStream(myOutName);
            OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outWriter);
            for (int i = 0;i < rst.size(); ++i){
                bufferedWriter.write(rst.get(i) + "\r\n");
            }
            bufferedWriter.close();
            outWriter.close();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("ERROR!" + myOutName +"文件写入错误");
        }


//  原始版本：命令行输出
//        for (int i = 0; i < rst.size(); i++) {
//            System.out.println(rst.get(i));
//        }

    }
}
