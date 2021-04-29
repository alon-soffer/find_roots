package exercise.find.roots;

//import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

//    @SuppressLint("DefaultLocale")
    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        TextView originalNumText = findViewById(R.id.originalNumberText);
        TextView rootsText = findViewById(R.id.rootsText);
        TextView timeText = findViewById(R.id.timeText);


        Intent rootsIntent = getIntent();
        long originalNum = rootsIntent.getLongExtra("original_num", 0);
        long root1 = rootsIntent.getLongExtra("root1", 0);
        long root2 = rootsIntent.getLongExtra("root2", 0);
        float timeCalc = rootsIntent.getFloatExtra("time_of_calculation", 0);

        originalNumText.setText(String.format("Original number: %d", originalNum));
        rootsText.setText(String.format("Roots: %d, %d", root1, root2));
        timeText.setText(String.format("Calculation time: %f seconds", timeCalc));
    }
}
