package com.example.pip.Home.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.pip.Adapters.CommentAdapter;
import com.example.pip.Models.CommentModel;
import com.example.pip.Models.PostDataImageModel;
import com.example.pip.Models.UserModel;
import com.example.pip.R;
import com.example.pip.databinding.PipViewBinding;
import com.example.pip.screens.CommentScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PostViewScreen extends AppCompatActivity {
    private String pip_id;
    private String uid;
    private final DatabaseReference userPipDataRef = FirebaseDatabase.getInstance().getReference("user").child("UserPost").child("UserPipData");
    private final DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference("user").child("UserInfo");
    private boolean like_dislike = false;
    private CommentAdapter showcommentadapter;
    private final ArrayList<CommentModel> storeUserReply = new ArrayList<>();


    private PipViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PipViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar bar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#ffffff"));
        assert bar != null;
        bar.setBackgroundDrawable(colorDrawable);
        bar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(this, R.drawable.arrow_back);
        assert upArrow != null;
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        bar.setHomeAsUpIndicator(upArrow);
        bar.setElevation(0);


        Bundle bundle = getIntent().getExtras();
        pip_id = String.valueOf(bundle.get("pip_id"));
        String username = String.valueOf(bundle.get("name"));


        userDataRef.orderByChild("usName").equalTo(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                uid = snapshot.getKey();
                takePipData(uid);
                setUserImagefuc(uid);
                pip_like_data_store(uid);
                likeStatus(uid);
                takeComment(uid);
                countComment(uid);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.settingUserPipData.setHasFixedSize(true);
        showcommentadapter = new CommentAdapter(this, storeUserReply);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.settingUserPipData.setLayoutManager(layoutManager);
        binding.settingUserPipData.setAdapter(showcommentadapter);


        binding.comment3.setOnClickListener(view -> {
            Intent moveCommentPage = new Intent(this, CommentScreen.class);
            moveCommentPage.putExtra("userName", binding.username.getText().toString());
            moveCommentPage.putExtra("pipData", binding.pipData.getText().toString());
            moveCommentPage.putExtra("pip_id", pip_id);
            startActivity(moveCommentPage);

        });

        binding.shearing.setOnClickListener(view -> {
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, binding.pipData.getText().toString());
            share.setType("text/plain");
            Intent shareIntent = Intent.createChooser(share, "Pip Post");
            startActivity(shareIntent);
        });

        int modeFlag = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (modeFlag == Configuration.UI_MODE_NIGHT_YES) {
            ColorDrawable colorDrawable2 = new ColorDrawable(Color.parseColor("#000000"));
            bar.setBackgroundDrawable(colorDrawable2);

            final Drawable upArrow2 =  ContextCompat.getDrawable(this, R.drawable.arrow_back);
            assert upArrow2 != null;
            upArrow2.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            bar.setHomeAsUpIndicator(upArrow2);

            bar.setTitle("");

            binding.heart3.setImageResource(R.drawable.white_heart);

            final Drawable comment =  ContextCompat.getDrawable(this, R.drawable.comment);
            assert comment != null;
            comment.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            binding.comment3.setImageDrawable(comment);

            final Drawable share =  ContextCompat.getDrawable(this, R.drawable.share);
            assert share != null;
            share.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            binding.shearing.setImageDrawable(share);

        }


    }


    private void takePipData(String uid) {
        userPipDataRef.child(uid).child(pip_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                assert userModel != null;
                binding.username.setText(userModel.pipuserName);
                binding.pipData.setText(userModel.pipPostData);
                binding.pipDateTime.setText(userModel.date);
                snapshot.getRef().child("ImageUriFromDatabase").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            PostDataImageModel imageModel = snapshot.getValue(PostDataImageModel.class);
                            binding.setUserPhoto.setVisibility(View.VISIBLE);
                            assert imageModel != null;
                            Glide.with(PostViewScreen.this).load(Uri.parse(imageModel.pipImageData)).into(binding.setUserPipPhoto);
                        } else {
                            binding.setUserPipPhoto.setImageResource(R.drawable.ic_baseline_home_24);
                            binding.setUserPipPhoto.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setUserImagefuc(String uid) {
        userDataRef.child(uid).child("Profile_Image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;
                    Glide.with(PostViewScreen.this).load(userModel.User_Profile_Image_Uri).into(binding.setUserPhoto);
                } else {
                    binding.setUserPhoto.setImageResource(R.drawable.usermodel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void pip_like_data_store(String uid) {
        binding.heart3.setOnClickListener(v -> {
            like_dislike = true;
            userPipDataRef.child(uid).child(pip_id).child("Likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (like_dislike) {
                        if (snapshot.hasChild((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))) {
                            binding.heart3.setImageResource(R.drawable.heart);
                            snapshot.getRef().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null);
                            like_dislike = false;
                        } else {
                            binding.heart3.setImageResource(R.drawable.heartred);
                            snapshot.getRef().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                            like_dislike = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

    }

    private void likeStatus(String uid) {

        userPipDataRef.child(uid).child(pip_id).child("Likes").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ((snapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())))) {
                    binding.heart3.setImageResource(R.drawable.heartred);
                    binding.heartCount3.setText(Integer.toString((int) snapshot.getChildrenCount()));

                } else {
                    nightMode();
                    binding.heartCount3.setText(Integer.toString((int) snapshot.getChildrenCount()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void takeComment(String uid) {
        userPipDataRef.child(uid).child(pip_id).child("Comments").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    storeUserReply.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CommentModel comment = ds.getValue(CommentModel.class);
                        storeUserReply.add(comment);
                    }
                    showcommentadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countComment(String uid) {
        userPipDataRef.child(uid).child(pip_id).child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    binding.commentCount3.setText(String.valueOf((int) snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void nightMode() {
        int modeFlag = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (modeFlag == Configuration.UI_MODE_NIGHT_YES) {
            binding.heart3.setImageResource(R.drawable.white_heart);
        } else {
            binding.heart3.setImageResource(R.drawable.heart);
        }
    }
}