package com.openet.labs.ml.autoscale;

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
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import scala.Tuple2;

public class RandomForestRegression implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(RandomForestRegression.class);
	
	public RandomForestModel trainModel(JavaRDD<LabeledPoint> inputLabeledPointRDD) {
		// Set parameters.
		// Empty categoricalFeaturesInfo indicates all features are continuous.
		Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
		String impurity = "variance";
		Integer maxDepth = 5; // maximum tree depth
		Integer maxBins = 32;

		// Train a DecisionTree model for classification.
		Integer numTrees = 100; // Use more in practice.
		String featureSubsetStrategy = "auto"; // Let the algorithm choose.

		Integer seed = 12345;

		// Train a RandomForest model for regression.		
		RandomForestModel model = RandomForest.trainRegressor(inputLabeledPointRDD, categoricalFeaturesInfo,
		numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins, seed);

		return model;
	}
	
	public JavaPairRDD<Double, Double> predictModelRdd(RandomForestModel model, JavaRDD<double[]> data) {
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
	
	public Tuple2<Double, Double> predictModel(RandomForestModel model, double[] data) {
	    
	    Vector v = Vectors.dense(Arrays.copyOfRange(data, 1, data.length));
	    Tuple2<Double, Double> predictionAndLabel = new Tuple2<>(data[0], model.predict(v));        

        return predictionAndLabel;
    }

}
