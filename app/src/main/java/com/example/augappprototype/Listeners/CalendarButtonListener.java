package com.example.augappprototype.Listeners;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bskim.maxheightscrollview.widgets.MaxHeightScrollView;
import com.example.augappprototype.MainActivity;
import com.example.augappprototype.R;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pao on 1/7/2018.
 * CalendarButtonListener
 * extends CaldroidListener
 * Responsible for the events that occur when a date on the calendar is clicked
 *
 * Methods:
 * onSelectDate(Date date, View view)
 *      popup of the events for that day displays on screen
 * dayEvents(Date date)
 *      checks if there are any events in the hash map
 * convertDate(Date date)
 *      Returns the year, month, and day
 * closeWindowListener(final Dialog eventPopup)
 *      on click will dismiss the event popup and go back to the calendar screen
 * filterEvent(final Dialog dialog)
 *      hides the basketball game event when the user unchecks the athletics category
 * showEditedEvent(final Dialog dialog)
 *      shows the Choir Performance event is showEdited event is true
 * studentMode(final Dialog dialog)
 *      shows the restrictions of student mode. Choir event is not visible to students unless
 *      showEditedEvent is set to true
 */
public class CalendarButtonListener extends CaldroidListener {

    /*--Data--*/
    private final MainActivity mainActivity;
    LinearLayout eventList;
    boolean athleticsCategory = false;
    boolean performanceCategory = false;
    boolean clubCategory = false;
    boolean researchCategory = false;
    boolean asaCategory = false;
    int numEvents;



    /*--Constructor--*/
    public CalendarButtonListener(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }//CalendarButtonListener

    /*--Methods--*/
    /**
     * onSelectDate(Date, View) --> void
     * When a date is selected a popup will be displayed that shows the events for that day
     * @param date
     * @param view
     */
    @Override
    public void onSelectDate(Date date, View view) {
        final Dialog eventPopupDialog = new Dialog(mainActivity);
        eventPopupDialog.setContentView(R.layout.eventpopup);
        closeWindowListener(eventPopupDialog);
        Date converted = convertDate(date);
        if (dayEvents(converted) == true) {
            dateForBanner(eventPopupDialog, date);
            addButtonsForEvents(eventPopupDialog, date);
            eventPopupDialog.show();
            Toast.makeText(mainActivity, "lol" + numEvents,
                    Toast.LENGTH_LONG).show();
        }//if
        else
            Toast.makeText(mainActivity, "No Events On This Day",
                    Toast.LENGTH_SHORT).show();
    }//onSelectDate

    /**
     * dayEvents(Date) --> boolean
     * Checks if there are any events in the hash map
     * @param date
     * @return
     */
    public boolean dayEvents(Date date){
        if (AddEventListener.allEvents.containsKey(date))//events in hash map
            return true;
        else
            return false;//no events in hash map
    }//dayEvents

    /**
     * convertDate(Date) --> Date
     * Returns the year, month, and day
     * @param date
     * @return
     */
    public Date convertDate(Date date){
        return new Date(date.getYear(), date.getMonth(), date.getDate());
    }//convertDate

    /**
     * closeWindowListener(Dialog) --> void
     * Closes the dialog when the close window button is clicked
     * @param eventPopup
     */
    public void closeWindowListener(final Dialog eventPopup){
        Button closeWindow = eventPopup.findViewById(R.id.closeButton);
        closeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventPopup.dismiss();
            }
        });
    }//closeWindowListener

    public void addButtonsForEvents(final Dialog eventPopup, final Date date) {
        numEvents = AddEventListener.allEvents.get(date).size();
        MaxHeightScrollView eventsInDay = eventPopup.findViewById(R.id.allEvents);
        eventList = new LinearLayout(mainActivity);
        eventList.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        eventList.setOrientation(LinearLayout.VERTICAL);
        for (int counter = 0; counter < AddEventListener.allEvents.get(date).size(); counter++) {
                Button eachEvent = new Button(mainActivity);
                eachEvent.setId(counter);
                eachEvent.setText(AddEventListener.allEvents.get(date).get(counter).get(0));
                eachEvent.setBackgroundResource(R.drawable.eventbuttonarrow);
                eachEvent.setTextSize(20);
                eachEvent.setLayoutParams(new LinearLayout
                        .LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                checkCategoryUserPicked(date, counter);
            final int finalCounter = counter;
            eachEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventPopup.setContentView(R.layout.eventpopup_details);
                        addTextViewForDetails(eventPopup, date, finalCounter);
                    }
                });

                eventList.addView(eachEvent);
                filterCategory(eachEvent);
        }

        eventsInDay.addView(eventList);
        mainActivity.setEventCount(date, numEvents);
    }


    public void addTextViewForDetails(Dialog eventPopup, Date date, int eventID){
        MaxHeightScrollView eventsInDay = eventPopup.findViewById(R.id.eventDetails);
        eventList = new LinearLayout(mainActivity);
        eventList.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        eventList.setOrientation(LinearLayout.VERTICAL);
            for(int y = 0; y < 3; y++ ) {
                TextView textView = new TextView(mainActivity);
                if (y == 0) {
                    textView.setText("Event Title: "
                            + AddEventListener.allEvents.get(date).get(eventID).get(y));
                    eventList.addView(textView);
                }
                else if (y == 1){
                    textView.setText("Event Location: "
                            + AddEventListener.allEvents.get(date).get(eventID).get(y));
                    eventList.addView(textView);
                }
                else{
                   textView.setText("Event Description: "
                           + AddEventListener.allEvents.get(date).get(eventID).get(y));
                    eventList.addView(textView);
                }
            }
        eventsInDay.addView(eventList);
    }

    public void dateForBanner(Dialog eventPopup, Date date){
        List<String> monthNames = Arrays.asList("January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November", "December");
        TextView dateBanner = eventPopup.findViewById(R.id.eventBanner);
        dateBanner.setText(" " + monthNames.get(date.getMonth()) + " " +
                date.getDate() + ", " + (date.getYear() + 1900));
    }

    public void checkCategoryUserPicked (Date date, int counter) {
        if (AddEventListener.allEvents.get(date).get(counter).get(4) == "yes") {
            athleticsCategory = true;
        }
        else {
            athleticsCategory = false;
        }
        if (AddEventListener.allEvents.get(date).get(counter).get(5) == "yes"){
            performanceCategory = true;
        }
        else {
            performanceCategory = false;
        }
        if (AddEventListener.allEvents.get(date).get(counter).get(6) == "yes") {
            clubCategory = true;
        }
        else {
            clubCategory = false;
        }
        if (AddEventListener.allEvents.get(date).get(counter).get(7) == "yes") {
            researchCategory = true;
        }
        else {
            researchCategory = false;
        }
        if (AddEventListener.allEvents.get(date).get(counter).get(8) == "yes") {
            asaCategory = true;
        }
        else {
            asaCategory = false;
        }
    }

    public void filterCategory(Button eachEvent) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        if (athleticsCategory == true && sharedPreferences.getBoolean("athleticsKey", true))
            eachEvent.setVisibility(View.VISIBLE);
        else if (performanceCategory == true && sharedPreferences.getBoolean("performanceKey", true))
            eachEvent.setVisibility(View.VISIBLE);
        else if (clubCategory == true && sharedPreferences.getBoolean("clubKey", true))
            eachEvent.setVisibility(View.VISIBLE);
        else if (researchCategory == true && sharedPreferences.getBoolean("researchKey", true))
            eachEvent.setVisibility(View.VISIBLE);
        else if (asaCategory == true && sharedPreferences.getBoolean("asaKey", true))
            eachEvent.setVisibility(View.VISIBLE);
        else {
            eachEvent.setVisibility(View.GONE);
            numEvents--;
        }
    }
}//CalendarButtonListener
