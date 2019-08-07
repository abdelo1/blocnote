package com.example.blocnote.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blocnote.Adapter.RequestViewAdapter;
import com.example.blocnote.R;
import com.example.blocnote.model.Request;
import com.example.blocnote.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_request extends Fragment {
  public static List<Request> mlist;
   private RecyclerView recycler;
    private TextView noNotif;
   private RequestViewAdapter adapter;
    public Fragment_request(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mlist=new ArrayList<>();
        View view =getLayoutInflater().inflate(R.layout.fragment_request,container,false);
        recycler=view.findViewById(R.id.recyclerRequest);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        noNotif=view.findViewById(R.id.notifText);
        readRequest();
        return view;
    }

    public void readRequest(){
        DatabaseReference reference;
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        String currentUserId=mAuth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(currentUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String NO="no";
                   Request request=snapshot.getValue(Request.class);
                      if (request.getHandle().equals(NO))
                    mlist.add(request);
                }
                if (mlist.size()==0)
                {
                    recycler.setVisibility(View.GONE);
                    noNotif.setVisibility(View.VISIBLE);
                }
                else{
                    noNotif.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
                adapter=new RequestViewAdapter(getContext(),mlist);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
