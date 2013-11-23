package hack.virginia.glasstutorial;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.List;

/**
 * The main application service that manages the lifetime of the compass live card and the objects
 * that help out with orientation tracking and landmarks.
 */
public class TutorialService extends Service {

    private static final String LIVE_CARD_ID = "tutorial";

    /**
     * A binder that gives other components access to the speech capabilities provided by the
     * service.
     */
    public class CompassBinder extends Binder {
        /**
         * Read the current heading aloud using the text-to-speech engine.
         */
        public void readHeadingAloud() {
            float heading = mOrientationManager.getHeading();

            Resources res = getResources();
            String[] spokenDirections = res.getStringArray(R.array.spoken_directions);
            String directionName = spokenDirections[MathUtils.getHalfWindIndex(heading)];

            int roundedHeading = Math.round(heading);
            int headingFormat;
            if (roundedHeading == 1) {
                headingFormat = R.string.spoken_heading_format_one;
            } else {
                headingFormat = R.string.spoken_heading_format;
            }

            String headingText = res.getString(headingFormat, roundedHeading, directionName);
            mSpeech.speak(headingText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private final CompassBinder mBinder = new CompassBinder();

    private OrientationManager mOrientationManager;
    private Landmarks mLandmarks;
    private TextToSpeech mSpeech;

    private TimelineManager mTimelineManager;
    private LiveCard mLiveCard;
    private CompassRenderer mRenderer;

    @Override
    public void onCreate() {
        super.onCreate();

        mTimelineManager = TimelineManager.from(this);

        // Even though the text-to-speech engine is only used in response to a menu action, we
        // initialize it when the application starts so that we avoid delays that could occur
        // if we waited until it was needed to start it up.
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
            }
        });

        SensorManager sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mOrientationManager = new OrientationManager(sensorManager, locationManager);
        mLandmarks = new Landmarks(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = mTimelineManager.getLiveCard(LIVE_CARD_ID);
            mRenderer = new CompassRenderer(this, mOrientationManager, mLandmarks);

            mLiveCard.enableDirectRendering(true).getSurfaceHolder().addCallback(mRenderer);
            mLiveCard.setNonSilent(true);

            // Display the options menu when the live card is tapped.
            Intent menuIntent = new Intent(this, CompassMenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mLiveCard.publish();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard.getSurfaceHolder().removeCallback(mRenderer);
            mLiveCard = null;
        }

        mSpeech.shutdown();

        mSpeech = null;
        mOrientationManager = null;
        mLandmarks = null;

        super.onDestroy();
    }
}
