import java.io.*;
import java.net.*;
import java.util.*;

// ####
class ClientAnswer{
    static String IP = "222.29.98.46";
    static String myID = "1700012886";
    private Socket client;
    ClientAnswer(){}
    ClientAnswer(Socket client) {
        this.client = client;
    }


    public void run(){
        getTest();
    }
    public void getTest() {
        try {
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
            Hashtable<String, Object> msg = (Hashtable<String, Object>)is.readObject();
//            System.out.println("Msg from " + client.getInetAddress() + ": " + msg.size());
            String method = (String)msg.get("method");
//            System.out.println(method);
            if (method.equals("test")){
                String questionID = (String)msg.get("id");
                String result = "";
                List<String> trainingSet0 = (List)msg.get("param1");
                List<String> trainingSet1 = (List)msg.get("param2");
                List<String> trainingSet2 = (List)msg.get("param3");
                List<String> testingSet = (List)msg.get("param4");

//            System.out.println(trainingSet0.size());
//            System.out.println(trainingSet1.size());
//            System.out.println(trainingSet2.size());
//            System.out.println(testingSet.size());
                List<Integer> train_label = new ArrayList<Integer>();
                List<String> train = new ArrayList<String>();
                for (int i = 0;i < trainingSet0.size(); ++i) {train_label.add(0);train.add(trainingSet0.get(i));}
                for (int i = 0;i < trainingSet1.size(); ++i) {train_label.add(1);train.add(trainingSet1.get(i));}
                for (int i = 0;i < trainingSet2.size(); ++i) {train_label.add(2);train.add(trainingSet2.get(i));}

                Classification clf = new Classification();



                clf.trainString(train, train_label,false);
                List<Integer> test_label = new ArrayList<Integer>();
                for (int i = 0;i < testingSet.size(); ++i) test_label.add(0);
                List<Integer> rst = clf.predictionString(testingSet,test_label);

                List<Integer> tmp_label = new ArrayList<Integer>();
                for (int i = 0;i < trainingSet2.size(); ++i)   tmp_label.add(0);
                List<Integer> tmp_rst = clf.predictionString(train,train_label);
                int zero = 0;
                int one = 1;
                int two = 2;
                {
                    int count0 = 0;
                    int count1 = 0;
                    int count2 = 0;
                    for (int i = 0;i < trainingSet0.size(); ++i){
                        if (tmp_rst.get(i) == 0)    count0 +=1;
                        else if(tmp_rst.get(i) == 1)    count1 += 1;
                        else    count2 += 1;
                    }
                    if (count0 > count1 && count0 > count2) zero = 0;
                    else if (count1 > count0 && count1 > count2) zero = 1;
                    else    zero = 2;

                    count0 = 0;
                    count1 = 0;
                    count2 = 0;
                    for (int i = trainingSet0.size();i < trainingSet0.size() + trainingSet1.size(); ++i){
                        if (tmp_rst.get(i) == 0)    count0 +=1;
                        else if(tmp_rst.get(i) == 1)    count1 += 1;
                        else    count2 += 1;
                    }
                    if (count0 > count1 && count0 > count2) one = 0;
                    else if (count1 > count0 && count1 > count2) one = 1;
                    else    one = 2;

                    count0 = 0;
                    count1 = 0;
                    count2 = 0;
                    for (int i = trainingSet0.size() + trainingSet1.size();i < trainingSet0.size() + trainingSet1.size() + trainingSet2.size(); ++i){
                        if (tmp_rst.get(i) == 0)    count0 +=1;
                        else if(tmp_rst.get(i) == 1)    count1 += 1;
                        else    count2 += 1;
                    }
                    if (count0 > count1 && count0 > count2) two = 0;
                    else if (count1 > count0 && count1 > count2) two = 1;
                    else    two = 2;
                }
//                System.out.println(tmp_rst);
//                System.out.println(zero);
//                System.out.println(one);
//                System.out.println(two);
                for (int i = 0; i < rst.size(); ++i) {
                    if (rst.get(i) == zero)    rst.set(i,0);
                    else if(rst.get(i) == one)    rst.set(i,1);
                    else    rst.set(i,2);
                }

//                System.out.println(tmp_rst);
//                System.out.println(zero);
//                System.out.println(one);
//                System.out.println(two);
                for (int i = 0; i < tmp_rst.size(); ++i) {
                    if (tmp_rst.get(i) == zero)    tmp_rst.set(i,0);
                    else if(tmp_rst.get(i) == one)    tmp_rst.set(i,1);
                    else    tmp_rst.set(i,2);
                }

//                System.out.println(tmp_rst);
//                System.out.println(train_label);

                submit(myID,questionID,rst,questionID);
            }
            else
            {
                System.out.println("Msg from " + client.getInetAddress() + ": " + msg.toString());
            }


        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public void getScore(String studentID){
        try{
            long curTime = System.currentTimeMillis();
            String id = studentID + "_" + curTime;

            Hashtable<String, Object> msg = new Hashtable<String, Object>();
            msg.put("method","getScore");
            msg.put("param1",studentID);
            msg.put("id",id);

            String ipAddr = IP;
            int port = 9876;
            Socket caller = new Socket(ipAddr,port);
            ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
            os.writeObject(msg);
            os.flush();
            caller.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
    public void clearScore(String studentID){
        try{
            long curTime = System.currentTimeMillis();
            String id = studentID + "_" + curTime;

            Hashtable<String, Object> msg = new Hashtable<String, Object>();
            msg.put("method","clearScore");
            msg.put("param1",studentID);
            msg.put("id",id);

            String ipAddr = IP;
            int port = 9876;
            Socket caller = new Socket(ipAddr,port);
            ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
            os.writeObject(msg);
            os.flush();
            caller.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void initTest(String studentID, String encoding){
        try{
            long curTime = System.currentTimeMillis();
            String id = studentID + "_" + curTime;

            Hashtable<String, Object> msg = new Hashtable<String, Object>();
            msg.put("method","initTest");
            msg.put("param1",studentID);
            msg.put("param2",encoding);
            msg.put("id",id);

            String ipAddr = IP;
            int port = 9876;
            Socket caller = new Socket(ipAddr,port);
            ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
            os.writeObject(msg);
            os.flush();
            caller.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
    public void submit(String studentID, String questionID, List<Integer> ans, String id){
        try{
//            List<Integer> ans = new ArrayList<Integer>();
//            Random rand = new Random();
//            for (int i = 0;i < 180; i++){
//                ans.add(rand.nextInt(3));
//            }
            long curTime = System.currentTimeMillis();
//            String id = studentID + "_" + curTime;

            Hashtable<String, Object> msg = new Hashtable<String, Object>();
            msg.put("method","submit");
            msg.put("param1",studentID);
            msg.put("param2",questionID);
            msg.put("param3",ans);
            msg.put("id",id);

            String ipAddr = IP;
            int port = 9876;
            Socket caller = new Socket(ipAddr,port);
            ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
            os.writeObject(msg);
            os.flush();
            caller.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
// ####
class HW3_Main
{
//    public static void servermain(String args[]) {
//        CallManager.initScoreBoard();
//        try {
//            ServerSocket server = new ServerSocket(9876);
//            while (true) {
//                Socket call = server.accept();
//                new CallManager(call).start();
//            }
//        }
//        catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//    }
    public static void main(String args[]){
        String studentID = "1700012886";
        String [] cmd_list = {"classify","getScore","clearScore"};
        int cmd = 1;
        if(cmd_list[cmd] == "classify")
        {
            ClientAnswer initT = new ClientAnswer();
            initT.initTest(studentID,"UTF-8");
            try{
                ServerSocket server = new ServerSocket(6789);
                while (true)
                {
                    Socket call = server.accept();
                    new ClientAnswer(call).run();
                }

            }catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        else if(cmd_list[cmd] == "getScore") {
            ClientAnswer getScore = new ClientAnswer();
            getScore.getScore(studentID);
            try{
                ServerSocket server = new ServerSocket(6789);
                Socket call = server.accept();
                new ClientAnswer(call).run();
            }catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        else if(cmd_list[cmd] == "clearScore"){
            ClientAnswer clearScore = new ClientAnswer();
            clearScore.clearScore(studentID);
            try{
                ServerSocket server = new ServerSocket(6789);
                Socket call = server.accept();
                new ClientAnswer(call).run();
            }catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }


    }
}

