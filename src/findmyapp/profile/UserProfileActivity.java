package findmyapp.profile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileActivity extends Activity {
	
	public static final String APP_ID = "245885202095548";
	private static final String[] PERMS = new String[] { "user_events" };
	private Facebook facebook;
	
	private TextView txtStatus;
	private Button btnLogin;
	private Button btnLogout;
	
//	private String fbinfo = "{ \"id\": \"772612305\", \"name\": \"Torstein M. Barkve\", \"first_name\": \"Torstein\", \"last_name\": \"M. Barkve\", \"link\": \"http://www.facebook.com/profile.php?id=772612305\", \"birthday\": \"05/10/1990\", \"education\": [    {\"school\": { \"id\": \"109956045693499\", \"name\": \"St. Svithun Videreg\u00e5ende Skole\"},\"type\": \"High School\"    },    {\"school\": { \"id\": \"109449622407837\", \"name\": \"Oslo University College\"},\"year\": { \"id\": \"136328419721520\", \"name\": \"2009\"},\"concentration\": [ {    \"id\": \"135775433156356\",    \"name\": \"Anvendt datateknologi\" }],\"type\": \"College\"    } ], \"gender\": \"male\", \"timezone\": 2, \"locale\": \"nn_NO\", \"verified\": true, \"updated_time\": \"2011-06-06T12:18:15+0000\" }";
	
	private JSONObject fbJson;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Save context for use in application, needed for access inside button listeners
		final Context mainView = this;
		
		facebook = new Facebook(APP_ID);
		
		// Init
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
					facebook.authorize((Activity) mainView, new DialogListener() {
						@Override
						public void onComplete(Bundle values) {
							TextView tvFbId = (TextView) findViewById(R.id.fbId);
						//	String httpRequest = "http://192.168.16.40:8080/findmyapp/auth?accessToken=" + URLEncoder.encode(facebook.getAccessToken());
							String httpRequest = "http://findmyapp.net/findmyapp/auth?accessToken=" + URLEncoder.encode(facebook.getAccessToken());
							
							try {
								HttpClient hc = new DefaultHttpClient();
								HttpGet request = new HttpGet(httpRequest);
								HttpResponse response = hc.execute(request); // Returns json string with user data
								
								txtStatus.setText("HttpRequest Status Code: " + response.getStatusLine().getStatusCode()); //DEBUG
								
								if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // if status code is 200
									
									// Grab json object
									fbJson = new JSONObject(EntityUtils.toString(response.getEntity()));
									
									ImageView imFbImage = (ImageView) findViewById(R.id.fbImage);
									tvFbId = (TextView) findViewById(R.id.fbId);
									TextView tvFbName = (TextView) findViewById(R.id.fbName);
									
									/* IGNORE
									// Image cannot be replaced in this way, see example on how to do it:
									// http://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
									String req = "http://graph.facebook.com/" + fbJson.getString("id") + "/picture";
								//	Uri uri = Uri.parse(facebook.request(req));
								//	imFbImage.setImageURI(uri);
								 	*/
									
									tvFbId.setText(fbJson.getString("id"));
									tvFbName.setText(fbJson.getString("name"));
									
									txtStatus.setText("Logged in as " + fbJson.getString("name")); //DEBUG
							//		txtStatus.setText(EntityUtils.toString(hr.getEntity())); // DEBUG
									
								}

							} catch (Exception e) {
								// TODO: handle exception
								txtStatus.setText(e.getMessage());
							}
						//	tvFbId.setText("Test"); // DEBUG
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
						facebook.logout(mainView);
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