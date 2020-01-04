//package MyExtraction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * 作者：胡天翔
 * 时间：2019.8.30
 * 功能：设计上预备支持对短信进行处理，得到信息
 *       对网购短信有——各种网购快递途径及具体数量（如中通、圆通）；代号为3，即网购短信种类代号
 * */

public class ExtractionALL {
    private String[] cla_of_ways={"中通","圆通","顺丰","海航天天","韵达","汇通","速尔","申通"
            ,"优速","EMS","德邦","宅急送","全一","联邦","中铁","DDS","TNT","晟邦","百世","苏宁"
            ,"品骏","京东","天天"};
    //下两个变量针对网购短信
    private MessageNode ms_WG=new MessageNode();
    private WordFrequency wordFrequency_WG=new WordFrequency();
    private String outputname;
    //等待补充……

    /*
     * 初始化ms，用于后续的匹配
     * */
    public ExtractionALL()
    {
        for(String x:this.cla_of_ways)
        {
            ms_WG.MN_input(x);
        }
        /*等待补充*/
    }

    /*
     * 删除字符串中指定的字符
     * */
    private static String deleteString0(String str, char delChar){
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != delChar){
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }

    /*
     * 对单条短信的处理
     * 匹配成功则改变储存的词频，不成功则不做改变
     * */
    private void slove_single(String str,Integer cla)
    {
        String regex1="!@#\\$%\\^&[\\s\\S]*!@#\\$%\\^&";
        Pattern pattern1= Pattern.compile(regex1);
        Matcher matcher1=pattern1.matcher(str);
        if(matcher1.find()) {
            String s1 = matcher1.group();
            String[] ss = s1.split("!@#\\$%\\^&");
            String temp_body = deleteString0(ss[1],'\n');
            List<String> single_body;
            switch (cla)
            {
                case 3:single_body=this.ms_WG.MN_match_extend(temp_body,0);//表明容错率为0
                    break;
                /*等待补充，计划提供对其他种类短信的处理*/
                default:single_body=new ArrayList<String>();
            }
            if(single_body.size()>=1)
            {
                String temp_str=single_body.get(0);//获得匹配的结果
                switch (cla)
                {
                    case 3:this.wordFrequency_WG.Handle(temp_str,1);//在词频统计中加入一个单词
                        break;
                    /*等待补充，计划提供对其他种类短信词频的记录*/
                    default:
                        ;
                }
            }
        }
    }

    /*
     * 对文件进行处理，理论上应该对已经完成分类的某一类短信进行处理
     * */
    private void solve_file(String pathname,Integer cla)
    {
        try{
            RandomAccessFile rf=new RandomAccessFile(pathname,"r");
            long filelength=rf.length();
            long readIndex=rf.getFilePointer();
            String context_all="";
            int count=0;
            Pattern pattern=Pattern.compile("------------");
            String oldline="";
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
                    if(count==2)//表明一条短信已经被接收到
                    {
                        if(oldline.compareTo(String.valueOf(cla))==0)
                            slove_single(context_all,cla);
                        count=0;
                        context_all="";
                    }
                }
                oldline=newline;
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
    }

    /*
     * 针对某一类短信完成从读取文件到输出一系列操作
     * */
    public void Handle(String pathname,Integer cla)
    {
        this.solve_file(pathname,cla);
        Integer K=0;
        switch (cla)
        {
            case 3:K=this.wordFrequency_WG.WF.size();
                break;
            /*等待补充，机会增加对其他种类短信的处理*/
            default:
                ;
        }

        //TODO: 2019.09.28  改变了原来的输出文件名 此处干脆采用硬编码的形式 --TJL
//        String suffix="-统计结果.txt";
//        String[] str_sp=pathname.split("\\.");
//        outputname=str_sp[0]+suffix;
        wordFrequency_WG.Output("C:\\From_Android\\Res_ExtracionALL\\Res_ExtracionALL.txt",K,wordFrequency_WG);
    }

    //2019/9/1
    public String get_outputname(){
        return outputname;
    }

}
