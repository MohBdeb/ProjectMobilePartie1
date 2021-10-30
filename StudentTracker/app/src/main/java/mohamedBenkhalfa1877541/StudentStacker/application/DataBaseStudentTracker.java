package mohamedBenkhalfa1877541.StudentStacker.application;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class DataBaseStudentTracker extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "StudentTracker.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseStudentTracker(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase Database) {
        String queryTableEntreprise = "CREATE TABLE entreprise (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, nom VARCHAR(255), " +
                "adresse VARCHAR(255), " +
                "ville VARCHAR(255), " +
                "province VARCHAR(255), " +
                "cp VARCHAR(7));";

        String queryTableCompte = "CREATE TABLE compte (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "created_at DATETIME, " +
                "deleted_at DATETIME, " +
                "email VARCHAR(255), " +
                "est_actif BIT(1), " +
                "mot_passe VARCHAR(255), " +
                "nom VARCHAR(255), " +
                "prenom VARCHAR(255), " +
                "photo BLOB, " +
                "updated_at DATETIME, " +
                "type_compte INT(11));";

        String queryTableStage = "CREATE TABLE stage (id VARCHAR PRIMARY KEY, annee_scolaire VARCHAR(255), priorite VARCHAR(255), entreprise_id INTEGER, etudiant_id INTEGER, professeur_id INTEGER," +
                "FOREIGN KEY (entreprise_id) REFERENCES entreprise (id), " +
                "FOREIGN KEY (etudiant_id) REFERENCES compte (id), " +
                "FOREIGN KEY (professeur_id) REFERENCES compte (id));";

        String queryTableVisite = "CREATE TABLE visite (id VARCHAR PRIMARY KEY, nom VARCHAR(255), stage_id VARCHAR(255), " +
                "date DATE, " +
                "heure_debut TIME, " +
                "duree INT(20), FOREIGN KEY (stage_id) REFERENCES stage(id));";

        Database.execSQL(queryTableEntreprise);
        Database.execSQL(queryTableCompte);
        Database.execSQL(queryTableStage);
        Database.execSQL(queryTableVisite);
        InsertionStudentOnCreate(Database);
        InsertionEntrepriseOnCreate(Database);
        addTeacher(Database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase Database, int i, int i1) {
        Database.execSQL("DROP TABLE IF EXISTS Stage");
        onCreate(Database);
    }

    /*Cette methode prend en paramettre le nom de l'etudiant et enleve le stage lie a cette etudiant
     */
    public void deleteStage(String etudiant_name){
        SQLiteDatabase db = this.getReadableDatabase();
        String getNameEtudiant = "SELECT id FROM compte WHERE nom='"+etudiant_name.split(" ")[0]+"'";
        Cursor cursor = db.rawQuery(getNameEtudiant,null);
        cursor = db.rawQuery(getNameEtudiant,null);
        cursor.moveToFirst();
        int etudiant_id = cursor.getInt(0);

        db = this.getWritableDatabase();
        String queryDeleteStage = "DELETE FROM stage WHERE etudiant_id='"+etudiant_id+"';";
        db.execSQL(queryDeleteStage);
    }

    public void addStudentImage(String studentName, Bitmap image){
        SQLiteDatabase db = this.getWritableDatabase();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imageStudent = Base64.encodeToString(byteArray, 0);
        String updateStageQuery = "UPDATE compte SET photo = '"+imageStudent+"' WHERE nom='"+studentName.split(" ")[0]+"'";
        db.execSQL(updateStageQuery);
    }

    /*Cette methode s'occupe de rajouter le prof a la base de donnee
     */
    @SuppressLint("NewApi")
    public void addTeacher(SQLiteDatabase db){
        int accountType = 1;
        ContentValues data = new ContentValues();
        data.put("created_at", String.valueOf(LocalDate.now()));
        data.put("deleted_at", "");
        data.put("email", "");
        data.put("est_actif", 0);
        data.put("mot_passe", "");
        data.put("nom", "Prades");
        data.put("prenom", "Pierre");
        //data.put("photo", "test");
        data.put("updated_at", String.valueOf(LocalDate.now()));
        data.put("type_compte", accountType);
        db.insert("compte",null, data);
    }

    /*Cette methode s'occupe de rajouter un stage avec les valeur passe en parametre comme le nom le nom de l'entreprise et ca priorite
     */
    public void addStage(String StudentName,String entreprise,String priorite){
        String idStudentQuery = "SELECT id FROM compte WHERE nom='"+StudentName.split(" ")+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String addStageQuery = "INSERT INTO stage (etudiant_id, entreprise_id,id,priorite) " +
                "VALUES((SELECT id from compte WHERE nom='"+StudentName.split(" ")[0]+"')," +
                "(SELECT id from entreprise WHERE nom='"+entreprise+"'),'"+uuid+"' , '"+priorite+"');";
        db.execSQL(addStageQuery);
    }

    /*Cette methode s'occupe de mettre a jour le stage que l'etudiant a choisi. Il prend en parametre
    le nom de l'etudiant, le nom de l'entreprise ainsi que la priorite du stage
     */
    public void updateStage(String etudiant_name,String entreprise_name,String priorite){
        SQLiteDatabase db = this.getReadableDatabase();
        String getIdEntreprise = "SELECT id FROM entreprise WHERE nom='"+entreprise_name+"'";
        Cursor cursor = db.rawQuery(getIdEntreprise,null);
        cursor.moveToFirst();
        int entreprise_id = cursor.getInt(0);

        String getNameEtudiant = "SELECT id FROM compte WHERE nom='"+etudiant_name.split(" ")[0]+"'";
        cursor = db.rawQuery(getNameEtudiant,null);
        cursor.moveToFirst();
        int etudiant_id = cursor.getInt(0);

        db = this.getWritableDatabase();
        String updateStageQuery = "UPDATE stage SET entreprise_id = '"+entreprise_id+"',priorite = '"+priorite+"' WHERE etudiant_id = '"+etudiant_id+"';";
        db.execSQL(updateStageQuery);
    }

    //Retourne un Cursor contenant le row complet de l'entreprise que l'etudiant veut avec le String nom passe en parametre
    public Cursor getEntrepriseCursor(String nom){
        String query = "SELECT * FROM entreprise WHERE nom='"+nom+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        return cursor;
    }
    //Retourne un ArrayList contenant le nom de toutes les etudiants
    public ArrayList<String> getStudentNames(){
        ArrayList<String> listStudentNames = new ArrayList<String>();
        String query = "SELECT * FROM compte WHERE type_compte='2'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(query,null);
        listStudentNames.add("Default");
        for(int i=0 ; i < cursor.getCount(); i++) {
            if(db != null){
                if(cursor.moveToNext())
                    listStudentNames.add(cursor.getString(6)+" "+cursor.getString(7));
            }
        }
        cursor.close();
        return listStudentNames;
    }

    //Retourne un ArrayList contenant le nom de toutes les entreprises
    public ArrayList<String> getEntrepriseNames(){
        ArrayList<String> listEntrepriseNames = new ArrayList<String>();
        String query = "SELECT * FROM entreprise";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(query,null);
        listEntrepriseNames.add("Default");
        for(int i=0 ; i < cursor.getCount(); i++) {
            if(db != null){
                if(cursor.moveToNext())
                    listEntrepriseNames.add(cursor.getString(1));
            }
        }
        cursor.close();
        return listEntrepriseNames;
    }

    /*Cette methode s'occupe de lire toutes la table de stage et de les retourner dans un Array de Arrayliste contenant
    le nom de l'etudiant, le nom de l'entreprise de son stage, l'addresse de l'entreprise de son stage ainsi que la priorite de
    celui-ci
     */
    ArrayList<Stage> readAllData(){
        ArrayList<Stage> tabInfo = new ArrayList<Stage>();
        Cursor cursorStage = null;
        Cursor cursorInfoStudent = null;
        Cursor cursorInfoEntreprise = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String stageQuery = "SELECT * FROM stage";
        String getStudentNameQuery = "SELECT nom, prenom, photo FROM stage INNER JOIN compte ON stage.etudiant_id = compte.id;";
        String getEntrepriseQuery = "SELECT nom, adresse FROM stage INNER JOIN entreprise ON stage.entreprise_id = entreprise.id;";

        cursorStage = db.rawQuery(stageQuery,null);
        if(cursorStage != null){
            cursorInfoStudent = db.rawQuery(getStudentNameQuery,null);
            cursorInfoEntreprise = db.rawQuery(getEntrepriseQuery,null);
            for (int i=0 ; i<cursorStage.getCount();i++){
                if (cursorStage.moveToNext()) {
                    cursorInfoStudent.moveToNext();
                    cursorInfoEntreprise.moveToNext();
                    Stage stage = new Stage();
                    stage.setEtudiantName(cursorInfoStudent.getString(0)+" "+cursorInfoStudent.getString(1));
                    stage.setEntrepriseName(cursorInfoEntreprise.getString(0));
                    stage.setEntrepriseAdresse(cursorInfoEntreprise.getString(1));
                    stage.setPriorite(cursorStage.getString(2));
                    String imageBlob = new String( cursorInfoStudent.getBlob(2));
                    byte[] byteArray = Base64.decode(imageBlob, Base64.DEFAULT);
                    Bitmap imageStudent =  BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    stage.setImageStudent(imageStudent);
                    tabInfo.add(stage);
                }
            }
        }
        cursorStage.close();
        cursorInfoStudent.close();
        cursorInfoEntreprise.close();
        return tabInfo;
    }

    /*S'occupe d'inserer toutes les etudiants qui ont ete donner par le proffeseur dans la bd
     */
    @SuppressLint("NewApi")
    public void InsertionStudentOnCreate(SQLiteDatabase db){
        int accountType = 2;
        ContentValues data = new ContentValues();
        ArrayList<String> StudentName = new ArrayList<String>(){
            {add("Mikaël Boucher");add("Thomas Caron");add("Simon Gingras");
             add("Kevin Leblanc");add("Cédric Masson");add("Vanessa Monette");
             add("Vincent Picard");add("Mélissa Poulain");add("Diego Vargas");
             add("Geneviève Tremblay");}
        };
        for(int i=0 ; i < StudentName.size(); i++){
            data.put("created_at", String.valueOf(LocalDate.now()));
            data.put("deleted_at", "");
            data.put("email", "");
            data.put("est_actif", 0);
            data.put("mot_passe", "");
            data.put("nom", StudentName.get(i).split(" ")[1]);
            data.put("prenom", StudentName.get(i).split(" ")[0]);
            //data.put("photo", "test");
            data.put("updated_at", String.valueOf(LocalDate.now()));
            data.put("type_compte", accountType);
            db.insert("compte",null, data);
        }
    }

    /*S'occupe d'inserer toutes les entreprises qui ont ete donner par le proffeseur dans la bd
     */
    public void InsertionEntrepriseOnCreate(SQLiteDatabase db) {
        ArrayList<String> EntrepriseName = new ArrayList<String>(){
            {add("Jean Coutu/4885 Henri-Bourassa Blvd W #731, Montreal, Quebec H3L 1P3");
                add("Garage/Tremblay-10142 Boul. Saint-Laurent, Montréal, QC H3L 2N7");
                add("Pharmaprix/3611 Rue Jarry E, Montréal, QC H1Z 2G1");
                add("Alimentation Générale/1853 Chem. Rockland, Mont-Royal, QC H3P 2Y7");
                add("Auto Repair/8490 Rue Saint-Dominique, Montréal, QC H2P 2L5");
                add("Subway/775 Rue Chabanel O, Montréal, QC H4N 3J7");
                add("Métro/1331 Blvd. de la Côte-Vertu, Saint-Laurent, QC H4L 1Z1");
                add("Épicerie les Jardinières/10345 Ave Christophe-Colomb, Montreal, QC H2C 2V1");
                add("Boucherie Marien/1499-1415 Rue Jarry E, Montréal, QC");
                add("IGA/8921 Rue Lajeunesse, Montréal, QC H2M 1S1");}
        };
        ContentValues data = new ContentValues();
        for(int i=0 ; i < EntrepriseName.size(); i++) {
            data.put("nom", EntrepriseName.get(i).split("/")[0]);
            data.put("adresse", EntrepriseName.get(i).split("/")[1]);
            data.put("ville", "Montreal");
            data.put("province", "Quebec");
            db.insert("entreprise",null, data);
            data.clear();
        }
    }



}
