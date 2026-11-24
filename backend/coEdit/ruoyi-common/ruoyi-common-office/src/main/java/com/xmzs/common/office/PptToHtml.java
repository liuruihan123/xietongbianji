package com.xmzs.common.office;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.xslf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @ClassName : PptToHtml
 * @Description ：
 * @Author : zxy
 * @Date : 2023/10/26 13:40
 * @Version : 1.0
 */
public class PptToHtml {


    static String path = "C:\\pdf测试\\";

    public static void main(String[] args) throws Exception {

        pptToHtml(path + "PPT模板(公开).pptx", path + "html\\PPT模板(公开)-pptx.html");
        pptToHtml(path + "PPT模板(公开).ppt", path + "html\\PPT模板(公开)-ppt.html");
    }

    /**
     * PPT 转 HTML
     *
     * @return
     */
    public static void pptToHtml(String sourcePath, String path) {
        File pptFile = new File(sourcePath);
        if (pptFile.exists()) {
            try {
                if (sourcePath.endsWith("ppt")) {
                    String htmlStr = toImage2003(sourcePath);
                    FileUtil.writeUtf8String(htmlStr, path);
                } else if (sourcePath.endsWith("pptx")) {
                    String htmlStr = toImage2007(sourcePath);
                    FileUtil.writeUtf8String(htmlStr, path);
                } else {
                    System.out.println("the file is not a ppt");
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("file does not exist!");
        }

    }

    public static String toImage2007(String sourcePath) throws Exception {
        return toImage2007(new FileInputStream(sourcePath));
    }


    public static String toImage2007(InputStream is) throws Exception {
        String htmlStr = "";
        XMLSlideShow ppt = new XMLSlideShow(is);
        is.close();
        Dimension pgsize = ppt.getPageSize();
        System.out.println(pgsize.width + "--" + pgsize.height);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ppt.getSlides().size(); i++) {
            try {
                // 防止中文乱码
                for (XSLFShape shape : ppt.getSlides().get(i).getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape tsh = (XSLFTextShape) shape;
                        for (XSLFTextParagraph p : tsh) {
                            for (XSLFTextRun r : p) {
                                r.setFontFamily("宋体");
                            }
                        }
                    }
                }
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                // clear the drawing area
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                // render
                ppt.getSlides().get(i).draw(graphics);
                sb.append("<html>");
                sb.append("<body>");
				/*sb.append(
						"<input type=" + "button" + " value=" + "打印" + " href=" + "javascript:void(0);" + " onclick=" + "window.print();"
								+ "  class=" + "noprint" + " style="
								+ "height:30px;width: 60px; padding-right:5px;align:right;float:left;FONT-WEIGHT: bold;FONT-SIZE: 12pt;COLOR: #000000;FONT-FAMILY: Arial"
								+ ">");
				sb.append("<br />");*/
                sb.append("<p style=text-align:center;>");
//                sb.append("<img src='data:image/png;base64," + Base64.encodeBase64String(bufferedImageToByteArray(img)) + "'/>");
                sb.append("<img src='data:image/png;base64," + Base64.encode(bufferedImageToByteArray(img)) + "'/>");
                sb.append("</p>");
                sb.append("<br />");
                sb.append("</html>");
                sb.append("</body>");
				/*FileOutputStream out = new FileOutputStream(imagePath);
				javax.imageio.ImageIO.write(img, "png", out);
				out.close();*/
                System.out.println("第" + i + "张ppt转换成功");
            } catch (Exception e) {
                System.out.println("第" + i + "张ppt转换出错");
            }
        }
        System.out.println("success");
        htmlStr = sb.toString();
        return htmlStr;
    }


    public static String toImage2003(String sourcePath) throws FileNotFoundException {
        return toImage2003(new FileInputStream(sourcePath));
    }

    public static String toImage2003(InputStream sourcePath) {
        String htmlStr = "";
        try {
            HSLFSlideShow ppt = new HSLFSlideShow(new HSLFSlideShowImpl(sourcePath));
            Dimension pgsize = ppt.getPageSize();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < ppt.getSlides().size(); i++) {
                // 防止中文乱码
                for (HSLFShape shape : ppt.getSlides().get(i).getShapes()) {
                    if (shape instanceof HSLFTextShape) {
                        HSLFTextShape tsh = (HSLFTextShape) shape;
                        for (HSLFTextParagraph p : tsh) {
                            for (HSLFTextRun r : p) {
                                r.setFontFamily("宋体");
                            }
                        }
                    }
                }
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                // clear the drawing area
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                // render
                ppt.getSlides().get(i).draw(graphics);
                sb.append("<html>");
                sb.append("<body>");
				/*sb.append(
						"<input type=" + "button" + " value=" + "打印" + " href=" + "javascript:void(0);" + " onclick=" + "window.print();"
								+ "  class=" + "noprint" + " style="
								+ "height:30px;width: 60px; padding-right:5px;align:right;float:left;FONT-WEIGHT: bold;FONT-SIZE: 12pt;COLOR: #000000;FONT-FAMILY: Arial"
								+ ">");
				sb.append("<br />");*/
                sb.append("<p style=text-align:center;>");
                //				sb.append("<img src=" + "\"" + imagewebpath + "\"" + "/>");

                //sb.append("<img src='data:image/png;base64," + Base64.encodeBase64String(bufferedImageToByteArray(img)) + "'/>");
                sb.append("<img src='data:image/png;base64," + Base64.encode(bufferedImageToByteArray(img)) + "'/>");


                sb.append("</p>");
                sb.append("<br />");
                sb.append("</html>");
                sb.append("</body>");
				/*FileOutputStream out = new FileOutputStream(imagePath);
				javax.imageio.ImageIO.write(img, "png", out);
				out.close();*/
                System.out.println("PPT转换：" + i);
            }
            System.out.println("success");
            htmlStr = sb.toString();
        } catch (Exception e) {

        }
        return htmlStr;
    }


    /**
     * 将BufferedImage转换为byte[]
     *
     * @param image
     * @return
     */
    public static byte[] bufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return os.toByteArray();
    }
}
