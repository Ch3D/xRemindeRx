import android.test.AndroidTestCase;

import com.ch3d.xreminderx.adapter.RemindersAdapter;

public class ReminderAdapterTest extends AndroidTestCase {
    public void testInstantioation() {
        new RemindersAdapter(getContext(), null, true);
    }
}
