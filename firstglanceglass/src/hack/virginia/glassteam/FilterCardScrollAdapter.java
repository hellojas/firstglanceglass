package hack.virginia.glassteam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;

public class FilterCardScrollAdapter extends CardScrollAdapter {
	private final Bitmap bitmap;
	private final Context context;
	private final String caption;


	public FilterCardScrollAdapter(Bitmap bitmap, String caption,
			Context context) {
		this.bitmap = bitmap;
		this.context = context;
		this.caption = caption;
	}

	@Override
	public int findIdPosition(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int findItemPosition(Object arg0) {
		throw new UnsupportedOperationException();
	}


	@Override
	public Object getItem(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.card_filtered_image, arg2, false);
		ImageView filteredImage = (ImageView) v
				.findViewById(R.id.filtered_image);
		TextView filteredName = (TextView) v.findViewById(R.id.filter_name);
		TextView captionText = (TextView) v.findViewById(R.id.caption);

		Bitmap bm = bitmap.copy(Config.ARGB_8888, true);

		filteredImage.setImageBitmap(bm);

		if (!TextUtils.isEmpty(caption)) {
			Log.d("asdf", "caption not empty");
			captionText.setVisibility(View.VISIBLE);
			captionText.setText(caption);
		} else {
			captionText.setVisibility(View.GONE);
		}
		return v;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
