package fr.insee.sabianedata.ws.service.xsl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.sabianedata.ws.utils.ExtractionType;
import fr.insee.sabianedata.ws.utils.InputStreamUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class PearlCampaignTransformer {

    private static final Logger logger = LoggerFactory.getLogger(PearlCampaignTransformer.class);
    private static final XslTransformation saxonService = new XslTransformation();
    private static final String PEARL_EXTRACT_CAMPAIGN =  "/xslt/pearl-extract-campaign.xsl";
    private static final String PEARL_EXTRACT_ASSIGNMENT =  "/xslt/pearl-extract-assignement.xsl";
    private static final String PEARL_EXTRACT_SURVEY_UNITS =  "/xslt/pearl-extract-survey-units.xsl";

    public File extractCampaign(File input) throws Exception {
        return extract(input, ExtractionType.CAMPAIGN);
    }

    public File extractSurveyUnits(File input) throws Exception {
        return extract(input, ExtractionType.SURVEY_UNITS);
    }

    public File extractAssignement(File input) throws Exception {
        return extract(input, ExtractionType.ASSIGNMENT);
    }

    public File extract(File input, ExtractionType type) throws Exception {
        File outputFile = new File(input.getParent(), type + ".xml");
        logger.warn("Output folder : {}", outputFile.getAbsolutePath());

        try (InputStream inputStream = FileUtils.openInputStream(input);
             OutputStream outputStream = FileUtils.openOutputStream(outputFile);

             InputStream xsl = switch (type) {
                 case CAMPAIGN -> InputStreamUtil.getInputStreamFromPath(PEARL_EXTRACT_CAMPAIGN);
                 case ASSIGNMENT -> InputStreamUtil.getInputStreamFromPath(PEARL_EXTRACT_ASSIGNMENT);
                 case SURVEY_UNITS -> InputStreamUtil.getInputStreamFromPath(PEARL_EXTRACT_SURVEY_UNITS);
                 case QUESTIONNAIRE_MODELS -> throw new IllegalArgumentException("Invalid type: QUESTIONNAIRE_MODELS is not supported.");
                 case NOMENCLATURES -> throw new IllegalArgumentException("Invalid type: NOMENCLATURES is not supported.");
             }) {
            saxonService.transformFods2XML(inputStream, outputStream, xsl);
        } catch (Exception e) {
            String errorMessage = "An error was occurred during the operations fods2xml transformation. "
                    + e.getMessage();
            logger.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        logger.info("End of extract pearl {}", type);

        return outputFile;
    }
}
