package com.mobileapp.shoppinglist;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ListElementsFragment extends Fragment {

    static ListView listView;
    static ArrayList<String> items;
    static ListViewElementAdapter adapter;
    static List<String> listToDisplay;
    static HashMap<String, List<String>> map;
    static String ListToAccess;
    View view;
    String websiteDomain="";


    EditText input;
    ImageView enter;
    static MediaPlayer player;

    ImageView display;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_list_elements, container, false);
        loadData();
        player = MediaPlayer.create(getContext(), R.raw.watersound);

        listView = view.findViewById(R.id.listview);
        input = view.findViewById(R.id.input);
        enter = view.findViewById(R.id.add);
        items = new ArrayList<>();

        adapter = new ListViewElementAdapter(getContext(), items);
        listView.setAdapter(adapter);


        ListToAccess = ListElementsFragmentArgs.fromBundle(requireArguments()).getListToAccess();
        map = ListElementsFragmentArgs.fromBundle(requireArguments()).getMapList();

        listToDisplay = map.get(ListToAccess);

        for(int i =0; i<listToDisplay.size(); i++){
            addItem(listToDisplay.get(i).toString());
        }



        enter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String text = input.getText().toString();
                if(text == null || text.length() == 0){
                    player.start();
                    Toast.makeText(getContext(),"Enter an Item", Toast.LENGTH_SHORT).show();
                }
                else{
                    player.start();
                    if (!listToDisplay.contains(text)) {
                        listToDisplay.add(text);
                        addItem(text);
                        input.setText("");
                        Toast.makeText(getContext(),"added " + text, Toast.LENGTH_SHORT).show();
                        map.replace(ListToAccess,listToDisplay);
                        System.out.println(map);
                    }
                    else{
                        Toast.makeText(getContext(),"item name already included", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                player.start();
                CreatePopUpWindow(items.get(position));
            }
        });

        TextView textRec = view.findViewById(R.id.textRec);
        textRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                CreatePopUpWindowTextRecognition();
            }
        });



        return view;
    }
    //saves data across section
    private void saveData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(map);
        editor.putString("task list", json);
        editor.apply();
    }
    //saves data across section
    private void loadData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<HashMap<String, List<String>>>() {}.getType();
        map = gson.fromJson(json, type);

        if (map == null){
            map = new HashMap<String, List<String>>();
        }
    }
    //saves data across section
    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

    //remove reminder function -> responds to ListViewElementAdapter
    public void addItem(String item){
        items.add(item);
        listView.setAdapter(adapter);
    }
    //remove reminder function -> responds to ListViewElementAdapter
    @SuppressLint("NewApi")
    public static void rem(int remove){
        player.start();
        items.remove(remove);
        listToDisplay.remove(remove);
        map.replace(ListToAccess,listToDisplay);
        listView.setAdapter(adapter);
        System.out.println(map);
    }
    //creates pop up window
    private void CreatePopUpWindow(String str){
        View popUpView = getLayoutInflater().inflate(R.layout.fragment_pop_up_window, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        PopupWindow popUpWindow = new PopupWindow(popUpView, width, height, true);
        view.post(new Runnable(){
            @Override
            public void run(){
                popUpWindow.showAtLocation(view, Gravity.CENTER,0,0);
            }
        });
        TextView searchOnline = popUpView.findViewById(R.id.searchOnlineButton);
        TextView read = popUpView.findViewById(R.id.ReadNewsButton);
        TextView close = popUpView.findViewById(R.id.closeButton);
        TextView genSearch = popUpView.findViewById(R.id.genSearch);
        EditText domain = popUpView.findViewById(R.id.websiteName);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                popUpWindow.dismiss();
            }
        });
        //starts intent to show items to buy at amazon
        searchOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/s?k="+ str));
                startActivity(intent);
            }
        });
        //starts intent to show news about item
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://news.google.com/search?q="+ str));
                startActivity(intent);
            }
        });
        //gets website domain from user
        domain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                websiteDomain = domain.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //search website given domain
        genSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(websiteDomain == ""){
                    Toast.makeText(getContext(),"Please enter website domain", Toast.LENGTH_SHORT).show();
                }
                else {
                    player.start();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/search?q=site:" + websiteDomain + "+" + str));
                    startActivity(intent);
                }
            }
        });
    }
    //creates pop up window
    private void CreatePopUpWindowTextRecognition() {
        View popUpView = getLayoutInflater().inflate(R.layout.text_recognition, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        PopupWindow popUpWindow = new PopupWindow(popUpView, width, height, true);
        view.post(new Runnable() {
            @Override
            public void run() {
                popUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });
        Button upload = popUpView.findViewById(R.id.upload);
        Button add = popUpView.findViewById(R.id.add);
        TextView closeCam = popUpView.findViewById(R.id.closeButtonCamera);
        display = popUpView.findViewById(R.id.imageDisplay);

        //starts camera to capture image
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
        //extract text from image
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer recognizer = new TextRecognizer.Builder(getContext()).build();
                if(!recognizer.isOperational()){
                    Toast.makeText(getContext(), "Error occurred", Toast.LENGTH_SHORT);
                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i=0; i<textBlockSparseArray.size(); i++){
                        TextBlock textBlock = textBlockSparseArray.valueAt(i);
                        stringBuilder.append(textBlock.getValue());
                        stringBuilder.append("\n");
                    }
                    System.out.println(stringBuilder.toString().toLowerCase());
                    listToDisplay.add(stringBuilder.toString().toLowerCase());
                    addItem(stringBuilder.toString().toLowerCase());
                    popUpWindow.dismiss();
                }
            }
        });
        //close popup window
        closeCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpWindow.dismiss();
            }
        });

    }
    //called when image is captured using camera
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 100){
                Bitmap CaptureImage = (Bitmap) data.getExtras().get("data");
                display.setImageBitmap(CaptureImage);
                bitmap = CaptureImage;

            }
        }
    }
}