package top.mortise.utils.netutils;

import top.mortise.utils.uniqueID.DBObjectID;
import top.mortise.utils.comm.DateExt;
import top.mortise.utils.loghelper.LogHelper;

import top.mortise.utils.encrypt.Base64Utils;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
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
    @Value("${mortise.uploadfile.save.uploadRemote}")
    private  String uploadRemote;
    // 临时文件目录
    @Value("${mortise.uploadfile.save.temppath}")private String _tempPath;
    @Value("${mortise.uploadfile.save.root}") private String _uploadRoot;

    @Value("${mortise.uploadfile.visitDomain}") private String visitDomain;
    //远程目录
    @Value("${mortise.uploadfile.save.remoteUploadUrl}")private String remoteUploadUrl;
    //远程目录
    @Value("${mortise.uploadfile.save.remoteUploadkey}")private String remoteUploadkey;

    String tempPath;
    String uploadRoot;
    File tempPathFile;


    private void init(HttpServletRequest request) throws ServletException, IOException {

        if (uploadRemote.equals("false")) {
            File file = ResourceUtils.getFile("classpath:static");
            String webDir= file.getCanonicalPath();
           // String webDir=this.getClass().getResource("/").getPath()+"/static";
            tempPath= (webDir+_tempPath).replace("//","/");
            uploadRoot=(webDir+_uploadRoot).replace("//","/");

        }else{
            tempPath = _tempPath;
            uploadRoot=_uploadRoot;
        }
        File uploadFile = new File(uploadRoot);
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }
        File tempPathFile = new File(tempPath);
        if (!tempPathFile.exists()) {
            tempPathFile.mkdirs();
        }
    }
    public String SaveFileByMultiFlie(MultipartFile file, HttpServletRequest request, String uploadEnum)
            throws ServletException, IOException {

        //init();
        if(uploadRemote.equals("false")){
            return SaveFileByMultiLocal(file,uploadEnum,request);
        }else {
            return SaveFileByRemoteServer(file,uploadEnum,request);
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
    String SaveFileByMultiLocal(MultipartFile file, String uploadEnum,HttpServletRequest request)
            throws ServletException, IOException {

        init(request);
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(1024 * 1024); // 设置缓冲区大小，这里是1mb
        factory.setRepository(tempPathFile);// 设置缓冲区目录

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        if (!file.isEmpty() && file.getSize() > 0) {
            String fullFileName = file.getOriginalFilename();
            //获取扩展名称
            String suffixName = fullFileName.substring(fullFileName.lastIndexOf("."));
            String uploadPath = uploadRoot + uploadEnum.toString();


            String newFileName = new DBObjectID().toString() + suffixName;
            String basicPath = "/"+uploadEnum+"/"+ DateExt.GetNowDate() + "/";
            String _uploadPath=uploadRoot+basicPath;
            _uploadPath=_uploadPath.replace("//","/");
            String newFilePath = _uploadPath+ newFileName;
            try {
                File dest = new File(newFilePath);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                try {
                    file.transferTo(dest);

                    String  returnUrl=(visitDomain+_uploadRoot+basicPath+"/"+newFileName).replace("//","/");
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
    String SaveFileByRemoteServer( MultipartFile file,  String uploadEnum,HttpServletRequest request)throws ServletException, IOException{
        init(request);
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(1024 * 1024); // 设置缓冲区大小，这里是1mb
        factory.setRepository(tempPathFile);// 设置缓冲区目录

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        if (!file.isEmpty() && file.getSize() > 0) {
            String fullFileName = file.getOriginalFilename();

            try {
                Map<String, String> textMap = new HashMap<String, String>();
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
     * @param Data base64内容
     * @param uploadEnum 保存类型，
     * @param fileType 文件类型：“.jpg”
     * @param request
     * @return
     * @throws Exception
     */
    public String SaveStringFile(String Data, String uploadEnum,String fileType, HttpServletRequest request) throws Exception {
        if(uploadRemote.equals("false")){
            return SaveStringFileLocal(Data,uploadEnum,fileType,request);
        }else{
            return  SaveStringFileremote(Data,uploadEnum,fileType,request);
        }

    }

    String SaveStringFileLocal(String Data,  String uploadEnum,String fileType,HttpServletRequest request) throws Exception {
        try {
            init(request);
            byte[] filedata = Base64Utils.decode(Data);

            String _virPath=  _uploadRoot+ "/"+uploadEnum+"/"+ DateExt.GetNowDate() + "/";
            _virPath=_virPath.replace("//","/");
            String _uploadPath=uploadRoot+_virPath;
            _uploadPath=_uploadPath.replace(_uploadRoot+_uploadRoot,_uploadRoot).replace("//","/");
            File file_uploadPath = new File(_uploadPath);
            if (!file_uploadPath.exists()) {
                file_uploadPath.mkdirs();
            }
            String _filename = new DBObjectID().toString();
            _filename+= fileType;
            String savefilename = _uploadPath+ _filename;
            File file = new File(savefilename);
            FileOutputStream fos = new FileOutputStream(file);

            //用FileOutputStream 的write方法写入字节数组
            fos.write(filedata);
            System.out.println("写入成功");

            //为了节省IO流的开销，需要关闭
            fos.close();

            return (_virPath + "/" + _filename).replace("//", "/");
        } catch (Exception ex) {
            LogHelper.logger().error("方法SaveStringFileLocal异常：" + ex.getMessage());
        }
        return "";

    }


    String SaveStringFileremote(String Data,  String uploadEnum,String fileType,HttpServletRequest request) throws Exception {
        try {
            init(request);


            String fullFileName = new DBObjectID().toString() + fileType;
            byte[] filedata = Base64Utils.decode(Data);
            InputStream inputStream = new ByteArrayInputStream(filedata);
            try {
                Map<String, String> textMap = new HashMap<String, String>();
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
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
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
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
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
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
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

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
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

}
