package com.example.naxasurvay;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Majestic on 11/8/2017.
 */


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule=
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void checkMainPageOpen() throws Exception{
        View v1,v2,v3,v4,v5,v6;

        v1=mainActivityActivityTestRule.getActivity().findViewById(R.id.toolbar);
        v2=mainActivityActivityTestRule.getActivity().findViewById(R.id.survayFormTopLayout);
        v3=mainActivityActivityTestRule.getActivity().findViewById(R.id.survayForm);
        v4=mainActivityActivityTestRule.getActivity().findViewById(R.id.savedFormTopLayout);
        v5=mainActivityActivityTestRule.getActivity().findViewById(R.id.mapFormTopLayout);
        v6=mainActivityActivityTestRule.getActivity().findViewById(R.id.mapHousehold);

        assertNotNull(v1);
        assertNotNull(v2);
        assertNotNull(v3);
        assertNotNull(v4);
        assertNotNull(v5);
        assertNotNull(v6);
    }

}
