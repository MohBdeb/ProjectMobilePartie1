package mohamedBenkhalfa1877541.StudentStacker.application;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class StudentInfoForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText EntrepriseNameForm,EntrepriseAdressForm,EntrepriseCityForm,EntrepriseProvinceForm;
    private String StudentName,EntrepriseName,Priorite="#00BA19";
    private Spinner students,entreprises;
    private Bitmap ImageStudent;
    private Button addButton;
    private Boolean photoAjouter = false;
    private FloatingActionButton addPicture;
    private ImageView StudentPicture,StudentStatus;
    private ActivityResultLauncher<String> getContent;
    DataBaseStudentTracker db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_info_form);
        db = new DataBaseStudentTracker(StudentInfoForm.this);

        EntrepriseNameForm = findViewById(R.id.EntrepriseNameForm);
        EntrepriseAdressForm = findViewById(R.id.EntrepriseAdressForm);
        EntrepriseCityForm = findViewById(R.id.EntrepriseCityForm);
        EntrepriseProvinceForm = findViewById(R.id.EntrepriseProvinceForm);
        addButton = findViewById(R.id.button);
        addPicture = findViewById(R.id.addPicture);
        StudentPicture = findViewById(R.id.StudentPicture);
        StudentStatus = findViewById(R.id.StatutsStudent);

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
                if(!StudentName.equals("Default") && !EntrepriseName.equals("Default") ){
                    db.addStage(StudentName,EntrepriseName,Priorite);
                    db.addStudentImage(StudentName,ImageStudent);
                    Toast.makeText(StudentInfoForm.this, "Stage Ajouter", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StudentInfoForm.this, StudentList.class);
                    startActivity(intent);
                }else{
                    if (StudentName.equals("Default") && EntrepriseName.equals("Default") && photoAjouter){
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

}