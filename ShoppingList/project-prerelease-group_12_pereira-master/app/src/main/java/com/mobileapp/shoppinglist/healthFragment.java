package com.mobileapp.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;


public class healthFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    static ListView listView;
    static ArrayList<String> items;
    static ListViewHealthAdapter adapter;
    View view;

    EditText input;
    ImageView enter;
    Spinner spinner;
    Button call;

    String pharmacy;
    static MediaPlayer player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health, container, false);
        view.getBackground().setAlpha(50);
        loadData();
        player = MediaPlayer.create(getContext(), R.raw.watersound);

        listView = view.findViewById(R.id.listview);
        input = view.findViewById(R.id.input);
        enter = view.findViewById(R.id.add);
        spinner = view.findViewById(R.id.spinner);
        call = view.findViewById(R.id.callButton);

        adapter = new ListViewHealthAdapter(getContext(), items);
        listView.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.pharmacies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //adds medication to the list
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = input.getText().toString();
                if(text == null || text.length() == 0){
                    player.start();
                    Toast.makeText(getContext(),"Enter medication", Toast.LENGTH_SHORT).show();
                }
                else{
                    player.start();
                    if (!items.contains(text)) {
                        addItem(text);
                        input.setText("");
                        Toast.makeText(getContext(),"added " + text, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(),"name has already been used", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // opens pharmacy website
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                player.start();
                if(Objects.equals(pharmacy, "CVS")){
                    player.start();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cvs.com/search?searchTerm="+ items.get(position)));
                    startActivity(intent);

                }
                else if (Objects.equals(pharmacy, "Walgreens")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.walgreens.com/search/results.jsp?Ntt="+ items.get(position)));
                    startActivity(intent);

                }

            }
        });
        // start 911 emergency call
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                String phone = "911";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });


        return view;
    }
    //remove reminder function -> responds to ListViewHealthAdapter
    public void addItem(String item){
        items.add(item);
        listView.setAdapter(adapter);
    }
    //remove reminder function -> responds to ListViewHealthAdapter
    public static void removeItem(int remove, String name){
        player.start();
        items.remove(remove);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        pharmacy = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //the whole section below is meant to save data accross sections
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
        editor.putString("pharmacy", json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("pharmacy", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        items = gson.fromJson(json, type);

        if (items == null){
            items = new ArrayList<String>();
        }
    }
}