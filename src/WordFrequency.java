
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import java.util.*;

import static java.lang.Math.min;

public class WordFrequency {
    private String Words=""; //记录被分割的单词
    private int Frequency=0;  //记录对应下标的单词的数量
    public  List<WordFrequency> WF=new ArrayList<>(); //充当记录所有单词与对应词频的数组
    public  List<String> W=new ArrayList<>(); //专门用来记录单词，用于充当WF的索引

    WordFrequency()
    {

    }

    WordFrequency(String w,int f)
    {
        this.Words=w;
        this.Frequency=f;
    }

    /*
     * 给出Words
     * */
    public String getWords()
    {
        return this.Words;
    }

    /*
     * 给出Frequency
     * */
    public int getFrequency()
    {
        return this.Frequency;
    }

    /*
     * 添加新单词进 WF，同时添加新单词进W
     * */
    private void Add_WF(String w,int f)
    {
        WordFrequency subwordf=new WordFrequency(w,f);
        W.add(w);
        WF.add(subwordf);
    }

    /*
     *确认WF中是否包含单词s，不包含返回-1，包含则返回索引值
     * */
    public int IndexOfWord(String s)
    {
        if(W==null)
            return -1;
        else {
            if (W.contains(s))
                ;
            else
                return -1;
            return W.indexOf(s);
        }
    }

    /*
     * 将WF中的特定单词的词频清零
     * */
    public int SetZero(String s)
    {
        int x=IndexOfWord(s);
        if(x==-1)
            return -1;
        else
        {
            WordFrequency my_wf=WF.get(x);
            my_wf.Frequency=0;
            WF.set(x,my_wf);
        }
        return 0;
    }

    /*
     * 删除WF和W中的特定元素
     * */
    public void Delete(String s)
    {
        int x=IndexOfWord(s);
        if(x==-1)
            return ;
        else
        {
            WordFrequency my_wf=WF.get(x);
            WF.remove(my_wf);
            W.remove(s);
        }
    }

    /*
     * 输入单词w（类型为Sting），若包含则改变WF中对应的词频，不包含则向
     * WF、W中添加新的元素
     * */
    public void Handle(String w,int numadd)
    {
        int x=IndexOfWord(w);
        if(x==-1)
        {
            Add_WF(w,1);
        }
        else
        {
            WordFrequency my_wf=WF.get(x);
            my_wf.Frequency+=numadd;
            WF.set(x,my_wf);
        }
    }

    /*
     * 输入单词w（类型为List<String>），若包含则改变WF中对应的词频，不包含则向
     * WF、W中添加新的元素
     * */
    public void Handle(List<String> s)
    {
        for (int i=0;i<s.size();i++)
        {
            String w=s.get(i);
            Handle(w,1);
        }
    }

    /*
     * 输入读取的文件名
     * 从读取文件中读取内容，填写词频
     * */
    public void Input_set(String inputname,WordFrequency wf) {
        wordSeg ws = new wordSeg();
        try {
            ws.resolveFormat(inputname);
            String regex="!@#\\$%\\^&";
            String sep="\r\n";
            String input=ws.readFile(inputname,regex);
            input=ws.SplitS(input,regex);
            Input_set_single(input,wf);
            /*
            List<Word> words= WordSegmenter.seg(input);
            String output=ws.listToStringW(words,sep);
            List<String> output2=ws.StringToList(output,sep);
            output2=ws.Deletesomewords(output2,null);
            wf.Handle(output2);
            */
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * 对单条短信进行处理，
     * 提取词频
     * */
    public void Input_set_single(String input_single,WordFrequency wf)
    {
        wordSeg ws=new wordSeg();
        String seq="\r\n";
        List<Word> words=WordSegmenter.seg(input_single);
        String output=ws.listToStringW(words,seq);
        List<String> output2=ws.StringToList(output,seq);
        output2=ws.Deletesomewords(output2,null);
        wf.Handle(output2);
    }

    /*
     * 对单条短信分词后的结果进行处理
     * 提取词频
     * */
    public void Input_set_done(List<String> ss,WordFrequency wf)
    {
        wordSeg ws=new wordSeg();
        List<String> output2=ws.Deletesomewords(ss,null);
        wf.Handle(output2);
    }

    /*
     * 输入输出文件名，
     * 向输出文件中输出词频的前K大
     * */
    public void Output(String outputname,int keyK,WordFrequency wf)    //2019.8.18改
    {
        wf.WF.sort(new Comparator<WordFrequency>() {
            @Override
            public int compare(WordFrequency o1, WordFrequency o2) {
                Integer com=o1.Frequency-o2.Frequency;
                if(com<0) return 1;
                else if(com>0) return -1;
                else return 0;
            }
        });
        int K=min(wf.WF.size(),keyK);
        String output_name="",output_frequency="";
        String spacing="\r\n";
        String output=String.valueOf(K);    //在开头加上数字
        output+=spacing;
        for (int i=0;i<K;i++)
        {
            WordFrequency nwf=wf.WF.get(i);
            output_name+=nwf.Words;
            output_name+=spacing;
            output_frequency+=String.valueOf(nwf.Frequency);
            output_frequency+=spacing;
        }
        output+=(output_name+output_frequency);
        wordSeg ws=new wordSeg();
        ws.writeFile2(outputname,output);
    }

}
