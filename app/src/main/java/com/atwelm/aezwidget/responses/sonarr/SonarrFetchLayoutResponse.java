/*
 * Copyright (c) 2015.
 *
 * The author of these files, Austin Wellman, allows modification, reproduction and private use of them. It, derived works, and works including derived files cannot be published to an app store, packaged and redistributed, used commercially, or sublicensed.
 *
 * The author is not responsible for anything this software causes to happen and is provided as-is with no guarantee of anything.
 *
 * During the building process, gradle pulls the following files covered under the Apache License 2.0 which is located in the root of this repository as the file "included_licenses/APACHE_2.0.txt":
 * gson
 * spring-android-core
 * spring-android-rest-template
 * support-annotations
 */

package com.atwelm.aezwidget.responses.sonarr;

import com.atwelm.aezwidget.responses.generic.GenericLayout;
import com.atwelm.aezwidget.responses.interfaces.AEZCell;
import com.atwelm.aezwidget.responses.interfaces.AEZFetchLayoutResponseInterface;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * A class that creates appropriate layout objects for responses from a Sonarr server
 */
public class SonarrFetchLayoutResponse extends ArrayList<SonarrCell> implements AEZFetchLayoutResponseInterface {

    /**
     * The title to identify the server
     */
    public String title = "Sonarr";


    @Override
    public String getTitle() {
        return title;
    }

    // TODO: FIX!!
    @Override
    /**
     * Generates appropriate layouts from the calendar response. Currently there is only Today and Tomorrow
     */
    public List<AEZLayout> getLayouts() {
        if (this.size() == 0) {
            return new ArrayList<AEZLayout>(0);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar cal = Calendar.getInstance();
            String today = sdf.format(cal.getTime());

            cal.add(Calendar.DAY_OF_MONTH, 1);
            String tomorrow = sdf.format(cal.getTime());

            ArrayList<SonarrCell> allCells = new ArrayList<SonarrCell>(this);

            ArrayList<SonarrCell> todayCells = new ArrayList<>();
            ArrayList<SonarrCell> tomorrowCells = new ArrayList<>();

            for( SonarrCell cell : allCells) {
                if(cell.airDate.equals(today)) {
                    todayCells.add(cell);
                } else if (cell.airDate.equals(tomorrow)) {
                    tomorrowCells.add(cell);
                }
            }

            ArrayList<AEZCell> layoutCells = new ArrayList<>(tomorrowCells.size() + todayCells.size());

            layoutCells.add(new SonarrCell("Today", null, null));
            layoutCells.add(new SonarrCell("Tomorrow", null, null));

            Iterator<SonarrCell> todayIter = todayCells.iterator();
            Iterator<SonarrCell> tomorrowIter = tomorrowCells.iterator();

            while(todayIter.hasNext() || tomorrowIter.hasNext()) {
                if (todayIter.hasNext()) {
                    layoutCells.add(todayIter.next());
                } else {
                    layoutCells.add(new SonarrCell("", null, null));
                }

                if(tomorrowIter.hasNext()) {
                    layoutCells.add(tomorrowIter.next());
                } else {
                    layoutCells.add(new SonarrCell("", null, null));
                }
            }

            AEZLayout layout1 = new GenericLayout("Today and Tomorrow", layoutCells);

            ArrayList<AEZLayout> layouts = new ArrayList<>(1);

            layouts.add(layout1);
            return layouts;
        }
    }
}
