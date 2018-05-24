package top.mortise.utils.comm;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName) {
       return readFileByLines(fileName,0);
    }
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName,int startLine) {
      return readFileByLines(fileName,startLine,Integer.MAX_VALUE);
    }
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName,int startLine,int endLine) {
        File file =null;// new File(fileName);
        List<String> result = new ArrayList<>();
        BufferedReader reader = null;
        try {
            file=new File(fileName);
            if(file.exists()){
                reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
                String tempString = null;
                int line = 1;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    if(startLine<=line&&line<=endLine){
                        line++;
                        result.add(tempString);
                    }
                    if(line>endLine){
                        break;
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return result;
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readAllStrFile(String fileName) {

        File file =null;// new File(fileName);
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            file=new File(fileName);
            if(file.exists()){
                reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
                String tempString = null;
                int line = 1;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    sb.append(tempString);
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString();
    }


    /**
     * 追加文件：使用FileWriter
     */
    public static void appendString(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 追加文件：使用FileWriter
     */
    public static void appendchars(String fileName, char[] content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 追加文件：使用FileWriter
     */
    public static void writeFile(String fileName, String content) {
        File file=new File(fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, false);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 追加文件：使用FileWriter
     */
    public static void writeFile(String fileName, char[] content) {
        File file=new File(fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, false);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        try {
            File file = new File(fileName);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    System.out.println("删除单个文件" + fileName + "成功！");
                    return true;
                } else {
                    System.out.println("删除单个文件" + fileName + "失败！");
                    return false;
                }
            } else {
                System.out.println("删除单个文件失败：" + fileName + "不存在！");
                return false;
            }
        }catch (Exception ex){
            System.out.println("删除单个文件失败：" + ex.getMessage());
            return false;

        }
    }
    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        try {
            // 如果dir不以文件分隔符结尾，自动添加文件分隔符
            if (!dir.endsWith(File.separator))
                dir = dir + File.separator;
            File dirFile = new File(dir);
            // 如果dir对应的文件不存在，或者不是一个目录，则退出
            if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
                System.out.println("删除目录失败：" + dir + "不存在！");
                return false;
            }

            // 删除当前目录
            if (dirFile.delete()) {
                System.out.println("删除目录" + dir + "成功！");
                return true;
            } else {
                return false;
            }
        }catch (Exception ex){
            System.out.println("删除目录失败：" + ex.getMessage());
            return false;
        }
    }

}
