package findmyapp.profile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class UserProfileActivity extends Activity {
	
	public static final String APP_ID = "245885202095548";
//	private static final String[] PERMS = new String[] { "user_events" };
	private Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		if (APP_ID == null || APP_ID.equals("")) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running");
		}
		
		facebook.authorize(this, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {
				TextView tv = (TextView) findViewById(R.id.textFacebook);
				tv.setText("Logged in " + facebook.getAccessToken());
			}

			@Override
			public void onFacebookError(FacebookError error) {
				TextView tv = (TextView) findViewById(R.id.textFacebook);
				tv.setText("Facebook Error");
				error.printStackTrace();
			}

			@Override
			public void onError(DialogError e) {
				TextView tv = (TextView) findViewById(R.id.textFacebook);
				tv.setText("We have an error!");
				e.printStackTrace();
			}

			@Override
			public void onCancel() {
				TextView tv = (TextView) findViewById(R.id.textFacebook);
				tv.setText("Cancel");
			}
		});
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
}