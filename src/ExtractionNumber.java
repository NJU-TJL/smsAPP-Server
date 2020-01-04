/*短信报告的预定功能：
*
1，短信总数量——统计完成
2，短信数量分布，以天为单位——提取短信的时间
3，短信类别分布——短信分类
4，总支出与总支出——金额信息提取
5，私人短信数量统计与分析——统计倒好做，分析做什么呢？
6，多个短信类别分析——这一类又分为程序通知、旅游、网购、消费分析
    程序通知？
    旅游？
    网购？
7，在一天中各时段短信的分布情况——提取短信时间，与第二条重合
*/

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractionNumber {
    /*该类用于从短信中提取出需要的数字信息。
     * 使用思路为特定的数字前和后都有着特定的搭配，对搭配进行搜索*/

    public Double income_num;  //支入总数
    public Double pay_num;     //支出总数
    public static HashMap<String,Integer> hash;    /*支入前缀、后缀，支出前缀、后缀，都存储在其中；
                                            前面为值，后面为类型,1--支入前缀，2--支出前缀，3--支入支出后缀*/
    private static wordSeg ws;

    ExtractionNumber()
    {
        ws = new wordSeg();
        hash=new HashMap<>();
        income_num=0.0;
        pay_num=0.0;
        String[] temp_inc_pre={"收款","存入","转入"};
        String[] temp_pay_pre={"支付","付款","支出","取出","转出"};
        String[] temp_suf={"元","美元","欧元","英镑","日元"};
        for(int i=0;i<temp_inc_pre.length;i++)
        {
            hash.put(temp_inc_pre[i],1);
        }
        for(int i=0;i<temp_pay_pre.length;i++)
        {
            hash.put(temp_pay_pre[i],2);
        }
        for(int i=0;i<temp_suf.length;i++)
        {
            hash.put(temp_suf[i],3);
        }
    }

    /*这个函数用来处理输入短信，得到支入和支出的信息
    处理思路为：每次处理输入一个单词，保留三个单词；若第二个单词为数字，则查看第三个单词是否符合后缀规则；
    符合则查看第一个单词是否符合前缀规则以及所属种类；
    按照金额与分类对全局变量进行更改*/
    public void record_pay_inc(List<String> str)
    {
        int index=0;    //作为移动下标
        int temp_index;
        int cla;
        final int len=2;    //锁死大小为2
        String[] str3=new String[2];
        String regex="[0-9]*\\.?[0-9]+元";
        Pattern p=Pattern.compile(regex);
        Matcher m;
        /*这个地方用来测试正则的正确性
        for (String x:str)
        {
            m=p.matcher(x);
            if(m.matches())
            {
                System.out.println("匹配:"+x);
            }
            else
            {
                System.out.println("不匹配:"+x);
            }
        }*/
        str3[index]=str.get(0);
        //str.remove(0);
        for (String x:str)
        {
            index = (index+1)%len;
            str3[index]=x;
            m=p.matcher(x);
            if(m.matches()) //如果x符合正则匹配
            {
                temp_index=(index-1+len)%len;
                if(hash.containsKey(str3[temp_index]))
                {
                    char c[]=str3[index].toCharArray();
                    String temp_str="";
                    for (int i=0;i<c.length-1;i++)
                    {
                        temp_str+=c[i];
                    }
                    double temp_value=Double.valueOf(temp_str);
                    cla=hash.get(str3[temp_index]);
                    if (cla==1) //支入金额
                        income_num+=temp_value;
                    else    //支出金额
                        pay_num+=temp_value;
                }
            }
        }

    }
}
