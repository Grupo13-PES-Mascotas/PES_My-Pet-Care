package org.pesmypetcare.mypetcare.activities.views.chart.statisticdata;

import org.pesmypetcare.httptools.utilities.DateTime;
import org.pesmypetcare.mypetcare.R;
import org.pesmypetcare.mypetcare.features.pets.Pet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Albert Pinto
 */
public class ExerciseFrequencyData extends StatisticData {
    private static final String UNIT = "times";

    public ExerciseFrequencyData(Pet pet) {
        Map<DateTime, Integer> weights = pet.getHealthInfo().getExerciseFrequency();
        List<String> xAxisValues = new ArrayList<>();
        List<Double> yAxisValues = new ArrayList<>();

        for (Map.Entry<DateTime, Integer> entry : weights.entrySet()) {
            xAxisValues.add(getDate(entry.getKey()));
            yAxisValues.add((double) entry.getValue());
        }

        setxAxisValues(xAxisValues);
        setyAxisValues(yAxisValues);
    }

    @Override
    public String getUnit() {
        return UNIT;
    }

    @Override
    public int getMessageIdentifier() {
        return R.string.add_not_implemented;
    }

    @Override
    public boolean getFocusableState() {
        return false;
    }
}
