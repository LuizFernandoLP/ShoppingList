package com.mobileapp.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


public class ListFragment extends Fragment {

    static ListView listView;
    static ArrayList<String> items;
    static ListViewAdapter adapter;
    public static LinkedHashMap<String, List<String>> map;
    static MediaPlayer player;
    static View view;
    private  static Context mContext;



    EditText input;
    ImageView enter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getContext();
        loadData();
        player = MediaPlayer.create(getContext(), R.raw.watersound);

        listView = view.findViewById(R.id.listview);
        input = view.findViewById(R.id.input);
        enter = view.findViewById(R.id.add);
        items = new ArrayList<>();


        adapter = new ListViewAdapter(getContext(), items);
        listView.setAdapter(adapter);

        ListFragmentArgs args = ListFragmentArgs.fromBundle(requireArguments());

        if(args.getFullMap() != null){
            map = args.getFullMap();
        }

        for ( String key : map.keySet() ) {
            addItem(key);
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = input.getText().toString();
                if(text == null || text.length() == 0){
                    player.start();
                    Toast.makeText(getContext(),"Enter an Item", Toast.LENGTH_SHORT).show();
                }
                else{
                    player.start();
                    if (!map.containsKey(text)) {
                        map.put(text, new LinkedList<String>());
                        addItem(text);
                        input.setText("");
                        Toast.makeText(getContext(),"added " + text, Toast.LENGTH_SHORT).show();
                        System.out.println(map);
                    }
                    else{
                        Toast.makeText(getContext(),"List name has already been used", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                player.start();
                String name = items.get(position);
                NavDirections action = (NavDirections) ListFragmentDirections.actionListFragmentToListElementsFragment(name, map);
                Navigation.findNavController(view).navigate(action);


            }
        });

        return view;
    }
    //save data across sections
    private void saveData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(map);
        editor.putString("task list", json);
        editor.apply();
    }
    //save data across sections
    private void loadData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<LinkedHashMap<String, List<String>>>() {}.getType();
        map = gson.fromJson(json, type);

        if (map == null){
            map = new LinkedHashMap<String, List<String>>();
        }
    }
    //save data across sections
    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }
    //remove reminder function -> responds to ListViewAdapter
    public void addItem(String item){
        items.add(item);
        listView.setAdapter(adapter);
    }
    //remove reminder function -> responds to ListViewAdapter
    public static void removeItem(int remove, String name){
        player.start();
        items.remove(remove);
        map.remove(name);
        listView.setAdapter(adapter);
        System.out.println(map);
    }
    //get user location and return nearby stores (given string item)
    public static void getLocation(String item){
        player.start();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/"+ item));
        view.getContext().startActivity(intent);
    }
}