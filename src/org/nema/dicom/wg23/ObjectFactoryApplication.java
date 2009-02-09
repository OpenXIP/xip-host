
package org.nema.dicom.wg23;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.nema.dicom.wg23 package. 
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
public class ObjectFactoryApplication {

    private final static QName _SetStateResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "setStateResponse");
    private final static QName _GetAsModelsResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getAsModelsResponse");
    private final static QName _BringToFrontResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "bringToFrontResponse");
    private final static QName _GetDataAsFile_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsFile");
    private final static QName _GetDataAsFileResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsFileResponse");
    private final static QName _GetStateResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getStateResponse");
    private final static QName _QueryModelResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "queryModelResponse");
    private final static QName _GetDataAsSpecificTypeFile_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsSpecificTypeFile");
    private final static QName _BringToFront_QNAME = new QName("http://wg23.dicom.nema.org/", "bringToFront");
    private final static QName _GetAsModels_QNAME = new QName("http://wg23.dicom.nema.org/", "getAsModels");
    private final static QName _SetState_QNAME = new QName("http://wg23.dicom.nema.org/", "setState");
    private final static QName _NotifyDataAvailable_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyDataAvailable");
    private final static QName _NotifyDataAvailableResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyDataAvailableResponse");
    private final static QName _GetState_QNAME = new QName("http://wg23.dicom.nema.org/", "getState");
    private final static QName _GetDataAsSpecificTypeFileResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsSpecificTypeFileResponse");
    private final static QName _QueryModel_QNAME = new QName("http://wg23.dicom.nema.org/", "queryModel");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.nema.dicom.wg23
     * 
     */
    public ObjectFactoryApplication() {
    }

    /**
     * Create an instance of {@link GetState }
     * 
     */
    public GetState createGetState() {
        return new GetState();
    }

    /**
     * Create an instance of {@link SetState }
     * 
     */
    public SetState createSetState() {
        return new SetState();
    }

    /**
     * Create an instance of {@link GetDataAsFileResponse }
     * 
     */
    public GetDataAsFileResponse createGetDataAsFileResponse() {
        return new GetDataAsFileResponse();
    }

    /**
     * Create an instance of {@link QueryResult }
     * 
     */
    public QueryResult createQueryResult() {
        return new QueryResult();
    }

    /**
     * Create an instance of {@link ArrayOfSeries }
     * 
     */
    public ArrayOfSeries createArrayOfSeries() {
        return new ArrayOfSeries();
    }

    /**
     * Create an instance of {@link GetStateResponse }
     * 
     */
    public GetStateResponse createGetStateResponse() {
        return new GetStateResponse();
    }

    /**
     * Create an instance of {@link ObjectLocator }
     * 
     */
    public ObjectLocator createObjectLocator() {
        return new ObjectLocator();
    }

    /**
     * Create an instance of {@link Series }
     * 
     */
    public Series createSeries() {
        return new Series();
    }

    /**
     * Create an instance of {@link Uid }
     * 
     */
    public Uid createUid() {
        return new Uid();
    }

    /**
     * Create an instance of {@link SetStateResponse }
     * 
     */
    public SetStateResponse createSetStateResponse() {
        return new SetStateResponse();
    }

    /**
     * Create an instance of {@link ModelSetDescriptor }
     * 
     */
    public ModelSetDescriptor createModelSetDescriptor() {
        return new ModelSetDescriptor();
    }

    /**
     * Create an instance of {@link QueryModelResponse }
     * 
     */
    public QueryModelResponse createQueryModelResponse() {
        return new QueryModelResponse();
    }

    /**
     * Create an instance of {@link BringToFrontResponse }
     * 
     */
    public BringToFrontResponse createBringToFrontResponse() {
        return new BringToFrontResponse();
    }

    /**
     * Create an instance of {@link NotifyDataAvailable }
     * 
     */
    public NotifyDataAvailable createNotifyDataAvailable() {
        return new NotifyDataAvailable();
    }

    /**
     * Create an instance of {@link BringToFront }
     * 
     */
    public BringToFront createBringToFront() {
        return new BringToFront();
    }

    /**
     * Create an instance of {@link GetDataAsSpecificTypeFileResponse }
     * 
     */
    public GetDataAsSpecificTypeFileResponse createGetDataAsSpecificTypeFileResponse() {
        return new GetDataAsSpecificTypeFileResponse();
    }

    /**
     * Create an instance of {@link ObjectDescriptor }
     * 
     */
    public ObjectDescriptor createObjectDescriptor() {
        return new ObjectDescriptor();
    }

    /**
     * Create an instance of {@link ArrayOfPatient }
     * 
     */
    public ArrayOfPatient createArrayOfPatient() {
        return new ArrayOfPatient();
    }

    /**
     * Create an instance of {@link NotifyDataAvailableResponse }
     * 
     */
    public NotifyDataAvailableResponse createNotifyDataAvailableResponse() {
        return new NotifyDataAvailableResponse();
    }

    /**
     * Create an instance of {@link ArrayOfQueryResult }
     * 
     */
    public ArrayOfQueryResult createArrayOfQueryResult() {
        return new ArrayOfQueryResult();
    }

    /**
     * Create an instance of {@link ArrayOfUUID }
     * 
     */
    public ArrayOfUUID createArrayOfUUID() {
        return new ArrayOfUUID();
    }

    /**
     * Create an instance of {@link GetAsModels }
     * 
     */
    public GetAsModels createGetAsModels() {
        return new GetAsModels();
    }

    /**
     * Create an instance of {@link Patient }
     * 
     */
    public Patient createPatient() {
        return new Patient();
    }

    /**
     * Create an instance of {@link Modality }
     * 
     */
    public Modality createModality() {
        return new Modality();
    }

    /**
     * Create an instance of {@link GetDataAsFile }
     * 
     */
    public GetDataAsFile createGetDataAsFile() {
        return new GetDataAsFile();
    }

    /**
     * Create an instance of {@link QueryModel }
     * 
     */
    public QueryModel createQueryModel() {
        return new QueryModel();
    }

    /**
     * Create an instance of {@link Uuid }
     * 
     */
    public Uuid createUuid() {
        return new Uuid();
    }

    /**
     * Create an instance of {@link GetAsModelsResponse }
     * 
     */
    public GetAsModelsResponse createGetAsModelsResponse() {
        return new GetAsModelsResponse();
    }

    /**
     * Create an instance of {@link GetDataAsSpecificTypeFile }
     * 
     */
    public GetDataAsSpecificTypeFile createGetDataAsSpecificTypeFile() {
        return new GetDataAsSpecificTypeFile();
    }

    /**
     * Create an instance of {@link Study }
     * 
     */
    public Study createStudy() {
        return new Study();
    }

    /**
     * Create an instance of {@link ArrayOfStudy }
     * 
     */
    public ArrayOfStudy createArrayOfStudy() {
        return new ArrayOfStudy();
    }

    /**
     * Create an instance of {@link ArrayOfObjectLocator }
     * 
     */
    public ArrayOfObjectLocator createArrayOfObjectLocator() {
        return new ArrayOfObjectLocator();
    }

    /**
     * Create an instance of {@link ArrayOfObjectDescriptor }
     * 
     */
    public ArrayOfObjectDescriptor createArrayOfObjectDescriptor() {
        return new ArrayOfObjectDescriptor();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link AvailableData }
     * 
     */
    public AvailableData createAvailableData() {
        return new AvailableData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetStateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "setStateResponse")
    public JAXBElement<SetStateResponse> createSetStateResponse(SetStateResponse value) {
        return new JAXBElement<SetStateResponse>(_SetStateResponse_QNAME, SetStateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAsModelsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getAsModelsResponse")
    public JAXBElement<GetAsModelsResponse> createGetAsModelsResponse(GetAsModelsResponse value) {
        return new JAXBElement<GetAsModelsResponse>(_GetAsModelsResponse_QNAME, GetAsModelsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BringToFrontResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "bringToFrontResponse")
    public JAXBElement<BringToFrontResponse> createBringToFrontResponse(BringToFrontResponse value) {
        return new JAXBElement<BringToFrontResponse>(_BringToFrontResponse_QNAME, BringToFrontResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataAsFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getDataAsFile")
    public JAXBElement<GetDataAsFile> createGetDataAsFile(GetDataAsFile value) {
        return new JAXBElement<GetDataAsFile>(_GetDataAsFile_QNAME, GetDataAsFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataAsFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getDataAsFileResponse")
    public JAXBElement<GetDataAsFileResponse> createGetDataAsFileResponse(GetDataAsFileResponse value) {
        return new JAXBElement<GetDataAsFileResponse>(_GetDataAsFileResponse_QNAME, GetDataAsFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getStateResponse")
    public JAXBElement<GetStateResponse> createGetStateResponse(GetStateResponse value) {
        return new JAXBElement<GetStateResponse>(_GetStateResponse_QNAME, GetStateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryModelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "queryModelResponse")
    public JAXBElement<QueryModelResponse> createQueryModelResponse(QueryModelResponse value) {
        return new JAXBElement<QueryModelResponse>(_QueryModelResponse_QNAME, QueryModelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataAsSpecificTypeFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getDataAsSpecificTypeFile")
    public JAXBElement<GetDataAsSpecificTypeFile> createGetDataAsSpecificTypeFile(GetDataAsSpecificTypeFile value) {
        return new JAXBElement<GetDataAsSpecificTypeFile>(_GetDataAsSpecificTypeFile_QNAME, GetDataAsSpecificTypeFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BringToFront }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "bringToFront")
    public JAXBElement<BringToFront> createBringToFront(BringToFront value) {
        return new JAXBElement<BringToFront>(_BringToFront_QNAME, BringToFront.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAsModels }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getAsModels")
    public JAXBElement<GetAsModels> createGetAsModels(GetAsModels value) {
        return new JAXBElement<GetAsModels>(_GetAsModels_QNAME, GetAsModels.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "setState")
    public JAXBElement<SetState> createSetState(SetState value) {
        return new JAXBElement<SetState>(_SetState_QNAME, SetState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyDataAvailable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "notifyDataAvailable")
    public JAXBElement<NotifyDataAvailable> createNotifyDataAvailable(NotifyDataAvailable value) {
        return new JAXBElement<NotifyDataAvailable>(_NotifyDataAvailable_QNAME, NotifyDataAvailable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyDataAvailableResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "notifyDataAvailableResponse")
    public JAXBElement<NotifyDataAvailableResponse> createNotifyDataAvailableResponse(NotifyDataAvailableResponse value) {
        return new JAXBElement<NotifyDataAvailableResponse>(_NotifyDataAvailableResponse_QNAME, NotifyDataAvailableResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getState")
    public JAXBElement<GetState> createGetState(GetState value) {
        return new JAXBElement<GetState>(_GetState_QNAME, GetState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataAsSpecificTypeFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getDataAsSpecificTypeFileResponse")
    public JAXBElement<GetDataAsSpecificTypeFileResponse> createGetDataAsSpecificTypeFileResponse(GetDataAsSpecificTypeFileResponse value) {
        return new JAXBElement<GetDataAsSpecificTypeFileResponse>(_GetDataAsSpecificTypeFileResponse_QNAME, GetDataAsSpecificTypeFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "queryModel")
    public JAXBElement<QueryModel> createQueryModel(QueryModel value) {
        return new JAXBElement<QueryModel>(_QueryModel_QNAME, QueryModel.class, null, value);
    }

}
