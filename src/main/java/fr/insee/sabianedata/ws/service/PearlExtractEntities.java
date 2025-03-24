package fr.insee.sabianedata.ws.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.insee.sabianedata.ws.model.pearl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PearlExtractEntities {

    private final PearlTransformService pearlTransformService;

    public List<PearlSurveyUnit> getPearlSurveyUnitsFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlSurveyUnits(fods);
        XmlMapper xmlMapper = new XmlMapper();
        PearlSurveyUnits pearlSurveyUnits = xmlMapper.readValue(file, PearlSurveyUnits.class);
        return pearlSurveyUnits.getSurveyUnits() == null ? new ArrayList<>() :
                pearlSurveyUnits.getSurveyUnits().stream().map(surveyUnit -> {
                    surveyUnit.cleanAttributes();
                    return surveyUnit;
                }).toList();
    }

    public PearlCampaign getPearlCampaignFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlCampaign(fods);
        XmlMapper xmlMapper = new XmlMapper();
        PearlCampaign pearlCampaign = xmlMapper.readValue(file, PearlCampaign.class);
        List<Visibility> visibilities = pearlCampaign.getVisibilities();
        List<Visibility> newVisibilities = visibilities.stream().map(Visibility::new).toList();
        pearlCampaign.setVisibilities(newVisibilities);
        return pearlCampaign;
    }

    public List<Assignment> getAssignementsFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlAssignement(fods);
        XmlMapper xmlMapper = new XmlMapper();
        Assignements assignementList = xmlMapper.readValue(file, Assignements.class);
        return assignementList.getAssignments() != null ? assignementList.getAssignments() : new ArrayList<>();
    }

}
