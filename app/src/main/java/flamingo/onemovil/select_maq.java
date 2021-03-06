package flamingo.onemovil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import io.requery.android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class select_maq extends AppCompatActivity {

    private Button btn_regr_maq, btn_sele_maq, btn_foto_maq;
    private TextView olab_cte02;
    private ListView lst_maq;
    private SQLiteDatabase db4;
    private Cursor data;
    private String cSqlLn;
    private Context oThis= this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_maq);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        getWindow().getDecorView().getRootView().setBackgroundColor(Color.rgb(204, 255, 204));

        btn_regr_maq = (Button) findViewById(R.id.obtn_regr_maq);
        btn_sele_maq = (Button) findViewById(R.id.obtn_sele_maq);
        btn_foto_maq = (Button) findViewById(R.id.obtn_foto_maq);

        lst_maq = (ListView) findViewById(R.id.olst_maq);
        lst_maq.setClickable(true);

        this.olab_cte02 = (TextView) findViewById(R.id.lab_cte02);
        this.olab_cte02.setText("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]");

        String databasePath = getDatabasePath("one2009.db").getPath();
        db4 = io.requery.android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databasePath, null, null);

        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();


        cSqlLn = "";
        //dialogBox("EMRPRESA["+cId_emp+"] / CLIENTE["+cId_Cte+"] / SQL["+cSqlLn+"]");

        cSqlLn = cSqlLn + "SELECT ";
        cSqlLn = cSqlLn + "	(trim(maq.maqtc_chapa) || SUBSTR('                   ', 1, 20-length(trim(maq.maqtc_chapa))) || trim(maq.maqtc_modelo)) AS expr1 ";
        //cSqlLn = cSqlLn + "	maq.maqtc_id,(maq.maqtc_chapa||' - '||maq.maqtc_modelo) as cList ";
        cSqlLn = cSqlLn + "FROM maquinastc maq ";
        cSqlLn = cSqlLn + "INNER JOIN maquinas_lnk maql ON (maq.maqtc_id = maql.maqtc_id) ";
        cSqlLn = cSqlLn + "WHERE  maq.maqtc_tipomaq = 1 ";
        cSqlLn = cSqlLn + "AND    maql.cte_id='" + Global.cCte_Id + "' ";
        cSqlLn = cSqlLn + "AND    maql.emp_id='" + Global.cEmp_Id + "' ";
        cSqlLn = cSqlLn + "ORDER BY UPPER(trim(maq.maqtc_chapa))";

        data = db4.rawQuery(cSqlLn, null);

        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            data.moveToFirst();
            do {
                theList.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                lst_maq.setAdapter(listAdapter);
            } while (data.moveToNext());
        }

        btn_regr_maq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                Global.cMaq_Id = "";
                Global.cMaq_De = "";

                db4.close();
                finish();
            }
        });

        btn_sele_maq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = getIntent();
                setResult(RESULT_OK, i);


                db4.close();
                finish();
            }
        });

        btn_foto_maq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent Int_TakePhoto = new Intent(getApplicationContext(), take_photo.class);
                Int_TakePhoto.putExtra("show_image", 1);
                startActivity(Int_TakePhoto);
            }
        });

        lst_maq.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object o = listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                String selectedFromList = (lst_maq.getItemAtPosition(position).toString());

                Global.cMaq_Id = selectedFromList.substring(0, 19).trim();
                Global.cMaq_De = selectedFromList.substring(20, selectedFromList.length()).trim();

                btn_sele_maq.setEnabled(true);
                btn_foto_maq.setEnabled(true);
            }
        });

    }

    private void dialogBox(String cMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(cMessage);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
