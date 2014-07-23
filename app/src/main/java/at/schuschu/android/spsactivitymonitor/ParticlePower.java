package at.schuschu.android.spsactivitymonitor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Vector;

import at.schuschu.android.spsactivitymonitor.R;

public class ParticlePower extends Activity implements ActivityInterface {

    private int[][] iti_map;
    Vector<Particle> particles;
    Vector<MoveData> move_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        iti_map = createITIMap();
        particles = new Vector<Particle>();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle_power);
        createParticles();
        getITIBitmap();

    }

    private int[][] createITIMap() {
        int[][] ret_val = new int[148][46];
        for (int i = 18; i < 46; i++) {
            for (int j = 0; j < 33; j++) {
                ret_val[j][i] = 1;
            }
        }

        for (int i = 0; i < 13; i++) {
            for (int j = 25; j < 38; j++) {
                ret_val[j][i] = 1;
            }
        }

        for (int i = 0; i < 26; i++) {
            for (int j = 147 - 89; j < 148; j++) {
                ret_val[j][i] = 1;
            }
        }

        for (int i = 31; i < 46; i++) {
            for (int j = 33; j < 10; j++) {
                ret_val[j][i] = 1;
            }
        }

        for (int i = 31; i < 46; i++) {
            for (int j = 51; j < 140; j++) {
                ret_val[j][i] = 1;
            }
        }

        for (int i = 13; i < 27; i++) {
            for (int j = 38; j < 59; j++) {
                ret_val[j][i] = 1;
            }
        }

        return ret_val;
    }

    public void getITIBitmap() {
        Bitmap dest = Bitmap.createBitmap(148, 46, Bitmap.Config.RGB_565);
        for (int i = 0; i < 148; i++) {
            for (int j = 0; j < 46; j++) {
                if (iti_map[i][j] == 1) {
                    dest.setPixel(i, j, Color.BLACK);
                } else {
                    dest.setPixel(i, j, Color.WHITE);
                }
            }
        }
        ImageView iti_map_view = (ImageView) findViewById(R.id.itiMap);
        iti_map_view.setImageBitmap(dest);
    }

    private void createParticles() {
        for (int i = 0; i < iti_map.length; i++) {
            for (int j = 0; j < iti_map[i].length; j++) {
                if (iti_map[i][j] == 0) {
                    particles.add(new Particle(i, j));
                }
            }
        }
        System.out.println("number of particles " + particles.size());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.particle_power, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateParticles(View view) {
        //Todo
    }

    @Override
    public void onActivityChange(ActivityMonitoring.ACTIVITY activity) {

    }

    @Override
    public void onFrequencyChange(int[] frequency) {

    }
}
