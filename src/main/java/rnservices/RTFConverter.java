package rnservices;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.rtf.RtfWriter2;

import models.ReleaseNote;

//	 ***************************************************************************************************************************************************
public class RTFConverter {
	private static Logger log = LogManager.getLogManager().getLogger("rnotes");
	
	public static boolean convertToRTF(List<ReleaseNote> releaseNotes, File logo, boolean isGoogleDoc) {
		Document document = new Document(PageSize.A4);
        try {
        	RtfWriter2.getInstance(document, new FileOutputStream("ReleaseNotes.rtf"));
            document.open();
            if (logo != null) {
            	writeLogoImage(document, logo);
            }
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            if (isGoogleDoc) {
            	table.setTotalWidth(new float[] {20f, 10f, 70f});
            } else {
            	table.setTotalWidth(new float[] {20f, 20f, 100f});
            }
            writeTableHeaderRow(table);
            writeReleaseNoteRow(table, releaseNotes);
            document.add(table);
           
        } catch (FileNotFoundException e) {
        	log.severe(e.getMessage());
            return false;
        } catch (DocumentException e) {
        	log.severe(e.getMessage());
            return false;
        }
        document.close();
		return true;
	}
	
	private static void addBoldText(PdfPTable table, String text) throws DocumentException {
		FontFactory.register("arial.ttf");
		FontFactory.register("arialbd.ttf");
        Phrase phrase = new Phrase("    " + text, FontFactory.getFont("Arial-Bold", 12, Font.BOLD));
        Paragraph paragraph = new Paragraph(phrase);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setPaddingLeft(10.0f);
        cell.setPaddingRight(20.0f);
        cell.setPaddingTop(20.0f);
        cell.setPaddingBottom(50.0f);
        cell.setBorderColor(Color.BLUE);
        table.addCell(cell);
	}
	
	private static com.lowagie.text.List getReleaseNotesList(String rnContent) {
		com.lowagie.text.List resultList = new com.lowagie.text.List();
		resultList.setSymbolIndent(10.0f);
		resultList.setIndentationLeft(20.0f);
		resultList.setListSymbol("\u2022");
		
		String[] lines = rnContent.split("\r");
		for (String line: lines) {
			line = line.trim() + ";";
			ListItem item = new ListItem(line, FontFactory.getFont("Arial", 11, Font.NORMAL));
			resultList.add(item);
		}
		
		return resultList;
	}
	
	private static void writeLogoImage(Document document, File logo) throws DocumentException {
		try {
			Image img = Image.getInstance(logo.getPath());
			img.scaleToFit(703, 119);
            img.setAlignment(img.ALIGN_CENTER);
            document.add(img);
		} catch (MalformedURLException e) {
			log.severe(e.getMessage());
		} catch (IOException e) {
			log.severe(e.getMessage());
		}
	}
	
	private static void writeTableHeaderRow(PdfPTable table) throws DocumentException {
		addBoldText(table, "Date");
        addBoldText(table, "Version");
        addBoldText(table, "Release Notes");
        table.completeRow();
	}
	
	private static void writeReleaseNoteRow(PdfPTable table, List<ReleaseNote> releaseNotes) throws DocumentException {
		if (releaseNotes != null) {
            Iterator<ReleaseNote> iterator = releaseNotes.iterator();
            ReleaseNote rnote;
            while (iterator.hasNext()) {
            	rnote = iterator.next();
            	
//            	set cell format: paddings, border color	*******************************************************************************************
            	PdfPCell cell = new PdfPCell();
            	cell.setBorderColor(Color.BLUE);
            	
//            	add cells content	***************************************************************************************************************
            	cell.setPhrase(new Phrase("    " + rnote.getTicketDate(), FontFactory.getFont("Arial", 11, Font.NORMAL)));
            	cell.setBorderColor(Color.BLUE);
            	table.addCell(cell);
            	
            	cell.setPhrase(new Phrase("    " + rnote.getPackVersion(), FontFactory.getFont("Arial", 11, Font.NORMAL)));
            	cell.setBorderColor(Color.BLUE);
            	table.addCell(cell);
            	
//            	add list of release notes to the last cell	***************************************************************************************
            	cell.setPhrase(new Phrase(""));
            	cell.addElement(getReleaseNotesList(rnote.getReleaseNotes()));
//            	cell.setPhrase(new Phrase(convertToRTF(rnote.getReleaseNotes())));
            	cell.setBorderColor(Color.BLUE);
            	table.addCell(cell);
    
	            table.completeRow();
            }
        }
	}
	
	private static String convertToRTF(String htmlStr) {
	    OutputStream os = new ByteArrayOutputStream();
	    HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
	    RTFEditorKit rtfEditorKit = new RTFEditorKit();
	    String rtfStr = null;

	    htmlStr = htmlStr.replaceAll("<br.*?>","#NEW_LINE#");
	    htmlStr = htmlStr.replaceAll("</p>","#NEW_LINE#");
	    htmlStr = htmlStr.replaceAll("<p.*?>","");
	    InputStream is = new ByteArrayInputStream(htmlStr.getBytes());
	    try {
	        javax.swing.text.Document doc = htmlEditorKit.createDefaultDocument();
	        htmlEditorKit.read(is, doc, 0);
	        rtfEditorKit .write(os, doc, 0, doc.getLength());
	        rtfStr = os.toString();
	        rtfStr = rtfStr.replaceAll("#NEW_LINE#","\\\\par ");
	    } catch (IOException e) {
	          e.printStackTrace();
	        } catch (BadLocationException e) {
	          e.printStackTrace();
	        }
	    return rtfStr.toString();
	}
	
}