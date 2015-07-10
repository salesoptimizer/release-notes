import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;


public class ITextTest {

	public static void main(String[] args) {
		Document document = new Document(PageSize.A4);
	        try {
	            PdfWriter.getInstance(document, new FileOutputStream("PDFExamplePdfPTable1.pdf"));
	            document.open();
	          
	            PdfPTable table = new PdfPTable(2);
	            table.setWidthPercentage(100);
	            // first movie
	            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
	            table.addCell("X-Men");
	            
	            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
	            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
	            table.addCell("X-Men");
	            
	            table.completeRow();
	            
	           /* PdfPTable t1 = new PdfPTable(3);
	            t1.setTotalWidth(new float[] {20.0f, 50.0f, 30.0f});
	            PdfPCell c = new PdfPCell(new Phrase("Entry T1.R1.C1"));
	            t1.addCell(c);
	            c = new PdfPCell(new Phrase("Entry T1.R2.C2"));
	            t1.addCell(c);           
	            c = new PdfPCell(new Phrase("Entry T1.R2.C3"));
	            t1.addCell(c);           
	            t1.completeRow();
	            
	            c = new PdfPCell(new Phrase("Entry T1.R1.C1"));
	            t1.addCell(c);
	            c = new PdfPCell(new Phrase(""));
	            t1.addCell(c);
	            c = new PdfPCell(new Phrase("Entry T1.R2.C2"));
	            t1.addCell(c);            
	            t1.completeRow();*/
	           
	            document.add(table);
	           
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (DocumentException e) {
	            e.printStackTrace();
	        }
	        document.close();
	}

}
