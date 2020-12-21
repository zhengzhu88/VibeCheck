package zhengzhu.vibecheck;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class FirstFragment extends Fragment {

    private static final int MAX_VIBRATION_DURATION_MS = 2000;
    private static final String DURATION_ERROR_MESSAGE = String.format(Locale.ENGLISH,"Duration must be an integer between 1 and %d.", MAX_VIBRATION_DURATION_MS);
    private static final String AMPLITUDE_ERROR_MESSAGE = "Amplitude must be an integer between 0 and 255 or left empty.";

    Vibrator vibrator;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.vibrate_button).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View buttonView) {
                boolean hasAmplitudeControls = vibrator.hasAmplitudeControl();

                EditText durationText = (EditText) view.findViewById(R.id.duration_ms);
                Integer durationMs;
                try {
                    durationMs = Integer.parseInt(durationText.getText().toString());
                } catch (NumberFormatException e) {
                    showDebugToast(DURATION_ERROR_MESSAGE);
                    return;
                }

                EditText amplitudeText = (EditText) view.findViewById(R.id.amplitude);
                boolean amplitudeIsEmpty = amplitudeText.getText().toString().isEmpty();
                Integer amplitude = -1;
                try {
                    amplitude = Integer.parseInt(amplitudeText.getText().toString());
                } catch (NumberFormatException e) {
                    if (!amplitudeIsEmpty) {
                        showDebugToast(AMPLITUDE_ERROR_MESSAGE);
                        return;
                    }
                }

                if (hasAmplitudeControls && (amplitude < -1 || amplitude > 255)) {
                    showDebugToast(AMPLITUDE_ERROR_MESSAGE);
                    return;
                }

                if (durationMs < 1 || durationMs > MAX_VIBRATION_DURATION_MS) {
                    showDebugToast(DURATION_ERROR_MESSAGE);
                    return;
                }

                String vibeMessage;
                if (amplitudeIsEmpty) {
                    vibeMessage = String.format(Locale.ENGLISH, "Amplitude is empty. Vibrating at default amplitude for %d ms.", durationMs);
                } else if (!hasAmplitudeControls) {
                    vibeMessage = String.format(Locale.ENGLISH, "This device doesn't support amplitude controls. Vibrating at default amplitude for %d ms.", durationMs);
                } else {
                    vibeMessage = String.format(Locale.ENGLISH, "Vibrating at amplitude %d for %d ms.", amplitude, durationMs);
                }

                Log.i("VibeCheck", vibeMessage);
                showDebugToast(vibeMessage);

                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude));
            }
        });
    }

    private void showDebugToast(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}