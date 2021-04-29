package exercise.find.roots;

import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MainActivityTest extends TestCase {

    @Test
    public void when_activityIsLaunching_then_theButtonShouldStartDisabled(){
        // create a MainActivity and let it think it's currently displayed on the screen
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        // test: make sure that the "calculate" button is disabled
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
        assertFalse(button.isEnabled());
    }

    @Test
    public void when_activityIsLaunching_then_theEditTextShouldStartEmpty(){
        // create a MainActivity and let it think it's currently displayed on the screen
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        // test: make sure that the "input" edit-text has no text
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        String input = inputEditText.getText().toString();
        assertTrue(input == null || input.isEmpty());
    }

    @Test
    public void when_userIsEnteringNumberInput_and_noCalculationAlreadyHappned_then_theButtonShouldBeEnabled(){
        // create a MainActivity and let it think it's currently displayed on the screen
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        // find the edit-text and the button
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);

        // test: insert input to the edit text and verify that the button is enabled
        // TODO: implement
        inputEditText.setText("123");
        assertTrue(button.isEnabled());
    }

    @Test
    public void when_activityIsLaunching_then_theProgressShouldBeHidden()
    {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);

        assertFalse(progressBar.isAnimating());
    }

    @Test
    public void when_badInputIsInserted_then_buttonShouldBeDisabled()
    {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        EditText editText = mainActivity.findViewById(R.id.editTextInputNumber);
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);

        editText.setText("17.3");
        assertFalse(button.isEnabled());
    }

    @Test
    public void when_goodInputAndThenClearInput_then_buttonShouldBeEnabledAndThenDisabled()
    {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        EditText editText = mainActivity.findViewById(R.id.editTextInputNumber);
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);

        editText.setText("17");
        assertTrue(button.isEnabled());

        editText.setText("");
        assertFalse(button.isEnabled());
    }


    // TODO: add 1 or 2 more unit tests to the activity. so your "writing tests" skill won't get rusty.
    //  possible flows to unit-test:
    //  - when activity launches, "progress" starts hidden
    //  - when inserting a good number and clicking the button, "progress" should be displayed
    //  - when user is entering a bad input (for example "17.3") the button should be disabled
    //  - when user is entering a good input and than deleting it, the button should be enabled and then disabled again
    //  - when user entered input and than activity recreates (user flipped the screen), the input in the new edit text is still there
    //  - when starting a calculation the button should be locked (disabled)
    //  - when starting a calculation and than activity receives "stopped_calculations" broadcast, the button should be unlocked (enabled)
    //  - when starting a calculation and than activity receives "stopped_calculations" broadcast, "progress" should disappear
    //
    // to mock a click on the button:
    //    call `button.performClick()`
    //
    // to mock inserting input to the edit-text:
    //    call `editText.setText("input here")`
    //
    // to mock sending a broadcast:
    //    create the broadcast intent (example: `new Intent("my_action_here")` ) and put extras
    //    call `RuntimeEnvironment.application.sendBroadcast()` to send the broadcast
    //    call `Shadows.shadowOf(Looper.getMainLooper()).idle();` to let the android OS time to process the broadcast the let your activity consume it
}