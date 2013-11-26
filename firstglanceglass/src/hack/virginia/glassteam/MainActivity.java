package hack.virginia.glassteam;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String EXTRA_PICTURE_FILE_PATH = "picture_file_path";
	private static final int TAKE_PHOTO = 0;
	private static final int SPEECH_REQUEST = 1;

	private CardScrollView scrollView;
	private Bitmap downsampledBitmap;

	private String selectedFilePath;
	private int selectedFilterIndex;
	private String caption = "";
	private boolean photoTakingPhase = true;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onResume() {
		super.onResume();

		BusFactory.getInstance().register(this);

		if (photoTakingPhase) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, TAKE_PHOTO);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		BusFactory.getInstance().unregister(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent resultIntent) {
		super.onActivityResult(requestCode, resultCode, resultIntent);
		Log.d(TAG, "onActivityResult called");

		switch (requestCode) {
		case TAKE_PHOTO:
			if (resultCode == RESULT_OK) {
				setContentView(R.layout.card_loading_image);

				final String path = resultIntent
						.getStringExtra(EXTRA_PICTURE_FILE_PATH);

				Intent intent = new Intent(this, PrepareFileIntentService.class);
				intent.putExtra(PrepareFileIntentService.EXTRA_IMAGE_PATH, path);
				startService(intent);

				photoTakingPhase = false;
			}
			break;
		case SPEECH_REQUEST:
			if (resultCode == RESULT_OK) {
				List<String> results = resultIntent
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				caption = results.get(0);
				Log.d(TAG, "Caption = " + caption);
				scrollView.deactivate();
				scrollView.setAdapter(new FilterCardScrollAdapter(
						downsampledBitmap, caption, this));
				scrollView.activate();

				scrollView.setSelection(selectedFilterIndex);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_add_caption:
			Intent speechIntent = new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			startActivityForResult(speechIntent, SPEECH_REQUEST);
			return true;
		default:
			throw new RuntimeException();
		}
	}
	
}
