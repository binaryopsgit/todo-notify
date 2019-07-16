package com.sayantanbanerjee.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sayantanbanerjee.todolist.data.ToDoContract;

public class ToDoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TODO_LOADER = 1;
    private Uri mCurrentToDoUri;
    TextView heading;
    TextView message;
    TextView date;
    TextView time;


    public void back(View view) {
        NavUtils.navigateUpFromSameTask(ToDoActivity.this);
    }

    public void edit(View view) {
        Intent intent = new Intent(ToDoActivity.this, EditActivity.class);
        intent.setData(mCurrentToDoUri);
        startActivity(intent);
    }

    private void deleteToDo() {
        int rowsDeleted = getContentResolver().delete(mCurrentToDoUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error with deleting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deletion of To Do Successfully",
                    Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure, you want to delete it?");
        builder.setCancelable(false);
        builder.setTitle("DELETE");
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteToDo();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void delete(View view) {
        showDeleteConfirmationDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        getSupportActionBar().hide();

        heading = (TextView) findViewById(R.id.heading_ToDo);
        message = (TextView) findViewById(R.id.message_ToDo);
        date = (TextView) findViewById(R.id.date_ToDo);
        time = (TextView) findViewById(R.id.time_ToDo);


        Intent intent = getIntent();
        mCurrentToDoUri = intent.getData();

        getSupportLoaderManager().initLoader(TODO_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ToDoContract.ToDoEntry.COLUMN_HEADING,
                ToDoContract.ToDoEntry.COLUMN_MESSAGE,
                ToDoContract.ToDoEntry.COLUMN_DATE,
                ToDoContract.ToDoEntry.COLUMN_TIME};

        return new CursorLoader(this, mCurrentToDoUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int headingColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_HEADING);
            int messageColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_MESSAGE);
            int dateColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_DATE);
            int timeColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_TIME);

            String heading_todo = cursor.getString(headingColumnIndex);
            String message_todo = cursor.getString(messageColumnIndex);
            String date_todo = cursor.getString(dateColumnIndex);
            String time_todo = cursor.getString(timeColumnIndex);

            heading.setText(heading_todo);
            message.setText(message_todo);
            date.setText(date_todo);
            time.setText(time_todo);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}