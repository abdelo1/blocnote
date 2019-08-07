package com.example.blocnote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blocnote.MessageActivity;
import com.example.blocnote.ProfileActivity;
import com.example.blocnote.R;
import com.example.blocnote.model.Friend;
import com.example.blocnote.model.Request;
import com.example.blocnote.model.UserClass;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private    DatabaseReference FriendRequreference,mReference,notificationRef,friendRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private UserClass user;
    private Context mcontext;
    private List<UserClass> mlistUsers;
    public static Boolean create=false;
    public static int index=0;

    public RecyclerViewAdapter(Context context, List<UserClass> mlistUsers ){
        this.mcontext=context;
        this.mlistUsers=mlistUsers;

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid());
        mReference.keepSynced(true);

     friendRef=FirebaseDatabase.getInstance().getReference("Users")
             .child(currentUser.getUid()).child("Friends");

        notificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
     notificationRef.keepSynced(true);

        FriendRequreference=FirebaseDatabase.getInstance().getReference().child("FriendRequest");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user =dataSnapshot.getValue(UserClass.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_users,viewGroup,false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final UserClass muser=mlistUsers.get(i);
       friendRef.child(muser.getId()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                    {
                       viewHolder.text_requ.setText("Ami");

                    }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        if (!muser.getImageUrl().equals("default"))
            Glide.with(mcontext).load(muser.getImageUrl()).into(viewHolder.image);
        else
            viewHolder.image.setImageResource(R.drawable.user_logo);
        viewHolder.text.setText(muser.getNom());
        viewHolder.textClass.setText(muser.getFiliere());

        viewHolder.text_requ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text =viewHolder.text_requ.getText().toString();
                if (text.equals("Ajouter ami"))
                {

                  //  Request request =
//                            new Request(muser.getId(),user.getId(),"no","no",user.getNom(),user.getImageUrl());
                                         HashMap  <String,String> hashMap=new HashMap<>();
                                        hashMap.put("senderId",user.getId());
                                        hashMap.put("receiverId",muser.getId());
                                        hashMap.put("handle","no");
                                        hashMap.put("accepted","no");
                                        hashMap.put("nom",user.getNom());
                                        hashMap.put("photourl",user.getImageUrl());
                                        hashMap.put("filiere",user.getFiliere());
                                        FriendRequreference.child(muser.getId()).child(user.getId()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            final HashMap<String,String> hashnMap=new HashMap<>();
                                                            hashnMap.put("envoye par",user.getId());
                                                            hashnMap.put("type","requete d'amis");
                                                            notificationRef.child(muser.getId()).push().setValue(hashnMap)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                Toast.makeText(mcontext,"Demande d'amis envoyée",Toast.LENGTH_LONG).show();
                                                                                viewHolder.text_requ.setText("Annuler demande");
                                                                            }

                                                                        }
                                                                    });

                                                        }

                                                    }
                                                });

                                    }


                else if(text.equals("Annuler demande"))
                {
                    FriendRequreference.child(muser.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                viewHolder.text_requ.setText("Ajouter ami");

                        }
                    });
                    notificationRef.child(user.getId()).removeValue();
                }
                else{}


            }
        });
    }


    @Override
    public int getItemCount() {
        return mlistUsers.size();
    }

    public  void updateList(List <UserClass> newList){
        mlistUsers=new ArrayList<>();
        mlistUsers.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image ;
        TextView text;
        TextView textClass;
        TextView text_requ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.img);
            text=itemView.findViewById(R.id.text);
            text_requ=itemView.findViewById(R.id.request);
            textClass=itemView.findViewById(R.id.fil);
        }

    }
}
