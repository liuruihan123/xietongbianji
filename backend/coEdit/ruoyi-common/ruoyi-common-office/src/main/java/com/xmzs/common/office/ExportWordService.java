package com.xmzs.common.office;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.xmzs.common.office.entity.Templates;
import com.xmzs.common.office.enums.StyleEnum;
import com.xmzs.common.office.utils.HtmlUtil;
import org.ddr.poi.html.HtmlRenderPolicy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出为word
 */
public class ExportWordService {
//    private final SysConfig sysConfig;
    public static void main(String[] args) throws IOException {

        String ti ="测试文档";
        String con ="<p> 测测测测测</p>";
        String docp = "D:\\ruoyi\\uploadPath\\zip\\tt.docx";
        new ExportWordService().exportWord(ti,con,docp);
    }

    /**
     * 导出word
     * @param title
     * @param richText
     * @param docPath
     * @return
     * @throws IOException

     */
    public Boolean exportWord(String title,String richText,String docPath) throws IOException {
        // 1、初始化数据
        Templates templates = initTemplates(title,richText);

// 2、填充数据
        Map<String, Object> data = new HashMap<>(8);
        data.put("title", templates.getTitle());
        // 3、判断图片是否超出word最大宽度，否则需要等比缩小
        // 3.1、处理前图片样式
//        data.put("richText1", templates.getRichtext());
// 3.2、处理后图片样式
        data.put("richText1", dealWithPictureWidthAndHeight(templates.getRichtext()));
// 4、指定插件
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();
        ConfigureBuilder builder = Configure.builder();
        Configure config = builder.build();
        config.customPolicy("richText1", htmlRenderPolicy);
        dynamicExportWord(data, new File(docPath), new ClassPathResource("templates" + File.separator + "exportWordTemplate.docx"), config, null, false);
        return true;
    }




    private Templates initTemplates(String title,String richText) {
        // 1、数据来源,实际项目里是通过sql查询出来的
        Templates templates = new Templates();
        templates.setTitle(title);
        // 解析图文中图片转化为base64
        Document doc = Jsoup.parse(richText);
        List<Element> imgels = doc.getElementsByTag("img");
//        String basePath = sysConfig.getUploadPath();
        String basePath = "待定";
        for(Element imgel:imgels){
            String imgurl = imgel.attr("src");
            imgurl = imgurl.replace("/dev-api/profile/upload",basePath);
            try {
                String baseString = base64String(new File(imgurl));
                baseString = "data:image/jpeg;base64," + baseString;
                imgel.attr("src",baseString);
            }catch (Exception e){
                System.out.println("error::"+e.getMessage());
            }

        }
        String contentstr = doc.toString();
        templates.setRichtext(contentstr);
        return templates;
    }

    private String dealWithPictureWidthAndHeight(String content) {
        List<HashMap<String, String>> imagesFiles = HtmlUtil.regexMatchWidthAndHeight(content);
        if (!imagesFiles.isEmpty()) {
            for (HashMap<String, String> imagesFile : imagesFiles) {
                String newFileUrl = imagesFile.get(StyleEnum.NEW_FILE_URL.getValue());
                String fileUrl = imagesFile.get(StyleEnum.FILE_URL.getValue());
                if (newFileUrl != null) {
                    content = content.replace(fileUrl, newFileUrl);
                }
            }
        }
        return content;
    }

    public String base64String(File file) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[1024];
            //用来定义一个准备接收图片总长度的局部变量
            int len;
            //将流的内容读取到bytes中
            while ((len = fileInputStream.read(bytes)) > 0) {
                //将bytes内存中的内容从0开始到总长度输出出去
                out.write(bytes, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //通过util包中的Base64类对字节数组进行base64编码
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    public static String getImageBase(String src)  {

        URL url = null;
        try {
            url = new URL(src);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream in = null;
        byte[] data = null;
        try {
            in = url.openStream();//远程文件
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    /**
     * @param data      填充数据
     * @param filePath  临时路径
     * @param resource  模板路径
     * @param response  通过浏览器下载需要
     * @param isBrowser true-通过浏览器下载  false-下载到临时路径
     */
    public void dynamicExportWord(Map<String, Object> data, File filePath, Resource resource, Configure config, HttpServletResponse response, boolean isBrowser) throws IOException {
        XWPFTemplate render = XWPFTemplate.compile(resource.getInputStream(), config).render(data);
        if (Boolean.FALSE.equals(isBrowser)) {
            File parentFile = filePath.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(filePath);
            render.writeAndClose(out);
        } else {
            ServletOutputStream out = response.getOutputStream();
            render.writeAndClose(out);
        }
    }
}
