/*
 * 作者：胡天翔
 * 时间：2019.8.35
 * 作用：该类用于对特定格式的短信进行信息提取，提取的信息有内容、时间、地域、类别。
 *       每个文件都是同一个手机APP发出的短信集合，末尾为手机所在地域；即所有该文件中短信均为同一地域
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractionMySQL {
    public String body; //短信主体
    public String number;   //号码
    public String time; //时间
    public boolean known;   //属性
    public int type;  //类别

    private String pub_ip="";   //默认的公网IP
    private String address=""; //默认的手机地域
    private String  MATCHStr="++++| This is the end of file! Next is the information about IP address. |++++";
    /*上个变量作为匹配字符串存在，表明该字符串的下一个即目标字符串*/

    public Bayes_main myBayes;

    public ExtractionMySQL(){
        myBayes=new Bayes_main();
    }

    /*
     * 删除字符串中指定的字符
     * */
    public static String deleteString0(String str, char delChar){
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != delChar){
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }

    /*
     * 该函数用于对文件确定公网IP与地域
     * 确定成功返回true，失败返回false
     * 参数为文件名加路径
     * */
    private boolean Find_ip_adr(String pathname)
    {
        String line=null;
        try {
            RandomAccessFile rf = new RandomAccessFile(pathname, "r");
            long filelength=rf.length();
            long start=rf.getFilePointer();
            long readIndex=start+filelength-1;//将指针指到文件末尾
            rf.seek(readIndex);
            int c=-1;
            while(readIndex>start)
            {
                c=rf.read();
                if(c=='\n'||c=='\r') {
                    line = rf.readLine();
                    if (line != null) {//得到最后一行的内容
                        //System.out.println("read line:" + line);
                        break;
                    }
                    readIndex--;
                }
                readIndex--;
                rf.seek(readIndex);
            }
            rf.close();
        }catch (FileNotFoundException fne)
        {
            fne.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        if(line!=null)
        {
            List<String> ls=new ArrayList<>();
            String regex="\"[^\"]*\"";
            Pattern pattern=Pattern.compile(regex);
            Matcher matcher=pattern.matcher(line);
            while(matcher.find())
            {
                ls.add(matcher.group());
            }
            if(ls.size()<6)
                return false;//说明最后一行的格式并不是约定好的格式，所以无法进行信息提取
            String temp_ip=ls.get(1);   //按照规定格式，ip应该在的位置
            String temp_adr=ls.get(5);  //按照规定格式，地域应该在的位置
            this.pub_ip=deleteString0(temp_ip,'\"');    //删除开头和结尾的字符"
            this.address=deleteString0(temp_adr,'\"');  //同上
            return true;
        }
        return false;
    }

    /*
     * 对单独的一条短信进行信息提取，并将信息上传至数据库
     * 特殊要求为
     *   1，短信中的换行符必须在str中
     *   2，必须首先使用了Find_ip_adr()函数设置完成了pub_ip和address
     *   3，因为一些玄学原因，Bayes_main类型需由外界提供
     * */
    private void solve_single(String str,Bayes_main bm)
    {
        //匹配、填写body变量
        String regex1="!@#\\$%\\^&[\\s\\S]*!@#\\$%\\^&";
        Pattern pattern1= Pattern.compile(regex1);
        Matcher matcher1=pattern1.matcher(str);
        if(matcher1.find()) {
            String s1 = matcher1.group();
            String[] ss = s1.split("!@#\\$%\\^&");
            String temp_body = deleteString0(ss[1],'\n');
            this.body=temp_body;
        }

        //匹配、填写time变量
        String regex2="\\d{4}-\\d{2}-\\d{2}\\s?\\d{2}:\\d{2}:\\d{2}";
        Pattern pattern2=Pattern.compile(regex2);
        Matcher matcher2=pattern2.matcher(str);
        if(matcher2.find())
        {
            String s2=matcher2.group();
            this.time=s2;
        }

        //获得短信类别
        int cla=bm.handle(this.body);
        this.type=cla;

        //将内容上传数据库
        String ip_adr=this.pub_ip+","+this.address;
        Inserter.input_mysql(this.body,ip_adr,this.time,this.type);
    }

    /*
     * 对文件进行处理，将短信内容上传数据库
     * */
    public boolean MySQL_solve_file(Bayes_main bm,String pathname)
    {
        if(!Find_ip_adr(pathname)) {
            return false;
        }
        try{
            RandomAccessFile rf=new RandomAccessFile(pathname,"r");
            long filelength=rf.length();
            long readIndex=rf.getFilePointer();
            String context_all="";
            int count=0;
            Pattern pattern=Pattern.compile("------------");
            while(readIndex<filelength)
            {
                String line=rf.readLine();
                String newline=new String(line.getBytes("8859_1"),"UTF-8");
                if(line!=null)
                {
                    Matcher matcher=pattern.matcher(line);
                    if(matcher.find())
                    {
                        count++;
                    }
                    context_all+=newline+"\n";
                    //context_all+=line+"\n";
                    if(count==2)//表明一条短信已经被接收到
                    {
                        count=0;
                        solve_single(context_all,bm);
                        context_all="";
                    }
                }
                readIndex=rf.getFilePointer();
            }
            rf.close();
        }catch (FileNotFoundException fne)
        {
            fne.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return true;
    }

    /*
     * 对文件夹进行处理——将文件夹内所有txt文件都进行处理
     * */
    public void MySQL_slove_flod(Bayes_main bm,String pathname)
    {
        File file=new File(pathname);
        File[] f_array=file.listFiles();
        Pattern pattern=Pattern.compile("(\\.txt)$");
        for (File f:f_array) {
            String s = f.getName();
            Matcher matcher = pattern.matcher(s);
            if (matcher.find())//对txt文件进行处理
            {
                String name = pathname + s;
                MySQL_solve_file(bm,name);
            }
        }
    }

}
