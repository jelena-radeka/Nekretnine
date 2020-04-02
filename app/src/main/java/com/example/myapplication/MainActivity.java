package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {


    List<String> drawerItems;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerList;
    RelativeLayout drawerPane;
    ActionBarDrawerToggle drawerToggle;
    AlertDialog dijalog;
    SharedPreferences prefs;
    ImageView preview;
    String imagePath = null;
    DatabaseHelper databaseHelper;
    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "PERMISSIONS";
    public static String NEKRETNINA_ID = "NEKRETNINA_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillData();
        setupToolbar();
        setupDrawer();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


    }

    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add("Sve nekretnine");
        drawerItems.add("Podesavanja");

    }

    public void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
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
                        title = "Sve nekretnine";
                        showAtrakcija();
                        break;
                    case 1:
                        title = "Podesavanja";
                        Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
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
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addNekretnina();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showAtrakcija() {

        final RecyclerView recyclerView = this.findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MyAdapter adapter = new MyAdapter(getDatabaseHelper(), this);
        recyclerView.setAdapter(adapter);

    }

//    private void showDialog() {
//        if (dijalog == null) {
//            dijalog = new AboutDialog(MainActivity.this).prepareDialog();
//        } else {
//            if (dijalog.isShowing()) {
//                dijalog.dismiss();
//            }
//        }
//        dijalog.show();
//    }

    private void addNekretnina() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_layout);
        dialog.setTitle("Unesite podatke");
        dialog.setCanceledOnTouchOutside(false);

        Button chooseBtn = dialog.findViewById(R.id.btn_izaberi);
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
                    Toast.makeText(MainActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT).show();
                    return;
                }

                Nekretnina nekretnina = new Nekretnina();
                nekretnina.setmNaziv(naziv.getText().toString());
                nekretnina.setmOpis(opis.getText().toString());
                nekretnina.setmAdresa(adresa.getText().toString());
                nekretnina.setmCena(cena.getText().toString());
                nekretnina.setmTelefon(telefon.getText().toString());
                nekretnina.setmKvadratura(kvadratura.getText().toString());
                nekretnina.setmBrojSoba(brSoba.getText().toString());

                nekretnina.setmImage(imagePath);

                try {
                    getDatabaseHelper().getNekretninaDao().create(nekretnina);


                    boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);

                    if (toast) {
                        Toast.makeText(MainActivity.this, "Uneta nova nekretnina", Toast.LENGTH_LONG).show();

                    }


                    refresh();
                    reset();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Cena karte mora biti broj", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (java.sql.SQLException e) {
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

    private void refresh() {

        RecyclerView recyclerView = findViewById(R.id.rvList);
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);


            MyAdapter adapter = new MyAdapter(getDatabaseHelper(), MainActivity.this);
            recyclerView.setAdapter(adapter);

        }
    }

    private void reset() {
        imagePath = "";
        preview = null;
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
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    private void
    selectPicture() {
        if (isStoragePermissionGranted()) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(MainActivity.this, DetaljiActivity.class);
        try {
            i.putExtra(NEKRETNINA_ID, getDatabaseHelper().getNekretninaDao().queryForAll().get(position).getmId());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        startActivity(i);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

}
