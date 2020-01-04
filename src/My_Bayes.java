import java.util.ArrayList;
import java.util.List;


public class My_Bayes {

    private double Val = 0;
    private wordSeg WS=new wordSeg();
    private List<Double> BD = new ArrayList<>();//记录文本属于某一类的概率，下标即类的标识
    private My_Struct_i my1 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my2 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my3 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my4 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my5 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my6 = new My_Struct_i();//用来记录一个类的属性和权重
    private My_Struct_i my7 = new My_Struct_i();//用来记录一个类的属性和权重

    /*
    *初始化算法，即读入七个类的特征值并进行赋值
    * */
    My_Bayes(String All_name)//All_name是储存所有类的储存特征值的txt文件的名称的文件的文件名
    {
        try {
            WS.resolveFormat(All_name);
            String name=WS.readFile(All_name,"!@#");
            List<String> name_list=WS.StringToList(name,"!@#");/*name_list中储存的是七个储存每一类储存特征值
            的文件的地址*/
            for (int i=0;i<name_list.size();i++)
                System.out.println(name_list.get(i));
            for (int i=0;i<7;i++) {
                String tname = name_list.get(i);//获取文件地址
                WS.resolveFormat(tname);
                String filecontent = WS.readFile(tname,"!@#");//读文件
                List<String> pro = WS.StringToList(filecontent, "!@#");//将文件中的内容分割方便用来初始化
                switch (i) {
                    case 0: {
                        System.out.println("类别一");
                        for (int j = 0; j < pro.size() - 1; j += 2) {
                            my1.MStr.add(pro.get(j));
                            Val = Double.parseDouble(pro.get(j + 1));
                            my1.MD.add(Val);
                        }
                        break;
                    }
                    case 1: {
                        System.out.println("类别二");
                        for (int j = 0; j < pro.size() - 1; j += 2) {
                            my2.MStr.add(pro.get(j));
                            Val = Double.parseDouble(pro.get(j + 1));
                            my2.MD.add(Val);
                        }
                        break;
                    }
                    case 2: {
                        System.out.println("类别三");
                        for (int j = 0; j < pro.size() - 1; j += 2) {
                            my3.MStr.add(pro.get(j));
                            Val = Double.parseDouble(pro.get(j + 1));
                            my3.MD.add(Val);
                        }
                        break;
                    }
                    case 3: {
                        System.out.println("类别四");
                        for (int j = 0; j < pro.size() - 1; j += 2) {
                            my4.MStr.add(pro.get(j));
                            Val = Double.parseDouble(pro.get(j + 1));
                            my4.MD.add(Val);
                        }
                        break;
                    }
                    case 4: {
                        System.out.println("类别五");
                        for (int j = 0; j < pro.size() - 1; j += 2) {
                            my5.MStr.add(pro.get(j));
                            Val = Double.parseDouble(pro.get(j + 1));
                            my5.MD.add(Val);
                        }
                        break;
                    }
                    case 5: {
                        System.out.println("类别六");
                        for (int j = 0; j < pro.size() - 1; j += 2) {
                            my6.MStr.add(pro.get(j));
                            Val = Double.parseDouble(pro.get(j + 1));
                            my6.MD.add(Val);
                        }
                        break;
                    }
                    case 6: {
                        System.out.println("类别七");
                        for (int j = 0; j < pro.size() - 1; j += 2) {
                            my7.MStr.add(pro.get(j));
                            Val = Double.parseDouble(pro.get(j + 1));
                            my7.MD.add(Val);
                        }
                        break;
                    }
                    default:
                        System.out.println("i的取值不对");
                        break;
                }
            }//循环初始化my1至my7
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
    * 输入输入的文本分类后的单词List，将属于某一类的概率初始化
    * */
    private void Input_Init_mx(List<String> wst,My_Struct_i myx) {
        double multiplicator=1;
        int mindex=1;
        List<String> mwst=new ArrayList<>();
        for (String x:wst)
        {//找到输入的单词中所有属于类别m1的单词储存如mwst中
           if(myx.MStr.contains(x))
           {
               mwst.add(x);
           }
        }
        for (String x:mwst)
        {
            mindex=myx.MStr.indexOf(x);
            multiplicator*=myx.MD.get(mindex);
        }
        Val=((double)mwst.size()/(double)wst.size())*multiplicator;
        BD.add(Val);//获得属于类别x的概率
    }

    private void Input_Init(List<String> wst) //对单条短信分词后得到的单词进行处理并得到属于各类的概率
    {
        Input_Init_mx(wst,my1);
        Input_Init_mx(wst,my2);
        Input_Init_mx(wst,my3);
        Input_Init_mx(wst,my4);
        Input_Init_mx(wst,my5);
        Input_Init_mx(wst,my6);
        Input_Init_mx(wst,my7);
    }

    protected void Del_BD()
    {
        /*CopyOnWriteArrayList<Double> copyOnWriteArrayList=new CopyOnWriteArrayList<Double>(BD);
        for(Double x:copyOnWriteArrayList)//清空BD中的内容防止对下次分类操作造成影响
        {
            copyOnWriteArrayList.remove(x);
        }
        BD=copyOnWriteArrayList;//将BD指向清空后的copyOnWriteArrayList
        */
        for(int i=BD.size()-1;i>=0;i--)
        {
            BD.remove(i);
        }
    }

    /*
    * 将已有的值进行排序，当最大值与次大值之间相差最大值的一半以上时确认分类结果
    * */
    public int Handle_Bayes(List<String> wst) {
        Input_Init(wst);
        int Max_index = 0;
        int Sec_Max_index = 0;
        for (int i = 0; i < BD.size(); i++) {
            if (BD.get(Sec_Max_index) < BD.get(i)) {
                if (BD.get(Max_index) < BD.get(i)) {
                    Sec_Max_index = Max_index;
                    Max_index = i;
                } else
                    Sec_Max_index = i;
            }
        }
        if (BD.get(Max_index)>0&&(((BD.get(Max_index) / BD.get(Sec_Max_index)) >= 2)||(Max_index==Sec_Max_index))) {
//            System.out.println("下标为"+Max_index+"，值为"+BD.get(Max_index));
//            System.out.println("下标为"+Sec_Max_index+"值为"+BD.get(Sec_Max_index));
            Del_BD();
            return (Max_index + 1);//返回应当属于的类别编号，类别编号从1开始
        } else {
            Del_BD();
            return 8;//返回类别编号8，即不确定的类的类别编号
        }
    }

}
