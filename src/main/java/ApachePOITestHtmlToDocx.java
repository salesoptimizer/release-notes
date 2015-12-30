import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;


public class ApachePOITestHtmlToDocx {

	public static void main(String[] args) throws IOException {
		//Blank Document
		XWPFDocument document= new XWPFDocument(); 
		//Write the Document in file system
		FileOutputStream out = new FileOutputStream(
		new File("Test_docx.docx"));
		String content = "";
		/*try {
			content = convertHtmlToDocx("<ul><li>test, test, test</li><li>test, test, test</li><li>test, test, test</li><li>test, test, test</li></ul><br><br><br><b>bold</b>");
		} catch (Docx4JException e) {
			e.printStackTrace();
		}*/
		//create Paragraph
		   XWPFParagraph paragraph = document.createParagraph();
		   XWPFRun run=paragraph.createRun();
		   run.setText("<ul><li>test, test, test</li><li>test, test, test</li><li>test, test, test</li><li>test, test, test</li></ul><br><br><br><b>bold</b>");
		document.write(out);
		out.close();
		System.out.println("win");
	}
	
	private static String convertHtmlToDocx(String htmlContent) throws Docx4JException {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
		wordMLPackage.getMainDocumentPart().getContent().addAll(
		XHTMLImporter.convert(htmlContent, null) );
		String str = wordMLPackage.getMainDocumentPart().getXML();
		String result = str.substring(str.indexOf("<w:body>") + "<w:body>".length(), str.indexOf("</w:body>"));
		return result;
	}
	

}
