package mohamedBenkhalfa1877541.StudentStacker.application;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

public class Stage {

    private String id;
    private String etudiantName;
    private String entrepriseName;
    private String entrepriseAdresse;
    private String priorite;
    private String journee;
    private String heureStageDebut;
    private String HeureStageFin;
    private String HeurePauseDebut;
    private String HeurePauseFin;
    private String DureeVisite;
    private String commentaire;
    private Bitmap imageStudent;

    Stage() {

    }

    public Stage(String etudiantId, String entrepriseId, String priorite) {
        this.etudiantName = etudiantId;
        this.entrepriseName = entrepriseId;
        this.priorite = priorite;
    }

    public Stage(JSONObject object) throws JSONException {
        this.etudiantName = object.getJSONObject("etudiant").getString("nom") + object.getJSONObject("etudiant").getString("prenom");
        this.entrepriseName = object.getJSONObject("entreprise").getString("nom");
        this.entrepriseAdresse = object.getJSONObject("entreprise").getString("adresse");
        this.priorite = object.getString("priorite");

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeureStageDebut() {
        return heureStageDebut;
    }

    public void setHeureStageDebut(String heureStageDebut) {
        this.heureStageDebut = heureStageDebut;
    }

    public String getHeureStageFin() {
        return HeureStageFin;
    }

    public void setHeureStageFin(String heureStageFin) {
        HeureStageFin = heureStageFin;
    }

    public String getHeurePauseDebut() {
        return HeurePauseDebut;
    }

    public void setHeurePauseDebut(String heurePauseDebut) {
        HeurePauseDebut = heurePauseDebut;
    }

    public String getHeurePauseFin() {
        return HeurePauseFin;
    }

    public void setHeurePauseFin(String heurePauseFin) {
        HeurePauseFin = heurePauseFin;
    }

    public String getDureeVisite() {
        return DureeVisite;
    }

    public void setDureeVisite(String dureeVisite) {
        DureeVisite = dureeVisite;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }


    public String getEtudiantName() {
        return etudiantName;
    }

    public void setEtudiantName(String etudiantName) {
        this.etudiantName = etudiantName;
    }

    public String getEntrepriseName() {
        return entrepriseName;
    }

    public void setEntrepriseName(String entrepriseName) {
        this.entrepriseName = entrepriseName;
    }

    public String getEntrepriseAdresse() {
        return entrepriseAdresse;
    }

    public void setEntrepriseAdresse(String entrepriseAdresse) {
        this.entrepriseAdresse = entrepriseAdresse;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public Bitmap getImageStudent() {
        return imageStudent;
    }

    public void setImageStudent(Bitmap imageStudent) {
        this.imageStudent = imageStudent;
    }

    public String getJournee() {
        return journee;
    }

    public void setJournee(String journee) {
        this.journee = journee;
    }

}
