import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.rtf.RtfWriter2;


public class ITextTest {

	public static void main(String[] args) {
		 Document document = new Document();
	        try {
	            RtfWriter2 writer = RtfWriter2.getInstance(document, new FileOutputStream(
	                    "RTFExamplePdfPTable1.rtf"));
	            document.open();
	          
	            PdfPTable t1 = new PdfPTable(3);
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
	            t1.completeRow();
	           
	            document.add(t1);
	           
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (DocumentException e) {
	            e.printStackTrace();
	        }
	        document.close();
	}

}
