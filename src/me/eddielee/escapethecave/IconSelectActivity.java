package me.eddielee.escapethecave;

import me.eddielee.escapethecave.shared.ResourceSelectImageAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class IconSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icon_select_screen);
        
        Integer[] availableIcons = new Integer[] {
        		R.raw.display_picture_1,
        		R.raw.display_picture_2,
        		R.raw.display_picture_3,
        		R.raw.display_picture_4,
        		R.raw.display_picture_5,
        		R.raw.display_picture_6,
        		R.raw.display_picture_7,
        		R.raw.display_picture_8,
        		R.raw.display_picture_9,
        		R.raw.display_picture_10,
        		R.raw.display_picture_11,
        		R.raw.display_picture_12,
        		R.raw.display_picture_13,
        		R.raw.display_picture_14,
        		R.raw.display_picture_15,
        		R.raw.display_picture_16
        };
        
        GridView gridview = (GridView) findViewById(R.id.icon_select_gridview);
        gridview.setAdapter(new ResourceSelectImageAdapter(this, availableIcons));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("selectedId", id);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}