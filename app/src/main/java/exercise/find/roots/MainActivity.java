package exercise.find.roots;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiverForSuccess = null;
    // TODO: add any other fields to the activity as you want
    private BroadcastReceiver broadcastReceiverForFail = null;
    private String inputText = "";

    private boolean calculating = false;
    private boolean legalInput = false;

    private boolean onlyDigits(String str)
    {
        if (str.length() == 0)
        {
            return false;
        }
        for (int i=0; i< str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
            {
                return false;
            }
        }
        try {
            Long.parseLong(str);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
        Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

        // set initial UI:
        progressBar.setVisibility(View.GONE); // hide progress
        editTextUserInput.setText(""); // cleanup text in edit-text
        editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
        buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

        // set listener on the input written by the keyboard to the edit-text
        editTextUserInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                // text did change
                String newText = editTextUserInput.getText().toString();
                inputText = newText;
                // todo: check conditions to decide if button should be enabled/disabled (see spec below)
                if (onlyDigits(newText))
                {
                    legalInput = true;
                    buttonCalculateRoots.setEnabled(true);
                }
                else
                {
                    legalInput = false;
                    buttonCalculateRoots.setEnabled(false);
                    //TODO: out put to screen invalid input
                    System.out.println("invalid input "+ newText); //TODO delete
                }
            }
        });

        // set click-listener to the button
        buttonCalculateRoots.setOnClickListener(v -> {
            Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
            String userInputString = editTextUserInput.getText().toString();
            // todo: check that `userInputString` is a number. handle bad input. convert `userInputString` to long
            long userInputLong = Long.parseLong(userInputString);
            intentToOpenService.putExtra("number_for_service", userInputLong);
            startService(intentToOpenService);
            calculating = true;
            // todo: set views states according to the spec (below)
            buttonCalculateRoots.setEnabled(false);
            editTextUserInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        });

        // register a broadcast-receiver to handle action "found_roots"
        broadcastReceiverForSuccess = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent incomingIntent) {
                if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots")) return;
                // success finding roots!
                System.out.println("succecss");
        /*
         TODO: handle "roots-found" as defined in the spec (below).
          also:
           - the service found roots and passed them to you in the `incomingIntent`. extract them.
           - when creating an intent to open the new-activity, pass the roots as extras to the new-activity intent
             (see for example how did we pass an extra when starting the calculation-service)
         */
                calculating = false;
                buttonCalculateRoots.setEnabled(true);
                editTextUserInput.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                long originalNum = incomingIntent.getLongExtra("original_number", 0);
                long root1 = incomingIntent.getLongExtra("root1", 0);
                long root2 = incomingIntent.getLongExtra("root2", 0);
                float calcTime = incomingIntent.getFloatExtra("time_of_calculation", 0);

                System.out.println("root1 " + root1);
                System.out.println("root2 " + root2);
                System.out.println("time " + calcTime);

                Intent successIntent = new Intent(MainActivity.this, SuccessActivity.class);
                successIntent.putExtra("original_num", originalNum);
                successIntent.putExtra("root1", root1);
                successIntent.putExtra("root2", root2);
                successIntent.putExtra("time_of_calculation", calcTime);
                startActivity(successIntent);
            }
        };
        registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));


            /*
    todo:
     add a broadcast-receiver to listen for abort-calculating as defined in the spec (below)
     to show a Toast, use this code:
     `Toast.makeText(this, "text goes here", Toast.LENGTH_SHORT).show()`
     */
        broadcastReceiverForFail = new BroadcastReceiver() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onReceive(Context context, Intent incomingIntent) {
                if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations")) return;

                System.out.println("fail");
                calculating = false;
                buttonCalculateRoots.setEnabled(true);
                editTextUserInput.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                float calculationTime = incomingIntent.getFloatExtra("time_of_calculation", 0);
                Toast.makeText(MainActivity.this, String.format("calculation aborted after %f seconds", calculationTime), Toast.LENGTH_LONG).show();
            }
        };
        registerReceiver(broadcastReceiverForFail, new IntentFilter("stopped_calculations"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // todo: remove ALL broadcast receivers we registered earlier in onCreate().
        //  to remove a registered receiver, call method `this.unregisterReceiver(<receiver-to-remove>)`
        this.unregisterReceiver(broadcastReceiverForFail);
        this.unregisterReceiver(broadcastReceiverForSuccess);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO: put relevant data into bundle as you see fit
        outState.putString("text_input", inputText);
        outState.putBoolean("calculating", calculating);
        outState.putBoolean("legal_input", legalInput);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // TODO: load data from bundle and set screen state (see spec below)
        ProgressBar progressBar = findViewById(R.id.progressBar);
        EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
        Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

        String lastInput = savedInstanceState.getString("text_input");
        editTextUserInput.setText(lastInput);

        legalInput = savedInstanceState.getBoolean("legal_input");
        calculating = savedInstanceState.getBoolean("calculating");
        if (calculating)
        {
            buttonCalculateRoots.setEnabled(false);
            editTextUserInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            editTextUserInput.setEnabled(true);
            buttonCalculateRoots.setEnabled(legalInput);
            progressBar.setVisibility(View.GONE);
        }
    }
}


/*

TODO:
the spec is:

upon launch, Activity starts out "clean":
* progress-bar is hidden
* "input" edit-text has no input and it is enabled
* "calculate roots" button is disabled

the button behavior is:
* when there is no valid-number as an input in the edit-text, button is disabled
* when we triggered a calculation and still didn't get any result, button is disabled
* otherwise (valid number && not calculating anything in the BG), button is enabled

the edit-text behavior is:
* when there is a calculation in the BG, edit-text is disabled (user can't input anything)
* otherwise (not calculating anything in the BG), edit-text is enabled (user can tap to open the keyboard and add input)

the progress behavior is:
* when there is a calculation in the BG, progress is showing
* otherwise (not calculating anything in the BG), progress is hidden

when "calculate roots" button is clicked:
* change states for the progress, edit-text and button as needed, so user can't interact with the screen

when calculation is complete successfully:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* open a new "success" screen showing the following data:
  - the original input number
  - 2 roots combining this number (e.g. if the input was 99 then you can show "99=9*11" or "99=3*33"
  - calculation time in seconds

when calculation is aborted as it took too much time:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* show a toast "calculation aborted after X seconds"


upon screen rotation (saveState && loadState) the new screen should show exactly the same state as the old screen. this means:
* edit-text shows the same input
* edit-text is disabled/enabled based on current "is waiting for calculation?" state
* progress is showing/hidden based on current "is waiting for calculation?" state
* button is enabled/disabled based on current "is waiting for calculation?" state && there is a valid number in the edit-text input


 */