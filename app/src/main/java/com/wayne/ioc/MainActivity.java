package com.wayne.ioc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();
    @ViewInject(R.id.btn1)
    Button btn;

    @ViewInject(R.id.btn2)
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,btn.toString());
    }

    @OnClick({R.id.btn1,R.id.btn2})
    public void Onclick(View view){
        Toast.makeText(this, view.getId()+"点击了", Toast.LENGTH_SHORT).show();
    }
}
