package com.gangoffive.rig2gig.navbar;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.band.management.DisplayMusiciansBands;

import java.util.HashMap;

/**
 * This class is used to help the concrete nav bar activity classes to choose which fragment to open from the navigation drawer.
 * @author Ben souch
 * @version #0.3b
 * @since #0.1b
 */
public class NavBarFactory
{
    private HashMap<String, Fragment> fragmentMap;

    /**
     * Constructor creates a new HashMap and adds all fragment instances to it.
     * @since #0.1b
     */
    NavBarFactory()
    {
        fragmentMap = new HashMap<>();
        fragmentMap.put("Notifications", new ViewCommsFragment());
        fragmentMap.put("Settings", new SettingsFragment());
        fragmentMap.put("Venue Console", new VenueConsoleFragment());
        fragmentMap.put("Musician Console", new MusicianConsoleFragment());
        fragmentMap.put("My Bands", new DisplayMusiciansBands());
    }

    /**
     * This method is used to obtain a fragment from the HashMap based upon the passed in parameter.
     * @param menuItem This parameter represents the nav bar label selected by the user.
     * @return Returns the fragment to be instantiated.
     * @since #0.1b
     */
    public Fragment selectFragment(@NonNull MenuItem menuItem)
    {
        return fragmentMap.get(menuItem.toString());
    }
}