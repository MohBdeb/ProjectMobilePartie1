package mohamedBenkhalfa1877541.StudentStacker.reseau;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import mohamedBenkhalfa1877541.StudentStacker.application.R;
import mohamedBenkhalfa1877541.StudentStacker.reseau.Priorite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Connection {

    private JustineAPI client;

    private String testStage_id = "";

    JSONArray entreprises;
    JSONArray eleves;
    JSONArray stages;
    JSONObject stageSelectione;


    public Connection(){

        client = JustineAPIClient.getRetrofit().create(JustineAPI.class);

        if (ConnectUtils.authToken.isEmpty()) {
        } else {
            HashMap<String, Object> user = new HashMap<>();
            user.put("id_compte", ConnectUtils.authId);
            client.testerConnexion(ConnectUtils.authToken, user).enqueue(
                    new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() != 200) {
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    }
            );
        }

    }

    public JSONArray getElevesArrayJson(){
        return eleves;
    }
    public JSONArray getEntrepriseArrayJson(){
        return entreprises;
    }
    public JSONArray getStagesArrayJson(){
        return stages;
    }

    public JSONObject getStageObjectJson(){
        return stageSelectione;
    }

    public void getEleves(View view) {
        client.getComptesEleves(mohamedBenkhalfa1877541.StudentStacker.reseau.ConnectUtils.authToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        eleves = new JSONArray(response.body().string());
                        String display = "Réponse OK\n";
                        for (int i = 0; i < eleves.length(); i++) {
                            display += eleves.get(i).toString() + "\n";
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void getEntreprises() {
        client.getEntreprises(mohamedBenkhalfa1877541.StudentStacker.reseau.ConnectUtils.authToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
                try {
                    if (response.code() == 200) {
                        entreprises = new JSONArray(response.body().string());
                        String display = "Réponse OK\n";
                        for (int i = 0; i < entreprises.length(); i++) {
                            display += entreprises.get(i).toString() + "\n";
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    public void getStages() {
        client.getStages(mohamedBenkhalfa1877541.StudentStacker.reseau.ConnectUtils.authToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("justine_tag", response.toString());
                try {
                    if (response.code() == 200) {
                        stages = new JSONArray(response.body().string());
                        String display = "Réponse OK\n";
                        for (int i = 0; i < stages.length(); i++) {
                            display += stages.get(i).toString() + "\n";
                            testStage_id = ((JSONObject) stages.get(i)).getString("id");
                        }
                        if (stages.length() == 0) {
                            ajouterStages();
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    private void ajouterStages() throws JSONException {
        if (eleves.length() < entreprises.length()) {
            return;
        }
        //tvResult.setText("Réponse OK\n");
        for (int i = 0; i < entreprises.length(); i++) {
            JSONObject entreprise = (JSONObject) entreprises.get(i);
            String entrepriseId = entreprise.getString("id");
            JSONObject eleve = (JSONObject) eleves.get(i);
            String eleveId = eleve.getString("id");
            HashMap<String, Object> requete = new HashMap<>();
            requete.put("id", UUID.randomUUID().toString());
            requete.put("annee", "2021-2022");
            requete.put("id_entreprise", entrepriseId);
            requete.put("id_etudiant", eleveId);
            requete.put("id_professeur", ConnectUtils.authId);
            requete.put("priorite", Priorite.HAUTE);

            client.ajouterStage(ConnectUtils.authToken, requete).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.i("justine_tag", response.toString());
                    try {
                        if (response.code() == 200) {
                            JSONObject stage = new JSONObject(response.body().string());
                            //String display = tvResult.getText().toString();
                            //display += stage.toString() + "\n";
                            //tvResult.setText(display);
                        } else {
                            //tvResult.setText("Echec");
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("TAG", t.toString());
                }
            });
        }

    }

    public void getStage() {

        if (!testStage_id.isEmpty()) {

            client.getStage(ConnectUtils.authToken, testStage_id).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.i("justine_tag", response.toString());
                    try {
                        if (response.code() == 200) {
                            JSONObject stage = new JSONObject(response.body().string());
                            stageSelectione = stage;
                            String display = "Réponse OK\n";
                            display += stage.toString() + "\n";
                            Log.i("BRUH", display);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("TAG", t.toString());
                }
            });
        }
    }
}