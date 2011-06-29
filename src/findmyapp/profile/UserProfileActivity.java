package findmyapp.profile;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UserProfileActivity extends Activity {
	
	public static final String APP_ID = "245885202095548";
	private static final String[] PERMS = new String[] { "user_events" };
	private Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;
	private TextView txtStatus;
	private Button btnLogin;
	private Button btnLogout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final Context c = this;
		
		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		txtStatus = (TextView) findViewById(R.id.textFacebook);
		btnLogin = (Button) findViewById(R.id.buttonLogin);
		btnLogout = (Button) findViewById(R.id.buttonLogout);
		
		if(facebook.isSessionValid()) {
			txtStatus.setText("User is already logged in");
		} else {
			txtStatus.setText("User is NOT logged in");
		}
		
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!facebook.isSessionValid()) {
					facebook.authorize((Activity) c, new DialogListener() {
						@Override
						public void onComplete(Bundle values) {
							txtStatus.setText("Logged in");
							// send facebook.getAccessToken() to back end with httpRequest
							
							try {
								HttpClient hc = new DefaultHttpClient();
								HttpPost request = new HttpPost("http://www.cuddly-zombie.com");
								HttpResponse hr = hc.execute(request);
								
								if(hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
									txtStatus.setText(EntityUtils.toString(hr.getEntity()));
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
						}

						@Override
						public void onFacebookError(FacebookError error) {
							txtStatus.setText("Facebook Error");
							error.printStackTrace();
						}

						@Override
						public void onError(DialogError e) {
							txtStatus.setText("We have an error!");
							e.printStackTrace();
						}

						@Override
						public void onCancel() {
							txtStatus.setText("Canceled");
						}
					});
				} else {
					txtStatus.setText("Already logged in");
				}
			}
		});
		
		btnLogout.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(facebook.isSessionValid()) {
						facebook.logout(c);
						txtStatus.setText("User has logged out");
					} else {
						txtStatus.setText("No user to log out");
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
}