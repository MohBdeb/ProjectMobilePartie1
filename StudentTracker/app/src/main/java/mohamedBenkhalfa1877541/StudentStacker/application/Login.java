package mohamedBenkhalfa1877541.StudentStacker.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private Button btnLogin;
    private EditText user;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        user   = findViewById(R.id.userInput);
        password   = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.button_login);

        //Redirige le user vers la StudentList
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Login.this,user.getText(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Login.this,"Connecté", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, StudentList.class);
                startActivity(intent);
            }
        });
    }


}
