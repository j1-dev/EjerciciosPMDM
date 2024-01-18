package com.example.restapiapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
  private DrawerLayout drawerLayout;
  private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
    @Override
    public void handleOnBackPressed() {
      atras();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    //NavigationDrawer
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(findViewById(R.id.toolbar));

    drawerLayout = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();
    NavigationView navigationView = findViewById(R.id.navigation_view);
    navigationView.setNavigationItemSelectedListener(this);

    MenuItem menuItem = navigationView.getMenu().getItem(0);
    onNavigationItemSelected(menuItem);
    menuItem.setChecked(true);
  }

  private void atras(){
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      finish();
    }
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int titleId = getTitulo(item);
    System.out.println("AAAAAAAAAAAa");
    mostrarFragmento(titleId);
    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }

  private int getTitulo(@NonNull MenuItem menuItem) {
    int omenu=menuItem.getItemId();
    if(omenu==R.id.nav_ubicacion) {
      return 1;
    }
    else{
      return 2;
    }
  }

  private void mostrarFragmento(int fragmento) {
    Fragment fragment;
    String titulo;
    if(fragmento==1){
      fragment = MainFragment.newInstance("","");
      titulo="Personas";
    }
    else{
      fragment = PersonaFragment.newInstance(1,2);
      titulo="Tienda";
    }
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.home_content, fragment)
        .commit();
    setTitle(titulo);
  }
}
