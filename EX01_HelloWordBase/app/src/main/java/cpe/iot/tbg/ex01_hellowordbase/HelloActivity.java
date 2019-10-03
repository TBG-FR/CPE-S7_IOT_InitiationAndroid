package cpe.iot.tbg.ex01_hellowordbase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelloActivity extends AppCompatActivity {

    boolean btnState = true;
    TextView tv;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        tv = findViewById(R.id.tv_HelloWord);
        tv.setText(R.string.txt_helloworld_long);

        btn = findViewById(R.id.btn_Button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(btnState)
                {
                    //((Button) v.findViewById(R.id.btn_Button)).setText(R.string.txt_btn);
                    btn.setText(R.string.txt_btn);
                    btnState = false;
                }
                else
                {
                    //((Button) v.findViewById(R.id.btn_Button)).setText("Alors ? C'est pas bô ça ? :D");
                    btn.setText("Alors ? C'est pas bô ça ? :D");
                    btnState = true;
                }
            }
        });
    }
}
