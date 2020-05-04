package com.gangoffive.rig2gig.navbar;

import java.util.HashMap;

/**
 * This class is used to help the NavBarCompatActivity to choose which concrete nav bar to create based upon the logged in user type.
 * @author Ben souch
 * @version #0.3b
 * @since #0.1b
 */
public class NavigationContext
{
    private HashMap<String, Class> navBarAlgorithmMap;

    /**
     * This constructor creates a new HashMap and adds all concrete versions of the navbar to it.
     * @since #0.1b
     */
    public NavigationContext()
    {
        navBarAlgorithmMap = new HashMap<>();
        navBarAlgorithmMap.put("Fan", ConcreteFanNavBar.class);
        navBarAlgorithmMap.put("Musician", ConcreteMusicianNavBar.class);
        navBarAlgorithmMap.put("Venue", ConcreteVenueNavBar.class);
    }

    /**
     * This method is used to obtain a concrete navbar from the  HashMap based upon the passed in parameter.
     * @param userType This parameter is used to match the appropriate concrete navbar from the HashMap.
     * @return Returns a class to instantiate from the HashMap.
     * @since #0.1b
     */
    public Class navBarFinder(String userType)
    {
        return navBarAlgorithmMap.get(userType);
    }
}
