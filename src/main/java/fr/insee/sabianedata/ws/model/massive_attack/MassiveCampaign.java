package fr.insee.sabianedata.ws.model.massive_attack;

import fr.insee.sabianedata.ws.model.pearl.Assignment;
import fr.insee.sabianedata.ws.model.pearl.PearlCampaign;
import fr.insee.sabianedata.ws.model.queen.QueenCampaign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor()
public class MassiveCampaign {

    private PearlCampaign pearlCampaign;
    private QueenCampaign queenCampaign;
    private List<MassiveSurveyUnit>surveyUnits;
    private List<Assignment> assignments;

    public String getId(){
        return pearlCampaign.getCampaign();
    }

    public void updateCampaignsId(String newId){
        pearlCampaign.setCampaign(newId);
        queenCampaign.setId(newId);
    }

    public void updateLabel(String newLabel){
        pearlCampaign.setCampaignLabel(newLabel);
        queenCampaign.setLabel(newLabel);
    }

    public MassiveCampaign deepClone(){
        MassiveCampaign clone= new MassiveCampaign();
        clone.setPearlCampaign(pearlCampaign.deepClone());
        clone.setQueenCampaign(queenCampaign.deepClone());
        return clone;
    }
}
