package com.spb.kbv.messageapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.services.Contacts;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.spb.kbv.messageapp.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class SelectContactActivity extends BaseAuthenticatedActivity implements AdapterView.OnItemClickListener {
    public static final String RESULT_CONTACT = "RESULT_CONTACT";

    private static final int REQUEST_ADD_CONTACT = 1;

    private UserDetailsAdapter adapter;

    @Override
    protected void onMessageAppCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_contact);
        getSupportActionBar().setTitle("Select Contact");

        adapter = new UserDetailsAdapter(this);
        ListView listView = (ListView) findViewById(R.id.activity_select_contact_listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bus.post(new Contacts.GetContactRequest(true));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectUser(adapter.getItem(position));
    }

    @Subscribe
    public void onContactReceived(final Contacts.GetContactResponse response){
        scheduler.invokeOnResume(Contacts.GetContactRequest.class, new Runnable() {
            @Override
            public void run() {
                response.showErrorToast(SelectContactActivity.this);
                adapter.clear();
                adapter.addAll(response.contacts);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_contact,  menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.activity_select_contact_menuAdd){
            startActivityForResult(new Intent(this, AddContactActivity.class), REQUEST_ADD_CONTACT);
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_CONTACT && resultCode == RESULT_OK){
            UserDetails user = data.getParcelableExtra(AddContactActivity.RESULT_CONTACT);
            selectUser(user);
        }
    }

    private void selectUser(UserDetails user) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CONTACT, user);
        setResult(RESULT_OK, intent);
        finish();
    }
}
