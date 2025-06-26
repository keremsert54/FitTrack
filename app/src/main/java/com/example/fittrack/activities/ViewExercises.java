package com.example.fittrack.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fittrack.R;
import com.example.fittrack.utils.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;

public class ViewExercises extends AppCompatActivity {
    private SharedPrefsManager prefsManager;
    private ArrayList<String> exercises;
    private ArrayAdapter<String> adapter;
    private ListView exerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefsManager = new SharedPrefsManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercises);
        setTitle(getString(R.string.view_exercises));

        exerciseList = findViewById(R.id.exercise_list);
        Button backButton = findViewById(R.id.back_button);

        exercises = new ArrayList<>();
        exercises.add("Walking");
        exercises.add("Yoga");
        exercises.add("Swimming");
        exercises.add("Running");
        exercises.add("Cycling");
        List<String> customExercises = prefsManager.getCustomExercises();
        if (customExercises != null) {
            exercises.addAll(customExercises);
        }

        // Özel adapter ile çarpı butonu ekle
        adapter = new ArrayAdapter<String>(this, R.layout.exercise_list_item, R.id.exercise_text, exercises) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(R.id.exercise_text);
                Button removeButton = view.findViewById(R.id.remove_button);
                String exercise = exercises.get(position);

                removeButton.setOnClickListener(v -> {
                    exercises.remove(position);
                    List<String> updatedCustomExercises = new ArrayList<>(exercises);
                    updatedCustomExercises.removeAll(List.of("Walking", "Yoga", "Swimming", "Running", "Cycling"));
                    prefsManager.saveCustomExercises(updatedCustomExercises);
                    notifyDataSetChanged();
                    // ExerciseActivity'deki spinnere yansıtmak için bir güncelleme tetikle
                    setResult(RESULT_OK);
                });

                return view;
            }
        };
        exerciseList.setAdapter(adapter);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        } else {
            android.util.Log.e("ViewExercises", "back_button is null. Check activity_view_exercises.xml");
        }
    }
}