import java.util.List;
import java.io.*;


import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.seg.*;
import com.hankcs.hanlp.seg.NShort.*;


public class HanLPTest{
    public static void main(String []   args){
        BufferedReader datafile = null;
        String filename = "hlm01.txt";
        try{
            datafile = new BufferedReader(new FileReader(filename));
//            datafile = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"GB2312"));
        } catch (FileNotFoundException ex){
            System.err.println("File not found: " + filename);
        }
        int count = 0;
//        String str = datafile.readLine();
//        System.out.print(str);
        Segment segment = new NShortSegment();
        try{

            List<Term> termList = null;
            String str = null;
            do {
                str = datafile.readLine();
                System.out.print(str);
                termList = segment.seg(str);
                boolean printFlag = false;
                for (int i = 0; i < termList.size(); ++i){
                    System.out.print(termList.get(0).word);
                    if(termList.get(i).word.equals("其")){
                        count += 1;
                        System.out.println(termList.get(i));
                        printFlag = true;
//                        break;
                    }
                }
                if (printFlag){
//                    System.out.println(str);
                }


            } while (str != null);

        } catch (Exception e){
        }
        System.out.print('\n');
        System.out.print(count);
        System.out.print("其");
    }
}