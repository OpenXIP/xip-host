/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
public class ObjectFactoryHost {

    private final static QName _GenerateUIDResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "generateUIDResponse");
    private final static QName _GetAsModelsResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getAsModelsResponse");
    private final static QName _GetAvailableScreen_QNAME = new QName("http://wg23.dicom.nema.org/", "getAvailableScreen");
    private final static QName _GetDataAsFile_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsFile");
    private final static QName _GetDataAsFileResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsFileResponse");
    private final static QName _GetOutputDir_QNAME = new QName("http://wg23.dicom.nema.org/", "getOutputDir");
    private final static QName _GetAvailableScreenResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getAvailableScreenResponse");
    private final static QName _NotifyStateChanged_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyStateChanged");
    private final static QName _NotifyStatus_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyStatus");
    private final static QName _QueryModelResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "queryModelResponse");
    private final static QName _GetDataAsSpecificTypeFile_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsSpecificTypeFile");
    private final static QName _GetAsModels_QNAME = new QName("http://wg23.dicom.nema.org/", "getAsModels");
    private final static QName _NotifyStatusResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyStatusResponse");
    private final static QName _NotifyStateChangedResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyStateChangedResponse");
    private final static QName _NotifyDataAvailable_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyDataAvailable");
    private final static QName _GetTmpDirResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getTmpDirResponse");
    private final static QName _NotifyDataAvailableResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "notifyDataAvailableResponse");
    private final static QName _GenerateUID_QNAME = new QName("http://wg23.dicom.nema.org/", "generateUID");
    private final static QName _GetTmpDir_QNAME = new QName("http://wg23.dicom.nema.org/", "getTmpDir");
    private final static QName _GetOutputDirResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getOutputDirResponse");
    private final static QName _QueryModel_QNAME = new QName("http://wg23.dicom.nema.org/", "queryModel");
    private final static QName _GetDataAsSpecificTypeFileResponse_QNAME = new QName("http://wg23.dicom.nema.org/", "getDataAsSpecificTypeFileResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.nema.dicom.wg23
     * 
     */
    public ObjectFactoryHost() {
    }

    /**
     * Create an instance of {@link Status }
     * 
     */
    public Status createStatus() {
        return new Status();
    }

    /**
     * Create an instance of {@link GenerateUIDResponse }
     * 
     */
    public GenerateUIDResponse createGenerateUIDResponse() {
        return new GenerateUIDResponse();
    }

    /**
     * Create an instance of {@link ArrayOfPatient }
     * 
     */
    public ArrayOfPatient createArrayOfPatient() {
        return new ArrayOfPatient();
    }

    /**
     * Create an instance of {@link ArrayOfObjectDescriptor }
     * 
     */
    public ArrayOfObjectDescriptor createArrayOfObjectDescriptor() {
        return new ArrayOfObjectDescriptor();
    }

    /**
     * Create an instance of {@link ModelSetDescriptor }
     * 
     */
    public ModelSetDescriptor createModelSetDescriptor() {
        return new ModelSetDescriptor();
    }

    /**
     * Create an instance of {@link GetOutputDirResponse }
     * 
     */
    public GetOutputDirResponse createGetOutputDirResponse() {
        return new GetOutputDirResponse();
    }

    /**
     * Create an instance of {@link Uuid }
     * 
     */
    public Uuid createUuid() {
        return new Uuid();
    }

    /**
     * Create an instance of {@link NotifyStatus }
     * 
     */
    public NotifyStatus createNotifyStatus() {
        return new NotifyStatus();
    }

    /**
     * Create an instance of {@link ArrayOfStudy }
     * 
     */
    public ArrayOfStudy createArrayOfStudy() {
        return new ArrayOfStudy();
    }

    /**
     * Create an instance of {@link ArrayOfUUID }
     * 
     */
    public ArrayOfUUID createArrayOfUUID() {
        return new ArrayOfUUID();
    }

    /**
     * Create an instance of {@link NotifyStatusResponse }
     * 
     */
    public NotifyStatusResponse createNotifyStatusResponse() {
        return new NotifyStatusResponse();
    }

    /**
     * Create an instance of {@link NotifyStateChanged }
     * 
     */
    public NotifyStateChanged createNotifyStateChanged() {
        return new NotifyStateChanged();
    }

    /**
     * Create an instance of {@link QueryResult }
     * 
     */
    public QueryResult createQueryResult() {
        return new QueryResult();
    }

    /**
     * Create an instance of {@link Rectangle }
     * 
     */
    public Rectangle createRectangle() {
        return new Rectangle();
    }

    /**
     * Create an instance of {@link GetAvailableScreen }
     * 
     */
    public GetAvailableScreen createGetAvailableScreen() {
        return new GetAvailableScreen();
    }

    /**
     * Create an instance of {@link GetTmpDir }
     * 
     */
    public GetTmpDir createGetTmpDir() {
        return new GetTmpDir();
    }

    /**
     * Create an instance of {@link GenerateUID }
     * 
     */
    public GenerateUID createGenerateUID() {
        return new GenerateUID();
    }

    /**
     * Create an instance of {@link Uid }
     * 
     */
    public Uid createUid() {
        return new Uid();
    }

    /**
     * Create an instance of {@link GetDataAsFileResponse }
     * 
     */
    public GetDataAsFileResponse createGetDataAsFileResponse() {
        return new GetDataAsFileResponse();
    }

    /**
     * Create an instance of {@link GetOutputDir }
     * 
     */
    public GetOutputDir createGetOutputDir() {
        return new GetOutputDir();
    }

    /**
     * Create an instance of {@link NotifyDataAvailableResponse }
     * 
     */
    public NotifyDataAvailableResponse createNotifyDataAvailableResponse() {
        return new NotifyDataAvailableResponse();
    }

    /**
     * Create an instance of {@link GetDataAsSpecificTypeFile }
     * 
     */
    public GetDataAsSpecificTypeFile createGetDataAsSpecificTypeFile() {
        return new GetDataAsSpecificTypeFile();
    }

    /**
     * Create an instance of {@link ArrayOfQueryResult }
     * 
     */
    public ArrayOfQueryResult createArrayOfQueryResult() {
        return new ArrayOfQueryResult();
    }

    /**
     * Create an instance of {@link NotifyStateChangedResponse }
     * 
     */
    public NotifyStateChangedResponse createNotifyStateChangedResponse() {
        return new NotifyStateChangedResponse();
    }

    /**
     * Create an instance of {@link GetDataAsFile }
     * 
     */
    public GetDataAsFile createGetDataAsFile() {
        return new GetDataAsFile();
    }

    /**
     * Create an instance of {@link NotifyDataAvailable }
     * 
     */
    public NotifyDataAvailable createNotifyDataAvailable() {
        return new NotifyDataAvailable();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link ArrayOfObjectLocator }
     * 
     */
    public ArrayOfObjectLocator createArrayOfObjectLocator() {
        return new ArrayOfObjectLocator();
    }

    /**
     * Create an instance of {@link GetAsModels }
     * 
     */
    public GetAsModels createGetAsModels() {
        return new GetAsModels();
    }

    /**
     * Create an instance of {@link ObjectLocator }
     * 
     */
    public ObjectLocator createObjectLocator() {
        return new ObjectLocator();
    }

    /**
     * Create an instance of {@link GetAvailableScreenResponse }
     * 
     */
    public GetAvailableScreenResponse createGetAvailableScreenResponse() {
        return new GetAvailableScreenResponse();
    }

    /**
     * Create an instance of {@link AvailableData }
     * 
     */
    public AvailableData createAvailableData() {
        return new AvailableData();
    }

    /**
     * Create an instance of {@link Study }
     * 
     */
    public Study createStudy() {
        return new Study();
    }

    /**
     * Create an instance of {@link QueryModelResponse }
     * 
     */
    public QueryModelResponse createQueryModelResponse() {
        return new QueryModelResponse();
    }

    /**
     * Create an instance of {@link GetTmpDirResponse }
     * 
     */
    public GetTmpDirResponse createGetTmpDirResponse() {
        return new GetTmpDirResponse();
    }

    /**
     * Create an instance of {@link ArrayOfSeries }
     * 
     */
    public ArrayOfSeries createArrayOfSeries() {
        return new ArrayOfSeries();
    }

    /**
     * Create an instance of {@link QueryModel }
     * 
     */
    public QueryModel createQueryModel() {
        return new QueryModel();
    }

    /**
     * Create an instance of {@link Series }
     * 
     */
    public Series createSeries() {
        return new Series();
    }

    /**
     * Create an instance of {@link Patient }
     * 
     */
    public Patient createPatient() {
        return new Patient();
    }

    /**
     * Create an instance of {@link GetAsModelsResponse }
     * 
     */
    public GetAsModelsResponse createGetAsModelsResponse() {
        return new GetAsModelsResponse();
    }

    /**
     * Create an instance of {@link ObjectDescriptor }
     * 
     */
    public ObjectDescriptor createObjectDescriptor() {
        return new ObjectDescriptor();
    }

    /**
     * Create an instance of {@link Modality }
     * 
     */
    public Modality createModality() {
        return new Modality();
    }

    /**
     * Create an instance of {@link GetDataAsSpecificTypeFileResponse }
     * 
     */
    public GetDataAsSpecificTypeFileResponse createGetDataAsSpecificTypeFileResponse() {
        return new GetDataAsSpecificTypeFileResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateUIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "generateUIDResponse")
    public JAXBElement<GenerateUIDResponse> createGenerateUIDResponse(GenerateUIDResponse value) {
        return new JAXBElement<GenerateUIDResponse>(_GenerateUIDResponse_QNAME, GenerateUIDResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableScreen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getAvailableScreen")
    public JAXBElement<GetAvailableScreen> createGetAvailableScreen(GetAvailableScreen value) {
        return new JAXBElement<GetAvailableScreen>(_GetAvailableScreen_QNAME, GetAvailableScreen.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOutputDir }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getOutputDir")
    public JAXBElement<GetOutputDir> createGetOutputDir(GetOutputDir value) {
        return new JAXBElement<GetOutputDir>(_GetOutputDir_QNAME, GetOutputDir.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableScreenResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getAvailableScreenResponse")
    public JAXBElement<GetAvailableScreenResponse> createGetAvailableScreenResponse(GetAvailableScreenResponse value) {
        return new JAXBElement<GetAvailableScreenResponse>(_GetAvailableScreenResponse_QNAME, GetAvailableScreenResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyStateChanged }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "notifyStateChanged")
    public JAXBElement<NotifyStateChanged> createNotifyStateChanged(NotifyStateChanged value) {
        return new JAXBElement<NotifyStateChanged>(_NotifyStateChanged_QNAME, NotifyStateChanged.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "notifyStatus")
    public JAXBElement<NotifyStatus> createNotifyStatus(NotifyStatus value) {
        return new JAXBElement<NotifyStatus>(_NotifyStatus_QNAME, NotifyStatus.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAsModels }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getAsModels")
    public JAXBElement<GetAsModels> createGetAsModels(GetAsModels value) {
        return new JAXBElement<GetAsModels>(_GetAsModels_QNAME, GetAsModels.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "notifyStatusResponse")
    public JAXBElement<NotifyStatusResponse> createNotifyStatusResponse(NotifyStatusResponse value) {
        return new JAXBElement<NotifyStatusResponse>(_NotifyStatusResponse_QNAME, NotifyStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyStateChangedResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "notifyStateChangedResponse")
    public JAXBElement<NotifyStateChangedResponse> createNotifyStateChangedResponse(NotifyStateChangedResponse value) {
        return new JAXBElement<NotifyStateChangedResponse>(_NotifyStateChangedResponse_QNAME, NotifyStateChangedResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTmpDirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getTmpDirResponse")
    public JAXBElement<GetTmpDirResponse> createGetTmpDirResponse(GetTmpDirResponse value) {
        return new JAXBElement<GetTmpDirResponse>(_GetTmpDirResponse_QNAME, GetTmpDirResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "generateUID")
    public JAXBElement<GenerateUID> createGenerateUID(GenerateUID value) {
        return new JAXBElement<GenerateUID>(_GenerateUID_QNAME, GenerateUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTmpDir }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getTmpDir")
    public JAXBElement<GetTmpDir> createGetTmpDir(GetTmpDir value) {
        return new JAXBElement<GetTmpDir>(_GetTmpDir_QNAME, GetTmpDir.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOutputDirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getOutputDirResponse")
    public JAXBElement<GetOutputDirResponse> createGetOutputDirResponse(GetOutputDirResponse value) {
        return new JAXBElement<GetOutputDirResponse>(_GetOutputDirResponse_QNAME, GetOutputDirResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "queryModel")
    public JAXBElement<QueryModel> createQueryModel(QueryModel value) {
        return new JAXBElement<QueryModel>(_QueryModel_QNAME, QueryModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataAsSpecificTypeFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://wg23.dicom.nema.org/", name = "getDataAsSpecificTypeFileResponse")
    public JAXBElement<GetDataAsSpecificTypeFileResponse> createGetDataAsSpecificTypeFileResponse(GetDataAsSpecificTypeFileResponse value) {
        return new JAXBElement<GetDataAsSpecificTypeFileResponse>(_GetDataAsSpecificTypeFileResponse_QNAME, GetDataAsSpecificTypeFileResponse.class, null, value);
    }

}
