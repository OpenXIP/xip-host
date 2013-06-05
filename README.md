Welcome to the XIP Host&trade; Project!
===============================

The eXtensible Imaging Platform&trade; (XIP&trade;) is an Open Source project the
provides a framework for developing image processing and visualization applications.
While XIP's emphasis is medical applications, it can be used for other types of
applications.

The XIP Host&trade; component of XIP&trade; performs the role of a
[DICOM Hosting System] (http://medical.nema.org/Dicom/2011/11_19pu.pdf).  It provides the
infrastructure commonly needed by medical applications, such as DICOM network
connectivity.  It also provides access to NCI services found on caGRID, such as
NBIA and the AIM Data Services, and includes access to the 
[AVT Assessment Database] (https://github.com/OpenXIP/avt-ad-api), an
[AIM] (https://github.com/NCIP/annotation-and-image-markup) and
[DICOM] (http://dicom.nema.org) aware local database.  It also plays roles in several
[IHE] (http://www.ihe.net) profiles, including the
[Patient Demographic Query (PDQ)] (http://wiki.ihe.net/index.php?title=Patient_Demographics_Query) and
[Cross Enterprise Document Sharing (XDS)] (http://wiki.ihe.net/index.php?title=Cross_Enterprise_Document_Sharing) 
with [Image Sharing] (http://wiki.ihe.net/index.php?title=Cross-enterprise_Document_Sharing_for_Imaging) profiles.
The XIP Host is written in Java using Swing and JAX-WS.

The ultimate goals of the project include:
  * Be a 'reference implementation' of a [DICOM Hosting System] (http://medical.nema.org/Dicom/2011/11_19pu.pdf)
  * Provide access to repositories of images and related data, with an emphasis on research workflows,
     instead of clinical workflows, the focus of most medical imaging (PACS) workstations

XIP&trade;, including the XIP Host&trade; is distributed under the Apache 2.0 License.
Please see the NOTICE and LICENSE files for details.

You will find more details about XIP&trade; in the following links:
    *  [Home Page] (http://www.OpenXIP.org)
    *  [Forum/Mailing List] (https://groups.google.com/forum/?fromgroups#!forum/openxip)
    *  [Issue tracker] (https://plans.imphub.org/browse/XIP)
    *  [Documentation] (https://docs.imphub.org/display/XIP)
    *  [Git code repository] (https://github.com/OpenXIP/xip-host)
