
package com.ch3d.xreminderx.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.ch3d.xreminderx.R;
import com.ch3d.xreminderx.model.ReminderEntry;
import com.ch3d.xreminderx.model.ReminderFactory;
import com.ch3d.xreminderx.provider.RemindersProvider;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

public class AddReminderActivity extends Activity implements OnClickListener
{
    private void addReminder()
    {
        final EditText editTitle = ((FloatLabeledEditText) findViewById(R.x_add_reminder.edit_title))
                .getEditText();
        final String titleText = editTitle.getText().toString().trim();
        if (titleText.length() > 0)
        {
            final ReminderEntry entry = ReminderFactory.createNull();
            entry.setText(titleText);
            entry.setSilent(true);
            RemindersProvider.addReminder(this, entry, true);
            Toast.makeText(this, R.string.xreminder_added, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.x_add_reminder.btn_ok:
                addReminder();
                break;

            case R.x_add_reminder.btn_cancel:
                finish();

            default:
                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x_add_reminder);
        findViewById(R.x_add_reminder.btn_ok).setOnClickListener(this);
        findViewById(R.x_add_reminder.btn_cancel).setOnClickListener(this);
    }
}
