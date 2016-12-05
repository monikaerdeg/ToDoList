package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.constant.ResultConstants;
import eu.execom.todolistgrouptwo.model.Task;

@EActivity(R.layout.activity_task_detail)
public class TaskDetailActivity extends AppCompatActivity {

    long taskId;

    @ViewById
    EditText detailTitle;

    @ViewById
    EditText detailDescription;

    @ViewById
    ImageButton editTitle;

    @ViewById
    ImageButton editDescription;

    @ViewById
    CheckedTextView done;

    @ViewById
    Button saveButton;

    @ViewById
    Button cancleButton;

    @ViewById
    Button deleteButton;

    @RestService
    RestApi restApi;

    @Extra("task")
    String taskJSON;

    @AfterViews
    void loadTaskInfo(){
        final Gson gson = new Gson();
        final Task task = gson.fromJson(taskJSON, Task.class);

        detailTitle.setText(task.getTitle());
        detailTitle.setTextColor(getResources().getColor(R.color.darkGrey));
        detailDescription.setText(task.getDescription());
        detailDescription.setTextColor(getResources().getColor(R.color.darkGrey));
        done.setTextColor(getResources().getColor(R.color.darkGrey));

        taskId = task.getId();

        if(task.isFinished()){
            done.setChecked(true);
        }else{
            done.setChecked(false);
        }
    }

    @Click
    void done(){
        done.toggle();
    }

    @Click
    void editTitle(){
        detailTitle.setFocusable(true);
        detailTitle.setEnabled(true);
        detailTitle.setCursorVisible(true);
        detailTitle.setFocusableInTouchMode(true);
        detailTitle.setFocusable(true);
        detailTitle.setTextColor(getResources().getColor(R.color.black));
        detailTitle.callOnClick();
    }

    @Click
    void editDescription(){
        detailDescription.setFocusable(true);
        detailDescription.setEnabled(true);
        detailDescription.setCursorVisible(true);
        detailDescription.setFocusableInTouchMode(true);
        detailDescription.setTextColor(getResources().getColor(R.color.black));
        detailDescription.callOnClick();
    }

    @Click
    void cancleButton(){
        final Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Click
    void deleteButton(){
        final Intent intent = new Intent();
        final Gson gson = new Gson();

        final Task editedTask = new Task(detailTitle.getText().toString(),
                detailDescription.getText().toString(), done.isChecked());

        editedTask.setId(taskId);

        intent.putExtra("task", gson.toJson(editedTask));

        setResult(ResultConstants.RESULT_DELETE, intent);
        finish();
    }

    @Click
    void saveButton(){
        final Intent intent = new Intent();
        final Gson gson = new Gson();

        final Task editedTask = new Task(detailTitle.getText().toString(),
                detailDescription.getText().toString(), done.isChecked());

        editedTask.setId(taskId);

        intent.putExtra("task", gson.toJson(editedTask));
        setResult(ResultConstants.RESULT_UPDATE, intent);
        finish();
    }




}
