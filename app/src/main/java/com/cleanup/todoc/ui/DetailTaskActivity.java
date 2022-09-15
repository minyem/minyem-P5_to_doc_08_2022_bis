package com.cleanup.todoc.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.cleanup.todoc.R;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.repository.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class DetailTaskActivity extends FragmentActivity {

    TaskRepository taskRepoy;
    TasksAdapter adap;
    Task ts;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        Intent bundle = getIntent();
        final int id_task = (int) bundle.getLongExtra("id_task", 0);

        final TaskRepository taskRepository = new TaskRepository(this);

        final EditText name_task = findViewById(R.id.inom_tache);
        final EditText date_task = findViewById(R.id.idate_creationtache);
        final EditText heure_task = findViewById(R.id.iheure_creation);
        final TextView name_projet = findViewById(R.id.inom_projet);

        Button modif = findViewById(R.id.modif);


        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ts = taskRepository.getTaskById(id_task);

                final String nameTache = ts.getName();
                final String nameProjet =ts.getProject().getName();
                final Long dateR = ts.getCreationTimestamp();

                runOnUiThread(new Runnable() {
                    public void run() {


                        name_task.setText(nameTache);

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(new Date(dateR));
                        SimpleDateFormat formatter1 = new SimpleDateFormat("h:mm a");
                        String hourString = formatter1.format(new Date(dateR));

                        date_task.setText(dateString);
                        heure_task.setText(hourString);

                        name_projet.setText(nameProjet);


                    }
                });

            }
        });

        modif.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


               ts.setName((name_task.getText().toString()));
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {

                        taskRepository.updateTask(ts);

                    }
                });

                Intent intent = new Intent();
                intent.putExtra("key", 1);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }


}