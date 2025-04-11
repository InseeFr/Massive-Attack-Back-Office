package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.service.xsl.PearlCampaignTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class PearlTransformService {

    private final PearlCampaignTransformer pearlCampaignTransformer;

    public File getPearlCampaign(File fodsInput) throws Exception {
        return pearlCampaignTransformer.extractCampaign(fodsInput);
    }

    public File getPearlSurveyUnits(File fodsInput) throws Exception {
        return pearlCampaignTransformer.extractSurveyUnits(fodsInput);
    }


    public File getPearlAssignement(File fodsInput) throws Exception {
        return pearlCampaignTransformer.extractAssignement(fodsInput);
    }

}
