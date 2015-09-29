import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import models.ReleaseNote;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;


public class ITextTest {

	public static void main(String[] args) throws MalformedURLException, IOException {
		Document document = new Document(PageSize.A4);
        try {
        	RtfWriter2 writer = RtfWriter2.getInstance(document, new FileOutputStream("ReleaseNotes.rtf"));
            document.open();
            
            File file = new File("c:/downloads/logo.png");
            
            Image img = Image.getInstance(file.getPath());
            img.scaleToFit(703, 119);
            img.setAlignment(img.ALIGN_CENTER);
            document.add(img);
            
            PdfPTable table = new PdfPTable(3);
            addBoldText(table, "Date");
            addBoldText(table, "Version");
            addBoldText(table, "Release Notes");
            table.completeRow();
            
	            	table.addCell("    22.09");
	            	table.addCell("    1.1");
	            	/*StringBuilder rNotesCellText = new StringBuilder();
	            	rNotesCellText.append("    ").append("\u2022").append(" ").append("note1;\n");
	            	rNotesCellText.append("    ").append("\u2022").append(" ").append("note2;\n");
	            	rNotesCellText.append("    ").append("\u2022").append(" ").append("note3;\n");*/
//	            	table.addCell("    " + rNotesCellText.toString());
	            	List list = new List(false, 10);
	            	list.setSymbolIndent(10.0f);
	            	list.setIndentationLeft(20.0f);
	            	list.setListSymbol("\u2022");
	            	list.add("arg1");
	            	list.add("arg2");
	            	list.add("arg3");
	            	PdfPCell cell1 = new PdfPCell();
	            	cell1.setPaddingLeft(20.0f);
	            	cell1.addElement(list);
	            	cell1.setPaddingLeft(20.0f);
	            	table.addCell(cell1);
		            table.completeRow();
            document.add(table);
           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
	}
	
	private static void addBoldText(PdfPTable table, String text) throws DocumentException {
		table.setWidthPercentage(100);
		table.setTotalWidth(new float[] {20f, 20f, 100f});
        // first movie
		FontFactory.register("arial.ttf");
		FontFactory.register("arialbd.ttf");
        Phrase phrase = new Phrase("    " + text, FontFactory.getFont("Arial", 12, Font.BOLD));
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

}
