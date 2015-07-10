package rnservices;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.HtmlParser;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

import models.ReleaseNote;

public class RTFConverter {
//	private List<ReleaseNote> releaseNotes;
	
	public static boolean convertToRTF(List<ReleaseNote> releaseNotes) {
		Document document = new Document(PageSize.A4);
        try {
            RtfWriter2 writer = RtfWriter2.getInstance(document, new FileOutputStream("ReleaseNotes.rtf"));
//        	PdfWriter.getInstance(document, new FileOutputStream("ReleaseNotes.pdf"));
            document.open();
          
            PdfPTable table = new PdfPTable(3);
            table.setTotalWidth(new float[] {20.0f, 70.0f, 30.0f});
            // first movie
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell("Ticket name");
            table.addCell("Ticket release notes");
            table.addCell("Fix.version");
            table.completeRow();
            
            /*PdfPTable t1 = new PdfPTable(3);
//            t1.setTotalWidth(new float[] {20.0f, 50.0f, 30.0f});
            
            PdfPCell c = new PdfPCell(new Phrase("Ticket name"));
            
            t1.addCell(c);
            c = new PdfPCell(new Phrase("Ticket release notes"));
            t1.addCell(c);           
            c = new PdfPCell(new Phrase("Fix.version"));
            t1.addCell(c);           
            t1.completeRow();
            
            HtmlParser hp = new HtmlParser();*/
            
            Iterator<ReleaseNote> iterator = releaseNotes.iterator();
            ReleaseNote rnote;
            while (iterator.hasNext()) {
            	rnote = iterator.next();
            	PdfPCell c = new PdfPCell(new Phrase(rnote.getTicketName()));
	            table.addCell(c);
	            c = new PdfPCell(new Phrase(rnote.getReleaseNotes()));
	            table.addCell(c);           
	            c = new PdfPCell(new Phrase(rnote.getPackVersion()));
	            table.addCell(c);           
	            table.completeRow();
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
	
}
