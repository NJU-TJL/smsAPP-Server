import java.io.*;
import java.util.ArrayList;

public class SortSMS {
    public static String res_rootPath ="C:\\From_Android/Results\\";
    ArrayList<MySMS> mySMSs;
    public Bayes_main myBayes;
    File smsFile;
    SortSMS(){
        myBayes=new Bayes_main();
        mySMSs = new ArrayList<>();
    }
    String doSortSMS(String filepath){
        mySMSs.clear();
        makeMySMSlist(filepath);
        StringBuilder sbForFile=new StringBuilder();
        for(MySMS iSMS:mySMSs){
            int res=myBayes.do_my_handle(iSMS.body);
            sbForFile.append("------------"+"\n");
            sbForFile.append("!@#$%^&"+"\n");
            sbForFile.append(iSMS.body); //由读取生成SMS的ArrayList 的过程可知，这里不需要多附加一个回车
            sbForFile.append("!@#$%^&"+"\n");
            sbForFile.append(iSMS.number+"\n");
            sbForFile.append(iSMS.time+"\n");
            sbForFile.append(iSMS.known+"\n");
            sbForFile.append(res+"\n");
            sbForFile.append("------------"+"\n");
            sbForFile.append("\n");
        }
        File destFile=new File(res_rootPath +smsFile.getName());
        if(!destFile.exists()){
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            Writer out = new FileWriter(destFile);
            out.write(sbForFile.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("处理结果写入文件失败！- "+destFile.getAbsolutePath());
        }
        return destFile.getAbsolutePath();
    }

    void makeMySMSlist(String filepath){
        smsFile=new File(filepath);
        //生成List
        try {
            FileReader fr = new FileReader(smsFile);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            int state=0;
            MySMS tempSMS=new MySMS();
            while ((str = bf.readLine()) != null) {
                if(str.equals("!@#$%^&") && state==0){
                    state=1;
                }
                else if(str.equals("!@#$%^&") && state==1) {
                    state=2;
                }
                else if(state==1){
                    tempSMS.body+=(str+"\n");
                    System.out.println(str);
                }
                else if(state==2){
                    tempSMS.number=str;
                    state=3;
                }
                else if(state==3){
                    tempSMS.time=str;
                    state=4;
                }
                else if(state==4){
                    if(!str.equals("0")&&!str.equals("null")) {
                        tempSMS.known = true;
                    }else{
                        tempSMS.known = false;
                    }
                    mySMSs.add(tempSMS);
                    tempSMS=new MySMS();
                    state=0;
                }
                else;
            }
            bf.close();
            fr.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
class MySMS{
    String body;
    String time;
    String number;
    //String person;
    boolean known;
    int type=8;
    MySMS(){body="";}
}