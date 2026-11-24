package com.xmzs.common.office.pdfdoms;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * 获取pdf目录
 */
public class GetIndex {


    /**
     * 根据pdf路径获取目录数据
     * @param pdfPath
     * @return
     * @throws IOException
     */
    public static  List<Map<String,String>>  getCatalog(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        PDDocument doc = null;
        FileInputStream  fis = null;
        try {
            fis = new FileInputStream(file);
            PDFParser parser = new PDFParser(new RandomAccessBuffer(fis));
            parser.parse();
            doc = parser.getPDDocument();
            PDDocumentOutline outline = doc.getDocumentCatalog().getDocumentOutline();
            List<Map<String,String>> mapList = printBookmarks(outline,"",null,new ArrayList<Map<String,String>>());
            if(mapList.size()==0){
                return new ArrayList<Map<String,String>>();
            }else{
                return mapList;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<Map<String,String>>();
    }


    public static List<Map<String,String>> printBookmarks(PDOutlineNode bookmark, String indentation,String pid,List<Map<String,String>> catalist) throws IOException{
       if(ObjectUtils.isEmpty(bookmark)){
           return catalist;
       }
        PDOutlineItem current = bookmark.getFirstChild();
//        List<Map<String,String>> thiscatalist = new ArrayList<Map<String,String>>();
        Map<String,String> map = null;
        while(current != null){
            int pages = 0;
            if(current.getDestination() instanceof PDPageDestination){
                PDPageDestination pd = (PDPageDestination) current.getDestination();
                pages = pd.retrievePageNumber() + 1;
            }
            if (current.getAction()  instanceof PDActionGoTo) {
                PDActionGoTo gta = (PDActionGoTo) current.getAction();
                if (gta.getDestination() instanceof PDPageDestination) {
                    PDPageDestination pd = (PDPageDestination) gta.getDestination();
                    pages = pd.retrievePageNumber() + 1;
                }
            }
            String id = UUID.randomUUID().toString();
            if (pages == 0) {
                System.out.println(indentation+current.getTitle());
            }else{
//                System.out.println(indentation.length()+"===================================");
                System.out.println(indentation+current.getTitle()+"  "+pages);
                map = new HashMap<String,String>();
                String thispid =null;
                if(!StringUtils.isEmpty(pid)){
//                map.put("page",String.valueOf(pages));
                    thispid = pid;
                    map.put("path","/"+thispid+"/"+id+"/");
                }else{
                    map.put("path","/"+id+"/");
                }

                map.put("id",id);
                map.put("title",indentation+current.getTitle());
                map.put("page",String.valueOf(pages));
                map.put("pid",thispid);

                catalist.add(map);
            }

//            System.out.println("current=="+current);
//            System.out.println("indentation=="+indentation);
            printBookmarks( current, indentation + "    ",id ,catalist);
            current = current.getNextSibling();
        }
        return catalist;
    }
    public static void main(String[] args) throws IOException {
        List<Map<String,String>>  catalist =  getCatalog("D:/f/file/军网百科-软件使用说明书.pdf");
        System.out.println(catalist);
//        File file = new File("D:/f/file/军网百科-软件使用说明书.pdf");
//        PDDocument doc = null;
//        FileInputStream  fis = null;
//        try {
//            fis = new FileInputStream(file);
//            PDFParser parser = new PDFParser(new RandomAccessBuffer(fis));
//            parser.parse();
//            doc = parser.getPDDocument();
//            PDDocumentOutline outline = doc.getDocumentCatalog().getDocumentOutline();
//            System.out.println(outline.toString()+"--");
//            System.out.println(printBookmarks(outline,"",null,new ArrayList<Map<String,String>>()));
////            Pdf pdf = new Pdf();
////            if (outline != null) {
////                pdf.printBookmarks(outline, "");
////            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }
}

