package rnservices;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.HtmlParser;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.rtf.RtfWriter2;

import models.ReleaseNote;

public class RTFConverter {
//	private List<ReleaseNote> releaseNotes;
	
	public static boolean convertToRTF(List<ReleaseNote> releaseNotes) {
		Document document = new Document();
        try {
            RtfWriter2 writer = RtfWriter2.getInstance(document, new FileOutputStream(
                    "ReleaseNotes.rtf"));
            document.open();
          
            PdfPTable t1 = new PdfPTable(3);
            t1.setTotalWidth(new float[] {20.0f, 70.0f, 30.0f});
            
            PdfPCell c = new PdfPCell(new Phrase("Ticket name"));
            t1.addCell(c);
            c = new PdfPCell(new Phrase("Ticket release notes"));
            t1.addCell(c);           
            c = new PdfPCell(new Phrase("Fix.version"));
            t1.addCell(c);           
            t1.completeRow();
            
            HtmlParser hp = new HtmlParser();
            
            Iterator<ReleaseNote> iterator = releaseNotes.iterator();
            ReleaseNote rnote;
            while (iterator.hasNext()) {
            	rnote = iterator.next();
	            c = new PdfPCell(new Phrase(rnote.getTicketName()));
	            c.setHorizontalAlignment(Element.ALIGN_MIDDLE);
	            t1.addCell(c);
	            c = new PdfPCell(new Phrase(rnote.getReleaseNotes()));
	            t1.addCell(c);           
	            c = new PdfPCell(new Phrase(rnote.getPackVersion()));
	            t1.addCell(c);           
	            t1.completeRow();
            }
           
            document.add(t1);
           
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
