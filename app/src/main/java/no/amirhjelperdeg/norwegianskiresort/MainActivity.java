package no.amirhjelperdeg.norwegianskiresort;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import no.amirhjelperdeg.norwegianskiresort.activities.AboutActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.AccessLiftActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.LoginActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.NearByResortActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.ProfileActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.SettingsActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.TransportationActivity;
import no.amirhjelperdeg.norwegianskiresort.fragments.HomeFragment;
import no.amirhjelperdeg.norwegianskiresort.fragments.ResortFragment;
import no.amirhjelperdeg.norwegianskiresort.privateactivity.ResortRegistration;
import no.amirhjelperdeg.norwegianskiresort.utility.CircleTransform;


public class MainActivity extends AppCompatActivity
{

    private NavigationView navigationview;
    private DrawerLayout  drawer;
    private View navHeader; // navigation header
    private ImageView imgNavHeaderBg,imgProfile;// profile pics
    private TextView txtEmailId;
    private Toolbar toolbar;
    FloatingActionButton fab ;
    // user profile image
    private static final String urlNavHeaderBg=""; // back ground image
    private static final String urlProfileImg=""; //

    private static  int navItemIndex=0; // index to indenty current navigation item
    private static final String TAG_HOME="Home";
    private static String CURRENT_TAG=TAG_HOME;

    private String[]activityTitles;
    //flag to load activity or fragment when user presses back button
    private Handler handler;
    private boolean loadHomeActivityWhenUserPressedBackButton=true;

    // firebaseAuth
    FirebaseAuth firebaseAuth;

    SharedPreferences userLoginInfo;

    String emailId;
    DatabaseReference mydDBRef;
    FirebaseUser firebaseUser;
    FirebaseDatabase fdb;
    FirebaseStorage  firebaseStorage;
    StorageReference storageRef;
    Uri profileUri;
    String roleId=" ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();

        drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        navigationview=(NavigationView)findViewById(R.id.nav_view) ;
        //fab=(FloatingActionButton)findViewById(R.id.fab);

        navHeader=navigationview.getHeaderView(0); // get the navigation header
        txtEmailId=(TextView)navHeader.findViewById(R.id.user_emaild_id) ;

        imgNavHeaderBg=(ImageView)navHeader.findViewById(R.id.img_profile_bg);
        imgProfile=(ImageView)navHeader.findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        /// check if user is logged in or not
        //if true  he can see main activity
        // if false
        // open login activity

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        userLoginInfo =getApplicationContext().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        emailId=userLoginInfo.getString("emailId",null);
        roleId=userLoginInfo.getString("roleId","user");

        fdb= FirebaseDatabase.getInstance();
        // initialize firabse storage
        firebaseStorage= FirebaseStorage.getInstance();
        storageRef=firebaseStorage.getReference();


        final String password=userLoginInfo.getString("password",null);

        if(emailId!=null && password!=null)
        {
            firebaseAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener
                    (this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        //userLoginInfo= getSharedPreferences("preferences", MODE_PRIVATE);
                        userLoginInfo= getSharedPreferences("logindata", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=userLoginInfo.edit();
                        editor.putString("emailId",emailId);
                        editor.putString("password",password);
                        editor.commit();
                        if(storageRef!=null)
                            loadNaveHeader();// load the menu header : image and email id
                    }
                    else
                    {
                        // if sign is failed then it will open the login activity
                        Toast.makeText(getApplicationContext(),"Failed to Login ",Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                }
            });
        }
        else //if  user preferences is null  then open login activity
        {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        // initialize the array values from resources
        activityTitles=getResources().getStringArray(R.array.activity_titles);

        //initialize the navigation menus
        setUpNavigationView();

        if(savedInstanceState==null)
        {
            navItemIndex=0;
            CURRENT_TAG =TAG_HOME;
            loadHomeFragment();
        }

    }
    // on create method  finished
    // load the  navigation menu header information
    // like background image and profile image , name etc..
    public  void loadNaveHeader()
    {
        txtEmailId.setText(emailId);
        // loading the header image back ground

        storageRef.child("images/"+firebaseUser.getUid()+"/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                profileUri=uri;
                //Toast.makeText(getApplicationContext(),"Load Header ",Toast.LENGTH_LONG).show();
                setProfileImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                //Toast.makeText(getApplicationContext(),"Failed to get image  url in header ",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setProfileImage()
    {
        Glide
                .with(this)
                .load(profileUri)
                .crossFade().thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
    }

    public void setUpNavigationView()
    {

        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        navItemIndex=0; // home
                        CURRENT_TAG=TAG_HOME;
                        break;
                    case R.id.nav_resort_list:
                        navItemIndex=1; //resort list
                        CURRENT_TAG="Resort";
                        break;
                    case R.id.nav_nearby_resort:
                        navItemIndex=2;
                        startActivity(new Intent(getApplicationContext(),NearByResortActivity.class));
                        return  true;

                    case  R.id.nav_accesslift:
                        navItemIndex=3;
                        startActivity(new Intent(getApplicationContext(),AccessLiftActivity.class));
                        return  true;
                    case R.id.nav_settings:
                        navItemIndex=4;
                        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                        return  true;
                    case R.id.nav_about_us:
                        navItemIndex=5;
                        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                        return  true;

                    case R.id.nav_transportation:
                        navItemIndex=6;
                        startActivity(new Intent(getApplicationContext(),TransportationActivity.class));
                        return  true;
                   /* case R.id.nav_add_resort:
                        Toast.makeText(getApplicationContext()," Role :  "+roleId,Toast.LENGTH_LONG).show();
                        if(roleId.equals("1")) {
                            startActivity(new Intent(getApplicationContext(), ResortRegistration.class));
                        }
                        return true;*/
                    default:
                        navItemIndex=0;

                }// switch is finished

                if(item.isChecked())// if selected by default  then set it inselect
                {
                    item.setChecked(false);
                }
                else
                {
                    item.setChecked(true);
                }

                loadHomeFragment();


                return true;
            }
        });

        ActionBarDrawerToggle  actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }
    // it will return the respective fragment when user selected menu from navigation drawer
    public void loadHomeFragment()
    {
        selectNavMenu();
        setToolbarTitle();

        if(getSupportFragmentManager().findFragmentByTag(CURRENT_TAG)!=null) // if user selected any menu then close the drawer
        {
            drawer.closeDrawers();

            showFab(); // to display  FloatingAction button desired activities

            return;
        }

        Runnable mRunnable= new Runnable() { // anonymous class
            @Override
            public void run() {

                Fragment fragment = getSelectedFragment();/////
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame,fragment,CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

            }
        };

        if(mRunnable!=null)
        {
            handler.post(mRunnable);
        }

        showFab();// to show floating action button
        drawer.closeDrawers();

        // it will refresh the tool bar menus;
        invalidateOptionsMenu();

    }
    //

    public void showFab()
    {

    }

    public void selectNavMenu()
    {
        navigationview.getMenu().getItem(navItemIndex).setChecked(true);
    }

    public void setToolbarTitle()
    {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }



    public Fragment getSelectedFragment()
    {
        switch (navItemIndex)
        {
            case 0:
                // home
                HomeFragment homeFragment=new HomeFragment();
                return homeFragment;
            case 1:
                ResortFragment resortFragment= new ResortFragment();
                return resortFragment;

            default:
                return new HomeFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawers();
            return;
        }
    }

   /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       if(roleId!="1") { // if its not admin
           MenuItem addResort = menu.findItem(R.id.action_add_resort);
           addResort.setVisible(false);
       }
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(roleId.equalsIgnoreCase("user"))
            menu.findItem(R.id.action_add_resort).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuId= item.getItemId();

        if (menuId==R.id.action_logout)
        {
            userLogout();
        }
        else if(menuId==R.id.action_add_resort)
        {
            if(roleId.equalsIgnoreCase("admin")) {
                startActivity(new Intent(getApplicationContext(), ResortRegistration.class));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void  userLogout()
    {
        //1
        userLoginInfo= getSharedPreferences("logindata", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor=userLoginInfo.edit();
        editor.remove("password");
        editor.remove("emailId");
        editor.commit();
        firebaseAuth.signOut();
        Toast.makeText(getApplicationContext()," Logged out !!",Toast.LENGTH_LONG).show();
        finish();// close main activity
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

    }


}
