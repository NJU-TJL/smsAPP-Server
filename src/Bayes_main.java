import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import java.util.List;

public class Bayes_main {

    private static wordSeg ws;
    private static My_Bayes my_bayes;
    public ExtractionNumber ex;
    public WordFrequency myWF;

    public static final int LJ = 1; //垃圾短信
    public static final int RJ = 2; //软件通知短信
    public static final int WG = 3; //网购短信
    public static final int YZM = 4;    //验证码短信
    public static final int YDYYS = 5;  //移动运营商短信
    public static final int ZP = 6; //诈骗短信
    public static final int ZRZC = 7;   //支入支出短信
    public static final int QT = 8; //其他短信

    public Bayes_main()    //构造函数，初始化
    {
        ws = new wordSeg();
        my_bayes = new My_Bayes("all_name.txt");
        ex=new ExtractionNumber();
        String temp_init="start";
        List<Word> temp_wt=WordSegmenter.segWithStopWords(temp_init);   //完成分词器的初始化
    }

    public int handle(String str)   //作为单个的String类型处理函数，返回类型
    {
        List<Word> w=WordSegmenter.segWithStopWords(str);
        String s=ws.listToStringW(w,"\r\n");
        List<String> ss=ws.StringToList(s,"\r\n");
        int classofstr=my_bayes.Handle_Bayes(ss);
        return classofstr;
    }


    public int do_my_handle(String str)   //作为单个的String类型处理函数，输入为单条短信的内容，返回短信类型
    {
        List<Word> w=WordSegmenter.segWithStopWords(str);
        String s=ws.listToStringW(w,"\r\n");
        List<String> ss=ws.StringToList(s,"\r\n");
        int classofstr=my_bayes.Handle_Bayes(ss); //短信分类
        ex.record_pay_inc(ss); //支入支出统计

        //统计词频
        ss=ws.Deletesomewords(ss,"C:\\From_Android\\停用词附加.txt");
        myWF.Handle(ss);

        return classofstr;
    }




//    public static void main(String args[]) {    //作为测试函数存在
//        Bayes_main b_m=new Bayes_main();
//        Scanner s = new Scanner(System.in);
//        System.out.print("输入：");
//        String str = s.nextLine();
//        System.out.println("输入为：" + str);
//        List<Word> wtest = WordSegmenter.segWithStopWords(str);
//        boolean flag=true;
//        String stop="stop";
//        while (flag) {
//            wtest = WordSegmenter.segWithStopWords(str); //包含一条短信的str被分词为List<Word>型存在
//            String stest1 = b_m.ws.listToStringW(wtest, "\r\n"); //List<Word>被转为String
//            List<String> stest2 = b_m.ws.StringToList(stest1, "\r\n"); //String被转为List<String>
//            int classofstr = b_m.my_bayes.Handle_Bayes(stest2); //String被传入处理，返回类别编号
//            System.out.println("输入为：" + str);
//            System.out.println("输入的类别为" + classofstr);
//
//            System.out.print("输入：");
//            str = s.nextLine();
//            System.out.println("输入为：" + str);
//            if (str.compareTo(stop)!=0)
//                ;
//            else {
//                flag = false;
//                System.out.println("成功退出");
//            }
//        }
//    }
}
