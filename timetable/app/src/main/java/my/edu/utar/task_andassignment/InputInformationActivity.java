package my.edu.utar.task_andassignment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InputInformationActivity extends AppCompatActivity {

    private EditText dateEditText;
    private Spinner typeSpinner;
    private EditText amountEditText;
    private Button submitButton;
    private Toolbar toolbar;

    private Calendar selectedDate;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "Record";
    private static final String KEY_DATE = "date";
    private static final String KEY_TYPE = "type";
    private static final String KEY_AMOUNT = "amount";
    static final String KEY_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_information);

        dateEditText = findViewById(R.id.dateEditText);
        typeSpinner = findViewById(R.id.typeSpinner);
        amountEditText = findViewById(R.id.amountEditText);
        submitButton = findViewById(R.id.submitButton);
        toolbar = findViewById(R.id.toolbar2);

        // Initialize selectedDate with current date
        selectedDate = Calendar.getInstance();

        // Populate type spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // Display current date in dateEditText
        updateDateEditText();

        // Date picker dialog listener
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });

        Button backButton = findViewById(R.id.backButton);

        // Set click listener for the button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateEditText();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateEditText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateEditText.setText(sdf.format(selectedDate.getTime()));
    }

    private void saveExpense() {
        String date = dateEditText.getText().toString();
        String type = typeSpinner.getSelectedItem().toString();
        String amountString = amountEditText.getText().toString();

        if (date.isEmpty() || amountString.isEmpty()) {
            Toast.makeText(this, "Please enter date and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        float amount = Float.parseFloat(amountString);

        // Save expense details to SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (type.equals("Expense")) {
            editor.putFloat(KEY_AMOUNT + "_Expense_" + date, amount);
        } else if (type.equals("Saving")) {
            editor.putFloat(KEY_AMOUNT + "_Saving_" + date, amount);
        }

        // Save description to SharedPreferences
        String description = ""; // You need to get the description from somewhere
        editor.putString(KEY_DESCRIPTION + "_" + date, description);

        editor.apply(); // Apply changes
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

        // Clear input fields after saving
        dateEditText.setText("");
        amountEditText.setText("");
    }

}
