package com.example.blocnote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;

import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blocnote.Adapter.RecyclerViewAdapterFriends;
import com.example.blocnote.Adapter.RequestViewAdapter;
import com.example.blocnote.Fragments.Fragment_events;
import com.example.blocnote.Fragments.Fragment_friends;
import com.example.blocnote.Fragments.Fragment_messages;
import com.example.blocnote.Fragments.Fragment_request;
import com.example.blocnote.Fragments.Fragment_users;
import com.example.blocnote.model.UserClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Fragment_messages.OnItemSelectedListener {
    Fragment_events eventF;
    Fragment_messages messF;
    Fragment_users userF;
    Fragment_request  requF;
    Fragment_friends friF;

    RecyclerViewAdapterFriends adapter;
 FirebaseAuth mAuth;
    FirebaseUser fuser;
    DatabaseReference reference;
    private ImageView user_profil;
    private TextView user_name;
    private TextView user_txt;
    private static TextView notif;
    private static TextView mess;

    private String title[]={"Trouver des amis","Messages","Evenements","Notifications"};
    Toolbar toolbar;
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (eventF!=null&&eventF.isVisible())
            outState.putString("LastVisible","eventF");
        else if (userF!=null&&userF.isVisible())
            outState.putString("LastVisible","userF");
        else if (requF!=null&&requF.isVisible())
            outState.putString("LastVisible","requF");
        else if (friF!=null&&friF.isVisible())
            outState.putString("LastVisible","friF");
        else
            outState.putString("LastVisible","messF");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Fragment_events eventF= (Fragment_events) getSupportFragmentManager().findFragmentByTag("eventfragment");
        Fragment_messages messF= (Fragment_messages) getSupportFragmentManager().findFragmentByTag("messfragment");
        Fragment_users userF= (Fragment_users) getSupportFragmentManager().findFragmentByTag("userfragment");
        Fragment_request  requF= (Fragment_request) getSupportFragmentManager().findFragmentByTag("requfragment");
        Fragment_friends  friF= (Fragment_friends) getSupportFragmentManager().findFragmentByTag("friendsfragment");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user_profil=(ImageView)findViewById(R.id.imageView);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        final View viewHeaderProfile=getLayoutInflater().inflate(R.layout.nav_header_profile,navigationView,true);

        notif=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_notification));

        mess=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_mess));

        user_name=(TextView)viewHeaderProfile.findViewById(R.id.user_name);
        user_profil=(ImageView)viewHeaderProfile.findViewById(R.id.imageView) ;
        user_txt=(TextView) viewHeaderProfile.findViewById(R.id.txt_filiere) ;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title[1]);
        setSupportActionBar(toolbar);


  if (savedInstanceState==null)
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_messages()).commit();
  else if (savedInstanceState.getString("LastVisible").equals("eventF"))
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_events()).commit();

else if (savedInstanceState.getString("LastVisible").equals("messF"))
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_messages()).commit();
  else if (savedInstanceState.getString("LastVisible").equals("requF"))
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_request()).commit();
  else if (savedInstanceState.getString("LastVisible").equals("friF"))
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_friends()).commit();
else
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_users()).commit();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

       mAuth=FirebaseAuth.getInstance();
        fuser=mAuth.getCurrentUser();
        String user_id=fuser.getUid();



        reference=FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               UserClass muser= dataSnapshot.getValue(UserClass.class);
                user_name.setText(muser.getNom());
                user_txt.setText(muser.getFiliere());


                    if (!muser.getImageUrl().equals("default"))
                    {

                        Glide.with(getApplicationContext()).load(Uri.parse(muser.getImageUrl())).apply(RequestOptions.circleCropTransform()).into(user_profil);
                    }

                    else
                        user_profil.setImageResource(R.drawable.user_logo);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public static void initializeCountNotif(int x){
        //Gravity property aligns the text
        notif.setGravity(Gravity.CENTER_VERTICAL);
       notif.setTypeface(null, Typeface.BOLD);
        notif.setTextColor(Color.parseColor("#D81B60"));
        if (x==0)
            notif.setText("");
        else
        notif.setText(Integer.toString(x));

    }
    public static void initializeCountMess(int x){

        mess.setGravity(Gravity.CENTER_VERTICAL);
        mess.setTypeface(null,Typeface.BOLD);
        mess.setTextColor(Color.parseColor("#D81B60"));
//count is added
        mess.setText(Integer.toString(x));
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            toolbar.setTitle("Messages");
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this,ConnectActivity.class));
            finish();
            return true;
        }
        else if (id==R.id.action_del)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                       builder.setMessage("Etes vous sure de vouloir supprimer ce compte ? Toutes vos donnees seront perdu " )
                        .setPositiveButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Oui",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.child("isDeleted").setValue("true");
                       FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent =new Intent(ProfileActivity.this,RegisterActivity.class);
                                            startActivity(intent);

                                           finish();
                                        }
                                        else
                                            Toast.makeText(ProfileActivity.this,"error",Toast.LENGTH_LONG).show();
                                    }
                                });



                    }
                })
                        .create()
                        .show();
            return true;
            }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find) {
            //fragments trouver amis
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_users(),"userfragment").commit();
            toolbar.setTitle(title[0]);


        } else if (id == R.id.nav_mess) {
            //fragments des messages
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_messages(),"messfragment").commit();
            toolbar.setTitle(title[1]);

        } else if (id == R.id.nav_tools) {
            //activites de parametrages
            startActivity(new Intent(ProfileActivity.this,SettingActivity.class));
        }
        else if (id == R.id.nav_notification) {
            // fragment des requetes
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_request(),"requfragment").commit();
            toolbar.setTitle(title[3]);

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRssItemSelected() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment_friends(),"friendsfragment").addToBackStack(null).commit();
        toolbar.setTitle("Amis");

}
}
