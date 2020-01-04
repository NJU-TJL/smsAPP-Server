import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static int myPort = 5000;
    public static String rootPath = "C:\\From_Android\\";
    public static String res_rootPath =rootPath+"Results\\";
    public static String raw_rootPath =rootPath+"raw\\";
    public static String picturePath ="C:\\From_Android\\pictures\\Test-WordCloud.txt";
    public static String wordCloudPath = rootPath+"WordCloudFile\\";

    File raw_directory;
    File res_directory;
    public final int SendFile=0; //相对客服端
    public final int GetFile=1;
    public static SortSMS mySorter;
    public static ExtractionMySQL em;

    //2019/9/1 Update
    public static ExtractionALL second_Classification;

    //2019.09.28 TJL 暂定存放Res_ExtracionALL.txt的路径
    public static String Res_ExtracionALL_Path ="C:\\From_Android\\Res_ExtracionALL\\Res_ExtracionALL.txt";

    public static void main(String[] args) {
        //标准输出重定向
        PrintStream myout = null;
        try {
            myout = new PrintStream("temp_output.no_use");
            System.setOut(myout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        new Main().go();
    }
    public void go () {
        try {
            ServerSocket serverSocket = new ServerSocket(myPort);
            mySorter = new SortSMS();
            em = new ExtractionMySQL();

            //以防目录不存在，预先创建
            raw_directory = new File(raw_rootPath);
            if (!raw_directory.exists()) {
                raw_directory.mkdirs();
            }
            res_directory = new File(res_rootPath);
            if (!res_directory.exists()) {
                res_directory.mkdirs();
            }
            res_directory = new File(wordCloudPath);
            if (!res_directory.exists()) {
                res_directory.mkdirs();
            }


            System.err.println("...TCP Socket已启动...");
            while (true) {
                try {
                    //此处是线程阻塞的,所以需要在子线程中
                    Socket socket = serverSocket.accept();
                    //请求成功，响应客户端的请求
                    //获取输入流,读取客户端发送来的文件
                    InputStream ins=socket.getInputStream();
                    DataInputStream dis = new DataInputStream(ins);
                    OutputStream out = socket.getOutputStream();
                    // 文件名和长度
                    int serviceCode=dis.readInt();
                    if(serviceCode == SendFile){
                        int numOfSMS=dis.readInt();

                        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy.MM.dd_HH.mm.ss");
                        Date curDate =  new Date(System.currentTimeMillis());
                        String fileName_Time = formatter.format(curDate);
                        // 文件名
                        String fileName=numOfSMS+"条-"+fileName_Time+".txt";
                        DataOutputStream dos = new DataOutputStream(out);
                        dos.writeUTF(fileName);

                        long fileLength=dis.readLong();
                        FileOutputStream fos = new FileOutputStream(raw_directory.getAbsolutePath() + "\\" + fileName);
                        // 开始接收文件
                        byte[] bytes = new byte[1024];
                        long length = 0;
                        while (length < fileLength) {
                            int tempSize = dis.read(bytes, 0, (int)Math.min((long)bytes.length,fileLength-length));
                            length +=tempSize;
                            fos.write(bytes, 0, tempSize);
                            fos.flush();
                        }
                        fos.close();
                        if(length != fileLength){
                            System.err.println("接收文件失败，大小不匹配："+ raw_directory.getAbsolutePath() + "\\" + fileName);
                        }

                        System.err.println("接收成功："+ raw_directory.getAbsolutePath() + "\\" + fileName+" | 进入处理...");
                        String rawFilePath=raw_directory.getAbsolutePath() + "\\" + fileName;
                        String destFile=dealWithFile(rawFilePath,fileName);
                        dos.writeUTF("Done...");
                        dos.flush();
                        System.err.println("处理结束："+fileName);

                        //发送统计得出的支入支出信息
                        dos.writeDouble(mySorter.myBayes.ex.income_num);
                        dos.writeDouble(mySorter.myBayes.ex.pay_num);

                        //发送处理结果
                        File file = new File(destFile);
                        long tempFileLength=file.length();
                        dos.writeLong(tempFileLength);
                        dos.flush();
                        if (file.exists()) {
                            FileInputStream fileInput = new FileInputStream(file);
                            int maxlength = 0;
                            while ((maxlength = fileInput.read(bytes)) != -1) {
                                dos.write(bytes, 0, maxlength);
                                dos.flush();
                            }
                            fileInput.close();
                        }

                        //发送词云图文件
                        File picfile = new File(wordCloudPath+fileName);
                        long tempFileLength2 = picfile.length();
                        //System.err.println("picFile Length:"+tempFileLength2); //DEBUG
                        dos.writeLong(tempFileLength2);
                        dos.flush();
                        if (picfile.exists()) {
                            FileInputStream fileInput = new FileInputStream(picfile);
                            int maxlength = 0;
                            while ((maxlength = fileInput.read(bytes)) != -1) {
                                dos.write(bytes, 0, maxlength);
                                dos.flush();
                            }
                            fileInput.close();
                        }

                        //2019/9/1
                        //发送二次分类文件
                        File second_file = new File(Res_ExtracionALL_Path);
                        long tempFileLength3 = second_file.length();
                        //System.err.println("picFile Length:"+tempFileLength2); //DEBUG
                        dos.writeLong(tempFileLength3);
                        dos.flush();
                        if (second_file.exists()) {
                            FileInputStream fileInput = new FileInputStream(second_file);
                            int maxlength = 0;
                            while ((maxlength = fileInput.read(bytes)) != -1) {
                                dos.write(bytes, 0, maxlength);
                                dos.flush();
                            }
                            fileInput.close();
                        }

                        dis.readUTF();
                        //可结束
                        socket.shutdownOutput();;
                        dos.flush();dos.close();out.flush();out.close();
                        dis.close();ins.close();
                        socket.close();
                        System.err.println("已向客户端发送："+file.getName());

                        //TODO:存入数据库：
                        em.MySQL_solve_file(em.myBayes,rawFilePath);
                        System.err.println("数据库线程结束 "+rawFilePath+"\n");

                    }else if(serviceCode==GetFile){ //使用不同数字，代表客户端请求的是哪种服务
                        //此服务编码 暂时未使用 即目前就一种与APP的交互方式
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }
    public String dealWithFile (String filePath, String fileName) {
//        //新线程，存入数据库
//        new Thread(() -> {
//            em.MySQL_solve_file(em.myBayes,filePath);
//            System.err.println("数据库线程结束 "+filePath);
//        }).start();

//        mySorter.em.MySQL_solve_file(mySorter.myBayes,filePath);

        mySorter.myBayes.ex.income_num=0.0;
        mySorter.myBayes.ex.pay_num=0.0;
        mySorter.myBayes.myWF=new WordFrequency();
        String results = mySorter.doSortSMS(filePath);

        //2019/9/28 ——TJl
        second_Classification = new ExtractionALL();
        second_Classification.Handle(res_rootPath+fileName,3);//提供分类后的短信
        second_Classification = null;

        mySorter.myBayes.myWF.Output(wordCloudPath+fileName,20,mySorter.myBayes.myWF);
        return results;
    }
}
