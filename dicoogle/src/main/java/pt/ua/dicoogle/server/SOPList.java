/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dcm4che2.data.UID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.datastructs.AdditionalSOPClass;
import pt.ua.dicoogle.sdk.datastructs.SOPClass;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings.DicomServices;
import pt.ua.dicoogle.server.web.management.SOPClassSettings;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Support class for keeping SOPClass/TransferSyntax association
 *
 *
 * @author Luís Bastião Silva <bastiao@bmd-softwre.com>
 * @author Marco Pereira
 */

public class SOPList {

    private static SOPList instance = null;
    private static final Logger logger = LoggerFactory.getLogger(SOPList.class);

    private Hashtable<String, TransfersStorage> table;

    private String[] SOP = {
        UID.BasicStudyContentNotificationSOPClassRetired,
        UID.StoredPrintStorageSOPClassRetired,
        UID.HardcopyGrayscaleImageStorageSOPClassRetired,
        UID.HardcopyColorImageStorageSOPClassRetired,
        UID.ComputedRadiographyImageStorage,
        UID.DigitalXRayImageStorageForPresentation,
        UID.DigitalXRayImageStorageForProcessing,
        UID.DigitalMammographyXRayImageStorageForPresentation,
        UID.DigitalIntraOralXRayImageStorageForPresentation,
        UID.DigitalIntraOralXRayImageStorageForProcessing,
        UID.StandaloneModalityLUTStorageRetired,
        UID.EncapsulatedPDFStorage,
        UID.StandaloneVOILUTStorageRetired,
        UID.GrayscaleSoftcopyPresentationStateStorageSOPClass,
        UID.ColorSoftcopyPresentationStateStorageSOPClass,
        UID.PseudoColorSoftcopyPresentationStateStorageSOPClass,
        UID.BlendingSoftcopyPresentationStateStorageSOPClass,
        UID.XRayAngiographicImageStorage,
        UID.EnhancedXAImageStorage,
        UID.XRayRadiofluoroscopicImageStorage,
        UID.EnhancedXRFImageStorage,
        UID.XRayAngiographicBiPlaneImageStorageRetired,
        UID.PositronEmissionTomographyImageStorage,
        UID.StandalonePETCurveStorageRetired,
        UID.CTImageStorage,
        UID.EnhancedCTImageStorage,
        UID.NuclearMedicineImageStorage,
        UID.UltrasoundMultiFrameImageStorageRetired,
        UID.UltrasoundMultiFrameImageStorage,
        UID.MRImageStorage,
        UID.EnhancedMRImageStorage,
        UID.MRSpectroscopyStorage,
        UID.RTImageStorage,
        UID.RTDoseStorage,
        UID.RTStructureSetStorage,
        UID.RTBeamsTreatmentRecordStorage,
        UID.RTPlanStorage,
        UID.RTBrachyTreatmentRecordStorage,
        UID.RTTreatmentSummaryRecordStorage,
        UID.NuclearMedicineImageStorageRetired,
        UID.UltrasoundImageStorageRetired,
        UID.UltrasoundImageStorage,
        UID.RawDataStorage,
        UID.SpatialRegistrationStorage,
        UID.SpatialFiducialsStorage,
        UID.RealWorldValueMappingStorage,
        UID.SecondaryCaptureImageStorage,
        UID.MultiFrameSingleBitSecondaryCaptureImageStorage,
        UID.MultiFrameGrayscaleByteSecondaryCaptureImageStorage,
        UID.MultiFrameGrayscaleWordSecondaryCaptureImageStorage,
        UID.MultiFrameTrueColorSecondaryCaptureImageStorage,
        UID.VLImageStorageTrialRetired,
        UID.VLEndoscopicImageStorage,
        UID.VideoEndoscopicImageStorage,
        UID.VLMicroscopicImageStorage,
        UID.VideoMicroscopicImageStorage,
        UID.VLSlideCoordinatesMicroscopicImageStorage,
        UID.VLPhotographicImageStorage,
        UID.VideoPhotographicImageStorage,
        UID.OphthalmicPhotography8BitImageStorage,
        UID.OphthalmicPhotography16BitImageStorage,
        UID.StereometricRelationshipStorage,
        UID.VLMultiFrameImageStorageTrialRetired,
        UID.StandaloneOverlayStorageRetired,
        UID.BasicTextSRStorage,
        UID.EnhancedSRStorage,
        UID.ComprehensiveSRStorage,
        UID.ProcedureLogStorage,
        UID.MammographyCADSRStorage,
        UID.KeyObjectSelectionDocumentStorage,
        UID.ChestCADSRStorage,
        UID.StandaloneCurveStorageRetired,
        //UID._12leadECGWaveformStorage,
        UID.GeneralECGWaveformStorage,
        UID.AmbulatoryECGWaveformStorage,
        UID.HemodynamicWaveformStorage,
        UID.CardiacElectrophysiologyWaveformStorage,
        UID.BasicVoiceAudioWaveformStorage,
        UID.HangingProtocolStorage,
        UID.SiemensCSANonImageStorage,
        UID.VLWholeSlideMicroscopyImageStorage,
        UID.BreastTomosynthesisImageStorage,
        UID.XRayRadiationDoseSRStorage
    };

    public static synchronized SOPList getInstance() {
        if (instance == null) {
            instance = new SOPList();
        }
        return instance;
    }

    /**
     * Creates a new list 
     */
    private SOPList() {
        this.reset();
    }

    /** Reset the SOP list to an empty slate.
     *
     * This is not recommended unless you know what you're doing.
     */
    public final void reset() {
        this.table = new Hashtable<>();

        // Hardcoded (pre-#498) SOPs
        for (String sop : SOP) {
            this.table.put(sop, new TransfersStorage());
        }
    }

    /**
     * If configuration file has no information, assume default settings
     */
    public synchronized void setDefaultSettings() {
        for (TransfersStorage ts : table.values()) {
            ts.setDefaultSettings();
        }
    }

    /**
     * Add a SOP Class to the list 
     * @param UID SOP Class
     * @return -1 if something went wrong, 1 otherwise
     */
    public synchronized int registerSOP(String UID) {

        if (table.containsKey(UID)) {
            return -1;
        }

        table.put(UID, new TransfersStorage());

        return 1;
    }

    /**
     * Given a SOP Class returns it's Tranfer Syntaxes
     * @param UID SOP Class
     * @return List of tranfer syntaxes for the given SOP Class
     */
    public synchronized TransfersStorage getTS(String UID) {
        TransfersStorage TS;

        TS = table.get(UID);
        return TS;
    }

    /**
     * Updates a given SOP Class accepted Tranfer Syntaxes
     * @param UID SOP Class
     * @param p Transfer Syntaxes accepted on a boolean array
     * @param a Globaly accept/reject this SOP Class
     * @return -1 if something went wrong, 1 otherwise
     */
    public synchronized int updateTS(String UID, boolean[] p, boolean a) {
        TransfersStorage TS;
        TS = table.get(UID);

        if (TS != null) {
            if (TS.setTS(p) != 0) {
                return -1;
            }
            TS.setAccepted(a);
        }
        return 0;
    }


    public int updateTSFieldByTsUID(String sopUID, String tsUID, boolean value) {
        return updateTSField(sopUID, TransfersStorage.getGlobalTransferUIDsMap().get(tsUID), value);
    }


    /**
     * Updates a given SOP Class accepted Tranfer Syntaxes
     * @param UID SOP Class
     * @param name
     * @param value
     * @return -1 if something went wrong, 1 otherwise
     */
    public synchronized int updateTSField(String UID, String name, boolean value) {
        logger.debug("UID: {}, name: {}, value: {}", UID, name, value);

        TransfersStorage TS;
        TS = table.get(UID);

        int index = -1;
        for (int i = 0; i < TransfersStorage.getGlobalTransferMap().size(); i++) {
            if (TransfersStorage.getGlobalTransferMap().get(i).equals(name)) {
                index = i;
                break;
            }
        }
        if (TS != null && index != -1) {
            TS.setAccepted(value);
            // TS.setDefaultSettings();
            if (TS.setTS(value, index) != 0) {
                return -1;
            }
        }
        logger.debug("UID: {}, name: {}, value: {}", UID, name, value);

        return 0;
    }

    /**
     * Remove a SOP Class from List
     * @param UID SOP Class
     */
    public synchronized void RemoveSOP(String UID) {
        table.remove(UID);
    }

    /**
     * Removes selected services that do not have accepted transfers syntaxes 
     */
    public synchronized void CleanList() {
        List<String> l = new ArrayList<>();
        Enumeration<?> e = table.keys();
        TransfersStorage TS;
        boolean[] p;
        boolean unused;
        int i;
        int j;
        while (e.hasMoreElements()) {
            l.add(e.nextElement().toString());
        }
        for (i = 0; i < l.size(); i++) {
            unused = true;
            TS = table.get(l.get(i).toString());
            if (TS.getAccepted()) {
                p = TS.getTS();
                for (j = 0; j < p.length; j++) {
                    if (p[j]) {
                        unused = false;
                        break;
                    }
                }
                if (unused) {
                    TS.setAccepted(false);
                }
            }

        }
    }

    /**
     * Get the name of all the SOP Classes used in list
     * @return List with all the identifiers of SOP Class currently in use
     */
    public synchronized List<String> getKeys() {
        List<String> l = new ArrayList<>();
        Enumeration<?> e = table.keys();

        while (e.hasMoreElements()) {
            l.add(e.nextElement().toString());
        }
        return l;
    }

    /**
     * Get the number of SOP Classes that are marked as accepted
     * @return The number of SOP Classes that are actually marked as accepted
     */
    public synchronized int getAccepted() {
        List<String> l = new ArrayList<>();
        TransfersStorage local;
        Enumeration<?> e = table.keys();

        while (e.hasMoreElements()) {
            l.add(e.nextElement().toString());
        }

        int count = 0;
        for (int i = 0; i < l.size(); i++) {
            local = table.get(l.get(i));
            if (local != null) {
                if (local.getAccepted()) {
                    count++;
                }
            }
        }
        return count;
    }

    public String getSOPList() {
        JSONArray sopList = new JSONArray();
        for (String uid : SOP) {
            JSONObject elem = new JSONObject();
            elem.put("uid", uid);
            elem.put("sop_name", SOPClassSettings.getInstance().getSOPClasses().get(uid));
            JSONArray options = new JSONArray();
            TransfersStorage ts = getTS(uid);
            for (int i = 0; i < ts.getTS().length; i++) {
                JSONObject tsobj = new JSONObject();
                String name = TransfersStorage.getGlobalTransferMap().get(i);
                boolean value = ts.getTS()[i];
                tsobj.put("uid", TransfersStorage.convertTsNameToUID(name));
                tsobj.put("name", name);
                tsobj.put("value", value);

                options.add(tsobj);
            }
            elem.put("options", options);
            sopList.add(elem);


        }
        return sopList.toString();
    }

    public synchronized List<SOPClass> asSOPClassList() {
        List<SOPClass> l = new ArrayList<>();
        for (Map.Entry<String, TransfersStorage> e : this.table.entrySet()) {
            l.add(new SOPClass(e.getKey(), e.getValue().asList()));
        }
        return l;
    }

    /** Register additional SOP classes from the given server settings
     * into the archive's DICOM services.
     */
    public synchronized void updateList() {
        // Add the extras on SOP from getSettings()
        updateList(ServerSettingsManager.getSettings());
    }

    /** Register additional SOP classes from the active server settings
     * into the archive's DICOM services.
     */
    public synchronized void updateList(ServerSettings settings) {
        // Get all new SOP classes' UID (not existing in String[] SOP)
        Collection<String> newSOPs = settings.getDicomServicesSettings().getAdditionalSOPClasses().stream()
                .map(AdditionalSOPClass::getUid)
                .filter(newSOPUID -> !Arrays.asList(SOP).contains(newSOPUID))
                .collect(Collectors.toList());
        // Refresh hardcoded SOPs (outdated TransfersStorage)
        Arrays.asList(SOP).forEach(sop -> table.put(sop, new TransfersStorage()));
        // Add "Additional" SOPs to table w/ default transfer syntaxes
        newSOPs.forEach(sop -> {
            TransfersStorage ts = new TransfersStorage();
            ts.setDefaultSettings();
            table.put(sop, ts);
        });
        // Add them to SOP
        newSOPs.addAll(Arrays.asList(SOP));
        SOP = newSOPs.toArray(new String[0]);
    }

    /** Register the given additional SOP classes into the archive's DICOM services.
     */
    public synchronized void updateList(Collection<AdditionalSOPClass> additionalSOPClasses) {
        additionalSOPClasses.forEach(elem -> table.put(elem.getUid(), new TransfersStorage()));
    }

    /** Read the DICOM service settings from the active server settings
     * and update the archive's SOP list and storage transfer options accordingly.
     */
    public synchronized void readFromSettings() {
        readFromSettings(ServerSettingsManager.getSettings());
    }

    /** Read the DICOM service settings from the given settings object
     * and update the archive's SOP list and storage transfer options accordingly.
     */
    public synchronized void readFromSettings(ServerSettings settings) {
        settings.getDicomServicesSettings()
            .getSOPClasses()
            .forEach(sopClass -> sopClass.getTransferSyntaxes()
                .forEach(ts -> this.updateTSFieldByTsUID(sopClass.getUID(), ts, true))
            );
    }

    /** Save any changes made to the SOP transfer options listings
     * to a specific server settings object.
     * 
     * @param settings the server settings
     */
    public void writeToSettings(ServerSettings settings) {
        DicomServices dicomServicesSettings = settings.getDicomServicesSettings();

        // Save SOP classes
        dicomServicesSettings.setSOPClasses(this.asSOPClassList());
    }

    /** Save any changes made to the SOP transfer options listings
     * to the active server settings object.
     */
    public void writeToSettings() {
        writeToSettings(ServerSettingsManager.getSettings());
    }
}
