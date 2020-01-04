import org.apdplat.word.segmentation.Word;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class wordSeg {

    private static String format="gbk";

    /*
    * 提取字符串，由于开头和结尾相同且有多条信息难以使用正则来进行提取，
    * 所以直接对"!@#$%^&"进行分割，由于需要的String前后均有"!@#$%^&",
    * 所以每隔一个无用String即使需要的String
    * */
    public static String SplitS(String str,String regex) {
        String[] arr=str.split(regex);
        String fcontent="";
        for (int i=0;i<arr.length;i++)
        {
            if(i%2==1)
                fcontent+=arr[i];
            else
                ;
        }
        return fcontent;
    }

    /*
    * 用来将String字符串写入指定文件中
    * */
    public static void writeFile(String fileName, String fileContent) {
        try
        {
            File f = new File(fileName);
            if (!f.exists())
            {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),format);
            BufferedWriter writer=new BufferedWriter(write);
            writer.write(fileContent);
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /*
     * 用来将String字符串写入指定文件中
     * */
    public static void writeFile2(String fileName, String fileContent) {
        try
        {
            File f = new File(fileName);
            if (!f.exists())
            {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f));
            BufferedWriter writer=new BufferedWriter(write);
            writer.write(fileContent);
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
    * 用于获知TXT文件的编码格式
    * */
    public static void resolveFormat(String path) throws Exception {
        InputStream inputStream=new FileInputStream(path);
        byte[] head=new byte[3];
        inputStream.read(head);
        format="gbk";
        if(head[0]==-1&&head[1]==-2)
            format="UTF-16";
        else if(head[0]==-2&&head[1]==-1)
            format="Unicode";
        else if((head[0]==-17&&head[1]==-69&&head[2]==-65)||(head[0]==-27&&head[1]==-80&&head[2]==-118))
            format="UTF-8";

        inputStream.close();
    }

    /*
    * 用来读取任意编写格式的TXT文件并将内容转为String返回
    * */
    public static String readFile(String fileName,String sep) {/*第一个为文件地址，第二个为每一行之间的分隔符，因为
    读取过程中会自动忽略换行符所以要想区分每一行需要添加换行符*/
        String fileContent = "";
        try
        {
            File f = new File(fileName);
            if(f.isFile()&&f.exists())
            {
                InputStreamReader read = new InputStreamReader(new FileInputStream(f),format);
                BufferedReader reader=new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null)
                {
                    fileContent += line;
                    fileContent+=sep;
                }
                //fileContent=SplitS(fileContent,regex);
                read.close();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return fileContent;
    }

    /*
    * 用于将List<Word>加上特定的分隔符拼接为String字符串并返回
    * */
    public static String listToStringW(List<Word> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                sb.append(list.get(i));
            } else {
                sb.append(list.get(i));
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /*
     * 用于将List<WordFrequency>加上特定的分隔符拼接为String字符串并返回
     * */
    public static String listToStringWF(List<WordFrequency> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getWords());
            sb.append("  ");
            sb.append(list.get(i).getFrequency());
            if (i == list.size() - 1)
                ;
            else {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /*
    * 用于将My_Struct_i中的内容加上特定的分隔符拼接为String字符串并返回
    * */
    public static String listToStringMSi(My_Struct_i myx,String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < myx.MStr.size(); i++) {
            sb.append(myx.MStr.get(i));
            sb.append(separator);
            sb.append(myx.MD.get(i));

            if(i==myx.MStr.size()-1)
                ;
            else
                sb.append(separator);
        }
        return sb.toString();
    }

    public static List<String> Deletesomewords(List<String> list,String stopfilename)   //2019.8.18改
    {
        CopyOnWriteArrayList<String> copyOnWriteArrayList=new CopyOnWriteArrayList<String>(list);
        try {
            List<String> stopwords = new ArrayList<>();
            if(stopfilename!=null) {
                resolveFormat(stopfilename);
                String sep = "!@#";
                String content = readFile(stopfilename, sep);
                stopwords = StringToList(content, sep);//c_list中储存的即应该被删去的停用词
            }
            //String reg="^\\d+$";
//            String reg="((^\\d+(\\.\\d+)?(元?|日?|分?|年?|月?|%?|天?|时?|号?))|((^\\w+))\\s*)|[-,.?:;!']";
            //String reg="((^\\d+(\\.\\d+)?(元?|日?|分?|年?|月?|%?|天?|时?|号?))|((^\\w+))\\s*)|[-.。`·,\\.?:;!'\\/\\\\“”]";
            String reg="((^\\d+(\\.\\d+)?(元?|日?|分?|年?|月?|%?|天?|时?|号?))|((^\\w+))\\s*)|[-.。`·,\\.?:;!'\\/\\\\“”\\*、]";


            for(String x:copyOnWriteArrayList) {
                if (stopwords.contains(x) || x.matches(reg)) {
                    //int index=list.indexOf(x);
                    //list.remove(index);
                    copyOnWriteArrayList.remove(x);
                }
            }
            if(copyOnWriteArrayList==null){
                copyOnWriteArrayList.add("");
            }
            return copyOnWriteArrayList;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return copyOnWriteArrayList;
    }

    /*
    * 将String拆分为List<String>类型
    * */
    public static List<String> StringToList(String str,String separator) {
        String[] arr=str.split(separator);
        List<String> ls=Arrays.asList(arr);
        return ls;
    }


}
