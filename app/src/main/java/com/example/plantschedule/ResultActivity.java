package com.example.plantschedule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.plantschedule.Zoompic.adjustImage2;

public class ResultActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvDescri;
    private TextView tvSpeci;
    private ImageView ivPic;
    private BaseAdapter adapter;
    private List<Plant> plantList = new ArrayList<Plant>();
    private ListView lvSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String str[] = {""};
        str[0] = intent.getStringExtra("searchname");
        Log.w("getExtra",str[0]);

        lvSearch = (ListView) findViewById(R.id.listSearchPlant);

        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.PlantEntry.COLUMN_PNAME,
                PlantContract.PlantEntry.COLUMN_PSNAME,
                PlantContract.PlantEntry.COLUMN_PSPECIES,
                PlantContract.PlantEntry.COLUMN_PPICPATH
        };

// Filter results WHERE "title" = 'My Title'
        String selection = PlantContract.PlantEntry.COLUMN_PNAME+" like ?";
        String[] selectionArgs = {"%" + str[0] + "%"};


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                PlantContract.PlantEntry.COLUMN_PNAME + " DESC";

        Cursor cursor = db.query(
                PlantContract.PlantEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        for (int i = 0; i < cursor.getCount(); i++) {

            cursor.moveToPosition(i);
            Plant pl = new Plant();
            pl.name = cursor.getString(0);
            pl.sname = cursor.getString(1);
            pl.speci = cursor.getString(2);
            pl.path = cursor.getString(3);
            plantList.add(pl);

        }
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return plantList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = ResultActivity.this.getLayoutInflater();
                View view;

                if (convertView == null) {
                    view = inflater.inflate(R.layout.item_layout, null);
                } else {
                    view = convertView;
                    Log.i("info", "有缓存，不需要重新生成" + position);
                }
                tvName = (TextView) view.findViewById(R.id.tv_lineone);
                tvDescri = (TextView) view.findViewById(R.id.tv_linthree);
                tvSpeci = (TextView) view.findViewById(R.id.tv_linetwo);
                ivPic = (ImageView)view.findViewById(R.id.item_image);
                tvName.setText(plantList.get(position).name);
                tvDescri.setText(plantList.get(position).sname);
                tvSpeci.setText(plantList.get(position).speci);
                Bitmap bm = null;
                bm = adjustImage2(plantList.get(position).path,bm);
                ivPic.setImageBitmap(bm);
                return view;
            }
        };
        lvSearch.setAdapter(adapter);

        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = "";
                tvName = (TextView) view.findViewById(R.id.tv_lineone);//找到Textviewname
                str = tvName.getText().toString();//得到数据
                Toast.makeText(ResultActivity.this, "" + str, Toast.LENGTH_SHORT).show();//显示数据

                Intent it = new Intent(ResultActivity.this, SpecialInfoActivity.class); //
                Bundle b = new Bundle();
                b.putString("plantname", str);  //string
                it.putExtras(b);
                startActivity(it);
            }


        });
        cursor.close();
    }
    protected void BtnSearchClick(){
        Intent it = new Intent(ResultActivity.this, ResultActivity.class); //
        Bundle b = new Bundle();
        String str = "";
        str =((EditText) findViewById(R.id.et_search)).getText().toString();
        Log.w("BtnSearchClick",str);
        b.putString("searchname", str);  //string
        it.putExtras(b);
        startActivity(it);
        finish();
    }
}
