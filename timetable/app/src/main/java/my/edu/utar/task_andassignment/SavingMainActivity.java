package my.edu.utar.task_andassignment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;
import java.util.Map;

public class SavingMainActivity extends AppCompatActivity {


    private CalendarView calendarView;
    private TextView expenseTextView, savingTextView;
    private boolean isDarkModeEnabled = false;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "Record";
    private static final String KEY_AMOUNT = "amount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendarView = findViewById(R.id.calendarView);
        expenseTextView = findViewById(R.id.expenseTextView);
        savingTextView = findViewById(R.id.savingTextView);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isDarkModeEnabled = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                showExpenseDetails(year, month, dayOfMonth);
            }
        });
    }

    private void showExpenseDetails(int year, int month, int dayOfMonth) {
        String selectedDateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);

        float totalExpense = 0;
        float totalSaving = 0;

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(KEY_AMOUNT)) {
                String[] keyParts = key.split("_");
                if (keyParts.length >= 3) {
                    String datePart = keyParts[2];
                    if (datePart.equals(selectedDateStr)) {
                        String type = keyParts[1];
                        float amount = sharedPreferences.getFloat(key, 0);
                        if (type.equals("Expense")) {
                            totalExpense += amount;
                        } else if (type.equals("Saving")) {
                            totalSaving += amount;
                        }
                    }
                }
            }
        }

        expenseTextView.setText(String.format(Locale.getDefault(), "Total Expense: RM %.2f", totalExpense));
        savingTextView.setText(String.format(Locale.getDefault(), "Total Saving: RM %.2f", totalSaving));
    }
    public void toggleDarkMode(View view) {
        // Toggle dark mode state
        isDarkModeEnabled = !isDarkModeEnabled;

        // Apply the appropriate theme
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Recreate the activity to apply the new theme
        recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(SavingMainActivity.this, InputInformationActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
