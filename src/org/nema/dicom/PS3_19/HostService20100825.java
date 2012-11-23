
package org.nema.dicom.PS3_19;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "HostService-20100825", targetNamespace = "http://dicom.nema.org/PS3.19/HostService-20100825")
public class HostService20100825
    extends Service
{

    private final static URL HOSTSERVICE20100825_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.nema.dicom.PS3_19.HostService20100825 .class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = org.nema.dicom.PS3_19.HostService20100825 .class.getResource(".");
            url = new URL(baseUrl, "file:/C:/Users/ltarbo01/Documents/DICOM/WG23/wsdl_ft/HostService-20100825.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'file:/C:/Users/ltarbo01/Documents/DICOM/WG23/wsdl_ft/HostService-20100825.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        HOSTSERVICE20100825_WSDL_LOCATION = url;
    }

    public HostService20100825(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public HostService20100825() {
        super(HOSTSERVICE20100825_WSDL_LOCATION, new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "HostService-20100825"));
    }

    /**
     * 
     * @return
     *     returns IHostService20100825
     */
    @WebEndpoint(name = "HostServiceBinding")
    public IHostService20100825 getHostServiceBinding() {
        return super.getPort(new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "HostServiceBinding"), IHostService20100825.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IHostService20100825
     */
    @WebEndpoint(name = "HostServiceBinding")
    public IHostService20100825 getHostServiceBinding(WebServiceFeature... features) {
        return super.getPort(new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "HostServiceBinding"), IHostService20100825.class, features);
    }

}
