import java.util.List;

/*本类用于实现TF IDF算法
* 输入为已经提前分类完成的八个类短信样本的经过预处理后的分词结果
* 输出为八个类分别对应的属性（单词）和权重值（TF IDF值）*/
public class My_tfidf {
    static String FileNames[]={"垃圾短信第二版词频.txt","软件通知短信第二版词频.txt","网购短信第二版词频.txt",
            "验证码第二版词频.txt", "移动运营商第二版词频.txt","诈骗短信第二版词频.txt",
            "支入支出第二版词频.txt","其他第二版词频.txt"};//用于记录等待打开的文件名
    private wordSeg WS=new wordSeg();
    private My_Struct_i my1 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my2 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my3 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my4 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my5 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my6 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my7 = new My_Struct_i();//用来记录一个类的属性和权重


    /*因为一些原因加进来的特殊的快排*/
    private void QuickSort(My_Struct_i num, int left, int right) {
        //如果left等于right，即数组只有一个元素，直接返回
        if (left >= right) {
            return;
        }
        //设置最左边的元素为基准值
        double key = num.MD.get(left);
        //数组中比key小的放在左边，比key大的放在右边，key值下标为i
        int i = left;
        int j = right;
        while (i < j) {
            //j向左移，直到遇到比key小的值
            double t1=num.MD.get(j);
            while (t1 >= key && i < j) {
                j--;
                t1=num.MD.get(j);
            }
            //i向右移，直到遇到比key大的值
            double t2=num.MD.get(i);
            while (t2 <= key && i < j) {
                i++;
                t2=num.MD.get(i);
            }
            //i和j指向的元素交换
            if (i < j) {
                String temp=num.MStr.get(i);
                String temp2=num.MStr.get(j);
                num.MStr.set(i,temp2);
                num.MStr.set(j,temp);
                t1=num.MD.get(i);
                t2=num.MD.get(j);
                num.MD.set(i,t2);
                num.MD.set(j,t1);
            }
        }
        String temp=num.MStr.get(i),temp2=num.MStr.get(left);
        num.MStr.set(i,temp2);
        num.MStr.set(left,temp);
        double t1=num.MD.get(i),t2=num.MD.get(left);
        num.MD.set(i,t2);
        num.MD.set(left,t1);
        QuickSort(num, left, i - 1);
        QuickSort(num, i + 1, right);
    }

    /*对特定的类进行初始化*/
    private void Init(String filename,My_Struct_i myx)
    {
        try {
            WS.resolveFormat(filename);
            String sep="!@#";
            String content=WS.readFile(filename,sep);
            List<String> c_list=WS.StringToList(content,sep);
            int count=0;
            for (String x:c_list)
            {
               count+=1;
               List<String> ts=WS.StringToList(x,"  ");
               myx.MStr.add(ts.get(0));
               double val=Double.parseDouble(ts.get(1));
               myx.MD.add(val);
            }
            myx.MD.add((double)count);//超出来的一位记录总词数
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private double Find_And_re(My_Struct_i myx,String x)
    {
        if(myx.MStr.contains(x))
        {
            int index=myx.MStr.indexOf(x);
            double val=myx.MD.get(index);
            return val;
        }
        else
            return 0;
    }

    /*计算特定类的属性和权重
    *计算方法为TF乘以改过的IDF，即IDF的值改为该短信在此类中出现的频数除以八类中出现的总频数
    * 小于一定数值的单词删去
    */
    private void Output(String filename,My_Struct_i myx)
    {
        My_Struct_i tm=new My_Struct_i();
        My_Struct_i em=new My_Struct_i();
        double Sum,Temp;//Sum为八个类中某一单词的出现总频数，Temp为指定类中某一单词出现的频数
        double TF,IDF;
        double sum=myx.MD.get(myx.MD.size()-1);//单词总数
        int index;
        for(index=0;index<myx.MStr.size();index++) {
            Sum = 0;//初始化一下，防止上次操作对这次造成影响
            String x = myx.MStr.get(index);

            Temp = Find_And_re(myx, x);
            Sum += Find_And_re(my1, x);
            Sum += Find_And_re(my2, x);
            Sum += Find_And_re(my3, x);
            Sum += Find_And_re(my4, x);
            Sum += Find_And_re(my5, x);
            Sum += Find_And_re(my6, x);
            Sum += Find_And_re(my7, x);

            /*
            * 毫不夸张地说，下面两行的设置将影响很大
            * */
            TF=Math.pow(2,Temp/sum);
            IDF=Math.pow(100,Temp/Sum);
            double result=TF*IDF;
            tm.MStr.add(x);
            tm.MD.add(result);
        }
        //接下来的问题是怎么确定属性的数量
        QuickSort(tm,0,tm.MStr.size()-1);
        System.out.println(filename+"该类单词数量为："+sum);
        //int MIn=(tm.MStr.size()/10)*9;
        int MIn=0;
        for (index=tm.MStr.size()-1;index>MIn;index--)
        {
            em.MStr.add(tm.MStr.get(index));
            em.MD.add(tm.MD.get(index));
        }
        String filecontent=WS.listToStringMSi(em,"\r\n");
        int size=filename.length();
        size-=4;
        String newfilename=filename.subSequence(0,size)+"TD-IDF值.txt";
        WS.writeFile(newfilename,filecontent);
    }

    /*对所有类进行初始化操作和计算操作*/
    My_tfidf()
    {
        Init(FileNames[0],my1);
        Init(FileNames[1],my2);
        Init(FileNames[2],my3);
        Init(FileNames[3],my4);
        Init(FileNames[4],my5);
        Init(FileNames[5],my6);
        Init(FileNames[6],my7);

        Output(FileNames[0],my1);
        Output(FileNames[1],my2);
        Output(FileNames[2],my3);
        Output(FileNames[3],my4);
        Output(FileNames[4],my5);
        Output(FileNames[5],my6);
        Output(FileNames[6],my7);
    }


    /*
    * 用于计算TF-IDF的main入口
    * */
    public  static void main(String[] args)
    {
        My_tfidf my_tfidf=new My_tfidf();
    }
}
