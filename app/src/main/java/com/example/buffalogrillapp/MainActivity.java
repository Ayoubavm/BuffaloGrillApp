package com.example.buffalogrillapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase menu_db;
    DBhelper dbHelper;
    Cursor cursor;

    int tab_position=0;

    String _prod_name,_category,_mode,_duré;
    int _id,_tmp;
    TextView printText;
    EditText searchItem;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    AutoCompleteTextView edcustfat;

    private int[] tabIcons = {
            R.drawable.entree,
            R.drawable.plats,
            R.drawable.dessrts
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab_position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        FloatingActionButton add_fab = findViewById(R.id.fab);

        UI_Init();
        //writeDummyDatabase();
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                AddItemDialog();
            }
        });

        edcustfat=(AutoCompleteTextView)findViewById(R.id.edcustfat);
        fatauto();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, (android.view.Menu) menu);
        // return true so that the menu pop up is opened
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_about: {
                // save profile changes
                AboutDialog();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void fatauto() {

        try {

            final String[] mydata;
            //Inside the method you've read the cursor, loop through it and add those item to array
            String sql =  ("SELECT " + MenuDataBase.NAME + " FROM " + MenuDataBase.TABLE_NAME);
            //execute SQL
            Cursor cr = menu_db.rawQuery(sql, null);
            cr.moveToFirst();//cursor pointing to first row
            mydata = new String[cr.getCount()];//create array string based on numbers of row

            int i = 0;
            do {
                mydata[i] = cr.getString(cr.getColumnIndex(MenuDataBase.NAME));//insert new stations to the array list
                i++;
            } while (cr.moveToNext());
            //Finally Set the adapter to AutoCompleteTextView like this,
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, mydata);
            //populate the list to the AutoCompleteTextView controls
            edcustfat.setAdapter(adapter);
        } catch (Exception e) {
            Log.d("Nazim"," "+e.toString());
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabOneFragment(), "Entrees");
        adapter.addFragment(new TabTwoFragment(), "Plats");
        adapter.addFragment(new TabThreeFragment(), "Desserts");
        viewPager.setAdapter(adapter);

    }
    public void AddItemDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_box, null);
        builder.setView(dialogView);

        builder.setTitle("Ajouter Nouveau Produit");
        final EditText product_name=(EditText)dialogView.findViewById(R.id.edit_name);

        final EditText product_mode=(EditText)dialogView.findViewById(R.id.edit_mode);
        final EditText product_duré=(EditText)dialogView.findViewById(R.id.edit_duré);
        final EditText product_tmp=(EditText)dialogView.findViewById(R.id.edit_tmp);

        builder.setCancelable(true);

        Button cancel=(Button)dialogView.findViewById(R.id.cancel);
        Button validate=(Button)dialogView.findViewById(R.id.validate);

        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }
        });
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                        ContentValues values=new ContentValues();
                        //if tab==1 , category = entrer etc..
                        //values.put(MenuDataBase.NAME,product_name.getText().toString());
                        values.put(MenuDataBase.NAME,product_name.getText().toString());
                        values.put(MenuDataBase.CATEGORY, Menu.MENU_TABs[tab_position]);

                        values.put(MenuDataBase.MODE,product_mode.getText().toString());
                        //does the date get updated
                        values.put(MenuDataBase.DURE_DE_VIE,product_duré.getText().toString());
                        values.put(MenuDataBase.TEMPERATURE,Integer.parseInt(product_tmp.getText().toString()));

                        menu_db.insert(MenuDataBase.TABLE_NAME,null,values);
                        //add notify changes

                    alertDialog.dismiss();

                }catch (Exception e){

                    Toast.makeText(MainActivity.this,"Wrong input format",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void AboutDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_about, null);
        builder.setView(dialogView);

        builder.setTitle("About");

        builder.setCancelable(true);

        final AlertDialog alertDialog=builder.create();
        alertDialog.show();


    }
        private void UI_Init(){

        dbHelper=new DBhelper(this);
        menu_db=dbHelper.getReadableDatabase();

        printText=(TextView)findViewById(R.id.print_textview);
    }

    void readUserDb(){
        StringBuilder stringBuilder=new StringBuilder();
        cursor = menu_db.query(MenuDataBase.TABLE_NAME,MenuDataBase.menuColumns,null,null,null,null,null,null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                _id = cursor.getInt(cursor.getColumnIndex("id"));
                _prod_name = cursor.getString(cursor.getColumnIndex(MenuDataBase.NAME));
                _tmp = cursor.getInt(cursor.getColumnIndex(MenuDataBase.TEMPERATURE));
                _mode = cursor.getString(cursor.getColumnIndex(MenuDataBase.MODE));
                _category = cursor.getString(cursor.getColumnIndex(MenuDataBase.CATEGORY));
                _duré = cursor.getString(cursor.getColumnIndex(MenuDataBase.DURE_DE_VIE));
                stringBuilder.append(("id : "+_id+"\nname : "+_prod_name+"\nduré d vie : "
                        +_duré+"\nmode : "+_mode
                        +"\ntemperature : "+_tmp+"\n category : "+_category));
            }
            printText.setText(stringBuilder.toString());
        }
    }

    void writeDummyDatabase(){
        ContentValues values1=new ContentValues();
        values1.put(MenuDataBase.NAME,"Mélange légumes salade d'accueil");
        values1.put(MenuDataBase.CATEGORY,Menu.ENTRER);
        values1.put(MenuDataBase.DURE_DE_VIE,"J+1");
        values1.put(MenuDataBase.MODE,"Après ouverture");
        values1.put(MenuDataBase.TEMPERATURE,5);

        ContentValues values2=new ContentValues();
        values2.put(MenuDataBase.NAME,"Tartare");
        values2.put(MenuDataBase.CATEGORY,Menu.PLAT);
        values2.put(MenuDataBase.DURE_DE_VIE,"A la minute");
        values2.put(MenuDataBase.MODE,"/");
        values2.put(MenuDataBase.TEMPERATURE,2);

        ContentValues values3=new ContentValues();
        values3.put(MenuDataBase.NAME,"Pâtisseries");
        values3.put(MenuDataBase.CATEGORY,Menu.DESSERT);
        values3.put(MenuDataBase.DURE_DE_VIE,"J+2");
        values2.put(MenuDataBase.MODE,"Decongelation");
        values3.put(MenuDataBase.TEMPERATURE,4);

        menu_db.insert(MenuDataBase.TABLE_NAME,null,values1);
        menu_db.insert(MenuDataBase.TABLE_NAME,null,values2);
        menu_db.insert(MenuDataBase.TABLE_NAME,null,values3);

    }

}