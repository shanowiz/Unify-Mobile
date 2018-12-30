package com.example.shano.unify;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by shano on 5/14/2017.
 */

public class SetPreferenceActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private Switch studentsUnionSwitch;
    private Switch facultySwitch;
    private Switch clubSwitch;
    private Switch discountSwitch;

    private static String studentsUnion, faculty, club, discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.set_preference_layout);

        studentsUnionSwitch = (Switch) findViewById(R.id.students_union_switch);
        facultySwitch = (Switch) findViewById(R.id.faculty_switch);
        clubSwitch = (Switch) findViewById(R.id.club_switch);
        discountSwitch = (Switch) findViewById(R.id.discount_switch);

        studentsUnionSwitch.setOnCheckedChangeListener(this);
        facultySwitch.setOnCheckedChangeListener(this);
        clubSwitch.setOnCheckedChangeListener(this);
        discountSwitch.setOnCheckedChangeListener(this);


        preferenceCheck();

        studentsUnion = "none";
        faculty = "none";
        club = "none";
        discount = "none";

        Button button = (Button) findViewById(R.id.preferenceButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

               SetPreferenceActivity.this.finish();


            }

        });


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (studentsUnionSwitch.isChecked()) {
            studentsUnion = "union";
        } else if (!studentsUnionSwitch.isChecked()) {
            studentsUnion = "none";
        }

        if (facultySwitch.isChecked()) {
            faculty = "faculty";
        }else if (!facultySwitch.isChecked()) {
            faculty = "none";
        }

        if (clubSwitch.isChecked()) {
            club ="club";
        }else if (!clubSwitch.isChecked()) {
            club ="none";
        }

        if (discountSwitch.isChecked()) {
            discount = "discount";
        }else if (!discountSwitch.isChecked()) {
            discount = "none";
        }

        storePreference();
    }

    public void storePreference () {

        String message = studentsUnion + "+" + faculty + "+" + club + "+" + discount;

        try {

            FileOutputStream fileOutputStream = openFileOutput("preference",MODE_PRIVATE);
            fileOutputStream.write(message.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getPreference () {

       ArrayList<String> preference = new ArrayList<>();

        try {
            String message ="";
            FileInputStream fileInputStream = openFileInput("preference");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            while ((message = bufferedReader.readLine()) != null) {

                stringBuffer.append(message);

                String split [] = stringBuffer.toString().split("\\+");


                for (int i=0; i<split.length; i++) {

                    preference.add(split[i]);
                }
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return preference;
    }


    public void preferenceCheck () {

        ArrayList<String> preference = getPreference();

        for (String temp : preference) {

            if (temp.equals("union")) {

                studentsUnionSwitch.setChecked(true);
            } else if (temp.equals("faculty")) {

                facultySwitch.setChecked(true);
            }else if (temp.equals("club")) {

                clubSwitch.setChecked(true);
            }else if (temp.equals("discount")) {

                discountSwitch.setChecked(true);
            }


        }


    }



}
