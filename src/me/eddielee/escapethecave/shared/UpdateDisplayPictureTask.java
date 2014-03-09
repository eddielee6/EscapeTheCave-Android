package me.eddielee.escapethecave.shared;

import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class UpdateDisplayPictureTask extends AsyncTask<Uri, Void, Bitmap> {
	
	private final WeakReference<Context> contextReference;
	private final WeakReference<ImageView> imageViewReference;
	private final WeakReference<ProgressBar> progressBarReference;
	private final String fileName;
	
	public UpdateDisplayPictureTask(Context context, ImageView imageView, ProgressBar progressBar, String _filename) {
		contextReference = new WeakReference<Context>(context);
        imageViewReference = new WeakReference<ImageView>(imageView);
        progressBarReference = new WeakReference<ProgressBar>(progressBar);
        fileName = _filename;
    }
	
	@Override
	protected void onPreExecute() {
		if(imageViewReference != null && progressBarReference != null) {
			ImageView imageView = imageViewReference.get();
			ProgressBar progressBar = progressBarReference.get();
			if(imageView != null && progressBar != null) {
				imageView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
			}
		}
	}
		
	public static Bitmap scaleDownBitmap(Bitmap photo, float sizeScale) {
		float height = photo.getHeight();
		float width = photo.getWidth();
		
		if(height > sizeScale && width > sizeScale) {
			if(width > height) {
				//height
				float newHight = sizeScale;
				float ratio = newHight / height;
				float newWidth = width * ratio;
				
				Log.i("Image size", newHight+"x"+newWidth);
				
				Bitmap temp =  Bitmap.createScaledBitmap(photo, (int)newWidth, (int)newHight, true);
				return Bitmap.createBitmap(temp, temp.getWidth()/2 - temp.getHeight()/2, 0, temp.getWidth()/2 + temp.getHeight()/2, temp.getHeight());
			} else {
				//width
				float newWidth = sizeScale;
				float ratio = newWidth / width;
				float newHight = height * ratio;
				
				Log.i("Image size", newHight+"x"+newWidth);
				
				Bitmap temp =  Bitmap.createScaledBitmap(photo, (int)newWidth, (int)newHight, true);
				return Bitmap.createBitmap(temp, 0, temp.getHeight()/2 - temp.getWidth()/2, temp.getWidth(), temp.getHeight()/2 + temp.getWidth()/2);
			}
		}
		return photo;
	}
		
	@Override
	protected Bitmap doInBackground(Uri... uris) {
		if(contextReference != null) {
			Context context = contextReference.get();
			if(context != null) {
				try {
					Bitmap newDisplayPicture = Media.getBitmap(context.getContentResolver(), uris[0]);
					newDisplayPicture = scaleDownBitmap(newDisplayPicture, 375);
					FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			    	newDisplayPicture.compress(Bitmap.CompressFormat.PNG, 100, fos);
			    	fos.close();
			    	return newDisplayPicture;
				} catch (Exception e) {
					Log.e("Escape the cave", "Failed to set Display Picture. Reason: " + e.getMessage());
				}
			}
		}
		return null;
	}

    @Override
	protected void onPostExecute(Bitmap newDisplayPicture) {
    	if (isCancelled() || newDisplayPicture == null) {
    		return;
    	}
    	
    	if(imageViewReference != null && progressBarReference != null) {
			ImageView imageView = imageViewReference.get();
			ProgressBar progressBar = progressBarReference.get();
			if(imageView != null && progressBar != null) {
				imageView.setImageBitmap(newDisplayPicture);
				imageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			}
		}
    }
}