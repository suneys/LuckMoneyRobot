package com.example.htmltest11;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
	
	WebView webView;
	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE=1; 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode==FILECHOOSER_RESULTCODE){
			if(null == mUploadMessage) return;
			Uri result = data == null || resultCode != RESULT_OK ? null: data.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.webView);
		webView.setInitialScale(50);
		WebSettings settings = webView.getSettings();

		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		 webView.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	               //����ֵ��true��ʱ�����ȥWebView�򿪣�Ϊfalse����ϵͳ�����������������
	             view.loadUrl(url);
	            return true;
	        }
	       });
		 webView.setWebChromeClient(new WebChromeClient(){
			 public void openFileChooser(ValueCallback<Uri> uploadMsg) {    
				  
		            mUploadMessage = uploadMsg;    
		            Intent i = new Intent(Intent.ACTION_GET_CONTENT);    
		            i.addCategory(Intent.CATEGORY_OPENABLE);    
		            i.setType("image/*");    
		            MainActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);    
		  
		           }  
		  
		        // For Android 3.0+  
		           public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {  
		           mUploadMessage = uploadMsg;  
		           Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
		           i.addCategory(Intent.CATEGORY_OPENABLE);  
		           i.setType("*/*");  
		           MainActivity.this.startActivityForResult(  
		           Intent.createChooser(i, "File Browser"),  
		           FILECHOOSER_RESULTCODE);  
		           }  
		  
		        //For Android 4.1  
		           public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){  
		               mUploadMessage = uploadMsg;    
		               Intent i = new Intent(Intent.ACTION_GET_CONTENT);    
		               i.addCategory(Intent.CATEGORY_OPENABLE);    
		               i.setType("image/*");    
		               MainActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), MainActivity.FILECHOOSER_RESULTCODE );  
		  
		           }  
		 });
		 webView.loadUrl("http://192.168.1.216/M/Logins/Login/");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
