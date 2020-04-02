package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;


import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetaljiActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;
    List<String> drawerItems;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerList;
    RelativeLayout drawerPane;
    ActionBarDrawerToggle drawerToggle;
    AlertDialog dijalog;
    Nekretnina nekretnina;
    boolean toast;
    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";
    private static final int SELECT_PICTURE = 1;
    public static String KEY = "KEY";
    private String imagePath = null;
    private ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji);

        createNotificationChannel();
//        fillData();
        setupToolbar();
//        setupDrawer();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int atrakcijaId = getIntent().getExtras().getInt(MainActivity.NEKRETNINA_ID);

        try {
            nekretnina = getDatabaseHelper().getNekretninaDao().queryForId(atrakcijaId);

            TextView naziv = findViewById(R.id.detalji_naziv);
            TextView adresa = findViewById(R.id.detalji_adresa);
            TextView kvadratura = findViewById(R.id.detalji_kvadratura);
            TextView cena = findViewById(R.id.detalji_cena);
            TextView telefon = findViewById(R.id.detalji_telefon);
            TextView brojSoba = findViewById(R.id.detalji_broj_soba);
            TextView opis = findViewById(R.id.detalji_opis);


            ImageView image = findViewById(R.id.detalji_slika);

            Uri mUri = Uri.parse(nekretnina.getmImage());
            image.setImageURI(mUri);

            naziv.setText(nekretnina.getmNaziv());
            adresa.setText("Adresa: " + nekretnina.getmAdresa());
            kvadratura.setText("Kvadratura: " + nekretnina.getmKvadratura());
            cena.setText("Cena: " + nekretnina.getmCena() + "eur");
            telefon.setText(nekretnina.getmTelefon());
            opis.setText(nekretnina.getmOpis());
            brojSoba.setText(nekretnina.getmBrojSoba());

            InputStream is = null;
            try {
                is = getApplicationContext().getAssets().open(nekretnina.getmImage());
                Drawable drawable = Drawable.createFromStream(is, null);
                image.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add("Sve atrakcije");
        drawerItems.add("Podesavanja");
        drawerItems.add("O Aplikaciji");
    }

    public void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_detalji);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }
    }


    private void setupDrawer() {
        drawerList = findViewById(R.id.left_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerPane = findViewById(R.id.drawerPane);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i) {
                    case 0:
                        title = "Sve atrakcije";
//                        showAtrakcija();
                        break;
                    case 1:
                        title = "Podesavanja";
                        Intent settings = new Intent(DetaljiActivity.this, SettingsActivity.class);
                        startActivity(settings);
                        break;
                        default:
                        break;
                }
                drawerList.setItemChecked(i, true);
                setTitle(title);
                drawerLayout.closeDrawer(drawerPane);
            }
        });
        drawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerItems));


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalji_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
               editAtrakcija();
                break;
            case R.id.delete:
               deleteAtrakcija();
               break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAtrakcija() {
        try {
            getDatabaseHelper().getNekretninaDao().delete(nekretnina);
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);
        boolean notif = prefs.getBoolean(getString(R.string.notif_key), false);

        if (toast) {
            Toast.makeText(this, "Nekretnina obrisana", Toast.LENGTH_LONG).show();
        }

        if (notif) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);

            builder.setSmallIcon(android.R.drawable.ic_menu_delete);
            builder.setContentTitle("Nekretnina");
            builder.setContentText("Nekretnina obrisana");

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dehaze_black_24dp);

            builder.setLargeIcon(bitmap);
            notificationManager.notify(1, builder.build());
        }


    }

    private void reset() {
        imagePath = "";
        preview = null;
    }
    private void editAtrakcija() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_layout);
        dialog.setTitle("Unesite podatke");
        dialog.setCanceledOnTouchOutside(false);

        Button chooseBtn = (Button) dialog.findViewById(R.id.btn_izaberi);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview = dialog.findViewById(R.id.preview_image1);
                selectPicture();
            }
        });

        Button add = dialog.findViewById(R.id.add_atrakcija);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText naziv = dialog.findViewById(R.id.naziv);
                EditText opis = dialog.findViewById(R.id.opis);
                EditText adresa = dialog.findViewById(R.id.adresa);
                EditText cena = dialog.findViewById(R.id.cena);
                EditText telefon = dialog.findViewById(R.id.broj_telefona);
                EditText kvadratura = dialog.findViewById(R.id.kvadratura);
                EditText brSoba = dialog.findViewById(R.id.brojSoba);

                if (preview == null || imagePath == null) {
                    Toast.makeText(DetaljiActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT).show();
                    return;

                }

                nekretnina.setmNaziv(naziv.getText().toString());
                nekretnina.setmOpis(opis.getText().toString());
                nekretnina.setmAdresa(adresa.getText().toString());
                nekretnina.setmCena(cena.getText().toString());
                nekretnina.setmTelefon(telefon.getText().toString());
                nekretnina.setmKvadratura(kvadratura.getText().toString());
                nekretnina.setmBrojSoba(brSoba.getText().toString());
                nekretnina.setmImage(imagePath);

                try {
                    getDatabaseHelper().getNekretninaDao().update(nekretnina);


                    boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);
                    boolean notif = prefs.getBoolean(getString(R.string.notif_key), false);

                    if (toast) {
                        Toast.makeText(DetaljiActivity.this, "Update-ovani podaci o nekretnini", Toast.LENGTH_LONG).show();

                    }

                    if (notif) {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(DetaljiActivity.this, NOTIF_CHANNEL_ID);

                        builder.setSmallIcon(android.R.drawable.ic_menu_edit);
                        builder.setContentTitle("Nekretnina");
                        builder.setContentText("Update-ovani podaci o nekretnini");

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dehaze_black_24dp);

                        builder.setLargeIcon(bitmap);
                        notificationManager.notify(1, builder.build());

                    }
                    reset();
                  //  refreshGlumac();


                } catch (NumberFormatException e) {

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();


            }

        });

        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Description of My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    if (preview != null) {
                        preview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    }
                }
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
          //  Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    private void
    selectPicture() {
        if (isStoragePermissionGranted()) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }
}




