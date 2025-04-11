package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;

@Getter
@Setter
public class NomenclatureDto extends Nomenclature {

    private JsonNode value;
    private static final String NOMENCLATURES = "nomenclatures";

    public NomenclatureDto(Nomenclature nomenclature, Path folderPath) {
        super(nomenclature.getId(), nomenclature.getLabel());

        Path nomenclatureFilePath = folderPath
                .resolve(NOMENCLATURES)
                .resolve(nomenclature.getFileName());
        
        File nomenclatureFile = nomenclatureFilePath.toFile();
        this.value = JsonFileToJsonNode.getJsonNodeFromFile(nomenclatureFile);
    }

}
