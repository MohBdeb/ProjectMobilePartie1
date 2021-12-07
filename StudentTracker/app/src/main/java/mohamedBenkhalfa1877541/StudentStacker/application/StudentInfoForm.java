package mohamedBenkhalfa1877541.StudentStacker.application;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StudentInfoForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText EntrepriseNameForm,EntrepriseAdressForm,EntrepriseCityForm,EntrepriseProvinceForm,CommentaireStage;
    private TextView HeureStageDebut,HeureStageFin,HeurePauseDebut,HeurePauseFin;
    private String StudentName, EntrepriseName, Priorite="#00BA19",journee = "", dureeVisite,HeureStageD,HeureStageF,HeurePauseD,HeurePauseF;
    private Spinner students,entreprises;
    private CheckBox lundi,mardi,mercredi,jeudi,vendredi;
    private Bitmap ImageStudent;
    private Button addButton;
    private Boolean photoAjouter = false, heureAjouter = false, dureeVisiteAjouter = false,journeeAjouter = false;
    private FloatingActionButton addPicture;
    private ImageView StudentPicture,StudentStatus;
    private RadioGroup radioGroup;
    private String[] listejournee = {"null","null","null","null","null"};
    private ActivityResultLauncher<String> getContent;
    private DataBaseStudentTracker db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_info_form);
        db = new DataBaseStudentTracker(StudentInfoForm.this);

        EntrepriseNameForm = findViewById(R.id.EntrepriseNameForm);
        EntrepriseAdressForm = findViewById(R.id.EntrepriseAdressForm);
        EntrepriseCityForm = findViewById(R.id.EntrepriseCityForm);
        EntrepriseProvinceForm = findViewById(R.id.EntrepriseProvinceForm);
        CommentaireStage = findViewById(R.id.CommentaireStage);
        addButton = findViewById(R.id.button);
        addPicture = findViewById(R.id.addPicture);
        StudentPicture = findViewById(R.id.StudentPicture);
        StudentStatus = findViewById(R.id.StatutsStudent);
        HeureStageDebut = findViewById(R.id.HeureStageDebut);
        HeureStageFin = findViewById(R.id.HeureStageFin);
        HeurePauseDebut = findViewById(R.id.HeurePauseDebut);
        HeurePauseFin = findViewById(R.id.HeurePauseFin);
        radioGroup = findViewById(R.id.radioGroupDuree);
        lundi = findViewById(R.id.checkBoxLundi);
        mardi = findViewById(R.id.checkBoxMardi);
        mercredi = findViewById(R.id.checkBoxMercredi);
        jeudi = findViewById(R.id.checkBoxJeudi);
        vendredi = findViewById(R.id.checkBoxVendredi);

        //Spinner Students setup
        students = findViewById(R.id.StudentsSpinner);
        ArrayAdapter adapterStudent = new ArrayAdapter(this, android.R.layout.simple_spinner_item, db.getStudentNames());
        adapterStudent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        students.setAdapter(adapterStudent);
        students.setOnItemSelectedListener(this);

        //Spinner Entreprises setup
        entreprises = findViewById(R.id.EntreprisesSpinner);
        ArrayAdapter adapterEntreprise = new ArrayAdapter(this, android.R.layout.simple_spinner_item, db.getEntrepriseNames());
        adapterEntreprise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        entreprises.setAdapter(adapterEntreprise);
        entreprises.setOnItemSelectedListener(this);



        /*Rajoute un stage avec les informations que l'utilisateur a choisi et ramene le user a la liste de stage.
        S'assure aussi que l'utilisateur n'essaie pas de rajouter un stage avec un eleve ou une entreprise "Default"
         */
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificationAjoutHeure();
                VerificationAjoutJournee();
                if(!StudentName.equals("Default") && !EntrepriseName.equals("Default") && photoAjouter && heureAjouter && dureeVisiteAjouter && journeeAjouter){
                    if(dureeVisite.equals("1 heure")){
                        dureeVisite = "60 minutes";
                    }
                    db.addStage(StudentName,EntrepriseName,Priorite, CommentaireStage.getText().toString(), journee, dureeVisite, HeureStageD, HeureStageF, HeurePauseD, HeurePauseF);
                    db.addStudentImage(StudentName,ImageStudent);
                    Toast.makeText(StudentInfoForm.this, "Stage Ajouter", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StudentInfoForm.this, StudentList.class);
                    startActivity(intent);
                }else{
                    if (StudentName.equals("Default") && EntrepriseName.equals("Default") && photoAjouter && heureAjouter){
                        Toast.makeText(StudentInfoForm.this, "Le nom de l'eleve et de l'entreprise ne peuvent pas etre default", Toast.LENGTH_LONG).show();
                    }else{
                        if(StudentName.equals("Default")){
                            Toast.makeText(StudentInfoForm.this, "Le nom de l'eleve ne peut pas etre default", Toast.LENGTH_LONG).show();
                        }else if(EntrepriseName.equals("Default")){
                            Toast.makeText(StudentInfoForm.this, "Le nom de l'entreprise ne peut pas etre default", Toast.LENGTH_LONG).show();
                        }
                    }
                    if(photoAjouter == false){
                        Toast.makeText(StudentInfoForm.this, "Il faut inserer une photo", Toast.LENGTH_LONG).show();
                    }
                    if(heureAjouter ==  false){
                        Toast.makeText(StudentInfoForm.this, "Il faut remplir tous les horaires", Toast.LENGTH_LONG).show();
                    }
                    if(dureeVisiteAjouter ==  false){
                        Toast.makeText(StudentInfoForm.this, "Il faut selectionner une duree de visite", Toast.LENGTH_LONG).show();
                    }
                    if(journeeAjouter == false){
                        Toast.makeText(StudentInfoForm.this, "Il faut selectionner au moins 1 journee", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //S'occupe de recuperer la photo que l'utilisateur a choisi et la place sur l'imageView qui represente la photo du user
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                try {
                    ImageStudent = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),result);
                    photoAjouter = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StudentPicture.setImageURI(result);
            }
        });

        //S'occupe d'appeller la methode pour selectionner une image dans les images de l'utilisateurs lorsque celui ci
        //appuie sur le bouton
        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosePicture();
            }
        });

        /*S'occupe de changer la couleur ainsi que le statuts de l'evele. En cliquant sur l'image il traverse les trois
        couleurs et loops lorsqu'il arrive a la couleur/statut rouge
         */
        StudentStatus.setOnClickListener(new View.OnClickListener() {
            int cmpt = 0;
            @Override
            public void onClick(View view) {
                switch (cmpt){
                    case 0 :
                        StudentStatus.setColorFilter(Color.parseColor("#EFDB27"));
                        Priorite = "#EFDB27";
                        Toast.makeText(StudentInfoForm.this, "Priorité Moyenne", Toast.LENGTH_SHORT).show();
                        cmpt++;
                        break;
                    case 1:
                        StudentStatus.setColorFilter(Color.parseColor("#FFF44336"));
                        Priorite = "#FFF44336";
                        Toast.makeText(StudentInfoForm.this, "Priorité Urgente", Toast.LENGTH_SHORT).show();
                        cmpt++;
                        break;
                    case 2:
                        StudentStatus.setColorFilter(Color.parseColor("#00BA19"));
                        Priorite = "#00BA19";
                        Toast.makeText(StudentInfoForm.this, "Priorité Normale", Toast.LENGTH_SHORT).show();
                        cmpt=0;
                        break;
                }

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int idButton = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(idButton);
                dureeVisite = radioButton.getText().toString();
                dureeVisiteAjouter = true;
            }
        });
        HeurePickerSetter();
        setCheckBoxJournee();

    }

    /*Cette methode s'occupe de donner le comportement de chaque CheckBox lie au journee*/
    public void setCheckBoxJournee(){
        lundi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    listejournee[0] = "Lundi";
                } else {
                    listejournee[0] = "null";
                }
            }
        });
        mardi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    listejournee[1] = "Mardi";
                } else {
                    listejournee[1] = "null";
                }
            }
        });
        mercredi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    listejournee[2] = "Mercredi";
                } else {
                    listejournee[2] = "null";
                }
            }
        });
        jeudi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    listejournee[3] = "Jeudi";
                } else {
                    listejournee[3] = "null";
                }
            }
        });
        vendredi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    listejournee[4] = "Vendredi";
                } else {
                    listejournee[4] = "null";
                }
            }
        });
    }

    //s'occupe d'assigner la fonction du choix d'heure a chaqu'un des pickers. Permet d'alleger le code dans le onCreate
    public void HeurePickerSetter(){
        HeureStageDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker(HeureStageDebut);
            }
        });
        HeureStageFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker(HeureStageFin);
            }
        });
        HeurePauseDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker(HeurePauseDebut);
            }
        });
        HeurePauseFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker(HeurePauseFin);
            }
        });
    }

    /*Cette methode verifie et concatene les journee ensemble*/
    public void VerificationAjoutJournee(){
        for (int i = 0; i < listejournee.length; i++) {
            if(!listejournee[i].equalsIgnoreCase("null")){
                journeeAjouter = true;
                journee += listejournee[i]+" ";
            }
        }
    }

    /*Methode qui s'assurre que l'utilisateur a selectionner une heure pour tous les form lie a l'horaire
    de l'etudiant
     */
    public void VerificationAjoutHeure(){
        if(HeureStageDebut.getText().equals("Choisisez")){
            heureAjouter = false;
        }else if(HeureStageFin.getText().equals("Choisisez")){
            heureAjouter = false;
        }else if(HeurePauseDebut.getText().equals("Choisisez")){
            heureAjouter = false;
        }else if(HeurePauseFin.getText().equals("Choisisez")){
            heureAjouter = false;
        }else{
            HeureStageD = HeureStageDebut.getText().toString();
            HeureStageF = HeureStageFin.getText().toString();
            HeurePauseD = HeurePauseDebut.getText().toString();
            HeurePauseF = HeurePauseFin.getText().toString();
            heureAjouter = true;
        }
    }

    /* S'occupe d'ouvrire les photos de l'appareil de l'utilisateur et de selectionner une image.
     */
    private void ChoosePicture(){
        Toast.makeText(this,"Photo Ajouter",Toast.LENGTH_LONG).show();
        getContent.launch("image/*");
    }

    //S'occupe de remplir les form automatiquement avec les information lie a l'entreprise et s'occupe de mettre le nom de l'etudiant selectionne
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch(adapterView.getId()){
            case R.id.StudentsSpinner:
                StudentName = adapterView.getItemAtPosition(position).toString();
                break;
            case R.id.EntreprisesSpinner:
                EntrepriseName = adapterView.getItemAtPosition(position).toString();
                if(position != 0){
                    Cursor cursor = db.getEntrepriseCursor(EntrepriseName);
                    EntrepriseNameForm.setText(cursor.getString(1));
                    EntrepriseAdressForm.setText(cursor.getString(2));
                    EntrepriseCityForm.setText(cursor.getString(3));
                    EntrepriseProvinceForm.setText(cursor.getString(4));
                    cursor.close();
                }
                break;
        }
    }

    //S'occupe de mettre les valeurs default lorsque l'activite s'ouvre pour la premiere fois
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        switch(adapterView.getId()){
            case R.id.StudentsSpinner:
                Toast.makeText(this,"Student",Toast.LENGTH_LONG).show();
                StudentName = adapterView.getItemAtPosition(0).toString();
                break;
            case R.id.EntreprisesSpinner:
                Toast.makeText(this,"Entreprise",Toast.LENGTH_LONG).show();
                EntrepriseName = adapterView.getItemAtPosition(0).toString();
                break;
        }
    }

    /*Methode qui s'occupe de gerer l'affichage ainsi que la selection de l'heure pour les form lie a l'horaire de
    l'etudiant
     */
    public void TimePicker(TextView section){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                StudentInfoForm.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                String time = hour+":"+min;
                SimpleDateFormat t24hours = new SimpleDateFormat("HH:mm");
                try {
                    Date date = t24hours.parse(time);
                    section.setText(t24hours.format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },12,0,false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE));
        timePickerDialog.show();
    }


}