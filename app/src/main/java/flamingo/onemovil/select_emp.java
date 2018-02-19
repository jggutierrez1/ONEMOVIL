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
import android.widget.Toast;

import java.util.ArrayList;

public class select_emp extends AppCompatActivity {

    private Button btn_regr_emp, btn_sele_emp;
    private ListView lst_emp;
    private SQLiteDatabase db3;
    private Cursor data;
    private String cSqlLn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_emp);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        getWindow().getDecorView().getRootView().setBackgroundColor(Color.rgb(204, 217, 255));

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        this.btn_regr_emp = (Button) findViewById(R.id.obtn_regr_emp);
        this.btn_sele_emp = (Button) findViewById(R.id.obtn_sele_emp);
        this.lst_emp = (ListView) findViewById(R.id.olst_emp);

        this.lst_emp.setClickable(true);

        ArrayList<String> theList = new ArrayList<>();

        String cEmp_Sql = "" +
                "SELECT " +
                "( trim(emp_id) || SUBSTR('                   ', 1, 20-length(trim(emp_id))) || trim(emp_descripcion)) AS expr1 " +
                "FROM empresas " +
                "WHERE emp_inactivo=0 " +
                "ORDER BY UPPER(trim(emp_descripcion))";

        String databasePath = getDatabasePath("one2009.db").getPath();
        db3 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
        data = db3.rawQuery(cEmp_Sql                , null);

        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            data.moveToFirst();
            do {
                theList.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                lst_emp.setAdapter(listAdapter);
            } while (data.moveToNext());
        }

        btn_regr_emp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                Global.cEmp_Id = "";
                Global.cEmp_De = "";

                Global.oGen_Cursor.close();
                finish();
            }
        });

        btn_sele_emp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = getIntent();
                setResult(RESULT_OK, i);

                Global.oGen_Cursor.close();
                finish();
            }
        });

        lst_emp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object o = listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                String selectedFromList = (lst_emp.getItemAtPosition(position).toString());

                Global.cEmp_Id = selectedFromList.substring(0, 19).trim();
                Global.cEmp_De = selectedFromList.substring(20, selectedFromList.length()).trim();

                btn_sele_emp.setEnabled(true);
            }
        });

    }

}
