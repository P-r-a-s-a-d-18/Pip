package com.example.pip.search.visit_another_profile;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.pip.Models.UserModel;
import com.example.pip.R;
import com.example.pip.databinding.ActivityVisitAnotherProfileBinding;
import com.example.pip.search.visit_another_profile.adaptor.AnotherUserPostAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class VisitOtherUserProfileScreen extends AppCompatActivity {

    private final static DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference("user").child("UserInfo");
    private final static DatabaseReference userPipDataRef = FirebaseDatabase.getInstance().getReference("user").child("UserPost").child("UserPipData");
    private final ArrayList<UserModel> Store_pip_dataOfAnotherProfile = new ArrayList<>();
    private String takeUserId, u_id;
    private AnotherUserPostAdaptor anotherUserPostAdaptor;
    private Boolean checkFollowOrNot = false, notifyData = true;

    private ActivityVisitAnotherProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisitAnotherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        int modeFlag = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (modeFlag == Configuration.UI_MODE_NIGHT_YES) {
            switchNightMode();
        }

        Bundle b = getIntent().getExtras();
        u_id = b.getString("Uid");

        userCanDoFollowOrUnfollow();

        binding.closeOtherProfileScreen.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference("user").child("UserInfo").orderByChild("usName").equalTo(u_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        takeUserId = snapshot.getKey();
                        putUserData(takeUserId);
                        setUser_data_in_recyclerView(takeUserId);
                        checkStatus(takeUserId);


                        userDataRef.child(takeUserId).child("Following").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                binding.fingCount.setText(String.valueOf(snapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        userDataRef.child(takeUserId).child("Follower").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                binding.fwerCount.setText(String.valueOf(snapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


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


    }

    private void putUserData(String takeUserId) {
        userDataRef.child(takeUserId).child("EditedData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;
                    binding.setUserBioInAnotherProfile.setText(userModel.Bio);
                    binding.setUserDOBInAnotherProfile.setText(userModel.dateOfBirth);
                    binding.setUserWebsiteInAnotherProfile.setText(userModel.Website);
                    binding.setUserLocationInAnotherProfile.setText(userModel.Location);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        userDataRef.child(takeUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                assert userModel != null;
                binding.setUserNameInAnotherProfile.setText(userModel.usName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userDataRef.child(takeUserId).child("Profile_Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;
                    Glide.with(VisitOtherUserProfileScreen.this).asBitmap().load(Uri.parse(userModel.User_Profile_Image_Uri))
                            .apply(RequestOptions.placeholderOf(R.drawable.usermodel))
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(
                                        @Nullable GlideException e,
                                        Object model,
                                        Target<Bitmap> target,
                                        boolean isFirstResource
                                ) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(
                                        Bitmap resource,
                                        Object model,
                                        Target<Bitmap> target,
                                        DataSource dataSource,
                                        boolean isFirstResource
                                ) {
                                    Palette.from(resource).generate(p -> {
                                        assert p != null;
                                        binding.setDynamicColorOfAnotherProfile.setCardBackgroundColor(p.getVibrantColor(292929));
                                    });
                                    return false;
                                }
                            })
                            .into(binding.setUserImageInAnotherProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUser_data_in_recyclerView(String takeUserUid) {
        binding.setAnotherUserPipData.setHasFixedSize(true);

        anotherUserPostAdaptor = new AnotherUserPostAdaptor(this, Store_pip_dataOfAnotherProfile, takeUserUid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.setAnotherUserPipData.setLayoutManager(layoutManager);
        binding.setAnotherUserPipData.setAdapter(anotherUserPostAdaptor);

        userPipDataRef.child(takeUserUid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Store_pip_dataOfAnotherProfile.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        UserModel userModel = ds.getValue(UserModel.class);
                        Store_pip_dataOfAnotherProfile.add(userModel);
                    }
                    if (notifyData) {
                        anotherUserPostAdaptor.notifyDataSetChanged();
                        notifyData = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userCanDoFollowOrUnfollow() {
        binding.DoFollowOrUnfollow.setOnClickListener(v -> {
            checkFollowOrNot = true;
            userDataRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Following").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (checkFollowOrNot) {
                        if (snapshot.hasChild(takeUserId)) {
                            snapshot.getRef().child(takeUserId).setValue(null);
                            binding.DoFollowOrUnfollow.setText("Follow");
                            userDataRef.child(takeUserId).child("Follower").child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).setValue(null);
                            checkFollowOrNot = false;
                        } else {
                            snapshot.getRef().child(takeUserId).setValue(true);
                            binding.DoFollowOrUnfollow.setText("Following");
                            userDataRef.child(takeUserId).child("Follower").child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).setValue(true);
                            checkFollowOrNot = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void checkStatus(String takeUserId) {
        userDataRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Following").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(takeUserId)) {
                    binding.DoFollowOrUnfollow.setText("Following");
                } else {
                    binding.DoFollowOrUnfollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void switchNightMode() {

        final Drawable profileUi = ContextCompat.getDrawable(this, R.drawable.pip_show_ui_for_profile);
        assert profileUi != null;
        profileUi.setColorFilter(ContextCompat.getColor(this, R.color.dimNight), PorterDuff.Mode.SRC_ATOP);
        binding.constraintLayout2.setBackground(profileUi);

        final  Drawable close = ContextCompat.getDrawable(this , R.drawable.arrow_back);
        assert  close != null;
        close.setColorFilter(ContextCompat.getColor(this , R.color.white) , PorterDuff.Mode.SRC_ATOP);
        binding.closeOtherProfileScreen.setImageDrawable(close);

        binding.setAnotherUserPipData.setBackgroundResource(R.color.dimNight);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}