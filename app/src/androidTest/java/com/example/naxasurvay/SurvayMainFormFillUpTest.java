package com.example.naxasurvay;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Majestic on 11/8/2017.
 */


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SurvayMainFormFillUpTest {

    private SurveyMain surveyMainObject;

    private Random random = null;

    private int idSurveryer = 0, idHousehold = 0,
            husband = 0, wife = 0, children = 0, relatives = 0, others = 0,
            land = 0, property = 0;

    @Rule
    public ActivityTestRule<SurveyMain> surveyMainActivityTestRule
            = new ActivityTestRule<SurveyMain>(SurveyMain.class);

    @Before
    public void setUp() throws Exception {
        surveyMainObject = surveyMainActivityTestRule.getActivity();

        random = new Random();

    }

    @Test
    public void surveyValueCheck() throws Exception {
        for (int i = 0; i < 1; i++) {
            sleepFor(1);
            enterValue();
        }

    }


    private void enterValue() {
        /*clickAndEnterOnThis(R.id.id_number_surveyor, getSurvayerId());
        clickAndEnterOnThis(R.id.name_of_survayor, getSurvayerName());
        onView(withId(R.id.date_of_survey)).perform(scrollTo(), clearText(), typeText(getDate(2017,2000)));

        onView(withId(R.id.id_code)).perform(scrollTo(), clearText(), typeText(getHouseholdId()));

        onView(withId(R.id.Survay_district)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(String.class)))).atPosition(getRandomValue(2, 0)).perform(click());
        onView(withId(R.id.Survay_municipality)).perform(scrollTo()).perform(click());
        onData(allOf(is(instanceOf(String.class)))).atPosition(getRandomValue(8, 0)).perform(click());

        clickAndEnterOnThis(R.id.Survay_ward, getRandomValue(15, 1) + "");
        clickAndEnterOnThis(R.id.Survay_address, getHouseHoldAddress());

        closeSoftKeyboard();

        selectHouseHoldTopology();

        onView(withId(R.id.Respondent_age)).perform(scrollTo(), click(), clearText(), typeText(getRespondentAge()));

        selectGender();

        onView(withId(R.id.Respondent_email)).perform(scrollTo(), click(), clearText(), typeText(getRespondentEmail()));

        selectFamilyMembers();

        selectWorkingFamilyMember();*/


        //selectSourceIncome();

       /* selectAverageMonthlyIncome();

        selectAreaOfLandOrProperty();

        selectPriceOfLandOrProperty();*/

        selectLandPooling();


    }

    private void selectLandPooling() {
        if(getRandomValue(1,0)==1){
            clickOnThis(R.id.land_pooling_house);
        }
        if(getRandomValue(1,0)==1){
            clickOnThis(R.id.land_pooling_land);
        }

       /* onView(withId(R.id.land_pooling_land)).check(matches(isChecked()));
        if(onView(withId(R.id.land_pooling_land)).check(matches(isChecked()))==true){

        }*/
        clickAndEnterOnThis(R.id.pooling_yearof_purchase,getDate(2017,1980));
        clickAndEnterOnThis(R.id.pooling_valueof_purchase,getRandomValue(100,15)*100000+"");

    }

    private void selectPriceOfLandOrProperty() {
        if (land != 0) {
            clickAndEnterOnThis(R.id.total_land_price, (land * getRandomValue(900000, 100000)) + "");
        } else {
            clickAndEnterOnThis(R.id.total_land_price, "");
        }

        if (property != 0) {
            clickAndEnterOnThis(R.id.total_property_price, (land * getRandomValue(900000, 100000)) + "");
        } else {
            clickAndEnterOnThis(R.id.total_property_price, "");
        }

    }

    private void selectAreaOfLandOrProperty() {
        if (getRandomValue(1, 0) == 1) {
            land = getRandomValue(15, 3);
            clickAndEnterOnThis(R.id.land_in_Anna, land + "");
        } else {
            land = 0;
            clickAndEnterOnThis(R.id.land_in_Anna, "");
        }
        if (getRandomValue(1, 0) == 1) {
            property = getRandomValue(15, 3);
            clickAndEnterOnThis(R.id.property_in_Anna, property + "");
        } else {
            property = 0;
            clickAndEnterOnThis(R.id.property_in_Anna, "");
        }

    }

    private void selectAverageMonthlyIncome() {
        if (doesEarn(husband) == true) {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_husband, getAmount() + "");
        } else {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_husband, "");
        }
        if (doesEarn(wife) == true) {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_wife, getAmount() + "");
        } else {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_wife, "");
        }
        if (doesEarn(children) == true) {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_children, getAmount() + "");
        } else {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_children, "");
        }
        if (doesEarn(relatives) == true) {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_relatives, getAmount() + "");
        } else {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_relatives, "");
        }
        if (doesEarn(others) == true) {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_others, getAmount() + "");
        } else {
            clickAndEnterOnThis(R.id.Survay_average_monthlyIncome_of_others, "");
        }
    }

    private void selectSourceIncome() {
        onView(withId(R.id.spinnerMulti_Husband)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(String.class)))).atPosition(getRandomValue(2, 0)).perform(click());


        if (husband != 0) {
            onView(withId(R.id.spinnerMulti_Husband)).perform(scrollTo(), click());
        }
        if (wife != 0) {

        }
        if (children != 0) {

        }
        if (relatives != 0) {

        }
        if (others != 0) {

        }
    }

    private void selectWorkingFamilyMember() {
        husband = getRandomValue(husband, 0);
        wife = getRandomValue(wife, 0);
        children = getRandomValue(children, 0);
        relatives = getRandomValue(relatives, 0);
        others = getRandomValue(others, 0);

        onView(withId(R.id.Working_husband_no)).perform(scrollTo(), click(), clearText(), typeText(husband + ""));
        onView(withId(R.id.Working_wife_no)).perform(scrollTo(), click(), clearText(), typeText(wife + ""));
        onView(withId(R.id.Working_childrens_no)).perform(scrollTo(), click(), clearText(), typeText(children + ""));
        onView(withId(R.id.Working_relatives_no)).perform(scrollTo(), click(), clearText(), typeText(relatives + ""));
        onView(withId(R.id.Working_others_no)).perform(scrollTo(), click(), clearText(), typeText(others + ""));

        onView(withId(R.id.Survay_Workin_family_member)).perform(scrollTo(), click(), clearText())
                .perform(typeText(getTotalMember()));
    }

    private void selectFamilyMembers() {
        husband = getRandomValue(2, 0);
        wife = getRandomValue(2, 0);
        children = getRandomValue(15, 0);
        relatives = getRandomValue(5, 0);
        others = getRandomValue(5, 0);

        onView(withId(R.id.husband_no)).perform(scrollTo(), click(), clearText(), typeText(husband + ""));
        onView(withId(R.id.wife_no)).perform(scrollTo(), click(), clearText(), typeText(wife + ""));
        onView(withId(R.id.children_no)).perform(scrollTo(), click(), clearText(), typeText(children + ""));
        onView(withId(R.id.relatives_no)).perform(scrollTo(), click(), clearText(), typeText(relatives + ""));
        onView(withId(R.id.others_no)).perform(scrollTo(), click(), clearText(), typeText(others + ""));

        onView(withId(R.id.Survay_Total_family_member)).perform(scrollTo(), click(), clearText())
                .perform(typeText(getTotalMember()));

    }

    private void selectGender() {
        switch (getRandomValue(2, 0)) {
            case 0:
                onView(withId(R.id.male)).perform(scrollTo(), click());
                break;
            case 1:
                onView(withId(R.id.female)).perform(scrollTo(), click());
                break;
            case 2:
                onView(withId(R.id.others)).perform(scrollTo(), click());
                break;
            default:
                break;
        }
    }

    private String getRespondentEmail() {
        char ch;
        String email = "";
        String[] emailservice =
                {"gmail", "outlook", "icloud", "live", "hotmail", "yahoo"};
        for (int i = getRandomValue(10, 5); i >= 0; i--) {
            ch = (char) getRandomValue(122, 97);
            email += ch;
        }
        email += "@";
        email += emailservice[getRandomValue(emailservice.length - 1, 0)];
        email += ".com";
        return email;
    }

    private String getRespondentAge() {
        int age = getRandomValue(50, 15);
        return age + "";
    }

    private void selectHouseHoldTopology() {
        if (getRandomValue(1, 0) == 1) {
            clickOnThis(R.id.single_family_detached);
        }
        if (getRandomValue(1, 0) == 1) {
            clickOnThis(R.id.multy_family_house);
        }
        if (getRandomValue(1, 0) == 1) {
            clickOnThis(R.id.apartment_block);
        }
        if (getRandomValue(1, 0) == 1) {
            clickOnThis(R.id.mixed_use_block);
        }
        if (getRandomValue(1, 0) == 1) {
            clickOnThis(R.id.number_of_floors);
        }


    }

    private String getHouseHoldAddress() {
        String[] address =
                {"Kalanki", "Balkhu", "New Road", "Naxal", "Basantapur", "Jorpati"};
        return address[getRandomValue(address.length - 1, 0)];
    }

    private String getHouseholdId() {
        return (++idHousehold) + "";
    }

    private String getDate(int maxYear,int minYear) {
        String date = getRandomValue(30, 1)
                + "-" + getRandomValue(12, 1)
                + "-" + getRandomValue(maxYear, minYear);
        return date;
    }


    private String getSurvayerId() {
        return (++idSurveryer) + "";
    }

    private String getSurvayerName() {
        String[] name =
                {"Nt Magar", "Samir Dangal", "Bidur Bastola", "Nishon Tandukar", "Sumit", "Pradeep Acharya"};
        return name[getRandomValue(name.length - 1, 0)];
    }

    //Extra Required Methods
    //Methods used inside methods

    private String getTotalMember() {
        return husband + wife + children + relatives + others + "";
    }

    private int getAmount() {
        return getRandomValue(50, 10) * 1000;
    }

    private boolean doesEarn(int member) {
        if (member != 0 && getRandomValue(1, 0) != 0) {
            return true;
        }
        return false;
    }

    private void clickOnThis(int id) {
        onView(withId(id)).perform(scrollTo(), click());
    }

    private void clickAndEnterOnThis(int viewId, String string) {
        onView(withId(viewId)).perform(scrollTo(), click(), clearText(), typeText(string));
    }

    private int getRandomValue(int max, int min) {
        max++;
        return random.nextInt(max - min) + min;
    }

    private void sleepFor(int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
