package at.schuschu.android.spsactivitymonitor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import at.schuschu.android.spsactivitymonitor.R;

public class ParticlePower extends Activity {

    private int[][] iti_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        iti_map = createITIMap();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle_power);
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
}
