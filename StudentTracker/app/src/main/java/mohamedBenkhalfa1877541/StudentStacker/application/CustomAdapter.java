package mohamedBenkhalfa1877541.StudentStacker.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList studentName, entrepriseName, entrepriseAdress, studentStatuts,studentPictures;


    CustomAdapter(Context context, ArrayList nameStudent, ArrayList nameEntreprise, ArrayList adressEntreprise, ArrayList StudentStatuts,ArrayList studentPictures) {
        this.context = context;
        this.studentName = nameStudent;
        this.entrepriseName = nameEntreprise;
        this.entrepriseAdress = adressEntreprise;
        this.studentStatuts = StudentStatuts;
        this.studentPictures = studentPictures;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_cards, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.studentName.setText(String.valueOf(studentName.get(position)));
        holder.entrepriseName.setText(String.valueOf(entrepriseName.get(position)));
        holder.entrepriseAdress.setText(String.valueOf(entrepriseAdress.get(position)));
        holder.studentStatuts.setColorFilter(Color.parseColor(studentStatuts.get(position).toString()));
        holder.studentPicture.setImageBitmap((Bitmap) studentPictures.get(position));
        //Insere les elements qu'on veut amener avec nous dans l'activite updateStudentForm
        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateStudentInfoForm.class);
                intent.putExtra("studentName", String.valueOf(studentName.get(position)));
                intent.putExtra("entrepriseName", String.valueOf(entrepriseName.get(position)));
                intent.putExtra("studentStatus", String.valueOf(studentStatuts.get(position)));
                Bitmap image = (Bitmap)studentPictures.get(position);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String imageStudent = Base64.encodeToString(byteArray, 0);
                intent.putExtra("studentPicture",String.valueOf(imageStudent));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentName.size();
    }

    //S'occupe d'assigner les valeur au form present dans le student_card qui sera ajouter au recyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, entrepriseName, entrepriseAdress;
        ImageView studentStatuts,studentPicture;
        private LinearLayout mainlayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.StudentName);
            entrepriseName = itemView.findViewById(R.id.EntrepriseName);
            entrepriseAdress = itemView.findViewById(R.id.EntrepriseAdress);
            studentStatuts = itemView.findViewById(R.id.StudentStatutsCard);
            studentPicture = itemView.findViewById(R.id.StudentProfilePicture);
            mainlayout = itemView.findViewById(R.id.student_card_layout);
        }
    }
}
