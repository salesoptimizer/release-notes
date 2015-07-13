package rnservices;

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
            Phrase phrase1 = new Phrase("Ticket name", FontFactory.getFont("MS Mincho", 10, Font.BOLD));
            Paragraph p1 = new Paragraph(phrase1);
            p1.setAlignment(Element.ALIGN_CENTER);
            PdfPCell cell1 = new PdfPCell(p1);
            table.addCell(cell1);
            table.addCell("Ticket release notes");
            table.addCell("Fix.version");
            table.completeRow();
            
            Iterator<ReleaseNote> iterator = releaseNotes.iterator();
            ReleaseNote rnote;
            while (iterator.hasNext()) {
            	rnote = iterator.next();
            	table.addCell(rnote.getTicketName());
            	table.addCell(rnote.getReleaseNotes());
            	table.addCell(rnote.getPackVersion());
            	/*PdfPCell c = new PdfPCell(new Phrase(rnote.getTicketName()));
	            table.addCell(c);
	            c = new PdfPCell(new Phrase(rnote.getReleaseNotes()));
	            table.addCell(c);           
	            c = new PdfPCell(new Phrase(rnote.getPackVersion()));
	            table.addCell(c);*/           
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
