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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class UpdateStudentInfoForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String EntrepriseName, Priorite, StudentName;
    private Spinner entreprises;
    private Button updateButton, deleteButton;
    private Bitmap ImageStudent;
    private TextView StudentNameUpdateForm, EntrepriseNameUpdateForm, EntrepriseAdressUpdateForm,
            EntrepriseCityUpdateForm, EntrepriseProvinceUpdateForm;
    private ImageView StudentStatus, StudentPicture;
    private FloatingActionButton updatePicture;
    private ActivityResultLauncher<String> getContent;
    DataBaseStudentTracker db;

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
                db.updateStage(StudentName, EntrepriseName, Priorite);
                db.addStudentImage(StudentName,ImageStudent);
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
        getAndSetIntentData();
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
            Bundle ex = getIntent().getExtras();
            Bitmap image = ex.getParcelable("studentPicture");
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
        } else {
            Toast.makeText(UpdateStudentInfoForm.this, "Aucune Information", Toast.LENGTH_SHORT).show();
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
}