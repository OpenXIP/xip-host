
package org.nema.dicom.PS3_19;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.nema.dicom.PS3_19 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactoryHost {

    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _State_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "State");
    private final static QName _ArrayOfSeries_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfSeries");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Status_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "Status");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _XPathNodeType_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.Xml.XPath", "XPathNodeType");
    private final static QName _ArrayOfUUID_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfUUID");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _Patient_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "Patient");
    private final static QName _Rectangle_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "Rectangle");
    private final static QName _MimeType_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "MimeType");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _QueryResult_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "QueryResult");
    private final static QName _ArrayOfMimeType_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfMimeType");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _ArrayOfQueryResult_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfQueryResult");
    private final static QName _ObjectDescriptor_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ObjectDescriptor");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _XPathNodeInfoSet_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "XPathNodeInfoSet");
    private final static QName _Study_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "Study");
    private final static QName _ModelSetDescriptor_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ModelSetDescriptor");
    private final static QName _ArrayOfObjectLocator_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfObjectLocator");
    private final static QName _ArrayOfStudy_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfStudy");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _ArrayOfXPathNodeInfoSet_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfXPathNodeInfoSet");
    private final static QName _AvailableData_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "AvailableData");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _ArrayOfUID_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfUID");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _XPathNode_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "XPathNode");
    private final static QName _UID_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "UID");
    private final static QName _Modality_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "Modality");
    private final static QName _Series_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "Series");
    private final static QName _ArrayOfQueryResultInfoSet_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfQueryResultInfoSet");
    private final static QName _StatusType_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "StatusType");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _ArrayOfPatient_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfPatient");
    private final static QName _QueryResultInfoSet_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "QueryResultInfoSet");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _ArrayOfstring_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfstring");
    private final static QName _UUID_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "UUID");
    private final static QName _ArrayOfXPathNode_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfXPathNode");
    private final static QName _ObjectLocator_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ObjectLocator");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _ArrayOfObjectDescriptor_QNAME = new QName("http://dicom.nema.org/PS3.19/HostService-20100825", "ArrayOfObjectDescriptor");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.nema.dicom.PS3_19
     * 
     */
    public ObjectFactoryHost() {
    }

    /**
     * Create an instance of {@link AvailableData }
     * 
     */
    public AvailableData createAvailableData() {
        return new AvailableData();
    }

    /**
     * Create an instance of {@link GetAvailableScreen }
     * 
     */
    public GetAvailableScreen createGetAvailableScreen() {
        return new GetAvailableScreen();
    }

    /**
     * Create an instance of {@link GetData }
     * 
     */
    public GetData createGetData() {
        return new GetData();
    }

    /**
     * Create an instance of {@link ReleaseDataResponse }
     * 
     */
    public ReleaseDataResponse createReleaseDataResponse() {
        return new ReleaseDataResponse();
    }

    /**
     * Create an instance of {@link ArrayOfUID }
     * 
     */
    public ArrayOfUID createArrayOfUID() {
        return new ArrayOfUID();
    }

    /**
     * Create an instance of {@link GetOutputLocation }
     * 
     */
    public GetOutputLocation createGetOutputLocation() {
        return new GetOutputLocation();
    }

    /**
     * Create an instance of {@link Status }
     * 
     */
    public Status createStatus() {
        return new Status();
    }

    /**
     * Create an instance of {@link NotifyDataAvailable }
     * 
     */
    public NotifyDataAvailable createNotifyDataAvailable() {
        return new NotifyDataAvailable();
    }

    /**
     * Create an instance of {@link XPathNodeInfoSet }
     * 
     */
    public XPathNodeInfoSet createXPathNodeInfoSet() {
        return new XPathNodeInfoSet();
    }

    /**
     * Create an instance of {@link GetAvailableScreenResponse }
     * 
     */
    public GetAvailableScreenResponse createGetAvailableScreenResponse() {
        return new GetAvailableScreenResponse();
    }

    /**
     * Create an instance of {@link NotifyStatusResponse }
     * 
     */
    public NotifyStatusResponse createNotifyStatusResponse() {
        return new NotifyStatusResponse();
    }

    /**
     * Create an instance of {@link ArrayOfUUID }
     * 
     */
    public ArrayOfUUID createArrayOfUUID() {
        return new ArrayOfUUID();
    }

    /**
     * Create an instance of {@link ArrayOfQueryResultInfoSet }
     * 
     */
    public ArrayOfQueryResultInfoSet createArrayOfQueryResultInfoSet() {
        return new ArrayOfQueryResultInfoSet();
    }

    /**
     * Create an instance of {@link QueryResultInfoSet }
     * 
     */
    public QueryResultInfoSet createQueryResultInfoSet() {
        return new QueryResultInfoSet();
    }

    /**
     * Create an instance of {@link ArrayOfSeries }
     * 
     */
    public ArrayOfSeries createArrayOfSeries() {
        return new ArrayOfSeries();
    }

    /**
     * Create an instance of {@link Study }
     * 
     */
    public Study createStudy() {
        return new Study();
    }

    /**
     * Create an instance of {@link ObjectDescriptor }
     * 
     */
    public ObjectDescriptor createObjectDescriptor() {
        return new ObjectDescriptor();
    }

    /**
     * Create an instance of {@link Rectangle }
     * 
     */
    public Rectangle createRectangle() {
        return new Rectangle();
    }

    /**
     * Create an instance of {@link XPathNode }
     * 
     */
    public XPathNode createXPathNode() {
        return new XPathNode();
    }

    /**
     * Create an instance of {@link ArrayOfQueryResult }
     * 
     */
    public ArrayOfQueryResult createArrayOfQueryResult() {
        return new ArrayOfQueryResult();
    }

    /**
     * Create an instance of {@link GetOutputLocationResponse }
     * 
     */
    public GetOutputLocationResponse createGetOutputLocationResponse() {
        return new GetOutputLocationResponse();
    }

    /**
     * Create an instance of {@link QueryInfoSetResponse }
     * 
     */
    public QueryInfoSetResponse createQueryInfoSetResponse() {
        return new QueryInfoSetResponse();
    }

    /**
     * Create an instance of {@link GetDataResponse }
     * 
     */
    public GetDataResponse createGetDataResponse() {
        return new GetDataResponse();
    }

    /**
     * Create an instance of {@link ArrayOfXPathNode }
     * 
     */
    public ArrayOfXPathNode createArrayOfXPathNode() {
        return new ArrayOfXPathNode();
    }

    /**
     * Create an instance of {@link ReleaseModels }
     * 
     */
    public ReleaseModels createReleaseModels() {
        return new ReleaseModels();
    }

    /**
     * Create an instance of {@link ArrayOfPatient }
     * 
     */
    public ArrayOfPatient createArrayOfPatient() {
        return new ArrayOfPatient();
    }

    /**
     * Create an instance of {@link QueryModelResponse }
     * 
     */
    public QueryModelResponse createQueryModelResponse() {
        return new QueryModelResponse();
    }

    /**
     * Create an instance of {@link ArrayOfObjectDescriptor }
     * 
     */
    public ArrayOfObjectDescriptor createArrayOfObjectDescriptor() {
        return new ArrayOfObjectDescriptor();
    }

    /**
     * Create an instance of {@link ReleaseData }
     * 
     */
    public ReleaseData createReleaseData() {
        return new ReleaseData();
    }

    /**
     * Create an instance of {@link NotifyStateChanged }
     * 
     */
    public NotifyStateChanged createNotifyStateChanged() {
        return new NotifyStateChanged();
    }

    /**
     * Create an instance of {@link ArrayOfXPathNodeInfoSet }
     * 
     */
    public ArrayOfXPathNodeInfoSet createArrayOfXPathNodeInfoSet() {
        return new ArrayOfXPathNodeInfoSet();
    }

    /**
     * Create an instance of {@link ArrayOfObjectLocator }
     * 
     */
    public ArrayOfObjectLocator createArrayOfObjectLocator() {
        return new ArrayOfObjectLocator();
    }

    /**
     * Create an instance of {@link Series }
     * 
     */
    public Series createSeries() {
        return new Series();
    }

    /**
     * Create an instance of {@link ArrayOfMimeType }
     * 
     */
    public ArrayOfMimeType createArrayOfMimeType() {
        return new ArrayOfMimeType();
    }

    /**
     * Create an instance of {@link ArrayOfStudy }
     * 
     */
    public ArrayOfStudy createArrayOfStudy() {
        return new ArrayOfStudy();
    }

    /**
     * Create an instance of {@link ModelSetDescriptor }
     * 
     */
    public ModelSetDescriptor createModelSetDescriptor() {
        return new ModelSetDescriptor();
    }

    /**
     * Create an instance of {@link GenerateUID }
     * 
     */
    public GenerateUID createGenerateUID() {
        return new GenerateUID();
    }

    /**
     * Create an instance of {@link ReleaseModelsResponse }
     * 
     */
    public ReleaseModelsResponse createReleaseModelsResponse() {
        return new ReleaseModelsResponse();
    }

    /**
     * Create an instance of {@link QueryInfoSet }
     * 
     */
    public QueryInfoSet createQueryInfoSet() {
        return new QueryInfoSet();
    }

    /**
     * Create an instance of {@link QueryResult }
     * 
     */
    public QueryResult createQueryResult() {
        return new QueryResult();
    }

    /**
     * Create an instance of {@link ObjectLocator }
     * 
     */
    public ObjectLocator createObjectLocator() {
        return new ObjectLocator();
    }

    /**
     * Create an instance of {@link QueryModel }
     * 
     */
    public QueryModel createQueryModel() {
        return new QueryModel();
    }

    /**
     * Create an instance of {@link UID }
     * 
     */
    public UID createUID() {
        return new UID();
    }

    /**
     * Create an instance of {@link UUID }
     * 
     */
    public UUID createUUID() {
        return new UUID();
    }

    /**
     * Create an instance of {@link NotifyDataAvailableResponse }
     * 
     */
    public NotifyDataAvailableResponse createNotifyDataAvailableResponse() {
        return new NotifyDataAvailableResponse();
    }

    /**
     * Create an instance of {@link Patient }
     * 
     */
    public Patient createPatient() {
        return new Patient();
    }

    /**
     * Create an instance of {@link GetAsModels }
     * 
     */
    public GetAsModels createGetAsModels() {
        return new GetAsModels();
    }

    /**
     * Create an instance of {@link GetAsModelsResponse }
     * 
     */
    public GetAsModelsResponse createGetAsModelsResponse() {
        return new GetAsModelsResponse();
    }

    /**
     * Create an instance of {@link NotifyStatus }
     * 
     */
    public NotifyStatus createNotifyStatus() {
        return new NotifyStatus();
    }

    /**
     * Create an instance of {@link MimeType }
     * 
     */
    public MimeType createMimeType() {
        return new MimeType();
    }

    /**
     * Create an instance of {@link ArrayOfstring }
     * 
     */
    public ArrayOfstring createArrayOfstring() {
        return new ArrayOfstring();
    }

    /**
     * Create an instance of {@link NotifyStateChangedResponse }
     * 
     */
    public NotifyStateChangedResponse createNotifyStateChangedResponse() {
        return new NotifyStateChangedResponse();
    }

    /**
     * Create an instance of {@link GenerateUIDResponse }
     * 
     */
    public GenerateUIDResponse createGenerateUIDResponse() {
        return new GenerateUIDResponse();
    }

    /**
     * Create an instance of {@link Modality }
     * 
     */
    public Modality createModality() {
        return new Modality();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link State }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "State")
    public JAXBElement<State> createState(State value) {
        return new JAXBElement<State>(_State_QNAME, State.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfSeries }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfSeries")
    public JAXBElement<ArrayOfSeries> createArrayOfSeries(ArrayOfSeries value) {
        return new JAXBElement<ArrayOfSeries>(_ArrayOfSeries_QNAME, ArrayOfSeries.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Status }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "Status")
    public JAXBElement<Status> createStatus(Status value) {
        return new JAXBElement<Status>(_Status_QNAME, Status.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XPathNodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.Xml.XPath", name = "XPathNodeType")
    public JAXBElement<XPathNodeType> createXPathNodeType(XPathNodeType value) {
        return new JAXBElement<XPathNodeType>(_XPathNodeType_QNAME, XPathNodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfUUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfUUID")
    public JAXBElement<ArrayOfUUID> createArrayOfUUID(ArrayOfUUID value) {
        return new JAXBElement<ArrayOfUUID>(_ArrayOfUUID_QNAME, ArrayOfUUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Patient }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "Patient")
    public JAXBElement<Patient> createPatient(Patient value) {
        return new JAXBElement<Patient>(_Patient_QNAME, Patient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Rectangle }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "Rectangle")
    public JAXBElement<Rectangle> createRectangle(Rectangle value) {
        return new JAXBElement<Rectangle>(_Rectangle_QNAME, Rectangle.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MimeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "MimeType")
    public JAXBElement<MimeType> createMimeType(MimeType value) {
        return new JAXBElement<MimeType>(_MimeType_QNAME, MimeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "QueryResult")
    public JAXBElement<QueryResult> createQueryResult(QueryResult value) {
        return new JAXBElement<QueryResult>(_QueryResult_QNAME, QueryResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfMimeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfMimeType")
    public JAXBElement<ArrayOfMimeType> createArrayOfMimeType(ArrayOfMimeType value) {
        return new JAXBElement<ArrayOfMimeType>(_ArrayOfMimeType_QNAME, ArrayOfMimeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfQueryResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfQueryResult")
    public JAXBElement<ArrayOfQueryResult> createArrayOfQueryResult(ArrayOfQueryResult value) {
        return new JAXBElement<ArrayOfQueryResult>(_ArrayOfQueryResult_QNAME, ArrayOfQueryResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObjectDescriptor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ObjectDescriptor")
    public JAXBElement<ObjectDescriptor> createObjectDescriptor(ObjectDescriptor value) {
        return new JAXBElement<ObjectDescriptor>(_ObjectDescriptor_QNAME, ObjectDescriptor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XPathNodeInfoSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "XPathNodeInfoSet")
    public JAXBElement<XPathNodeInfoSet> createXPathNodeInfoSet(XPathNodeInfoSet value) {
        return new JAXBElement<XPathNodeInfoSet>(_XPathNodeInfoSet_QNAME, XPathNodeInfoSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Study }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "Study")
    public JAXBElement<Study> createStudy(Study value) {
        return new JAXBElement<Study>(_Study_QNAME, Study.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModelSetDescriptor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ModelSetDescriptor")
    public JAXBElement<ModelSetDescriptor> createModelSetDescriptor(ModelSetDescriptor value) {
        return new JAXBElement<ModelSetDescriptor>(_ModelSetDescriptor_QNAME, ModelSetDescriptor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfObjectLocator }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfObjectLocator")
    public JAXBElement<ArrayOfObjectLocator> createArrayOfObjectLocator(ArrayOfObjectLocator value) {
        return new JAXBElement<ArrayOfObjectLocator>(_ArrayOfObjectLocator_QNAME, ArrayOfObjectLocator.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfStudy }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfStudy")
    public JAXBElement<ArrayOfStudy> createArrayOfStudy(ArrayOfStudy value) {
        return new JAXBElement<ArrayOfStudy>(_ArrayOfStudy_QNAME, ArrayOfStudy.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfXPathNodeInfoSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfXPathNodeInfoSet")
    public JAXBElement<ArrayOfXPathNodeInfoSet> createArrayOfXPathNodeInfoSet(ArrayOfXPathNodeInfoSet value) {
        return new JAXBElement<ArrayOfXPathNodeInfoSet>(_ArrayOfXPathNodeInfoSet_QNAME, ArrayOfXPathNodeInfoSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AvailableData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "AvailableData")
    public JAXBElement<AvailableData> createAvailableData(AvailableData value) {
        return new JAXBElement<AvailableData>(_AvailableData_QNAME, AvailableData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfUID")
    public JAXBElement<ArrayOfUID> createArrayOfUID(ArrayOfUID value) {
        return new JAXBElement<ArrayOfUID>(_ArrayOfUID_QNAME, ArrayOfUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XPathNode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "XPathNode")
    public JAXBElement<XPathNode> createXPathNode(XPathNode value) {
        return new JAXBElement<XPathNode>(_XPathNode_QNAME, XPathNode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "UID")
    public JAXBElement<UID> createUID(UID value) {
        return new JAXBElement<UID>(_UID_QNAME, UID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Modality }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "Modality")
    public JAXBElement<Modality> createModality(Modality value) {
        return new JAXBElement<Modality>(_Modality_QNAME, Modality.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Series }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "Series")
    public JAXBElement<Series> createSeries(Series value) {
        return new JAXBElement<Series>(_Series_QNAME, Series.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfQueryResultInfoSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfQueryResultInfoSet")
    public JAXBElement<ArrayOfQueryResultInfoSet> createArrayOfQueryResultInfoSet(ArrayOfQueryResultInfoSet value) {
        return new JAXBElement<ArrayOfQueryResultInfoSet>(_ArrayOfQueryResultInfoSet_QNAME, ArrayOfQueryResultInfoSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "StatusType")
    public JAXBElement<StatusType> createStatusType(StatusType value) {
        return new JAXBElement<StatusType>(_StatusType_QNAME, StatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfPatient }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfPatient")
    public JAXBElement<ArrayOfPatient> createArrayOfPatient(ArrayOfPatient value) {
        return new JAXBElement<ArrayOfPatient>(_ArrayOfPatient_QNAME, ArrayOfPatient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryResultInfoSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "QueryResultInfoSet")
    public JAXBElement<QueryResultInfoSet> createQueryResultInfoSet(QueryResultInfoSet value) {
        return new JAXBElement<QueryResultInfoSet>(_QueryResultInfoSet_QNAME, QueryResultInfoSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name = "ArrayOfstring")
    public JAXBElement<ArrayOfstring> createArrayOfstring(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_ArrayOfstring_QNAME, ArrayOfstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "UUID")
    public JAXBElement<UUID> createUUID(UUID value) {
        return new JAXBElement<UUID>(_UUID_QNAME, UUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfXPathNode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfXPathNode")
    public JAXBElement<ArrayOfXPathNode> createArrayOfXPathNode(ArrayOfXPathNode value) {
        return new JAXBElement<ArrayOfXPathNode>(_ArrayOfXPathNode_QNAME, ArrayOfXPathNode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObjectLocator }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ObjectLocator")
    public JAXBElement<ObjectLocator> createObjectLocator(ObjectLocator value) {
        return new JAXBElement<ObjectLocator>(_ObjectLocator_QNAME, ObjectLocator.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfObjectDescriptor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dicom.nema.org/PS3.19/HostService-20100825", name = "ArrayOfObjectDescriptor")
    public JAXBElement<ArrayOfObjectDescriptor> createArrayOfObjectDescriptor(ArrayOfObjectDescriptor value) {
        return new JAXBElement<ArrayOfObjectDescriptor>(_ArrayOfObjectDescriptor_QNAME, ArrayOfObjectDescriptor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

}
