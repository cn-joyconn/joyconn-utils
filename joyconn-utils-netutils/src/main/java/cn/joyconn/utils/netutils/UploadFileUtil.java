package cn.joyconn.utils.netutils;

import cn.joyconn.utils.uniqueID.DBObjectID;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import cn.joyconn.utils.comm.DateExt;
import cn.joyconn.utils.encrypt.Base64Utils;
import cn.joyconn.utils.loghelper.LogHelper;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Created by Eric.Zhang on 2017/3/13.
 */
@Service
public class UploadFileUtil {
    @Value("${joyconn.uploadfile.save.uploadRemote:}")
    private  String uploadRemote;
    // 临时文件目录
    @Value("${joyconn.uploadfile.save.temppath:}")
    private String saveTempPath;
    // 缓冲区文件大小
    @Value("${joyconn.uploadfile.save.temppath.size:1}")
    private int tempPathSize;
    @Value("${joyconn.uploadfile.save.root:}")
    private String saveUploadRoot;

    @Value("${joyconn.uploadfile.visitDomain:}")
    private String visitDomain;
    //远程目录
    @Value("${joyconn.uploadfile.save.remoteUploadUrl:}")
    private String remoteUploadUrl;
    //远程目录
    @Value("${joyconn.uploadfile.save.remoteUploadkey:}")
    private String remoteUploadkey;

    




    private FileSaveVal init(HttpServletRequest request) throws ServletException, IOException {

        FileSaveVal fileSaveVal =new FileSaveVal();
        if ("false".equals(uploadRemote)) {
            File file = ResourceUtils.getFile("classpath:static");
            String webDir= file.getCanonicalPath();
           // String webDir=this.getClass().getResource("/").getPath()+"/static";
            fileSaveVal.setTempPath((webDir+saveTempPath).replace("//","/"));
            fileSaveVal.setUploadRoot((webDir+saveUploadRoot).replace("//","/"));

        }else{

            fileSaveVal.setTempPath(saveTempPath);
            fileSaveVal.setUploadRoot(saveUploadRoot);
        }
        File uploadFile = new File(fileSaveVal.getUploadRoot());
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }
        fileSaveVal.setTempPathFile( new File(fileSaveVal.getTempPath()));
        if (!fileSaveVal.getTempPathFile().exists()) {
            fileSaveVal.getTempPathFile().mkdirs();
        }
        return fileSaveVal;
    }
    public String saveFileByMultiFlie(MultipartFile file, HttpServletRequest request, String uploadEnum)
            throws ServletException, IOException {

        //init();
        if ("false".equals(uploadRemote)) {
            return saveFileByMultiLocal(file,uploadEnum,request);
        }else {
            return saveFileByRemoteServer(file,uploadEnum,request);
        }
    }

    /**
     * 保存到本地
     * @param file
     * @param uploadEnum
     * @return
     * @throws ServletException
     * @throws IOException
     */
    String saveFileByMultiLocal(MultipartFile file, String uploadEnum,HttpServletRequest request)
            throws ServletException, IOException {

        FileSaveVal fileSaveVal = init(request);
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(tempPathSize*1024 * 1024); // 设置缓冲区大小，这里是1mb
        factory.setRepository(fileSaveVal.getTempPathFile());// 设置缓冲区目录

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        if (!file.isEmpty() && file.getSize() > 0) {
            String fullFileName = file.getOriginalFilename();
            //获取扩展名称
            String suffixName = fullFileName.substring(fullFileName.lastIndexOf("."));
            String uploadPath = fileSaveVal.getUploadRoot() + uploadEnum.toString();


            String newFileName = new DBObjectID().toString() + suffixName;
            String basicPath = "/"+uploadEnum+"/"+ DateExt.getNowDate() + "/";
            String newUploadPath=fileSaveVal.getUploadRoot()+basicPath;
            newUploadPath=newUploadPath.replace("//","/");
            String newFilePath = newUploadPath+ newFileName;
            try {
                File dest = new File(newFilePath);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                try {
                    file.transferTo(dest);

                    String  returnUrl=(visitDomain+saveUploadRoot+basicPath+"/"+newFileName).replace("//","/");
                    return returnUrl;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // 可以跳转出错页面
                LogHelper.logger().error("后台用户上传头像失败：" + e.getMessage());
                return "";
            }
        } else {
            //没有文件\
            return "";
        }

        return "";
    }

    /**
     * 上传到远程服务器
     * @param file
     * @param uploadEnum
     * @return
     * @throws ServletException
     * @throws IOException
     */
    String saveFileByRemoteServer( MultipartFile file,  String uploadEnum,HttpServletRequest request)throws ServletException, IOException{
        FileSaveVal fileSaveVal = init(request);
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(1024 * 1024); // 设置缓冲区大小，这里是1mb
        factory.setRepository(fileSaveVal.getTempPathFile());// 设置缓冲区目录

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        if (!file.isEmpty() && file.getSize() > 0) {
            String fullFileName = file.getOriginalFilename();

            try {
                Map<String, String> textMap = new HashMap<>(2);
                textMap.put("uploadEnum", uploadEnum.toString());
                textMap.put("uploadKey", remoteUploadkey);
                String proff=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String result =  formUpload(remoteUploadUrl,textMap,"file",fullFileName,file.getInputStream(),proff);
                return  result;

            } catch (Exception e) {
                // 可以跳转出错页面
                LogHelper.logger().error("后台用户上传头像失败：" + e.getMessage());
                return "";
            }
        } else {
            //没有文件\
            return "";
        }

    }




    /**
     * base64保存成文件
     * @param data base64内容
     * @param uploadEnum 保存类型，
     * @param fileType 文件类型：“.jpg”
     * @param request
     * @return
     * @throws Exception
     */
    public String saveStringFile(String data, String uploadEnum,String fileType, HttpServletRequest request) throws Exception {

        if ("false".equals(uploadRemote)) {
            return saveStringFileLocal(data,uploadEnum,fileType,request);
        }else{
            return  saveStringFileremote(data,uploadEnum,fileType,request);
        }

    }

    String saveStringFileLocal(String data,  String uploadEnum,String fileType,HttpServletRequest request) throws Exception {
        try {
            FileSaveVal fileSaveVal = init(request);
            byte[] filedata = Base64Utils.decode(data);
            String newFilename = new DBObjectID().toString()+fileType;
            String basicPath = "/"+uploadEnum+"/"+ DateExt.getNowDate() + "/";
            String newUploadPath=fileSaveVal.getUploadRoot()+basicPath;
            newUploadPath=newUploadPath.replace("//","/");

             File fileUploadPath = new File(newUploadPath);
            if (!fileUploadPath.exists()) {
                fileUploadPath.mkdirs();
            }
            String savefilename = newUploadPath+ newFilename;
            File file = new File(savefilename);
            FileOutputStream fos = new FileOutputStream(file);

            //用FileOutputStream 的write方法写入字节数组
            fos.write(filedata);
            System.out.println("写入成功");

            //为了节省IO流的开销，需要关闭
            fos.close();

            String  returnUrl=(visitDomain+saveUploadRoot+basicPath+"/"+newFilename).replace("//","/");
            return returnUrl;
        } catch (Exception ex) {
            LogHelper.logger().error("方法SaveStringFileLocal异常：" + ex.getMessage());
        }
        return "";

    }


    String saveStringFileremote(String data,  String uploadEnum,String fileType,HttpServletRequest request) throws Exception {
        try {
            init(request);


            String fullFileName = new DBObjectID().toString() + fileType;
            byte[] filedata = Base64Utils.decode(data);
            InputStream inputStream = new ByteArrayInputStream(filedata);
            try {
                Map<String, String> textMap = new HashMap<>(2);
                textMap.put("uploadEnum",uploadEnum);
                textMap.put("uploadKey", remoteUploadkey);
                String result =  formUpload(remoteUploadUrl,textMap,"file",fullFileName,inputStream,fileType);
                return  result;

            } catch (Exception e) {
                // 可以跳转出错页面
                LogHelper.logger().error("后台用户上传头像失败：" + e.getMessage());
                return "";
            }

        } catch (Exception ex) {
            LogHelper.logger().error("后台用户上传头像失败SaveStringFile：" + ex.getMessage());
        }
        return "";

    }
    /**
     * 上传单个文件
     *
     * @param urlStr        上传地址
     * @param textMap       form中的参数
     * @param fileName      文件名
     * @param fileParamName 在表单中file的参数名
     * @param fileStream    文件流
     * @param fileType      文件类型
     * @return
     */
    public static String formUpload(String urlStr, Map<String, String> textMap, String fileParamName, String fileName, InputStream fileStream, String fileType) {
        String res = "";
        HttpURLConnection conn = null;
        String boundary= "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"" + fileParamName + "\"; filename=\"" + fileName + "\"\r\n");
            strBuf.append("Content-Type:" + new Mime().getContentTypeByFileExt(fileType) + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(fileStream);
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
            if(res!=null&&res.endsWith("\n")){
                res=res.substring(0,res.length()-1);
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }

    class FileSaveVal{
        String tempPath;
        String uploadRoot;
        File tempPathFile;

        public String getTempPath() {
            return tempPath;
        }

        public void setTempPath(String tempPath) {
            this.tempPath = tempPath;
        }

        public String getUploadRoot() {
            return uploadRoot;
        }

        public void setUploadRoot(String uploadRoot) {
            this.uploadRoot = uploadRoot;
        }

        public File getTempPathFile() {
            return tempPathFile;
        }

        public void setTempPathFile(File tempPathFile) {
            this.tempPathFile = tempPathFile;
        }
    }
}
