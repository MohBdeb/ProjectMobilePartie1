package mohamedBenkhalfa1877541.StudentStacker.application;

public class Visite {
    private String nom, stade_id, date, heure_debut, duree;

    public Visite() {

    }

    public Visite(String nom, String stage_id, String date, String heure_debut, String duree) {
        this.stade_id = stage_id;
        this.nom = nom;
        this.date = date;
        this.heure_debut = heure_debut;
        this.duree = duree;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getStade_id() {
        return stade_id;
    }

    public void setStade_id(String stade_id) {
        this.stade_id = stade_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure_debut() {
        return heure_debut;
    }

    public void setHeure_debut(String heure_debut) {
        this.heure_debut = heure_debut;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }
}
