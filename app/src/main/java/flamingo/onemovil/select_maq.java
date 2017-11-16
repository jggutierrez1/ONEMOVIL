package flamingo.onemovil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class select_maq extends AppCompatActivity {

    private Button btn_regr_maq, btn_sele_maq, btn_clear_maq, btn_find_maq;
    ;
    private TextView text_fnd;
    private ListView lst_maq;
    private SQLiteDatabase db4;
    private Cursor data;
    private String cId_emp, cId_Cte, cId_Maq;
    private String cSqlLn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_maq);
        getWindow().getDecorView().getRootView().setBackgroundColor(Color.rgb(204, 255, 204));

        btn_regr_maq = (Button) findViewById(R.id.obtn_regr_maq);
        btn_sele_maq = (Button) findViewById(R.id.obtn_sele_maq);
        //btn_clear_maq = (Button) findViewById(R.id.obtn_clear);
        //btn_find_maq = (Button) findViewById(R.id.obtn_find);
        //text_fnd_maq= (TextView) findViewById(R.id.otext_fnd);

        lst_maq = (ListView) findViewById(R.id.olst_maq);
        lst_maq.setClickable(true);

        String databasePath = getDatabasePath("one2009.db").getPath();
        db4 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();


        Intent i = getIntent();
        cId_emp =  i.getStringExtra("EMP_ID");
        cId_Cte =i.getStringExtra("CTE_ID");

        cSqlLn = "";
        //dialogBox("EMRPRESA["+cId_emp+"] / CLIENTE["+cId_Cte+"] / SQL["+cSqlLn+"]");

        cSqlLn = cSqlLn + "SELECT ";
        cSqlLn = cSqlLn + "	(SUBSTR(maq.maqtc_chapa || '                    ', 1, 20) || maq.maqtc_modelo) AS expr1 ";
        //cSqlLn = cSqlLn + "	maq.maqtc_id,(maq.maqtc_chapa||' - '||maq.maqtc_modelo) as cList ";
        cSqlLn = cSqlLn + "FROM maquinastc maq ";
        cSqlLn = cSqlLn + "INNER JOIN maquinas_lnk maql ON (maq.maqtc_id = maql.maqtc_id) ";
        cSqlLn = cSqlLn + "WHERE  maq.maqtc_tipomaq = 1 ";
        cSqlLn = cSqlLn + "AND    maql.cte_id='" + cId_Cte + "' ";
        cSqlLn = cSqlLn + "AND    maql.emp_id='" + cId_emp + "' ";


        data = db4.rawQuery(cSqlLn, null);

        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                theList.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                lst_maq.setAdapter(listAdapter);
            }
        }

        btn_regr_maq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                i.putExtra("MAQ_ID", "");
                setResult(RESULT_CANCELED, i);

                db4.close();
                finish();
            }
        });

        btn_sele_maq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(getApplicationContext(), "Selecciono[" + cId_Maq + "]:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                i.putExtra("MAQ_ID", cId_Maq);
                setResult(RESULT_OK, i);

                db4.close();
                finish();
            }
        });

        lst_maq.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object o = listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                String selectedFromList = (lst_maq.getItemAtPosition(position).toString());
                cId_Maq = selectedFromList.substring(0, 19).trim();
                Intent i = getIntent();
                i.putExtra("CTE_ID", cId_Maq);
                btn_sele_maq.setEnabled(true);
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

        alertDialogBuilder.setNegativeButton("cancel",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
