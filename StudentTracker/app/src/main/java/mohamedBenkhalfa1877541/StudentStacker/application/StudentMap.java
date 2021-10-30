package mohamedBenkhalfa1877541.StudentStacker.application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentMap extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    private DrawerLayout menu;
    private Toolbar toolbar;
    DataBaseStudentTracker myDB;
    private boolean isPermissionGranted = false;
    private GoogleMap mapGoogle;
    private LocationManager locationManager;
    private FusedLocationProviderClient client;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private ImageView GreenPriority, YellowPriority, RedPriority;
    private Boolean greenActif = true, yellowActif = true, redActif = true;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        menu = findViewById(R.id.side_menu);
        GreenPriority = findViewById(R.id.GreenPriorityGoogleMap);
        YellowPriority = findViewById(R.id.YellowPriorityGoogleMap);
        RedPriority = findViewById(R.id.RedPriorityGoogleMap);

        NavigationView navigationView = findViewById(R.id.side_menu_viewer_googlemap);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, menu, toolbar, R.string.side_menu_open, R.string.side_menu_close);
        menu.addDrawerListener(toggle);
        toggle.syncState();

        myDB = new DataBaseStudentTracker(StudentMap.this);

        CheckPermission();
        if (isPermissionGranted) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
            supportMapFragment.getMapAsync(StudentMap.this);
            client = LocationServices.getFusedLocationProviderClient(this);
        }

        /*Gere le fonctionnement pour les marqueurs selon leur priorite. Dependant des priorite choisi par le user
        les marqueurs seront sois cache ou remis sur la carte afin de les afficher.
         */
        GreenPriority.setOnClickListener(new View.OnClickListener() {
            int active = 0;

            @Override
            public void onClick(View view) {
                if (active == 0) {
                    GreenPriority.setColorFilter(Color.parseColor("#FFFFFF"));
                    GreenPriority.setBackgroundColor(Color.parseColor("#00BA19"));
                    greenActif = false;
                    active++;
                    mapGoogle.clear();
                    try {
                        CreateStageMarkers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    GreenPriority.setColorFilter(Color.parseColor("#00BA19"));
                    GreenPriority.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    greenActif = true;
                    active = 0;
                    try {
                        CreateStageMarkers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        YellowPriority.setOnClickListener(new View.OnClickListener() {
            int active = 0;

            @Override
            public void onClick(View view) {
                if (active == 0) {
                    YellowPriority.setColorFilter(Color.parseColor("#FFFFFF"));
                    YellowPriority.setBackgroundColor(Color.parseColor("#EFDB27"));
                    yellowActif = false;
                    active++;
                    mapGoogle.clear();
                    try {
                        CreateStageMarkers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    YellowPriority.setColorFilter(Color.parseColor("#EFDB27"));
                    YellowPriority.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    yellowActif = true;
                    active = 0;
                    try {
                        CreateStageMarkers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        RedPriority.setOnClickListener(new View.OnClickListener() {
            int active = 0;

            @Override
            public void onClick(View view) {
                if (active == 0) {
                    RedPriority.setColorFilter(Color.parseColor("#FFFFFF"));
                    RedPriority.setBackgroundColor(Color.parseColor("#FFF44336"));
                    redActif = false;
                    active++;
                    mapGoogle.clear();
                    try {
                        CreateStageMarkers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    RedPriority.setColorFilter(Color.parseColor("#FFF44336"));
                    RedPriority.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    redActif = true;
                    active = 0;
                    try {
                        CreateStageMarkers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /*Verifie si l'utilisateur a les permissions necessaire pour faire fonctionner la map. si ce n'est pas le cas
    le programme redirigera le user afin qu'il donne les acces necessaire pour faire fonctionner l'application
     */
    private void CheckPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(StudentMap.this, "Permision Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(StudentMap.this, "Permision Denied", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    //S'occupe de l'interaction avec les items du menu qui se trouve du cote gauche de l'application
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listeEleveMenu:
                Intent intent = new Intent(StudentMap.this, StudentList.class);
                startActivity(intent);

                break;

            case R.id.googleMapMenu:
                menu.closeDrawer(GravityCompat.START);
                break;
        }
        menu.closeDrawer(GravityCompat.START);
        return true;
    }

    //Initialise la carte Google
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapGoogle = googleMap;
        mapGoogle.setMyLocationEnabled(true);
        mapGoogle.getUiSettings().setMyLocationButtonEnabled(true);
        try {
            CreateStageMarkers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*Creation des marqueurs de stage. S'occupe d'assigner les bonnes couleurs au marqueurs selon le stages qui est presente et
    s'occupe aussi de gerer la logique pour afficher les differentes priorite selon celle que l'utilisateur veut afficher
     */
    public void CreateStageMarkers() throws IOException {

        ArrayList<Stage> listStage = myDB.readAllData();
        if (listStage.size() > 0) {
            for (int i = 0; i < listStage.size(); i++) {
                if (greenActif && listStage.get(i).getPriorite().equals("#00BA19")) {
                    stageToMarker(listStage.get(i));
                } else if (yellowActif && listStage.get(i).getPriorite().equals("#EFDB27")) {
                    stageToMarker(listStage.get(i));
                } else if (redActif && listStage.get(i).getPriorite().equals("#FFF44336")) {
                    stageToMarker(listStage.get(i));
                }
            }

        }
    }

    public void stageToMarker(Stage stage) throws IOException {
        LatLng position;
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(stage.getPriorite()), hsv);
        position = getLocationFromAddress(stage.getEntrepriseAdresse());
        mapGoogle.addMarker(new MarkerOptions().position(position).title(stage.getEtudiantName())
                .snippet("Stage").icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));
    }

    //S'occupe de retourner des cordonne selon l'addresse qui a ete passer en paramettre
    public LatLng getLocationFromAddress(String strAddress) throws IOException {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            address.size();
            if (address == null) {
                return null;
            }
            Address location = address.get(0);

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }

    //Dirige la camera vers la nouvelle position du user
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mapGoogle.animateCamera(cameraUpdate);
        locationManager.removeUpdates((android.location.LocationListener) StudentMap.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //S'occupe de rediriger l'utilisateur vers la derniere location ou il se situait
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mapGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            }
        });
    }

}