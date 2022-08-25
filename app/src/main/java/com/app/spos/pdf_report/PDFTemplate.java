package com.app.spos.pdf_report;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PDFTemplate {

    private Context context;
    private File pdfFile;
    private Document document;
    PdfWriter pdfWriter;
    private Paragraph paragraph;
    //here you can change fonts,fonts size and fonts color

    public static final String FONT = "resources/font/nazli.ttf";
    public static final String ARABIC = "\u0627\u0644\u0633\u0639\u0631 \u0627\u0644\u0627\u062c\u0645\u0627\u0644\u064a";
  //  Font f = FontFactory.getFont("assets/nazli.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    Font f = FontFactory.getFont("assets/Janna LT Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    BaseFont bf;

    {
        try {
            bf = BaseFont.createFont("assets/NotoNaskhArabic-Regular.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Font trebuchetSmaller = new Font(bf, 10, 0);
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
    private Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK);
    private Font fRowText = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK);

    Font font = FontFactory.getFont("Simplified Arabic", BaseFont.IDENTITY_H, true, 22, Font.BOLD);



//     private Font forArabic;
//    BaseFont bfBold;
//
//    {
//        try {
//            bfBold = BaseFont.createFont("assets/nazli.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//            forArabic = new Font(bfBold,8);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public PDFTemplate(Context context) {
        this.context = context;
    }


    public void openDocument(boolean ifCustomerReceive) {
        if(ifCustomerReceive){
            createFile(true);
        }else{
            createFile(false);
        }

        try {

            //adjust your page size here
            Rectangle pageSize = new Rectangle(300.41f, 500.41f); //14400 //for 58 mm pos printer
            document = new Document(pageSize);

            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        } catch (Exception e) {
            Log.e("createFile", e.toString());
        }
    }

    private void createFile(boolean value) {

        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "PDF");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "PDF");
        }
       // File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");
        if (!dir.exists())
            dir.mkdir();

        //your file name
        if(value) {
            pdfFile = new File(dir, "order_receipt_receive.pdf");

        }else{

            if (dir.exists() && dir.isDirectory()) {
                dir.delete();
            }
                pdfFile = new File(dir, "order_receipt.pdf");
        }
    }
    private void deleteTempFolder(String dir) {
        File myDir = new File(Environment.getExternalStorageDirectory() + "/"+dir);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }
    public void closeDocument() {
        document.close();
    }

    public void addMetaData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);

    }

    public void addTitle(String title, String subTitle, String date) {


        try {


            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fTitle));
            addChildP(new Paragraph(subTitle, fSubTitle));
            addChildP(new Paragraph( date, fHighText));
          //  addChildP(new Paragraph( new Phrase(servedBy,trebuchetSmaller)));

            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addTitle", e.toString());
        }
    }

    public void addChildP(Paragraph childParagraph) {

        childParagraph.setAlignment(Element.ALIGN_CENTER);

        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) {

        try {

            paragraph = new Paragraph(text, fText);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingBefore(1);
            paragraph.setSpacingAfter(1);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("data", e.toString());
        }


    }


    public void addRightParagraph(String text) {

        try {

            paragraph = new Paragraph(text+"\n", fText);


            paragraph.setAlignment(Element.ALIGN_CENTER);

            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }


    }


    public void addImage(Bitmap bm) {

        try {

            Bitmap bmp = bm;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);

            byte[] byteArray = stream.toByteArray();
            // PdfImage img = new PdfImage(arg0, arg1, arg2)

            // Converting byte array into image Image
            Image img = Image.getInstance(byteArray);

            img.setAlignment(Image.ALIGN_BOTTOM);
            img.setAlignment(Image.ALIGN_CENTER);
            img.scaleAbsolute(80f, 80f);
            //img.setAbsolutePosition(imageStartX, imageStartY); // Adding Image

            document.add(img);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }


    }
    public void addImageLogo(Bitmap bm) {

        try {

            Bitmap bmp = bm;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);

            byte[] byteArray = stream.toByteArray();
            // PdfImage img = new PdfImage(arg0, arg1, arg2)

            // Converting byte array into image Image
            Image img = Image.getInstance(byteArray);

            img.setAlignment(Image.ALIGN_TOP);
            img.setAlignment(Image.ALIGN_CENTER);
            img.scaleAbsolute(120f, 80f);
            //img.setAbsolutePosition(imageStartX, imageStartY); // Adding Image

            document.add(img);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }


    }

    public void createTable(String[] header, ArrayList<String[]> clients) {

        try {


            paragraph = new Paragraph();
            paragraph.setFont(fText);

            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(1);
          //  pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

          //  paragraph.setAlignment(PdfPCell.ALIGN_LEFT);
            PdfPCell pdfPCell;

            int indexC = 0;
            while (indexC < header.length) {
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubTitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBorderColor(BaseColor.GRAY);
                pdfPTable.addCell(pdfPCell);
            }

            for (int indexR = 0; indexR < clients.size(); indexR++) {
                String[] row = clients.get(indexR);

                for (indexC = 0; indexC < header.length; indexC++) {
                    pdfPCell = new PdfPCell(new Phrase(row[indexC], fRowText));
                  //  pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                  //  pdfPCell.setBorder(Rectangle.NO_BORDER);
                    pdfPCell.setArabicOptions(ArabicLigaturizer.ar_composedtashkeel);
                    pdfPCell.setBorderColor(BaseColor.GRAY);
                  //  pdfPCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                    pdfPTable.addCell(pdfPCell);
                }
            }


            paragraph.add(pdfPTable);
            document.add(paragraph);

        } catch (Exception e) {
            Log.e("createTable", e.toString());
        }
    }

    public void viewPDF() {
        Intent intent = new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }



}
