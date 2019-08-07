package com.example.blocnote.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blocnote.ProfileActivity;
import com.example.blocnote.R;
import com.example.blocnote.model.Request;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.List;


public class RequestViewAdapter extends RecyclerView.Adapter<RequestViewAdapter.ViewHolder> {
   private    DatabaseReference reference;
   private FirebaseAuth mAuth;
   private FirebaseUser currentUser;
   private UserClass userRecei;
    private Context mcontext;
   private  List<Request> mlistrequest;
   private static List<Request> anotherList;

  private UserClass user;
    public RequestViewAdapter(Context context, List<Request> request ){
        this.mcontext=context;
        this.mlistrequest=request;
        this.anotherList=request;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_request,viewGroup,false);
        return new RequestViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        ProfileActivity.initializeCountNotif(mlistrequest.size());

       mAuth=FirebaseAuth.getInstance();
       currentUser=mAuth.getCurrentUser();
        final Request listItem =mlistrequest.get(i);
        FirebaseDatabase.getInstance().getReference("Users").child(listItem.getReceiverId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userRecei=dataSnapshot.getValue(UserClass.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (!listItem.getPhotourl().equals("default"))
        Glide.with(mcontext).load(listItem.getPhotourl()).into(viewHolder.image);
        else
            viewHolder.image.setImageResource(R.drawable.user_logo);
        viewHolder.text.setText(listItem.getNom());
        viewHolder.textClass.setText(listItem.getFiliere());
        viewHolder.text_requ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text =viewHolder.text_requ.getText().toString();
                if (text.equals("Accepter"))
                {
                    reference = FirebaseDatabase.getInstance().getReference()
                            .child("FriendRequest")
                            .child(listItem.getReceiverId()).child(listItem.getSenderId());
                    reference.child("handle").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                reference.child("accepted").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            HashMap<String,String> hashMap=new HashMap<>();

                                            hashMap.put("nom",listItem.getNom());
                                            hashMap.put("urlphoto",listItem.getPhotourl());
                                            hashMap.put("filiere",listItem.getFiliere());
                                            hashMap.put("id",listItem.getSenderId());
                                            reference = FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(listItem.getReceiverId())
                                                    .child("Friends").child(listItem.getSenderId());

                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        HashMap<String,String> hashMap=new HashMap<>();

                                                        hashMap.put("nom",userRecei.getNom());
                                                        hashMap.put("urlphoto",userRecei.getImageUrl());
                                                        hashMap.put("filiere",userRecei.getFiliere());
                                                        hashMap.put("id",userRecei.getId());
                                                        reference = FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(listItem.getSenderId())
                                                                .child("Friends").child(listItem.getReceiverId());
                                                                reference.setValue(hashMap);

                                                    }
                                                        Toast.makeText(mcontext,"Amis ajoute",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            ProfileActivity.initializeCountNotif(mlistrequest.size());
                                        }

                                    }
                                });

                        }
                    });
                }
                else
                {
                    reference = FirebaseDatabase.getInstance().getReference("FriendRequest")
                            .child(user.getId())
                            .child(currentUser.getUid());

                    reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(mcontext,"Demande d'ami annule",Toast.LENGTH_LONG);
                            else
                                Toast.makeText(mcontext,"erreur lors de l'annulation",Toast.LENGTH_LONG);
                            viewHolder.text_requ.setText("Ajouter ami");
                        }
                    });
                }


            }
        });
    }

    @Override
    public int getItemCount() {

        return mlistrequest.size();
    }
    public static int  nbreItem(){
        return anotherList.size();
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
