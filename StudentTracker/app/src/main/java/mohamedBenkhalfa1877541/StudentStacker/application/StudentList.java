package mohamedBenkhalfa1877541.StudentStacker.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudentList  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout menu;
    private Toolbar toolbar;
    RecyclerView recyclerView;
    private FloatingActionButton addStudent;
    private ImageView GreenPriority,YellowPriority,RedPriority;
    private DataBaseStudentTracker myDB;
    ArrayList<Stage> listStage;
    private ArrayList<String> nameStudent,nameEntreprise,adressEntreprise,studentStatuts;
    private ArrayList<Bitmap> studentImage;
    private Boolean greenActif =true,yellowActif =true,redActif =true;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GreenPriority = findViewById(R.id.GreenPriority);
        YellowPriority = findViewById(R.id.YellowPriority);
        RedPriority =  findViewById(R.id.RedPriority);

        menu = findViewById(R.id.side_menu);
        NavigationView navigationView = findViewById(R.id.side_menu_viewer);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        //Integration du menu slide present dans la liste
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,menu,toolbar,R.string.side_menu_open,R.string.side_menu_close);
        menu.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.studentList);
        addStudent = findViewById(R.id.addStudent);
        myDB = new DataBaseStudentTracker(StudentList.this);

        nameStudent = new ArrayList<>();
        nameEntreprise = new ArrayList<>();
        adressEntreprise = new ArrayList<>();
        studentStatuts = new ArrayList<>();
        studentImage =  new ArrayList<>();

        StockerDataTableau();

        customAdapter = new CustomAdapter(StudentList.this, nameStudent, nameEntreprise, adressEntreprise,studentStatuts,studentImage);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentList.this));
        recyclerView.setAdapter(customAdapter);

        /*Comportement pour les priorite des stage. S'occupe de changer l'affichage des bouttons de priorite et
        de changer le statuts pour la liste. Appelle aussi les methode pour mettre a jour le recyclerview pour les stages suite
        au changement de priorite
         */
        GreenPriority.setOnClickListener(new View.OnClickListener() {
            int active = 0;
            @Override
            public void onClick(View view) {
                if(active == 0){
                    GreenPriority.setColorFilter(Color.parseColor("#FFFFFF"));
                    GreenPriority.setBackgroundColor( Color.parseColor("#00BA19"));
                    greenActif = false;
                    active++;
                    resetRecyclerView();
                    StockerDataTableau();
                }else{
                    GreenPriority.setColorFilter(Color.parseColor("#00BA19"));
                    GreenPriority.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    greenActif = true;
                    active = 0;
                    resetRecyclerView();
                    StockerDataTableau();
                }
            }
        });

        YellowPriority.setOnClickListener(new View.OnClickListener() {
            int active = 0;
            @Override
            public void onClick(View view) {
                if(active == 0){
                    YellowPriority.setColorFilter(Color.parseColor("#FFFFFF"));
                    YellowPriority.setBackgroundColor( Color.parseColor("#EFDB27"));
                    yellowActif = false;
                    active++;
                    resetRecyclerView();
                    StockerDataTableau();
                }else{
                    YellowPriority.setColorFilter(Color.parseColor("#EFDB27"));
                    YellowPriority.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    yellowActif = true;
                    active = 0;
                    resetRecyclerView();
                    StockerDataTableau();
                }
            }
        });

        RedPriority.setOnClickListener(new View.OnClickListener() {
            int active = 0;
            @Override
            public void onClick(View view) {
                if(active == 0){
                    RedPriority.setColorFilter(Color.parseColor("#FFFFFF"));
                    RedPriority.setBackgroundColor( Color.parseColor("#FFF44336"));
                    redActif = false;
                    active++;
                    resetRecyclerView();
                    StockerDataTableau();
                }else{
                    RedPriority.setColorFilter(Color.parseColor("#FFF44336"));
                    RedPriority.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    redActif = true;
                    active = 0;
                    resetRecyclerView();
                    StockerDataTableau();
                }
            }
        });

        //Comportement du boutton addStudent qui redirige le user vers l'activite qui s'occupe de la creation de nouveau stage
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentList.this, StudentInfoForm.class);
                startActivity(intent);
            }
        });


    }

    /*S'occupe de vider la liste des stages ainsi que les arrays pour le recyclerview. La methode
    est principalement appeler pour mettre a jour le recyclerview lorsque l'utilisateur decide d'afficher certain stage
    selon leur priorite. Cela permet de ne pas recreate toute l'activite a chaque fois
     */
    private void resetRecyclerView(){
        listStage.clear();
        nameStudent.clear();
        nameEntreprise.clear();
        adressEntreprise.clear();
        studentStatuts.clear();
        customAdapter.notifyDataSetChanged();
    }

    /*S'occupe de remplir les differents array avec les stages presents. Si la table stage est vide, les arrays seront vide
    aussi et s'il y a des stages, le stages remplira les differents array pour les information qui seront ensuite envoyer au
    customAdapter pour creer le recyclerview
     */
    private void StockerDataTableau(){
        listStage = myDB.readAllData();
        Collections.sort(listStage, StudentList.StageNameComparator);
        if(listStage.size() > 0){
            for(int i =0;i < listStage.size();i++){
                if(greenActif && listStage.get(i).getPriorite().equals("#00BA19")){
                    stageToList(listStage.get(i));
                }else if(yellowActif && listStage.get(i).getPriorite().equals("#EFDB27")){
                    stageToList(listStage.get(i));
                }else if(redActif && listStage.get(i).getPriorite().equals("#FFF44336")){
                    stageToList(listStage.get(i));
                }
            }
        }

    }

    public void stageToList(Stage stage){
        nameStudent.add(stage.getEtudiantName());
        nameEntreprise.add(stage.getEntrepriseName());
        adressEntreprise.add(stage.getEntrepriseAdresse());
        studentStatuts.add(stage.getPriorite());
        studentImage.add(stage.getImageStudent());
    }

    //S'occupe du comportement pour fermer le menu
    @Override
    public void onBackPressed() {
        if(menu.isDrawerOpen(GravityCompat.START)){
            menu.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    //S'occupe du comportement des item dans le menu de l'activite pour les dirigers vers les deux activite (GoogleMap,StudentList)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.listeEleveMenu:
                menu.closeDrawer(GravityCompat.START);
                break;

            case R.id.googleMapMenu:
                Intent intent = new Intent(StudentList.this, StudentMap.class);
                startActivity(intent);
                break;
        }
        menu.closeDrawer(GravityCompat.START);
        return true;
    }


    public static Comparator<Stage> StageNameComparator = new Comparator<Stage>() {

        public int compare(Stage s1, Stage s2) {
            String EtudiantName1 = s1.getEtudiantName().toUpperCase();
            String EtudiantName2 = s2.getEtudiantName().toUpperCase();

            //ascending order
            return EtudiantName1.compareTo(EtudiantName2);

            //descending order
            //return EtudiantName2.compareTo(EtudiantName1);
        }};
}
