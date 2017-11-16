package flamingo.onemovil;

import android.content.Context;
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

public class select_cte extends AppCompatActivity {

    private Button btn_regr_cte, btn_sele_cte, btn_clear_cte, btn_find_cte;
    private TextView text_fnd;
    private ListView olst_cte2;
    private SQLiteDatabase db3;
    private Cursor data;
    private String cId_Cte;
    private String cSqlLn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cte);
        getWindow().getDecorView().getRootView().setBackgroundColor(Color.rgb(204, 217, 255));

        btn_regr_cte = (Button) findViewById(R.id.obtn_regr_cte);
        btn_sele_cte = (Button) findViewById(R.id.obtn_sele_cte);
        //btn_clear_cte = (Button) findViewById(R.id.obtn_clear);
        //btn_find_cte = (Button) findViewById(R.id.obtn_find);
        //text_fnd_cte = (TextView) findViewById(R.id.otext_fnd);

        olst_cte2 = (ListView) findViewById(R.id.olst_cte);
        olst_cte2.setClickable(true);

        String databasePath = getDatabasePath("one2009.db").getPath();
        db3 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();

        data = db3.rawQuery("SELECT ( SUBSTR(cte_id || '                    ', 1, 20) || cte_nombre_loc) AS expr1 FROM clientes ORDER BY cte_nombre_loc", null);

        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                theList.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                olst_cte2.setAdapter(listAdapter);
            }
        }

        btn_regr_cte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                i.putExtra("CTE_ID", "");
                setResult(RESULT_CANCELED, i);

                db3.close();
                finish();
            }
        });

        btn_sele_cte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(getApplicationContext(), "Selecciono[" + cId_Cte + "]:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                i.putExtra("CTE_ID", cId_Cte);
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
                cId_Cte = selectedFromList.substring(0, 19).trim();
                Intent i = getIntent();
                i.putExtra("CTE_ID", cId_Cte);
                btn_sele_cte.setEnabled(true);
            }
        });

    }
}
