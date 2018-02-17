package no.amirhjelperdeg.norwegianskiresort.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by apple on 2/13/18.
 */

public  abstract class AppCompactPrefrenceActivity  extends PreferenceActivity
{

    AppCompatDelegate appCompatDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getAppCompatDelegate().installViewFactory();
        getAppCompatDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getAppCompatDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar()
    {
        return getAppCompatDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(Toolbar toolbar)
    {
        getAppCompatDelegate().setSupportActionBar(toolbar);
    }

    public MenuInflater getMenuInflater()
    {
        return getAppCompatDelegate().getMenuInflater();
    }

    public void setContentView(int layoutResID)
    {
       getAppCompatDelegate().setContentView(layoutResID);
    }

    public void addContentView(View view , ViewGroup.LayoutParams params)
    {
        getAppCompatDelegate().addContentView(view,params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getAppCompatDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getAppCompatDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getAppCompatDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getAppCompatDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getAppCompatDelegate().onDestroy();
    }


    @Override
    public void invalidateOptionsMenu() {
        getAppCompatDelegate().invalidateOptionsMenu();
    }

    public AppCompatDelegate getAppCompatDelegate()
    {
        if(appCompatDelegate==null)
        {
            appCompatDelegate= AppCompatDelegate.create(this,null);
        }
        return  appCompatDelegate;
    }
}
