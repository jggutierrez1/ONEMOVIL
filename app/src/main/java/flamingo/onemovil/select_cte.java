package flamingo.onemovil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

public class select_cte extends AppCompatActivity {

    private Button btn_regr_cte, btn_sele_cte, btn_clear_cte, btn_find_cte;
    private ListView olst_cte2;
    private SQLiteDatabase db3;
    private Cursor data;
    private String cSqlLn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cte);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        getWindow().getDecorView().getRootView().setBackgroundColor(Color.rgb(204, 217, 255));

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        this.btn_regr_cte = (Button) findViewById(R.id.obtn_regr_cte);
        this.btn_sele_cte = (Button) findViewById(R.id.obtn_sele_cte);
        //btn_clear_cte = (Button) findViewById(R.id.obtn_clear);
        //btn_find_cte = (Button) findViewById(R.id.obtn_find);
        this.olst_cte2 = (ListView) findViewById(R.id.olst_cte);

        this.olst_cte2.setClickable(true);

        String databasePath = getDatabasePath("one2009.db").getPath();
        db3 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();

        String cCte_Sql = "" +
                "SELECT " +
                "( trim(clientes.cte_id) || SUBSTR('                   ', 1, 20-length(trim(clientes.cte_id))) || trim(clientes.cte_nombre_loc)) AS expr1 " +
                "FROM clientes " +
                "INNER JOIN ( " +
                "   SELECT " +
                "       maquinas_lnk.emp_id,maquinas_lnk.cte_id " +
                "   FROM maquinas_lnk " +
                "   INNER JOIN maquinastc ON (maquinas_lnk.maqtc_id=maquinastc.maqtc_id) AND (maquinastc.maqtc_inactivo=0) " +
                "   WHERE (maquinas_lnk.emp_id='" + Global.cEmp_Id + "') " +
                "   GROUP BY maquinas_lnk.emp_id,maquinas_lnk.cte_id " +
                "			) maquinas_lnk2 on maquinas_lnk2.cte_id=clientes.cte_id " +
                "ORDER BY  UPPER(trim(clientes.cte_nombre_loc)) ";

        data = db3.rawQuery(cCte_Sql, null);

        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            data.moveToFirst();
            do {
                theList.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                olst_cte2.setAdapter(listAdapter);
            } while (data.moveToNext());
        }

        btn_regr_cte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                Global.cCte_Id = "";
                Global.cCte_De = "";

                db3.close();
                finish();
            }
        });

        btn_sele_cte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = getIntent();
                setResult(RESULT_OK, i);

                db3.close();
                finish();
            }
        });

        olst_cte2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object o = listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                String selectedFromList = (olst_cte2.getItemAtPosition(position).toString());

                Global.cCte_Id = selectedFromList.substring(0, 19).trim();
                Global.cCte_De = selectedFromList.substring(20, selectedFromList.length()).trim();

                btn_sele_cte.setEnabled(true);
            }
        });

    }
}
