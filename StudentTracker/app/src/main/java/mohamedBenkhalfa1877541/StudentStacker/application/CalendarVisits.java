package mohamedBenkhalfa1877541.StudentStacker.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jonaswanke.calendar.BaseEvent;
import com.jonaswanke.calendar.CalendarView;
import com.jonaswanke.calendar.Event;
import com.jonaswanke.calendar.utils.Day;
import com.jonaswanke.calendar.utils.Week;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CalendarVisits extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private DrawerLayout menu;
    private FloatingActionButton addEvent;
    private Spinner students;
    private Toolbar toolbar;
    private Dialog ChoixJourPopUp, CreationVisite;
    private RadioGroup radioGroup, radioGroupCreationVisite;
    private DataBaseStudentTracker myDB;
    private ArrayList<Stage> listeStageVisite = new ArrayList<Stage>(), listeStage = new ArrayList<Stage>();
    private CalendarView calendar;
    private Week currentWeek = new Week();
    private Day currentDay = new Day();
    private ArrayList<Event> listeVisiteEvent = new ArrayList<Event>();
    private String choixJour, StudentName = "";
    private long journeeDebut = 0;
    boolean visiteStudentRequirement = false, visiteJourneeRequirement = false;
    private static final long distanceEntreStage = 3600000;
    private ArrayList<Visite> listeVisite = new ArrayList<Visite>();
    private ArrayList<String> nomStageActuelle = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_visits);
        toolbar = findViewById(R.id.toolbar);
        menu = findViewById(R.id.side_menu);
        NavigationView navigationView = findViewById(R.id.side_menu_viewer_calendarView);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        myDB = new DataBaseStudentTracker(CalendarVisits.this);
        //calendar = findViewById(R.id.calendar);
        addEvent = findViewById(R.id.addEvent);
        ChoixJourPopUp = new Dialog(this);
        CreationVisite = new Dialog(this);
        ChoixJourPopUp.setContentView(R.layout.popout_question_day);
        CreationVisite.setContentView(R.layout.popout_creation_visite);
        radioGroup = ChoixJourPopUp.findViewById(R.id.GrpChoixJour);
        radioGroupCreationVisite = CreationVisite.findViewById(R.id.VisiteGrpChoixJour);
        listeStage = myDB.readAllDataStage();
        nomStageActuelle.add("Default");
        for (int i = 0; i < listeStage.size(); i++) {
            nomStageActuelle.add(listeStage.get(i).getEtudiantName());
        }

        students = CreationVisite.findViewById(R.id.StudentsSpinnerVisite);
        //Spinner Students setup
        ArrayAdapter adapterStudent = new ArrayAdapter(CreationVisite.getContext(), android.R.layout.simple_spinner_item, nomStageActuelle);
        adapterStudent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        students.setAdapter(adapterStudent);


        //Integration du menu slide present dans la liste
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, menu, toolbar, R.string.side_menu_open, R.string.side_menu_close);
        menu.addDrawerListener(toggle);
        toggle.syncState();

        //S'occupe de l'affichage ainsi que de la logique lorsque l'utilisateur veut rajouter une visite
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Stage[] stage = new Stage[1];
                TextView close,confirmStudent, choixHeure;
                Button btnConfirm;
                close = CreationVisite.findViewById(R.id.ClosePopUp);
                choixHeure = CreationVisite.findViewById(R.id.heureVisiteDebut);
                confirmStudent = CreationVisite.findViewById(R.id.confirmEtudiant);
                btnConfirm = CreationVisite.findViewById(R.id.confirmVisite);
                choixHeure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TimePicker(choixHeure);
                    }
                });
                confirmStudent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StudentName = students.getSelectedItem().toString();
                        if (!StudentName.equals("Default")) {
                            for (int i = 0; i < listeStage.size(); i++) {
                                if (StudentName.equals(listeStage.get(i).getEtudiantName())) {
                                    stage[0] = listeStage.get(i);
                                    JourDisponible(stage[0].getJournee(),CreationVisite);
                                    visiteStudentRequirement = true;
                                }
                            }
                        } else {
                            Toast.makeText(CalendarVisits.this, "Il faut selectionner un eleve", Toast.LENGTH_LONG);
                        }
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CreationVisite.dismiss();
                    }
                });
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(visiteStudentRequirement && visiteJourneeRequirement){
                            creationEvent(stage[0], choixHeure.getText().toString());
                            CreationVisite.dismiss();
                        }

                    }
                });
                CreationVisite.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                CreationVisite.show();
            }
        });

        //S'occupe de selectionner le jour que l'utilisateur a selectionner pour les visite suite a la selection de stage
        //dans la map
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int idButton = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(idButton);
                Button confirm;
                confirm = ChoixJourPopUp.findViewById(R.id.SelectChoixDay);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choixJour = radioButton.getText().toString();
                        setJourneeChoix();
                        creationEventIntent();
                        ChoixJourPopUp.dismiss();
                    }
                });
            }
        });

        //S'occupe de selectionner le jour que l'utilisateur a selectionner pour les visite lors de la creation de visite
        radioGroupCreationVisite.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int idButton = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(idButton);
                choixJour = radioButton.getText().toString();
                visiteJourneeRequirement =true;
                setJourneeChoix();
            }
        });

    }


    //S'occupe de l'interaction avec les items du menu qui se trouve du cote gauche de l'application
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.listeEleveMenu:
                intent = new Intent(CalendarVisits.this, StudentList.class);
                startActivity(intent);
                break;
            case R.id.googleMapMenu:
                intent = new Intent(CalendarVisits.this, StudentMap.class);
                startActivity(intent);
                break;
            case R.id.calendarVisitMenu:
                menu.closeDrawer(GravityCompat.START);
                break;
        }
        menu.closeDrawer(GravityCompat.START);
        return true;
    }

    /*Methode qui s'occupe de faire afficher le popup suite a la selection de stage dans l'activite googleMap.
    Elle permet a l'utilisateur de choisir la journee pour l'ajout de visite
     */
    private void getAndSetIntentData() {
        TextView close;
        close = ChoixJourPopUp.findViewById(R.id.ClosePopUp);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoixJourPopUp.dismiss();
            }
        });
        ChoixJourPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ChoixJourPopUp.show();
        JourDisponible("Lundi Mardi Mercredi Jeudi Vendredi", ChoixJourPopUp);
        getIntentStage();
    }

    /*Cette methode s'occupe d'afficher les journee possible que l'utilisateur peut selectionner pour sa creation de
    visite selon les journee disponible
     */
    public void JourDisponible(String journee, Dialog dialog) {
        RadioButton lundi, mardi, mercredi, jeudi, vendredi;
        lundi = dialog.findViewById(R.id.Lundi);
        mardi = dialog.findViewById(R.id.Mardi);
        mercredi = dialog.findViewById(R.id.Mercredi);
        jeudi = dialog.findViewById(R.id.Jeudi);
        vendredi = dialog.findViewById(R.id.Vendredi);
        String[] listJournee = journee.split(" ");
        for (int i = 0; i < listJournee.length; i++) {
            switch (listJournee[i]) {
                case "Lundi":
                    lundi.setClickable(true);
                    lundi.setTextColor(Color.GREEN);
                    break;
                case "Mardi":
                    mardi.setClickable(true);
                    mardi.setTextColor(Color.GREEN);
                    break;
                case "Mercredi":
                    mercredi.setClickable(true);
                    mercredi.setTextColor(Color.GREEN);
                    break;
                case "Jeudi":
                    jeudi.setClickable(true);
                    jeudi.setTextColor(Color.GREEN);
                    break;
                case "Vendredi":
                    vendredi.setClickable(true);
                    vendredi.setTextColor(Color.GREEN);
                    break;
            }
        }
    }

    /*Cette methode s'occupe d'inserer les evenement present dans la liste listeVisiteEvent afin de les insérer dans le
    calendrier
     */
    public void insertionEvent() {
        calendar.setEventsForWeek(currentWeek, listeVisiteEvent);
    }

    /*Methode qui s'occupe de set la journee pour l'ajout des visites selon la journee que l'utilisateur a selectionner
     */
    public void setJourneeChoix() {
        switch (choixJour) {
            case "Lundi":
                journeeDebut = currentWeek.getFirstDay().getNextDay().getStart();
                break;
            case "Mardi":
                journeeDebut = currentWeek.getFirstDay().getNextDay().getNextDay().getStart();
                break;
            case "Mercredi":
                journeeDebut = currentWeek.getFirstDay().getNextDay().getNextDay().getNextDay().getStart();
                break;
            case "Jeudi":
                journeeDebut = currentWeek.getFirstDay().getNextDay().getNextDay().getNextDay().getNextDay().getStart();
                break;
            case "Vendredi":
                journeeDebut = currentWeek.getFirstDay().getNextDay().getNextDay().getNextDay().getNextDay().getNextDay().getStart();
                break;
        }
    }

    /* Methode qui s'occupe de rajouter les stages selectionner dans l'activite de google map dans une liste
    de stage
     */
    public void getIntentStage() {
        ArrayList<Stage> templist = myDB.readAllDataStage();
        int taille = getIntent().getIntExtra("taille", 0);
        for (int i = 0; i < templist.size(); i++) {
            for (int j = 0; j < taille; j++) {
                String nom = getIntent().getStringExtra("studentName " + j);
                if (templist.get(i).getEtudiantName().equals(nom)) {
                    listeStageVisite.add(templist.get(i));
                }
            }
        }

    }

    /*Debut de la methode qui devait s'ocuper de verifier si la creation de l'evenement
    respect les horaires de l'étudiant concernée
    * */
    public long verificationRespectHeure(Stage stage, String heure) {
        long time = TimeConverter(heure);
        if (TimeConverter(stage.getHeureStageDebut()) > TimeConverter(heure)) {
            time = TimeConverter(stage.getHeureStageDebut());
        }

        return time;
    }

    /*Methode qui s'occupe de créer un evenement avec le popup qui apparait lorsque l'utilisateur click sur le bouton
    creation de visite
    * */
    public void creationEvent(Stage stage, String debutHeureStage) {
        long heureDebut = TimeConverter(debutHeureStage);
        long duree = 0;
        //heureDebut = verificationRespectHeure(stage, debutHeureStage);

        DateFormat dateFormat = new SimpleDateFormat("mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String myDate = stage.getDureeVisite();
        try {
            Date date = dateFormat.parse(myDate);
            duree = date.getTime() / 1L;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date d = new Date((journeeDebut + heureDebut) * 1000);
        myDB.addVisite(stage.getEtudiantName(), d.toString(), debutHeureStage, stage.getDureeVisite());
        BaseEvent event = new BaseEvent(stage.getEtudiantName(), "Visite", Color.BLUE, journeeDebut + heureDebut, journeeDebut + heureDebut + duree, false);
        listeVisiteEvent.add(event);
        insertionEvent();
    }

    /*S'occupe de la creation des evenements lorsque l'utilisateur avait selectione auparavant des stages via la
    selection sur le google map. Cette methode rajouteras les stages selectione l'un apres l'autre.
    * */
    public void creationEventIntent() {
        long heureDebut = 0;
        long heureFin = 0;
        String HeureStart = "13:30";
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = dateFormat.parse(HeureStart);
            heureDebut = date.getTime() / 1L;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String HeureEnd = "15:30";
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = dateFormat.parse(HeureEnd);
            heureFin = date.getTime() / 1L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < listeStageVisite.size(); i++) {
            String nom = getIntent().getStringExtra("studentName " + i);
            Date d = new Date((journeeDebut + heureDebut) * 1000);
            myDB.addVisite(nom, d.toString(), HeureStart, listeStageVisite.get(i).getDureeVisite());
           // BaseEvent event = new BaseEvent(nom, listeStageVisite.get(i).getCommentaire(), Color.BLUE, journeeDebut + heureDebut, journeeDebut + heureFin, false);
           // listeVisiteEvent.add(event);
            heureDebut = heureFin + distanceEntreStage;
            long milliseconds = 0;
            String myDate = listeStageVisite.get(i).getDureeVisite().split(" ")[0];
            dateFormat = new SimpleDateFormat("mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = dateFormat.parse(myDate);
                milliseconds = date.getTime() / 1L;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            heureFin = heureDebut + 2700000;

        }
        insertionEvent();

    }

    //S'occupe de remplir les form automatiquement avec les information lie a l'entreprise et s'occupe de mettre le nom de l'etudiant selectionne
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.StudentsSpinner:
                StudentName = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(this, "Student", Toast.LENGTH_LONG).show();
                break;
        }
    }

    //S'occupe de mettre les valeurs default lorsque l'activite s'ouvre pour la premiere fois
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        switch (adapterView.getId()) {
            case R.id.StudentsSpinner:
                String StudentName = adapterView.getItemAtPosition(0).toString();
                Toast.makeText(this, StudentName, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /*Methode qui s'occupe de gerer l'affichage ainsi que la selection de l'heure pour le rajout d'une visite.
     */
    public void TimePicker(TextView section) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                CalendarVisits.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                String time = hour + ":" + min;
                SimpleDateFormat t24hours = new SimpleDateFormat("HH:mm");
                try {
                    Date date = t24hours.parse(time);
                    section.setText(t24hours.format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 12, 0, false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
        timePickerDialog.show();
    }

    //Methode qui permet de convertir un temps (String) en une valeur de temps en long
    public long TimeConverter(String heure) {
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = dateFormat.parse(heure);
            time = date.getTime() / 1L;
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

}
