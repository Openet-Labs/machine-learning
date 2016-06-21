package com.openet.labs.machineLearning.autoScale;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;


import scala.Tuple2;

public class DecisionTreeRegression implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DecisionTreeRegression.class);

    public DecisionTreeModel trainModel(JavaRDD<LabeledPoint> inputLabeledPointRDD) {
        // Set parameters.
        // Empty categoricalFeaturesInfo indicates all features are continuous.
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "variance";
        Integer maxDepth = 5; // maximum tree depth
        Integer maxBins = 32;

        // Train a DecisionTree model for regression.		
        DecisionTreeModel model = DecisionTree.trainRegressor(inputLabeledPointRDD, categoricalFeaturesInfo, impurity, maxDepth, maxBins);
        
        return model;
    }

    public JavaPairRDD<Double, Double> predictModel(DecisionTreeModel model, JavaRDD<double[]> data) {
        JavaPairRDD<Double, Double> predictionAndLabel = data.mapToPair(new PairFunction<double[], Double, Double>() {

            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<Double, Double> call(double[] p) {
                //First data in index is id
                Vector v = Vectors.dense(Arrays.copyOfRange(p, 1, p.length));
                return new Tuple2<>(p[0], model.predict(v));

            }
        });

        return predictionAndLabel;
    }

}
