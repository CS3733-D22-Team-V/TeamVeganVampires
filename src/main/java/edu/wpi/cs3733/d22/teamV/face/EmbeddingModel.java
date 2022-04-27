package edu.wpi.cs3733.d22.teamV.face;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class EmbeddingModel {

  private static EmbeddingModel model = null;
  private static final String modelURL = "https://nihilistkitten.me/traced_facenet.pt";

  private Predictor<Image, double[]> predictor;

  private HashMap<String, ArrayList<Double>> embeddings;

  public static EmbeddingModel getModel() {
    if (model == null) {
      model = new EmbeddingModel();
    }
    return model;
  }

  private EmbeddingModel() {
    try {
      this.initialize();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  private void initialize() throws SQLException {

    System.setProperty("ai.djl.repository.zoo.location", "facenet");
    this.buildModel();
  }

  private ZooModel buildModel() {

    Criteria<Image, double[]> criteria =
        Criteria.builder()
            .setTypes(ai.djl.modality.cv.Image.class, double[].class)
            .optTranslator(new customTranslator())
            .optArtifactId("ai.djl.localmodelzoo:facenet")
            .optProgress(new ai.djl.training.util.ProgressBar())
            .build();

    ZooModel model = null;
    try {
      model = ModelZoo.loadModel(criteria);
    } catch (IOException | MalformedModelException | ModelNotFoundException e) {
      e.printStackTrace();
    }
    ZooModel zooModel = model;
    assert model != null;
    predictor = model.newPredictor();
    return zooModel;
  }

  public double[] embedding(Image imgIn) {
    try {
      return predictor.predict(imgIn);
    } catch (TranslateException e) {
      e.printStackTrace();
      return null;
    }
  }

  public double cosineDistance(double[] a, double[] b) {
    double dotproduct = 0;
    double amag = 0;
    double bmag = 0;
    for (int i = 0; i < a.length; i++) {
      dotproduct += a[i] * b[i];
      amag += a[i] * a[i];
      bmag += b[i] * b[i];
    }
    amag = Math.sqrt(amag);
    bmag = Math.sqrt(bmag);
    return (dotproduct) / (amag * bmag);
  }

  public double euclideanDistance(double[] a, double[] b) {
    double acc = 0;
    for (int i = 0; i < a.length; i++) {
      acc += Math.pow(b[i] - a[i], 2);
    }
    return Math.sqrt(acc);
  }

  public String userFromEmbedding(double[] a) {
    return userFromEmbedding(a, 0.65);
  }

  public String userFromEmbedding(double[] a, double threshold) {
    for (String key : this.embeddings.keySet()) {
      double[] storedEmbedding = this.embeddings.get(key).stream().mapToDouble(d -> d).toArray();
      double cosineDistance = cosineDistance(a, storedEmbedding);
      if (cosineDistance > threshold) {
        return key;
      }
    }

    return null;
  }

  // based on https://github.com/jmformenti/face-recognition-java with permission
  static class customTranslator implements Translator<Image, double[]> {
    public customTranslator() {}

    @Override
    public double[] processOutput(TranslatorContext ctx, NDList list) {
      if (list != null && !list.isEmpty()) {
        float[] floatArray = list.get(0).toFloatArray();
        return IntStream.range(0, floatArray.length).mapToDouble(i -> floatArray[i]).toArray();
      } else {
        return null;
      }
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
      NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
      Resize resize = new Resize(160, 160);
      array = resize.transform(array);
      array = array.sub(0.498).div(0.5);
      array = array.expandDims(0);
      array = array.getNDArrayInternal().toTensor();
      return new NDList(array);
    }

    @Override
    public Batchifier getBatchifier() {
      return null;
    }
  }
}
