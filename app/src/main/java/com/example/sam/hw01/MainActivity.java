package com.example.sam.hw01;

/**
 * HW #1
 * MainActivity.java
 * Samuel Painter, Praveen Surenani
 */

import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final double MALE_MULT = .73;
    private static final double FEMAALE_MULT = .66;

    private SeekBar alcohol_percent;
    private TextView current_percent;
    private EditText weight_input;
    private Switch gender_switch;
    private Button save_button;
    private Button add_button;
    private Button reset_button;
    private RadioGroup drink_sizes;
    private TextView bac_level;
    private ProgressBar progress;
    private TextView status;

    private double drink_percent;
    private double current_mult;
    private double current_bac;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alcohol_percent = (SeekBar) findViewById(R.id.seek);
        current_percent = (TextView) findViewById(R.id.current);
        weight_input = (EditText) findViewById(R.id.weight_input);
        gender_switch = (Switch) findViewById(R.id.gender);
        save_button = (Button) findViewById(R.id.save);
        drink_sizes = (RadioGroup) findViewById(R.id.radio_buttons);
        reset_button = (Button) findViewById(R.id.reset);
        add_button = (Button) findViewById(R.id.add);
        bac_level = (TextView) findViewById(R.id.bac_level);
        progress = (ProgressBar) findViewById(R.id.progress);
        status = (TextView) findViewById(R.id.status);

        current_percent.setText("5%");
        String bac_text = getString(R.string.level_tag) + "0.0";
        bac_level.setText(bac_text);
        drink_percent = .05;
        current_mult = MALE_MULT;
        amount = 0.0;
        current_bac = 0.0;

        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alcohol_percent.setProgress(1);
                current_percent.setText("5%");
                drink_percent = .05;
                drink_sizes.check(R.id.oz1);
                current_bac = 0.0;
                amount = 0.0;
                bac_level.setText((getString(R.string.level_tag)) + "0.0");
                progress.setProgress(0);
                gender_switch.setChecked(true);
                current_mult = MALE_MULT;
                weight_input.setText("");
                updateStatus();
                checkBac();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(weight_input)) {
                    weight_input.setError("Enter the weight in lbs.");
                    return;
                }

                double weight = Double.parseDouble((weight_input.getText().toString()));
                current_bac = (5.14 * amount) / (weight * current_mult);
                String bac_string = String.format ("%.2f", current_bac);
                bac_level.setText((getString(R.string.level_tag) + bac_string));
                progress.setProgress(Math.min(25, (int) current_bac) * 100);
                updateStatus();
                checkBac();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(weight_input)) {
                    weight_input.setError("Enter the weight in lbs.");
                    return;
                }
                double alcohol_consumed = (double)currentDrinkSize() * drink_percent;
                amount += alcohol_consumed;
                double weight = Double.parseDouble(weight_input.getText().toString());
                double bac_to_add = (alcohol_consumed * 5.14) / (weight * current_mult);
                current_bac += bac_to_add;
                String bac_string = String.format("%.2f", current_bac);
                bac_level.setText((getString(R.string.level_tag) + bac_string));
                progress.setProgress(Math.min(25, (int) current_bac) * 100);
                updateStatus();
                checkBac();
            }
        });

        gender_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    current_mult = MALE_MULT;
                else
                    current_mult = FEMAALE_MULT;
            }
        });

        alcohol_percent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drink_percent = (double) (progress * 5) / 100.0;
                String text = Integer.toString(progress * 5) + "%";
                current_percent.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    private int currentDrinkSize() {
        int id = drink_sizes.getCheckedRadioButtonId();
        View rb = drink_sizes.findViewById(id);
        int index = drink_sizes.indexOfChild(rb);
        if (index == 0)
            return 1;
        else if (index == 1)
            return 5;
        else
            return 12;
    }

    private void checkBac() {
        if (current_bac >= .25) {
            add_button.setEnabled(false);
            save_button.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No more drinks for you", Toast.LENGTH_SHORT).show();
        } else {
            add_button.setEnabled(true);
            save_button.setEnabled(true);
        }
    }

    private void updateStatus() {
        if (current_bac <= .08) {
            status.setText(getString(R.string.safe));
            status.setBackgroundColor(getColor(R.color.safe));
        } else if (current_bac <= .20) {
            status.setText(getString(R.string.careful));
            status.setBackgroundColor(getColor(R.color.careful));
        } else {
            status.setText(getString(R.string.danger));
            status.setBackgroundColor(getColor(R.color.danger));
        }

    }

}
