/*
 * Copyright (C) 2016  Hendrik Borghorst & Frederik Luetkes
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package andrompd.org.andrompd;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import andrompd.org.andrompd.application.ConnectionManager;
import andrompd.org.andrompd.application.fragments.database.AlbumTracksFragment;
import andrompd.org.andrompd.application.fragments.database.AlbumsFragment;
import andrompd.org.andrompd.application.fragments.database.ArtistsFragment;
import andrompd.org.andrompd.application.fragments.database.MyMusicTabsFragment;
import andrompd.org.andrompd.application.views.CurrentPlaylistView;
import andrompd.org.andrompd.application.views.NowPlayingView;
import andrompd.org.andrompd.mpdservice.handlers.serverhandler.MPDCommandHandler;
import andrompd.org.andrompd.mpdservice.handlers.serverhandler.MPDQueryHandler;
import andrompd.org.andrompd.mpdservice.handlers.serverhandler.MPDStateMonitoringHandler;
import andrompd.org.andrompd.mpdservice.handlers.responsehandler.MPDResponseAlbumList;
import andrompd.org.andrompd.mpdservice.mpdprotocol.mpddatabase.MPDAlbum;
import andrompd.org.andrompd.mpdservice.profilemanagement.MPDProfileManager;
import andrompd.org.andrompd.mpdservice.profilemanagement.MPDServerProfile;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlbumsFragment.AlbumSelectedCallback, ArtistsFragment.ArtistSelectedCallback {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.andrompd_play_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPDProfileManager profileManager = new MPDProfileManager(getApplicationContext());


                MPDServerProfile autoProfile = profileManager.getAutoconnectProfile();
                Log.v(TAG, "Auto connect profile with statemonitoring: " + autoProfile);
                //MPDQueryHandler.setServerParameters(autoProfile.getHostname(),autoProfile.getPassword(),autoProfile.getPort());
//                MPDQueryHandler.connectToMPDServer();
//                MPDQueryHandler.startIdle();

                MPDStateMonitoringHandler.setServerParameters(autoProfile.getHostname(), autoProfile.getPassword(), autoProfile.getPort());
                MPDStateMonitoringHandler.connectToMPDServer();

                MPDQueryHandler.setServerParameters(autoProfile.getHostname(), autoProfile.getPassword(), autoProfile.getPort());
                MPDQueryHandler.connectToMPDServer();

                Fragment artistFragment = new ArtistsFragment();
                Fragment albumFragment = new AlbumsFragment();
                Fragment albumTracksFragment = new AlbumTracksFragment();

                Bundle args = new Bundle();
                //args.putString(AlbumTracksFragment.BUNDLE_STRING_EXTRA_ARTISTNAME,"P!nk");
                //args.putString(AlbumTracksFragment.BUNDLE_STRING_EXTRA_ALBUMNAME,"Alice Through the Looking Glass");
                //albumTracksFragment.setArguments(args);

                args.putString(CurrentPlaylistView.BUNDLE_STRING_EXTRA_PLAYLISTNAME, "");
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, currentPlaylistFragment);
//                transaction.commit();
            }
        });

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            Fragment fragment = new MyMusicTabsFragment();

            Bundle args = new Bundle();
            args.putInt(MyMusicTabsFragment.MY_MUSIC_REQUESTED_TAB, 0);

            fragment.setArguments(args);


            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MPDProfileManager profileManager = new MPDProfileManager(getApplicationContext());


        MPDServerProfile autoProfile = profileManager.getAutoconnectProfile();
        Log.v(TAG, "Auto connect profile with statemonitoring: " + autoProfile);
        //MPDQueryHandler.setServerParameters(autoProfile.getHostname(),autoProfile.getPassword(),autoProfile.getPort());
//                MPDQueryHandler.connectToMPDServer();
//                MPDQueryHandler.startIdle();

        ConnectionManager.setParameters(autoProfile.getHostname(), autoProfile.getPassword(), autoProfile.getPort());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NowPlayingView nowPlayingView = (NowPlayingView) findViewById(R.id.now_playing_layout);
        if (nowPlayingView != null) {
            nowPlayingView.onResume();
        }
        ConnectionManager.reconnectLastServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");

        NowPlayingView nowPlayingView = (NowPlayingView) findViewById(R.id.now_playing_layout);
        if (nowPlayingView != null) {
            nowPlayingView.registerDragStatusReceiver(null);

            nowPlayingView.onPause();
        }

        // Disconnect from MPD server
        ConnectionManager.disconnectFromServer();
    }

    @Override
    public void onAlbumSelected(String albumname, String artistname) {
        Log.v(TAG, "Album selected: " + albumname + ":" + artistname);
        // Create fragment and give it an argument for the selected article
        AlbumTracksFragment newFragment = new AlbumTracksFragment();
        Bundle args = new Bundle();
        args.putString(AlbumTracksFragment.BUNDLE_STRING_EXTRA_ALBUMNAME, albumname);
        args.putString(AlbumTracksFragment.BUNDLE_STRING_EXTRA_ARTISTNAME, artistname);

        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("AlbumTracksFragment");

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onArtistSelected(String artistname) {
        Log.v(TAG, "Artist selected: " + artistname);
        // Create fragment and give it an argument for the selected article
        AlbumsFragment newFragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putString(AlbumsFragment.BUNDLE_STRING_EXTRA_ARTISTNAME,artistname);


        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("ArtistAlbumsFragment");

        // Commit the transaction
        transaction.commit();
    }
}
