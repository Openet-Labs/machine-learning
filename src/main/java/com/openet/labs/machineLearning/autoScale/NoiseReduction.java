package com.openet.labs.machineLearning.autoScale;

import java.util.Arrays;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.log4j.Logger;

public class NoiseReduction {
    private static final Logger LOGGER = Logger.getLogger(NoiseReduction.class);
    
    public static enum NormalizeType {
        //STANDARD, The formula is $ y_n = \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k $
        //UNITARY,  The formula is $y_n = (1/\sqrt{N}) \Sigma_{k=0}^{N-1} e^{-2 \pi i nk/N} x_k$
        Standard, Unitary
    }
    
    public static enum FilterType {
        ReduceNoiseHard, ReduceNoiseSoft, ReduceNoiseDynamic, ReduceNoiseTomsWay
    }

    public double[] normalize(double[] signalInput, NormalizeType normalizeType, FilterType filterType) {
        // Select FFT normalization method
        DftNormalization normalization;
        if(normalizeType.equals(NormalizeType.Standard)) {
            normalization = DftNormalization.STANDARD;
        } else {
            normalization = DftNormalization.UNITARY;
        }
        
        //Perform Fast Fourier Transform
        FastFourierTransformer transformer = new FastFourierTransformer(normalization);
        Complex fftInput[] = transformer.transform(padToPowerofTwo(signalInput), TransformType.FORWARD);

        // Store Real Number in array
        double[] realNumberInput = convertToRealNumber(fftInput);
        double threshold = 1.1 * StatUtils.mean(realNumberInput);
        double[] processedOutput = null;

        switch (filterType) {
        case ReduceNoiseHard:
            processedOutput = reduceNoiseHardT(realNumberInput, threshold);
            break;
        case ReduceNoiseSoft:
            processedOutput = reduceNoiseSoftT(realNumberInput, threshold);
            break;
        case ReduceNoiseDynamic:
            processedOutput = reduceNoiseDynamicT(realNumberInput);
            break;
        case ReduceNoiseTomsWay:
            processedOutput = reduceNoiseTomsWay(realNumberInput);
            break;
        default:
            processedOutput = reduceNoiseSoftT(realNumberInput, threshold);
            break;
        }

        // Return finalOutput in realNumber
        Complex fftOutput[] = transformer.transform(processedOutput, TransformType.INVERSE);
        return convertToRealNumber(fftOutput);
    }
    
    private double[] padToPowerofTwo(double[] input) {
        return Arrays.copyOf(input, nextPowerOf2(input.length));        
    }
    
    public static int nextPowerOf2(final int a)
    {
        int b = 1;
        while (b < a)
        {
            b = b << 1;
        }
        return b;
    }
    
    private double[] convertToRealNumber(Complex[] complexInput) {
        double[] realNumberOutput = new double[complexInput.length];
        for (int i = 0; i < complexInput.length; i++) {
            realNumberOutput[i] = complexInput[i].getReal();
        }

        return realNumberOutput;
    }

    private double[] reduceNoiseHardT(double signal[], double threshold) {
        int len = signal.length;
        int count = 0;
        double[] sig2 = Arrays.copyOf(signal, len);

        for (int i = 0; i < len; i++) {
            if (Math.abs(sig2[i]) < threshold) {
                sig2[i] = 0;
                count++;
            }
        }
        LOGGER.info("reduceNoiseHardT::" + count + " signals set to 0");
        return sig2;
    }

    private double[] reduceNoiseSoftT(double signal[], double threshold) {
        int len = signal.length;
        double[] sig2 = Arrays.copyOf(signal, len);

        for (int i = 0; i < len; i++) {
            if (Math.abs(sig2[i]) < threshold) {
                sig2[i] = 0;
            } else {
                if (sig2[i] > 0) {
                    sig2[i] -= threshold;
                } else {
                    sig2[i] += threshold;
                }
            }
        }
        return sig2;
    }

    private double[] reduceNoiseDynamicT(double signal[]) {
        int n = 4;
        if (signal.length < 8) {
            n = 2;
        }

        int significant = (int) (Math.pow(2, n - 1) - 1);
        
        double[] tempSignal = new double[signal.length - significant];
        System.arraycopy(signal, significant, tempSignal, 0, tempSignal.length);
        double[] sorted = absoluteBubbleSort(tempSignal);
        double median = Math.abs(sorted[(int) ((sorted.length) / 2.0)]);
        double delta = median / 0.6745;

        double threshold = delta * Math.sqrt(Math.log(signal.length));

        LOGGER.info("reduceNoiseDynamicT Dynamic Threshold = " + threshold);
        double[] sig2 = reduceNoiseSoftT(tempSignal, threshold);
        System.arraycopy(sig2, 0, signal, significant, sig2.length);
        return signal;
    }

    private double[] reduceNoiseTomsWay(double signal[]) {
        double[] inputSecondPart = Arrays.copyOfRange(signal, signal.length / 2, signal.length);
        double[] inputFirstPart = Arrays.copyOfRange(signal, 0, (signal.length / 2));
        double[] sorted = absoluteBubbleSort(inputSecondPart);
        double median = Math.abs(sorted[(int) sorted.length / 2]);
        double delta = median / 0.6745;

        double threshold = delta * Math.sqrt(Math.log(inputSecondPart.length));

        LOGGER.info("reduceNoiseTomsWay Dynamic Threshold = " + threshold);
        double[] noiseReducedSecondPart = reduceNoiseHardT(inputSecondPart, threshold);
        double[] output = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            if (i < signal.length / 2) {
                output[i] = inputFirstPart[i];
            } else {
                output[i] = noiseReducedSecondPart[i - signal.length / 2];
            }

        }
        return output;
    }

    private double[] absoluteBubbleSort(double[] signal) {
        double[] input = Arrays.copyOf(signal, signal.length);
        for (int i = 0; i < input.length; i++) {
            input[i] = Math.abs(input[i]);
        }

        for (int i = 0; i < input.length; i++) {
            for (int j = i; j < input.length; j++) {
                if (Math.abs(input[i]) > Math.abs(input[j])) {
                    double help = input[i];
                    input[i] = input[j];
                    input[j] = help;
                }
            }
        }
        return input;
    }
}
