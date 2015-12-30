import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;


public class Docx4jTest {

	public static void main(String[] args) throws Docx4JException {
//		    	String xhtml= "<div>" +
//				"<h1>Heading</h1>" +
//				"<table style='border:solid 1px white;'><tr><th>1</th></tr></table>" +
//			  "</div>";    	
		
		//String xhtml = "<div><p>Hello here we <span style='background-color:red;'> were </span> and are now </p></div>";
		
		//String xhtml = "<table><tr><td>1</td></tr></table>";
		
		String xhtml = "<ul><li>test, test, test</li><li>test, test, test</li><li>test, test, test</li><li>test, test, test</li></ul>";
		
		/*String PNG_IMAGE_DATA = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACAgMAAAAP2OW3AAAADFBMVEUDAP//AAAA/wb//AAD4Tw1AAAACXBIWXMAAAsTAAALEwEAmpwYAAAADElEQVQI12NwYNgAAAF0APHJnpmVAAAAAElFTkSuQmCC";		
		String xhtml= "<div align=\"center\">" +
		//	"<p><img src='" + PNG_IMAGE_DATA + "'  /></p>" +
			"<img src='" + PNG_IMAGE_DATA + "'  />" +
		"</div>";    */	
		
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		//WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(System.getProperty("user.dir") + "/Hello.docx"));
		
		//// Setup white list
		//Set<String> cssWhiteList = new HashSet<String>();
		//List lines = FileUtils.readLines(new File(System.getProperty("user.dir") + "/src/main/resources/CSS-WhiteList.txt"));
		//// TODO catch exception
		//for (Object o : lines) {
		//String line = ((String)o).trim();
		//if (line.length()>0 && !line.startsWith("#")) {
		//cssWhiteList.add(line);
		//}
		//}
		//XHTMLImporter.setCssWhiteList(cssWhiteList);
		
		XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
		
		wordMLPackage.getMainDocumentPart().getContent().addAll(
		XHTMLImporter.convert( xhtml, null) );
		String str = wordMLPackage.getMainDocumentPart().getXML();
		String result = str.substring(str.indexOf("<w:body>") + "<w:body>".length(), str.indexOf("</w:body>"));
		
		System.out.println(str.substring(str.indexOf("<w:body>") + "<w:body>".length(), str.indexOf("</w:body>")));
		
//		System.out.println(
//		XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true));
//		
//		wordMLPackage.save(new java.io.File("OUT_from_XHTML.docx") );
	}

}
