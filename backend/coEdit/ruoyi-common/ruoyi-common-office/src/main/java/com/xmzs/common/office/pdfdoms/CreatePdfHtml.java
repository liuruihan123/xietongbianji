package com.xmzs.common.office.pdfdoms;
/**
 * PDFToHTML.java
 * (c) Radek Burget, 2011
 *
 * Pdf2Dom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pdf2Dom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CSSBox. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created on 19.9.2011, 13:34:54 by burgetr
 */

import com.xmzs.common.office.pdfdoms.resource.HtmlResourceHandler;
import com.xmzs.common.office.pdfdoms.resource.IgnoreResourceHandler;
import com.xmzs.common.office.pdfdoms.resource.SaveResourceToDirHandler;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author burgetr
 *
 */
public class CreatePdfHtml
{

    public static void main(String[] args) throws IOException, ParserConfigurationException {

        String filepath = "E:\\测试pdf目录导航.pdf";
        String content = getDocFileTextContent(new FileInputStream(new File(filepath)));
        System.out.println("content = " + content);
//       args = new String[]{"D:/f/file/世界军事.pdf", "D:/f/file/out3.html"};
//        saveHtmlFile(args);
//        String infile = args[0];
//        String outfile;
//        if (args.length > 1 && !args[1].startsWith("-"))
//            outfile = args[1];
//        else
//        {
//            String base = args[0];
//            if (base.toLowerCase().endsWith(".pdf"))
//                base = base.substring(0, base.length() - 4);
//            outfile = base + ".html";
//        }
//
//        PDFDomTreeConfig config = parseOptions(args);
//
//        PDDocument document = null;
//        try
//        {
//            document = PDDocument.load(new File(infile));
//            PDFDomTree parser = new PDFDomTree(config);
//            //parser.setDisableImageData(true);
//            Writer output = new PrintWriter(outfile, "utf-8");
//            parser.writeText(document, output);
//            output.close();
//        }
//        catch (Exception e)
//        {
//            System.err.println("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        finally
//        {
//            if( document != null )
//            {
//                try
//                {
//                    document.close();
//                } catch (IOException e) {
//                    System.err.println("Error: " + e.getMessage());
//                    //e.printStackTrace();
//                }
//            }
//        }
    }


    /**
     *
     * 根据pdf生成对应的html页面
     * @param sourcePath
     *     pdf路径
     * @param savePath
     *     生成的html路径
     * @return
     *   生成html文件路径
     */
    public static String createHtmlPage(String sourcePath,String savePath){
        String ps[] = {sourcePath,savePath};
        CreatePdfHtml.saveHtmlFile(ps);
        return savePath;
    }
    public static String getDocFileTextContent(InputStream fileStream) throws IOException, ParserConfigurationException {
        PDDocument document = null;
        String content = "";
        try
        {
            document = PDDocument.load(fileStream);
            content = convertPDDocumentToString(document);
        }catch (Exception e){
            System.out.println(e);
        }
        return content;
    }

    public static String convertPDDocumentToString(PDDocument document) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        return pdfStripper.getText(document);
    }
    /**
     * 根据pdf生成对应的html文件
     * @param args
     *   args[0] pdf路径    args[1] html路径
     * @return
     */
    private static boolean saveHtmlFile(String[] args){
        String infile = args[0];
        String outfile;
        if (args.length > 1 && !args[1].startsWith("-"))
            outfile = args[1];
        else
        {
            String base = args[0];
            if (base.toLowerCase().endsWith(".pdf"))
                base = base.substring(0, base.length() - 4);
            outfile = base + ".html";
        }

        PDFDomTreeConfig config = parseOptions(args);

        PDDocument document = null;
        try
        {
            document = PDDocument.load(new File(infile));
            PDFDomTree parser = new PDFDomTree(config);
            //parser.setDisableImageData(true);
            Writer output = new PrintWriter(outfile, "utf-8");
            parser.writeText(document, output);
            output.close();
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if( document != null )
            {
                try
                {
                    document.close();
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                    //e.printStackTrace();
                }
            }
        }
        return true;
    }




    private static PDFDomTreeConfig parseOptions(String[] args)
    {
        PDFDomTreeConfig config = PDFDomTreeConfig.createDefaultConfig();

        List<CommandLineFlag> flags = parseFlags(args);
        for (CommandLineFlag flagOn : flags)
        {
            if (flagOn.flagName.equals("fm"))
            {
                HtmlResourceHandler handler = createResourceHandlerFor(flagOn.value);
                config.setFontHandler(handler);
            } else if (flagOn.flagName.equals("fdir"))
                config.setFontHandler(new SaveResourceToDirHandler(new File(flagOn.value)));

            else if (flagOn.flagName.equals("im"))
            {
                HtmlResourceHandler handler = createResourceHandlerFor(flagOn.value);
                config.setImageHandler(handler);
            } else if (flagOn.flagName.equals("idir"))
                config.setImageHandler(new SaveResourceToDirHandler(new File(flagOn.value)));
        }

        return config;
    }

    private static HtmlResourceHandler createResourceHandlerFor(String value)
    {
        HtmlResourceHandler handler = PDFDomTreeConfig.embedAsBase64();
        if (value.equalsIgnoreCase("EMBED_BASE64"))
            handler = PDFDomTreeConfig.embedAsBase64();
        else if (value.equalsIgnoreCase("SAVE_TO_DIR"))
            handler = new SaveResourceToDirHandler();
        else if (value.equalsIgnoreCase("IGNORE"))
            handler = new IgnoreResourceHandler();

        return handler;
    }

    private static List<CommandLineFlag> parseFlags(String[] args)
    {
        List<CommandLineFlag> flags = new ArrayList<CommandLineFlag>();
        for (String argOn : args)
        {
            if (argOn.startsWith("-"))
                flags.add(CommandLineFlag.parse(argOn));
        }
        return flags;
    }

    private static class CommandLineFlag
    {
        public String flagName;
        public String value = "";

        public static CommandLineFlag parse(String argOn)
        {
            CommandLineFlag flag = new CommandLineFlag();
            String[] flagSplit = argOn.split("=");
            flag.flagName = flagSplit[0].replace("-", "");
            if (flagSplit.length > 1)
                flag.value = flagSplit[1].replace("=", "");

            return flag;
        }
    }
}
