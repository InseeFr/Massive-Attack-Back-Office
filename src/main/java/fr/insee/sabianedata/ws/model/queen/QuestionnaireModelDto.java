package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;

@Getter
@Setter
public class QuestionnaireModelDto extends QuestionnaireModel {

	private JsonNode value;
	private static final String QUESTIONNAIRE_MODELS = "questionnaireModels";

	public QuestionnaireModelDto(QuestionnaireModel questionnaireModel, Path folderPath) {
		super(questionnaireModel.getIdQuestionnaireModel(), questionnaireModel.getLabel(),
				questionnaireModel.getRequiredNomenclatureIds());

		Path questionnaireFilePath = folderPath
				.resolve(QUESTIONNAIRE_MODELS)
				.resolve(questionnaireModel.getFileName());

		File questionnaireFile = questionnaireFilePath.toFile();
		this.value = JsonFileToJsonNode.getJsonNodeFromFile(questionnaireFile);
	}

}
