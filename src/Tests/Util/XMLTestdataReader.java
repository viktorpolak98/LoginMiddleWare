package Tests.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLTestdataReader {
    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static List<User> readUsers(String filename){
        List<User> users = new ArrayList<>();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder docBuilder = dbf.newDocumentBuilder();

            Document doc = docBuilder.parse(new File(filename));

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("user");

            for (int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);

                if(node.getNodeType() != Node.ELEMENT_NODE){
                    continue;
                }

                Element element = (Element) node;
                String username = element.getElementsByTagName("username").item(0).getTextContent();
                String password = element.getElementsByTagName("password").item(0).getTextContent();

                users.add(new User(username, password));
            }

        }catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return users;
    }
}
