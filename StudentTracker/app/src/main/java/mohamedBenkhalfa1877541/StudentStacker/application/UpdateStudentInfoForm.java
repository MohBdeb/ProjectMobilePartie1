package mohamedBenkhalfa1877541.StudentStacker.application;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class UpdateStudentInfoForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String EntrepriseName, Priorite, StudentName, dureeVisite,journee = "";
    private Spinner entreprises;
    private CheckBox lundi,mardi,mercredi,jeudi,vendredi;
    private String[] listejournee = {"null","null","null","null","null"};
    private Button updateButton, deleteButton;
    private Bitmap ImageStudent;
    private TextView StudentNameUpdateForm, EntrepriseNameUpdateForm, EntrepriseAdressUpdateForm,
            EntrepriseCityUpdateForm, EntrepriseProvinceUpdateForm, CommentaireStage;
    private TextView HeureStageDebut, HeureStageFin, HeurePauseDebut, HeurePauseFin;
    private RadioGroup radioGroupDuree;
    private ImageView StudentStatus, StudentPicture;
    private FloatingActionButton updatePicture;
    private ActivityResultLauncher<String> getContent;
    private DataBaseStudentTracker db;
    private Stage stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student_info_form);
        db = new DataBaseStudentTracker(UpdateStudentInfoForm.this);

        StudentNameUpdateForm = findViewById(R.id.NameUpdateForm);
        StudentStatus = findViewById(R.id.StudentStatusUpdateForm);
        EntrepriseNameUpdateForm = findViewById(R.id.NomEntrepriseUpdateForm);
        EntrepriseAdressUpdateForm = findViewById(R.id.AdressEntrepriseUpdateForm);
        EntrepriseCityUpdateForm = findViewById(R.id.CityEntrepriseUpdateForm);
        EntrepriseProvinceUpdateForm = findViewById(R.id.ProvinceEntrepriseUpdateForm);
        StudentPicture = findViewById(R.id.StudentPictureUpdateForm);
        updateButton = findViewById(R.id.updateButtonUpdateForm);
        deleteButton = findViewById(R.id.deleteButtonUpdateForm);
        updatePicture = findViewById(R.id.updatePictureButton);
        StudentStatus = findViewById(R.id.StudentStatusUpdateForm);
        CommentaireStage = findViewById(R.id.CommentaireStageUpdate);
        HeureStageDebut = findViewById(R.id.HeureStageDebutUpdate);
        HeureStageFin = findViewById(R.id.HeureStageFinUpdate);
        HeurePauseDebut = findViewById(R.id.HeurePauseDebutUpdate);
        HeurePauseFin = findViewById(R.id.HeurePauseFinUpdate);
        radioGroupDuree = findViewById(R.id.radioGroupDureeUpdate);
        lundi = findViewById(R.id.checkBoxLundiUpdate);
        mardi = findViewById(R.id.checkBoxMardiUpdate);
        mercredi = findViewById(R.id.checkBoxMercrediUpdate);
        jeudi = findViewById(R.id.checkBoxJeudiUpdate);
        vendredi = findViewById(R.id.checkBoxVendrediUpdate);

        //Spinner Entreprises setup
        entreprises = findViewById(R.id.EntreprisesSpinnerUpdate);
        ArrayAdapter adapterEntreprise = new ArrayAdapter(this, android.R.layout.simple_spinner_item, db.getEntrepriseNames());
        adapterEntreprise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        entreprises.setAdapter(adapterEntreprise);
        entreprises.setOnItemSelectedListener(this);

        /*S'occupe de gerer le statuts de l'etudiant en changeant la couleur de l'icone et en assigant le status actuelle
        a celui ci et assigne le statuts a une variable globale
        */
        StudentStatus.setOnClickListener(new View.OnClickListener() {
            int cmpt = 0;

            @Override
            public void onClick(View view) {
                if (Priorite.equals("#EFDB27")) {
                    cmpt = 1;
                } else if (Priorite.equals("#FFF44336")) {
                    cmpt = 2;
                } else {
                    cmpt = 0;
                }
                switch (cmpt) {
                    case 0:
                        StudentStatus.setColorFilter(Color.parseColor("#EFDB27"));
                        Priorite = "#EFDB27";
                        Toast.makeText(UpdateStudentInfoForm.this, "Priorité Moyenne", Toast.LENGTH_SHORT).show();
                        cmpt++;
                        break;
                    case 1:
                        StudentStatus.setColorFilter(Color.parseColor("#FFF44336"));
                        Priorite = "#FFF44336";
                        Toast.makeText(UpdateStudentInfoForm.this, "Priorité Urgente", Toast.LENGTH_SHORT).show();
                        cmpt++;
                        break;
                    case 2:
                        StudentStatus.setColorFilter(Color.parseColor("#00BA19"));
                        Priorite = "#00BA19";
                        Toast.makeText(UpdateStudentInfoForm.this, "Priorité Normale", Toast.LENGTH_SHORT).show();
                        cmpt = 0;
                        break;
                }
            }
        });

        //Update le stage actuelle
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dureeVisite.equals("1 heure")){
                    dureeVisite = "60 minutes";
                }
                AjoutJournee();
                db.updateStage(StudentName, EntrepriseName, Priorite,CommentaireStage.getText().toString(),journee,dureeVisite
                ,HeureStageDebut.getText().toString(),HeureStageFin.getText().toString(),HeurePauseDebut.getText().toString(),HeurePauseFin.getText().toString());
                db.addStudentImage(StudentName, ImageStudent);
                Toast.makeText(UpdateStudentInfoForm.this, "Stage mis a jour", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateStudentInfoForm.this, StudentList.class);
                startActivity(intent);
            }
        });

        //Delete le stage actuelle
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteStage(StudentName);
                Toast.makeText(UpdateStudentInfoForm.this, "Stage Supprimer", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateStudentInfoForm.this, StudentList.class);
                startActivity(intent);
            }
        });

        //S'occupe de recuperer la photo que l'utilisateur a choisi et la place sur l'imageView qui represente la photo du user
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                try {
                    ImageStudent = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StudentPicture.setImageURI(result);
            }
        });
        //S'occupe d'appeller la methode pour selectionner une image dans les images de l'utilisateurs lorsque celui ci
        //appuie sur le bouton
        updatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosePicture();
            }
        });

        radioGroupDuree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int idButton = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(idButton);
                dureeVisite = radioButton.getText().toString();
            }
        });
        HeurePickerSetter();
        setCheckBoxJournee();
        getAndSetIntentData();
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

    /*Cette methode s'occupe de concatene les differentes journee choisi*/
    public void AjoutJournee(){
        for (int i = 0; i < listejournee.length; i++) {
            if(!listejournee[i].equalsIgnoreCase("null")){
                journee += listejournee[i]+" ";
            }
        }
    }

    /*Cette methode s'occupe d'associer les valeur des selectionneur d'heure avec les differentes
    valeur du stagiere
     */
    public void HeurePickerSetter() {
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


    /* S'occupe d'ouvrire les photos de l'appareil de l'utilisateur et de selectionner une image.
     */
    private void ChoosePicture() {
        Toast.makeText(this, "Photo Ajouter", Toast.LENGTH_LONG).show();
        getContent.launch("image/*");
    }

    //Choisi l'entreprise selectionner dans le spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        if (position != 0) {
            setEntrepriseForm(adapterView.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //Recoit les informations du stage cliquer dans le StudentList menu et insert les information dans leur place respective
    private void getAndSetIntentData() {
        if (getIntent().hasExtra("studentName") && getIntent().hasExtra("entrepriseName")
                && getIntent().hasExtra("studentStatus")) {
            StudentName = getIntent().getStringExtra("studentName");
            EntrepriseName = getIntent().getStringExtra("entrepriseName");
            Priorite = getIntent().getStringExtra("studentStatus");
            String imageStudent = getIntent().getStringExtra("studentPicture");
            byte[] byteArray = Base64.decode(imageStudent, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //Bundle ex = getIntent().getExtras();
            //Bitmap image = ex.getParcelable("studentPicture");
            ImageStudent = image;
            StudentPicture.setImageBitmap(image);
            StudentNameUpdateForm.setText(StudentName);
            for (int i = 0; i < entreprises.getCount(); i++) {
                if (entreprises.getItemAtPosition(i).toString().equals(EntrepriseName)) {
                    entreprises.setSelection(i);
                    i = entreprises.getCount();
                }
            }
            setEntrepriseForm(EntrepriseName);
            StudentStatus.setColorFilter(Color.parseColor(Priorite));
            getIntentStage(StudentName);
            HeureStageDebut.setText(stage.getHeureStageDebut());
            HeureStageFin.setText(stage.getHeureStageFin());
            HeurePauseDebut.setText(stage.getHeurePauseDebut());
            HeurePauseFin.setText(stage.getHeurePauseFin());
            CommentaireStage.setText(stage.getCommentaire());
            setRadioButtonDureeGetandSetData(stage.getDureeVisite());
            setCheckBockGetandSetData(stage.getJournee());
        } else {
            Toast.makeText(UpdateStudentInfoForm.this, "Aucune Information", Toast.LENGTH_SHORT).show();
        }
    }

    /*Cette methode s'occupe de cocher la ou les journee lie aux journee du stagiere selectioner*/
    public void setCheckBockGetandSetData(String journee){
        String[] listejourneetemp = journee.split(" ");
        for (int i = 0; i < listejourneetemp.length; i++) {
            switch (listejourneetemp[i]) {
                case "Lundi":
                    lundi.setChecked(true);
                    break;
                case "Mardi":
                    mardi.setChecked(true);
                    break;
                case "Mercredi":
                    mercredi.setChecked(true);
                    break;
                case "Jeudi":
                    jeudi.setChecked(true);
                    break;
                case "Vendredi":
                    vendredi.setChecked(true);
                    break;

            }
        }

    }

    /*Cette methode s'occupe de cocher le RadioButton lie a la valeur du stage selectioner*/
    public void setRadioButtonDureeGetandSetData(String duree){
        RadioButton btn;
        switch (duree) {
            case "30 minutes":
                btn = findViewById(R.id.Btn30min);
                btn.setChecked(true);
                dureeVisite = btn.getText().toString();
                break;

            case "45 minutes":
                btn = findViewById(R.id.Btn45min);
                btn.setChecked(true);
                dureeVisite = btn.getText().toString();
                break;

            case "60 minutes":
                btn = findViewById(R.id.Btn1hour);
                btn.setChecked(true);
                dureeVisite = btn.getText().toString();
                break;
        }

    }

    /*Cette methode s'occupe de recuper le stage du intent
     */
    public void getIntentStage(String nom) {
        ArrayList<Stage> templist = db.readAllDataStage();
        for (int i = 0; i < templist.size(); i++) {
            if (templist.get(i).getEtudiantName().equals(nom)) {
                stage = templist.get(i);
            }
        }
    }

    //Automatiquement remplie les zones de text pour la compagnie
    public void setEntrepriseForm(String entrepriseName) {
        EntrepriseName = entrepriseName;
        Cursor cursor = db.getEntrepriseCursor(EntrepriseName);
        EntrepriseNameUpdateForm.setText(cursor.getString(1));
        EntrepriseAdressUpdateForm.setText(cursor.getString(2));
        EntrepriseCityUpdateForm.setText(cursor.getString(3));
        EntrepriseProvinceUpdateForm.setText(cursor.getString(4));
        cursor.close();
    }

    /*Methode qui s'occupe de gerer l'affichage ainsi que la selection de l'heure pour les form lie a l'horaire de
        l'etudiant
         */
    public void TimePicker(TextView section) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                UpdateStudentInfoForm.this,
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
}