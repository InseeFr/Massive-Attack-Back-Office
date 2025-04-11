package fr.insee.sabianedata.ws.service.xsl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.sabianedata.ws.utils.ExtractionType;
import fr.insee.sabianedata.ws.utils.InputStreamUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueenCampaignTransformer {

    private static final Logger logger = LoggerFactory.getLogger(QueenCampaignTransformer.class);
    private static final XslTransformation saxonService = new XslTransformation();
    public static final String QUEEN_EXTRACT_CAMPAIGN =  "/xslt/queen-extract-campaign.xsl";
    public static final String QUEEN_EXTRACT_QUESTIONNAIRE =  "/xslt/queen-extract-questionnaire-models.xsl";
    public static final String QUEEN_EXTRACT_SURVEY_UNITS =  "/xslt/queen-extract-survey-units.xsl";
    public static final String QUEEN_EXTRACT_NOMENCLATURES =  "/xslt/queen-extract-nomenclatures.xsl";


    public File extractCampaign(File input) throws Exception {
        return extract(input, ExtractionType.CAMPAIGN);
    }

    public File extractQuestionnaireModels(File input) throws Exception {
        return extract(input, ExtractionType.QUESTIONNAIRE_MODELS);
    }

    public File extractSurveyUnits(File input) throws Exception {
        return extract(input, ExtractionType.SURVEY_UNITS);
    }

    public File extractNomenclatures(File input) throws Exception {
        return extract(input, ExtractionType.NOMENCLATURES);
    }

    public File extract(File input, ExtractionType type) throws Exception {
        File outputFile = new File(input.getParent(),type + ".xml");
        logger.debug("Output folder : {}", outputFile.getAbsolutePath());

        InputStream inputStream = FileUtils.openInputStream(input);
        OutputStream outputStream = FileUtils.openOutputStream(outputFile);

        InputStream xsl = switch (type) {
            case CAMPAIGN -> InputStreamUtil.getInputStreamFromPath(QUEEN_EXTRACT_CAMPAIGN);
            case QUESTIONNAIRE_MODELS ->
                    InputStreamUtil.getInputStreamFromPath(QUEEN_EXTRACT_QUESTIONNAIRE);
            case SURVEY_UNITS -> InputStreamUtil.getInputStreamFromPath(QUEEN_EXTRACT_SURVEY_UNITS);
            case NOMENCLATURES -> InputStreamUtil.getInputStreamFromPath(QUEEN_EXTRACT_NOMENCLATURES);
            case ASSIGNMENT -> throw new IllegalArgumentException("Invalid type: ASSIGNMENT is not supported.");
        };

        try {
            saxonService.transformFods2XML(inputStream, outputStream, xsl);
        } catch (Exception e) {
            String errorMessage = "An error occurred during the operations fods2xml transformation.";
            logger.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        inputStream.close();
        outputStream.close();
        xsl.close();
        logger.info("End of extract queen {}", type);

        return outputFile;
    }
}
