package rnservices;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.HtmlParser;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

import models.ReleaseNote;

public class RTFConverter {
	
	public static boolean convertToRTF(List<ReleaseNote> releaseNotes) {
		Document document = new Document(PageSize.A4);
        try {
        	RtfWriter2 writer = RtfWriter2.getInstance(document, new FileOutputStream("ReleaseNotes.rtf"));
            document.open();
          
            PdfPTable table = new PdfPTable(3);
            addBoldText(table, "Date");
            addBoldText(table, "Version");
            addBoldText(table, "Release Notes");
            table.completeRow();
            
            if (releaseNotes != null) {
	            Iterator<ReleaseNote> iterator = releaseNotes.iterator();
	            ReleaseNote rnote;
	            while (iterator.hasNext()) {
	            	com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED, com.lowagie.text.List.MARKED);
		            list.setPreSymbol("\u2022");
		            list.setPostSymbol(";");
		            list.setLowercase(com.lowagie.text.List.LOWERCASE);
		            list.add("note1");
	            	list.add("note2");
	            	list.add("note3");
		            
	            	rnote = iterator.next();
	            	table.addCell("    " + rnote.getTicketDate());
	            	table.addCell("    " + rnote.getPackVersion());
//	            	table.addCell("    " + rnote.getReleaseNotes());
	            	table.addCell("    " + list.toString());
		            table.completeRow();
	            }
            }
           
            document.add(table);
           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
        document.close();
		return true;
	}
	
	private static void addBoldText(PdfPTable table, String text) throws DocumentException {
		table.setTotalWidth(new float[] {20.0f, 20.0f, 80.0f});
        // first movie
        Phrase phrase = new Phrase("    " + text, FontFactory.getFont("Arial", 12, Font.BOLD));
        Paragraph paragraph = new Paragraph(phrase);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setPaddingLeft(10.0f);
        cell.setPaddingRight(20.0f);
        cell.setPaddingTop(20.0f);
        cell.setPaddingBottom(50.0f);
        cell.getBorderColor();
        table.addCell(cell);
	}
	
}
