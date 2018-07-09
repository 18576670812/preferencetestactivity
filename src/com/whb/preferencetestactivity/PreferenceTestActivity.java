package com.whb.preferencetestactivity;  
  
import com.whb.preferencetestactivity.R;

import android.app.AlertDialog;  
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;  
import android.content.SharedPreferences;
import android.os.Bundle;  
import android.preference.EditTextPreference;  
import android.preference.ListPreference;  
import android.preference.Preference;  
import android.preference.PreferenceActivity;  
import android.preference.PreferenceScreen;  
import android.view.WindowManager;
import android.widget.Toast;  
  
public class PreferenceTestActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {  
	private static final String SHAREDPREFERENCES = "SharedInfo";
	private static final String sharedpreferences = "sharedinfo";
  
    private EditTextPreference nickName;  
    private ListPreference textSize;  
    private Preference cleanHistory;  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
        addPreferencesFromResource(R.xml.preference);
        nickName = (EditTextPreference) findPreference("nickName");  
        textSize = (ListPreference) findPreference("textSize");  
        cleanHistory = findPreference("cleanHistory");  
          
        //为nickName和textSize注册Preference.OnPreferenceChangeListener监听事件  
        //当值更改时我们可以立即更新summary  
        nickName.setOnPreferenceChangeListener(this);  
        textSize.setOnPreferenceChangeListener(this);  
          
        initSummary();  
        
        createSharedPreference(SHAREDPREFERENCES);
        createSharedPreference(sharedpreferences);
    }  
      
    //初始化summary  
    private void initSummary() {  
        nickName.setSummary(nickName.getText());  
          
        setTextSizeSummary(textSize.getValue());  
    }  
      
    private void setTextSizeSummary(String textSizeValue) {  
        if (textSizeValue.equals("0")) {  
            textSize.setSummary("小");  
        } else if (textSizeValue.equals("1")) {  
            textSize.setSummary("中");  
        } else if (textSizeValue.equals("2")) {  
            textSize.setSummary("大");  
        }  
    }  
  
    /** 
     * 重写PreferenceActivity的onPreferenceTreeClick方法 
     * 在首选项被点击时 做出相应处理操作 
     */  
    @Override  
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {  
        if (preference == cleanHistory) {  
            new AlertDialog.Builder(this)  
                    .setTitle("清除历史记录")  
                    .setMessage("是否真的要清除历史记录？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            //cleaning history...  
                            Toast.makeText(PreferenceTestActivity.this, "清除成功", Toast.LENGTH_SHORT).show();  
                        }  
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            dialog.dismiss();  
                        }  
                    }).create().show();  
        } else if(preference == textSize) {
        	//textSize.setDefaultValue("0");
        	textSize.setValue("0");
        	clickedPreference(preference);
        	Toast.makeText(PreferenceTestActivity.this, "reset text size to 0", Toast.LENGTH_SHORT).show();
        } else if(preference == nickName) {
        	clickedPreference(preference);
        }
        return true;  
    }  
    
    private void clickedPreference(Preference preference) {
    	if(preference == textSize) {
        	// setEnabled(false); 将置灰该 Preference选项
        	textSize.setEnabled(!textSize.isEnabled());
        	// setSelectable(false); 点击该 Preference 选项将无选中效果
        	textSize.setSelectable(false);
    	} else if(preference == nickName) {
    		int flags = getWindow().getAttributes().flags;
    		if(false) {
	    		if((flags & WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED) != 0) {
	    			flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
	    		} else {
	    			flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
	    		}
    		} else {
	    		if((flags & WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED) == 0) {
	    			flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
	    		} else {
	    			//flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
	    			flags |= WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
	    			Toast.makeText(this, "dismiss keygaurd", Toast.LENGTH_SHORT).show();
	    		}
    		}

    		if(true) {
    			getWindow().addFlags(flags);
    		} else {
    			WindowManager.LayoutParams lp = getWindow().getAttributes();
    			lp.flags = flags;
    			getWindow().setAttributes(lp);
    	    }
    	}
    }
    
    private boolean isKeyguardLocked() {
    	boolean isLocked = false;
    	KeyguardManager mKM = (KeyguardManager)this.getSystemService(KEYGUARD_SERVICE);
    	
    	if(mKM != null) {
    		isLocked = mKM.isKeyguardLocked();
    	}
    	Toast.makeText(this, isLocked? "Keyguard is locked" : "Keyguard is unlocked", Toast.LENGTH_SHORT).show();
    	return isLocked;
    }
      
    /** 
     * 重写Preference.OnPreferenceChangeListener的onPreferenceChange方法 
     * 当首选项的值更改时 做出相应处理操作 
     */  
    @Override  
    public boolean onPreferenceChange(Preference preference, Object newValue) {  
        if (preference == nickName) {  
            nickName.setSummary(newValue.toString());  
        } else if (preference == textSize) {  
            setTextSizeSummary(newValue.toString());  
        }  
        return true;  
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		isKeyguardLocked();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		isKeyguardLocked();
		super.onResume();
	}
    
    private void createSharedPreference(String sharedPreferencesName)  {
    	//SharedPreferences sharedPreferences = ((Context)this).getSharedPreferences(sharedPreferencesName, Context.MODE_WORLD_READABLE);
    	//SharedPreferences sharedPreferences = ((Context)this).getSharedPreferences(sharedPreferencesName, Context.MODE_WORLD_WRITEABLE);
    	SharedPreferences sharedPreferences = ((Context)this).getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	if(sharedPreferencesName.equals(SHAREDPREFERENCES)){
    		editor.putString("String-Value", "HELLO, WORLD");
    	} else if(sharedPreferencesName.equals(sharedpreferences)) {
    		editor.putString("String-Value", "hello, world");
    	}
    	editor.commit();
    }
}