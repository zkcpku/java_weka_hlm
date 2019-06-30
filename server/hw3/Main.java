import java.io.*;
import java.net.*;
import java.util.*;

// ####
class Score {
	private int correct = 0;
	private int total = 0;

	public void update(boolean hit) {
		total++;
		if (hit) {
			correct++;
		}
	}

	public void setScore(int total, int correct) {
		this.total = total;
		this.correct = correct;
	}

	public int getTotal() {
		return total;
	}

	public int getCorrect() {
		return correct;
	}

	public double getRatio() {
		if (total > 0) {
			return (double)correct/total;
		} else {
			return -1;
		}
	}

	public void clear() {
		correct = 0;
		total = 0;
	}
}

// ####
class Question {
	private String id;
	private long time;
	private List<Integer> ans;
	public Question(String id, long time, List<Integer> ans) {
		this.id = id;
		this.time = time;
		this.ans = ans;
	}
	public int getAns(int i) {
		if (i < ans.size()) {
			return ans.get(i);
		} else {
			return -1;
		}
	}
}

// ####
class ChapterList
{
	private ArrayList<String> txtList;
	private ArrayList<Integer> tagList;

	public ChapterList() {
		txtList = new ArrayList<String>();
		tagList = new ArrayList<Integer>();
	}

	public void add(String bookname, int chapter_begin, int chapter_end, String encoding, int tag) {
		for (int i = chapter_begin; i <= chapter_end; i++) {
			String txt = readTxt(bookname, i, encoding);
			if (txt != null) {
				txtList.add(txt);
				tagList.add(tag);
			}
		}
	}

	public void add(ChapterList list, int i0, int i1) {
		txtList.addAll(list.getTxtList().subList(i0, i1));
		tagList.addAll(list.getTagList().subList(i0, i1));
	}

	public void add(ChapterList list) {
		add(list, 0, list.size());
	}

	private String readTxt(String name, int chapter, String encoding) {
		try {
			File f = new File(encoding+"/"+name, chapter+".txt");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(f), encoding);
			BufferedReader reader = new BufferedReader(isr);
			StringBuilder txt = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				txt.append(line);
			}
			reader.close();
			isr.close();
			return txt.toString();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public void permutate() {
		Random rand = new Random();
		for (int i = 0; i < size(); i++) {
			String tmpTxt = txtList.get(i);
			int tmpTag = tagList.get(i);
			int j = rand.nextInt(size());
			txtList.set(i, txtList.get(j));
			tagList.set(i, tagList.get(j));
			txtList.set(j, tmpTxt);
			tagList.set(j, tmpTag);
		}
	}

	public ArrayList<String> getTxtList() {
		return txtList;
	}

	public ArrayList<Integer> getTagList() {
		return tagList;
	}

	public int size() {
		return txtList.size();
	}
}

// ####
class CallManager extends Thread
{
	private static String[] studentList = { 
	"1500010647",  "1500012446",  "1500012825",  "1500012859",  "1500017824",  "1600011302",  
	"1600011309",  "1600011606", "1600011751",  "1600012722",  "1600012758",  "1600012770",  
	"1600012779",  "1600012800",  "1600012819",  "1600012821", "1600012894",  "1600012917",  
	"1600013012",  "1600013016",  "1600013041",  "1600013042",  "1600013044",  "1600013221",
	"1600013239",  "1600013522",  "1600013534",  "1600015530", "1600015838",  "1600017715",  
	"1600017722",  "1600017754", "1600017773",  "1600017787",  "1600017809",  "1700010701",  
	"1700011026",  "1700011027",  "1700011060",  "1700011064",  "1700012148",  "1700012716",  
	"1700012723",  "1700012725",  "1700012735",  "1700012745",  "1700012746",  "1700012749",  
	"1700012750",  "1700012753",  "1700012765",  "1700012773",  "1700012775",  "1700012781",  
	"1700012789",  "1700012793",  "1700012798",  "1700012800",  "1700012802",  "1700012806",  
	"1700012810",  "1700012823",  "1700012829",  "1700012833",  "1700012837",  "1700012840",  
	"1700012843",  "1700012845",  "1700012862",  "1700012879",  "1700012881",  "1700012885",  
	"1700012886",  "1700012888",  "1700012890",  "1700012891",  "1700012894",  "1700012901",  
	"1700012903",  "1700012904",  "1700012905",  "1700012909",  "1700012913",  "1700012918",  
	"1700012923",  "1700012925",  "1700012942",  "1700012945",  "1700012951",  "1700012952",  
	"1700012953",  "1700012958",  "1700012967",  "1700012969",  "1700012977",  "1700012978",  
	"1700012980",  "1700012984",  "1700013000",  "1700013009",  "1700013015",  "1700013021",  
	"1700013212",  "1700014110",  "1700014401",  "1700014402",  "1700015852",  "1700015882",  
	"1700017720",  "1700017783",  "1700017830",  "1700017857",  "1700018603",  "1800017759" 
	};

	private static Hashtable<String, Question> pendingQuestions = new Hashtable<String, Question>();
	private static Hashtable<String, Score> scoreBoard = new Hashtable<String, Score>();

	private Socket client;

	// ####
	CallManager(Socket client) {
			this.client = client;
	}

	private String findStudentID(String studentID) {
		if (studentID == null)
			return null;
		for (int i = 0; i < studentList.length; i++) {
			if (studentList[i].equals(studentID))
				return studentList[i];
		}
		return null;
	}

	private int[] randTag() {
		int[] tag = new int[3];
		tag[0] = 0; tag[1] = 1; tag[2] = 2;
		Random rand = new Random();
		for (int i = 0; i < 3; i++) {
			int tmp = tag[i];
			int j = rand.nextInt(3);
			tag[i] = tag[j];
			tag[j] = tmp;
		}
		return tag;
	}

	public void run() {
		try {
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
			Hashtable<String, Object> msg = (Hashtable<String, Object>)is.readObject();
			System.out.println("Msg from " + client.getInetAddress() + ": " + msg.toString());
			String method = (String)msg.get("method");
			String id = (String)msg.get("id");
			String result = "";
			if (method == null) {
				result = "no method";
			}
			else if (method.equals("initTest")) {
				result = initTest((String)msg.get("param1"), (String)msg.get("param2"));
			} 
			else if (method.equals("submit")) {
				result = submit((String)msg.get("param1"), (String)msg.get("param2"), (List)msg.get("param3"));
			}
			else if (method.equals("getScore")) {
				result = getScore((String)msg.get("param1"));
			}
			else if (method.equals("clearScore")) {
				result = clearScore((String)msg.get("param1"));
			}
			else {
				result = "unknown method";
			}

			msg.clear();
			msg.put("method", method);
			msg.put("result", result);
			msg.put("id", id);

			System.out.println("Sending result to " + client.getInetAddress() + ":" + msg.toString());

			Socket caller = new Socket(client.getInetAddress(), 6789);
			ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
			os.writeObject(msg);
			os.flush();
			caller.close();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String initTest(String studentID, String encoding) {
		if (studentID == null || findStudentID(studentID) == null) {
			return "unknown studentID " + studentID;
		}
		encoding = encoding.toUpperCase();
		if (encoding == null || (!encoding.equals("UTF-8") && !encoding.equals("GBK"))) {
			return "unknown encoding " + encoding;
		}

		for (int test = 0; test < 100; test++) {
			ChapterList[] list = {new ChapterList(), new ChapterList(), new ChapterList()};
			int[] tag = randTag();
			list[0].add("hlm", 1, 80, encoding, tag[0]); list[0].permutate();
			list[1].add("hlm", 81, 120, encoding, tag[1]); list[1].permutate();
			list[2].add("sgyy", 1, 120, encoding, tag[2]); list[2].permutate();

			ChapterList[] trainingSet = {new ChapterList(), new ChapterList(), new ChapterList()};
			trainingSet[0].add(list[0], 0, 20);
			trainingSet[1].add(list[1], 0, 20);
			trainingSet[2].add(list[2], 0, 20);

			ChapterList testingSet = new ChapterList();
			testingSet.add(list[0], 20, list[0].size());
			testingSet.add(list[1], 20, list[1].size());
			testingSet.add(list[2], 20, list[2].size());
			testingSet.permutate();

			long curTime = System.currentTimeMillis();
			String id = studentID + "_" + curTime + "_" + test;

			ArrayList<Integer> ans = testingSet.getTagList();
			Question q = new Question(id, curTime, ans);
			pendingQuestions.put(id, q);

			Hashtable<String, Object> msg = new Hashtable<String, Object>();
			msg.put("method", "test");
			for (int i = 0; i < 3; i++) {
				switch (tag[i]) {
				case 0: msg.put("param1", trainingSet[i].getTxtList()); break;
				case 1: msg.put("param2", trainingSet[i].getTxtList()); break;
				case 2: msg.put("param3", trainingSet[i].getTxtList()); break;
				}
			}
			msg.put("param4", testingSet.getTxtList());
			msg.put("id", id);

			try {
				Socket caller = new Socket(client.getInetAddress(), 6789);
				ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
				os.writeObject(msg);
				os.flush();
				caller.close();
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
				return "Exception thrown when sending questions";
			}
		}

		return "Success";
	}

	public String submit(String studentID, String questionID, List<Integer> ans) {
		if (studentID == null || findStudentID(studentID) == null) {
			return "unknown studentID " + studentID;
		}
		if (questionID == null || pendingQuestions.get(questionID) == null) {
			return "unknown question " + questionID;
		}
		Question q = pendingQuestions.get(questionID);
		int nCorrect = 0;
		for (int i = 0; i < ans.size(); i++) {
			boolean correct = ans.get(i) == q.getAns(i);
			scoreBoard.get(studentID).update(correct);
			if (correct) {
				nCorrect ++;
			}
		}
		writeScoreLog(studentID);
		return String.valueOf(nCorrect);
	}

	public String getScore(String studentID) {
		if (studentID == null || findStudentID(studentID) == null) {
			return "unknown studentID " + studentID;
		}
		if (scoreBoard.get(studentID).getTotal() == 0) {
			return "no score available";
		}
		double score = scoreBoard.get(studentID).getRatio();
		return String.valueOf(score);
	}

	public String clearScore(String studentID) {
		if (studentID == null || findStudentID(studentID) == null) {
			return "unknown studentID " + studentID;
		}
		scoreBoard.get(studentID).clear();
		writeScoreLog(studentID);
		return "Success";
	}

	private void writeScoreLog(String studentID) {
		try {
			synchronized(scoreBoard) {
				File file = new File("ScoreBoard/"+studentID+".txt");
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
				bw.write(System.currentTimeMillis() + "");
				bw.newLine();
				bw.write(scoreBoard.get(studentID).getTotal() + "");
				bw.newLine();
				bw.write(scoreBoard.get(studentID).getCorrect() + "");
				bw.newLine();
				bw.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void initScoreBoard() {
		try {
			for (int i = 0; i < studentList.length; i++) {
				int total = 0, correct = 0;
				Score score = new Score();
				File file = new File("ScoreBoard/" + studentList[i] + ".txt");
				BufferedReader br = new BufferedReader(new FileReader(file));
				String str = br.readLine();
				if (str != null) {
					str = br.readLine();
					if (str != null)	total = Integer.parseInt(str);
					str = br.readLine();
					if (str != null)	correct = Integer.parseInt(str);
				}
				br.close();
				score.setScore(total, correct);
				scoreBoard.put(studentList[i], score);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}

// ####
class Main
{
	public static void main(String args[]) {
		CallManager.initScoreBoard();
		try {
			ServerSocket server = new ServerSocket(9876);
			while (true) {
				Socket call = server.accept();
				new CallManager(call).start();
			}
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}

