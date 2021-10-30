package mohamedBenkhalfa1877541.StudentStacker.application;

import android.graphics.Bitmap;

public class Stage {

    private String etudiantName,entrepriseName,entrepriseAdresse,priorite;
    private Bitmap imageStudent;
    Stage(){

    }

    public Stage(String etudiantId, String entrepriseId, String priorite) {
        this.etudiantName = etudiantId;
        this.entrepriseName = entrepriseId;
        this.priorite = priorite;
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
}
