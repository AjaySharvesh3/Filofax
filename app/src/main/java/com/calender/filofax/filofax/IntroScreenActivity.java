package com.calender.filofax.filofax;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.Snackbar;

import com.calender.filofax.filofax.Fragments.IntroPhotosFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroScreenActivity extends AppCompatActivity {

    private static String CURRENT_PAGE_BUNDLE_KEY ="current_key";
    private static final int RC_SIGN_IN = 100;
    private int currentPage = 0;
    private int totalNumOfPages = 2;
    private final long DELAY = 800;
    private final long PERIOD = 2500;
    private static final String TAG = IntroScreenActivity.class.getSimpleName();

    private GoogleSignInClient mSignInClient;
    private FirebaseAuth mFirebaseAuth;

    @BindView(R.id.btn_login)
    SignInButton btnLogin;
    @BindView(R.id.container_viewPager)
    ViewPager viewPager;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        ButterKnife.bind(this);

        if (savedInstanceState != null){
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_BUNDLE_KEY);
        }
        
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this,signInOptions);
        mFirebaseAuth = FirebaseAuth.getInstance();
        btnLogin.setSize(SignInButton.SIZE_STANDARD);

        viewPager.setLayoutParams(getLayoutParams());
        viewPager.setAdapter(new PhotosPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        addTimerForViewPager();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(getApplicationContext(), CalenderActivity.class));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE_BUNDLE_KEY,currentPage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }

    @OnClick(R.id.btn_login)
    public void onClickSignIn(View view) {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public class PhotosPagerAdapter extends FragmentStatePagerAdapter {

        PhotosPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    currentPage = 0;
                    return IntroPhotosFragment.newInstance("Mark the time and take a nap... we notify you",
                            "https://firebasestorage.googleapis.com/v0/b/filofax-f535f.appspot.com/o/style-imagery-bestpractices-narrative1.png?alt=media&token=956caf4f-52c4-4133-8755-e088ac257421");
                case 1:
                    currentPage = 1;
                    return IntroPhotosFragment.newInstance("Add all your expenses... we keep track of it for you",
                            "https://firebasestorage.googleapis.com/v0/b/filofax-f535f.appspot.com/o/busy_two.png?alt=media&token=5fb442cd-f61d-4be0-b8b4-1a89f1cfab49");
                default:
                    currentPage = 0;
                    return IntroPhotosFragment.newInstance("Add all your expenses... we keep track of it for you",
                            "https://firebasestorage.googleapis.com/v0/b/filofax-f535f.appspot.com/o/style-imagery-bestpractices-narrative1.png?alt=media&token=956caf4f-52c4-4133-8755-e088ac257421");
            }
        }

        @Override
        public int getCount() {
            return totalNumOfPages;
        }

    }

    private ViewGroup.LayoutParams getLayoutParams() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutParams.width = viewPager.getWidth();
            layoutParams.height = (int) (getScreenHeight(this) * .8);
        }else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutParams.width = (int) (getScreenWeight(this) * .6);
            layoutParams.height = viewPager.getHeight();
        }
        return layoutParams;
    }

    private void addTimerForViewPager() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == totalNumOfPages){
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++,true);
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },DELAY,PERIOD);
    }
    
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), CalenderActivity.class));
                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.btn_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public int getScreenHeight(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
    public int getScreenWeight(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
