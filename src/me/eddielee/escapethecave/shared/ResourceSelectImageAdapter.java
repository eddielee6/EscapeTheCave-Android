package me.eddielee.escapethecave.shared;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ResourceSelectImageAdapter extends BaseAdapter {
    private Context context;
    private Integer[] resources;

    public ResourceSelectImageAdapter(Context c, Integer[] resourceList) {
    	context = c;
        resources = resourceList;
    }

    @Override
	public int getCount() {
        return resources.length;
    }

    @Override
	public Object getItem(int position) {
        return null;
    }

    @Override
	public long getItemId(int position) {
        return resources[position];
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(180, 180));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView)convertView;
        }

        imageView.setImageResource(resources[position]);
        return imageView;
    }
}