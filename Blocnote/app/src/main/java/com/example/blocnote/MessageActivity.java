package com.example.blocnote;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MessageActivity extends AppCompatActivity {
Toolbar toolbar;
ImageView image_profile;
TextView mreceiverName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        image_profile=findViewById(R.id.img);
        mreceiverName=findViewById(R.id.text);



        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
        Intent intent =getIntent();
       String receiverName=intent.getStringExtra("receiverName");
       String receiverId=intent.getStringExtra("receiverId");
       String receiverPhoto=intent.getStringExtra("receiverPhoto");
        Toast.makeText(this,""+receiverName,Toast.LENGTH_LONG).show();


        if (receiverPhoto.equals("default"))
          image_profile.setImageResource(R.drawable.user_logo);
          else
        Glide.with(this).load(receiverPhoto).into(image_profile);
          mreceiverName.setText(receiverName);

    }


}
