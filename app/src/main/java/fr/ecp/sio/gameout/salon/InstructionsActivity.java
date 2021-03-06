package fr.ecp.sio.gameout.salon;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;

import fr.ecp.sio.gameout.MainActivity;
import fr.ecp.sio.gameout.R;
import fr.ecp.sio.gameout.utils.GameoutUtils;

public class InstructionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        TextView creditsTxtView = (TextView) findViewById(R.id.instructions_content);
        InputStream inputStream = getResources().openRawResource(R.raw.instructions);
        creditsTxtView.setText(Html.fromHtml(GameoutUtils.readTxt(inputStream)));
        creditsTxtView.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(InstructionsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
