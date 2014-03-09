package me.eddielee.escapethecave;

import java.io.File;
import java.io.InputStream;

import me.eddielee.escapethecave.dataaccess.UserDetailsRepository;
import me.eddielee.escapethecave.shared.HelperMethods;
import me.eddielee.escapethecave.shared.UpdateDisplayPictureTask;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class OptionsActivity extends AppActivity {
	
	private UserDetailsRepository _userDetailsRepository;
	
	private static final int SET_DISPLAY_PICTURE_FROM_CAMERA = 1;
	private static final int SET_DISPLAY_PICTURE_FROM_GALLERY = 2;
	private static final int SET_DISPLAY_PICTURE_FROM_DEFAULTS = 3;
	
	private Uri newDisplayPictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_screen);
        
        _userDetailsRepository = new UserDetailsRepository(this);
        
        setUpUsersNameControl();
        setUpSkillLevelControl();
        setCurrentValues();
        
        //Disable the take photo button if device does not have a camera
        if(!HelperMethods.isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
        	Button takePhotoButton = (Button)findViewById(R.id.button_take_photo);
        	takePhotoButton.setEnabled(false);
        }
    }
    
    private void setUpUsersNameControl() {
    	EditText userNameEditText = (EditText)findViewById(R.id.user_name);
    	userNameEditText.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {
            	_userDetailsRepository.SetNameForUser(USER_ID, s.toString());
            }
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
    }
    
    private void setUpSkillLevelControl() {
		RatingBar skillRatingBar = (RatingBar)findViewById(R.id.user_skill);
	 
		skillRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				TextView skillRatingName = (TextView)findViewById(R.id.user_skill_name);
				
				_userDetailsRepository.SetSkillLevelForUser(USER_ID, (int)rating);
				
				switch((int)rating) {
				case 0:
	    			skillRatingName.setText(getResources().getString(R.string.skill_level_0));
					break;
				case 1:
					skillRatingName.setText(getResources().getString(R.string.skill_level_1));
					break;
				case 2:
					skillRatingName.setText(getResources().getString(R.string.skill_level_2));
					break;
				case 3:
					skillRatingName.setText(getResources().getString(R.string.skill_level_3));
					break;
				case 4:
					skillRatingName.setText(getResources().getString(R.string.skill_level_4));
					break;
				case 5:
					skillRatingName.setText(getResources().getString(R.string.skill_level_5));
					break;
				}
			}
		});
    }
    
    private void setCurrentValues() {
    	ImageView displayPictureView = (ImageView)findViewById(R.id.user_display_picture);
    	
    	File displayPictureFile = new File(getFilesDir(), DISPLAY_PICTURE_FILE_NAME);
    	if(displayPictureFile.exists()) {
    		String displayPicturePath = getFilesDir() + "/" + DISPLAY_PICTURE_FILE_NAME;
        	Bitmap displayPicture = BitmapFactory.decodeFile(displayPicturePath);
        	displayPictureView.setImageBitmap(displayPicture);
    	} else {
    		InputStream is = getResources().openRawResource(R.raw.display_picture_1);
    		displayPictureView.setImageBitmap(BitmapFactory.decodeStream(is));
    	}
    	
    	EditText userName = (EditText)findViewById(R.id.user_name);
    	userName.setText(_userDetailsRepository.GetNameForUser(USER_ID));
    	
    	RatingBar skillRatingBar = (RatingBar)findViewById(R.id.user_skill);
    	skillRatingBar.setRating(_userDetailsRepository.GetSkillLevelForUser(USER_ID));
    }
    
    public void setDisplayPictureFromCameraClicked(View view) {
    	ContentValues values = new ContentValues();
    	values.put(MediaColumns.TITLE, "Display picture from escape the cave");
    	newDisplayPictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, newDisplayPictureUri);
    	startActivityForResult(intent, SET_DISPLAY_PICTURE_FROM_CAMERA);
    }
    
    public void setDisplayPictureFromGalleryClicked(View view) {
    	Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SET_DISPLAY_PICTURE_FROM_GALLERY);
    }
    
    public void setDisplayPictureFromDefaultsClicked(View view) {
    	Intent intent = new Intent(this, IconSelectActivity.class);
		startActivityForResult(intent, SET_DISPLAY_PICTURE_FROM_DEFAULTS);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		UpdateDisplayPictureTask updateDisplayPictureTask = new UpdateDisplayPictureTask(this,(ImageView)findViewById(R.id.user_display_picture), (ProgressBar)findViewById(R.id.display_picture_spinner), DISPLAY_PICTURE_FILE_NAME);
	    	switch(requestCode) {
	    	case SET_DISPLAY_PICTURE_FROM_GALLERY:
	    		if(data != null && data.getData() != null) {
	    			updateDisplayPictureTask.execute(data.getData());
	    		}
	    		break;
	    	case SET_DISPLAY_PICTURE_FROM_CAMERA:
	    		ContentResolver cr = getContentResolver();
                cr.notifyChange(newDisplayPictureUri, null);
    			updateDisplayPictureTask.execute(newDisplayPictureUri);
	    		break;
	    	case SET_DISPLAY_PICTURE_FROM_DEFAULTS:
	    		long selectedResourceId = data.getLongExtra("selectedId", -1);
	    		if(selectedResourceId > 0) {
	    			Uri path = Uri.parse("android.resource://" + this.getPackageName() + "/raw/" + selectedResourceId);
	    			updateDisplayPictureTask.execute(path);
	    		}
	    		break;
	    	}
    	}
        super.onActivityResult(requestCode, resultCode, data);
    } 
}