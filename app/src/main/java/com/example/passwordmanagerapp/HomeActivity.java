package com.example.passwordmanagerapp;



import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements AccountsAdapter.ContactsAdapterListener {
    private RecyclerView recyclerView;
    private List<Account> accountList= new ArrayList<>();
    private AccountsAdapter mAdapter;
    private SearchView searchView;
    private FloatingActionButton floatingButton;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    //getting user id from login class
//    private String userID;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dl = (DrawerLayout) findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);

        //get firebase auth instance
        mAuth=FirebaseAuth.getInstance();

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.changePassword:
                        Intent intent=new Intent(HomeActivity.this,NewPasswordActivity.class);
                        startActivity(intent);
//                        Toast.makeText(getApplicationContext(), "Change Password", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.logout:
                        logOut();
//                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });



//        userID=getIntent().getStringExtra("user id");

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        recyclerView = findViewById(R.id.recycler_view);
        floatingButton=findViewById(R.id.floating);





//        System.out.println("my uid is:"+mAuth.getCurrentUser().getUid());

        //get all accounts from realtime database
        getAllAccounts();







        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                showNoteDialog(true,-1);
//                Account account=new Account("Gmail","123","");
//                accountList.add(account);
//                mAdapter.notifyDataSetChanged();
                showDialogue();

            }
        });

        mAdapter = new AccountsAdapter(getApplicationContext(),accountList,this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//         Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        searchView.setMaxWidth(Integer.MAX_VALUE);

//         listening to search query text change
       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search || t.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.BLUE);
        }
    }

    @Override
    public void onContactSelected(Account account) {
        selectOptionDialogue(account);
    }

void showDialogue() {
    // get alert_dialog.xml view
    LayoutInflater li = LayoutInflater.from(HomeActivity.this);
    View promptsView = li.inflate(R.layout.activity_custom_dialog, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            HomeActivity.this);

    // set alert_dialog.xml to alertdialog builder
    alertDialogBuilder.setView(promptsView);

    final EditText accountTitle= (EditText) promptsView.findViewById(R.id.accountitle);
    final EditText accountPassword = (EditText) promptsView.findViewById(R.id.accountpassword);

    //change password symbol dot to asterisk
    accountPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

    // set dialog message
    alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // get user input and set it to result
                    // edit text
                    String title=accountTitle.getText().toString();
                    String password=accountPassword.getText().toString();

                    if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(password)){



                        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                        DatabaseReference mref=firebaseDatabase.getReference("users");
                        String ID= mref.child(mAuth.getCurrentUser().getUid()).push().getKey();

                        //create random color for image
                        ColorGenerator generator = ColorGenerator.MATERIAL;
                        int imageColor=generator.getRandomColor();

                        Account account=new Account(ID,title,password,imageColor);
//
                        mref.child(mAuth.getCurrentUser().getUid()).child("account").child(ID).setValue(account);
//                        Account account=new Account("",title,password,"");

                        accountList.add(account);
                        mAdapter.notifyDataSetChanged();

                    }
                    else if(TextUtils.isEmpty(title)){
                        Toast.makeText(HomeActivity.this,"Please Enter Title of Account",Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(password)){
                        Toast.makeText(HomeActivity.this,"Please Enter Password of Account",Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show it
    alertDialog.show();
 }
    private void selectOptionDialogue(Account account){
        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("What you want to do?");
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateAccount(account);
                dialog.dismiss();

            }
        }).setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeAccount(account);
                dialog.dismiss();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();

    }

    private void updateAccount(Account account){
        LayoutInflater li = LayoutInflater.from(HomeActivity.this);
        View promptsView = li.inflate(R.layout.activity_custom_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HomeActivity.this);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText accountTitle= (EditText) promptsView.findViewById(R.id.accountitle);
        final EditText accountPassword = (EditText) promptsView.findViewById(R.id.accountpassword);
        accountTitle.setText(account.getName());
        accountPassword.setText(account.getPassword());
        accountPassword.setInputType(InputType.TYPE_CLASS_TEXT);



        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        String title=accountTitle.getText().toString();
                        String password=accountPassword.getText().toString();


                        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(password)){


                            FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                            DatabaseReference mref=firebaseDatabase.getReference("users");

                            //create random color for image
                            ColorGenerator generator = ColorGenerator.MATERIAL;
                            int imageColor=generator.getRandomColor();

                            Account acc=new Account(account.getId(),title,password,imageColor);

                            mref.child(mAuth.getCurrentUser().getUid()).child("account").child(account.getId()).setValue(acc);
                            account.setName(acc.getName());
                            account.setPassword(acc.accountPassword);

                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),"Account Updated Successfully",Toast.LENGTH_SHORT).show();


                        }
                        else if(TextUtils.isEmpty(title)){
                            Toast.makeText(getApplicationContext(),"Please Enter Title of Account",Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(password)){
                            Toast.makeText(getApplicationContext(),"Please Enter Password of Account",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
    private void removeAccount(Account account){
        LayoutInflater li = LayoutInflater.from(HomeActivity.this);
        View promptsView = li.inflate(R.layout.remove_account_custom_dialogue, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HomeActivity.this);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView accountTitle= (TextView) promptsView.findViewById(R.id.accountitle);
        final TextView accountPassword = (TextView) promptsView.findViewById(R.id.accountpassword);
        accountTitle.setText(account.getName());
        accountPassword.setText(account.getPassword());
        accountPassword.setInputType(InputType.TYPE_CLASS_TEXT);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                        DatabaseReference mref=firebaseDatabase.getReference("users");
                        mref.child(mAuth.getCurrentUser().getUid()).child("account").child(account.getId()).removeValue();

                        removeAccountFromList(account);

                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),"Account removed successfully",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void removeAccountFromList(Account account){
        for(Account acc:accountList){
            if(acc.getId().equals(account.getId())){
                accountList.remove(account);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    //get all accounts from realtime database
    private void getAllAccounts(){
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference mref=firebaseDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).child("account");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Account account = data.getValue(Account.class);
//                    System.out.println(account.getName());
                    accountList.add(account);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //logout user
    public void logOut(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            mAuth.signOut();
            Toast.makeText(this, user.getEmail()+ " Sign out!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "You aren't login Yet!", Toast.LENGTH_SHORT).show();
        }
    }
}
