package com.mobileapp.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;



public class CalendarFragment extends Fragment {

    static ListView listView;
    static ArrayList<String> items;
    static ListViewCalendarAdapter adapter;
    View view;

    CalendarView calendarView;
    static MediaPlayer player;

    int hr, min, sec;
    String yyyy, mm, dd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_calendar, container, false);
        player = MediaPlayer.create(getContext(), R.raw.watersound);

        calendarView = view.findViewById(R.id.calendarView);
        listView = view.findViewById(R.id.listview);
        loadData();


        adapter = new ListViewCalendarAdapter(getContext(), items);
        listView.setAdapter(adapter);


        //set calendar listener to respond to click
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                yyyy = String.valueOf(year);
                mm = String.valueOf(month+1);
                dd = String.valueOf(dayOfMonth);
                //display popup window when date is clicked
                CreatePopUpWindow();
            }
        });

        return view;
    }
    //add reminder function -> responds to ListViewCalendarAdapter
    public void addItem(String item){
        items.add(item);
        listView.setAdapter(adapter);
    }
    //remove reminder function -> responds to ListViewCalendarAdapter
    public static void removeItem(int remove, String name){
        player.start();
        items.remove(remove);
        listView.setAdapter(adapter);
    }
    //creates popup window
    private void CreatePopUpWindow() {
        View popUpView = getLayoutInflater().inflate(R.layout.fragment_pop_up_window_calendar, null);
        player.start();

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        PopupWindow popUpWindow = new PopupWindow(popUpView, width, height, focusable);
        view.post(new Runnable() {
            @Override
            public void run() {
                popUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });

        TextView close = popUpView.findViewById(R.id.closeCalendar);
        TextView save = popUpView.findViewById(R.id.saveTime);
        EditText reminder = popUpView.findViewById(R.id.reminderName);
        EditText hours = popUpView.findViewById(R.id.Hours);
        EditText minutes =popUpView.findViewById(R.id.Minutes);
        EditText seconds=popUpView.findViewById(R.id.seconds);

        hours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    hr = Integer.valueOf(hours.getText().toString());
                    System.out.println(hr);
                }
                catch (NumberFormatException e){
                    Toast.makeText(getContext(),"Need a number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    min = Integer.parseInt(minutes.getText().toString());
                    System.out.println(min);
                }
                catch (NumberFormatException e){
                    Toast.makeText(getContext(),"Need a number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    sec = Integer.valueOf(seconds.getText().toString());
                    System.out.println(sec);
                }
                catch (NumberFormatException e){
                    Toast.makeText(getContext(),"Need a number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                if(hr >=0 && hr <=23 && min >=0 && min <= 59 && sec >= 0 && sec <= 59 && !reminder.getText().toString().equals("")) {
                    String thisHr, thisMin, thisSec;
                    if(hr < 10)
                        thisHr = "0" + Integer.toString(hr);
                    else
                        thisHr =  Integer.toString(hr);
                    if(min < 10)
                        thisMin = "0" + Integer.toString(min);
                    else
                        thisMin =  Integer.toString(min);
                    if(sec < 10)
                        thisSec = "0" + Integer.toString(sec);
                    else
                        thisSec = Integer.toString(sec);

                    addItem(reminder.getText().toString() + " at " + thisHr + ": "+ thisMin + ": "+ thisSec + " on " + mm + "/" + dd + "/" + yyyy);
                    System.out.println(items);
                    popUpWindow.dismiss();
                }
                else{
                    if (reminder.getText().toString().equals(""))
                        Toast.makeText(getContext(),"Please add a name for your reminder", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(),"Invalid Time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                popUpWindow.dismiss();
            }
        });

    }

    //the whole section below is meant to save data accross sections
    @Override
    public void onPause() {
        saveData();
        System.out.println("onPause() called");
        super.onPause();
    }
    private void saveData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        System.out.println("json: " + json);
        editor.putString("lists", json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        items = gson.fromJson(json, type);

        if (items == null){
            items = new ArrayList<String>();
        }
    }



}