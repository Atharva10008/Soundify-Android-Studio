package com.example.soundify.ml;

import android.content.Context;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.CastOp;
import org.tensorflow.lite.support.common.ops.DequantizeOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.common.ops.QuantizeOp;
import org.tensorflow.lite.support.image.BoundingBoxUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.label.LabelUtil;
import org.tensorflow.lite.support.metadata.MetadataExtractor;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

/**
 * Identify which of a known set of objects might be present and provide information about their positions within the given image or a video stream. */
public final class SsdMobilenetV11Metadata1 {
  @NonNull
  private final ImageProcessor imageProcessor;

  private int imageHeight;

  private int imageWidth;

  @NonNull
  private final TensorProcessor locationsPostProcessor;

  @NonNull
  private final List<String> labelmap;

  @NonNull
  private final TensorProcessor classesPostProcessor;

  @NonNull
  private final TensorProcessor scoresPostProcessor;

  @NonNull
  private final TensorProcessor numberOfDetectionsPostProcessor;

  @NonNull
  private final Model model;

  private SsdMobilenetV11Metadata1(@NonNull Context context, @NonNull Model.Options options) throws
      IOException {
    model = Model.createModel(context, "ssd_mobilenet_v1_1_metadata_1.tflite", options);
    MetadataExtractor extractor = new MetadataExtractor(model.getData());
    ImageProcessor.Builder imageProcessorBuilder = new ImageProcessor.Builder()
      .add(new ResizeOp(300, 300, ResizeMethod.NEAREST_NEIGHBOR))
      .add(new NormalizeOp(new float[] {127.5f}, new float[] {127.5f}))
      .add(new QuantizeOp(128f, 0.0078125f))
      .add(new CastOp(DataType.UINT8));
    imageProcessor = imageProcessorBuilder.build();
    TensorProcessor.Builder locationsPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    locationsPostProcessor = locationsPostProcessorBuilder.build();
    TensorProcessor.Builder classesPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    classesPostProcessor = classesPostProcessorBuilder.build();
    labelmap = FileUtil.loadLabels(extractor.getAssociatedFile("labelmap.txt"));
    TensorProcessor.Builder scoresPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    scoresPostProcessor = scoresPostProcessorBuilder.build();
    TensorProcessor.Builder numberOfDetectionsPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    numberOfDetectionsPostProcessor = numberOfDetectionsPostProcessorBuilder.build();
  }

  @NonNull
  public static SsdMobilenetV11Metadata1 newInstance(@NonNull Context context) throws IOException {
    return new SsdMobilenetV11Metadata1(context, (new Model.Options.Builder()).build());
  }

  @NonNull
  public static SsdMobilenetV11Metadata1 newInstance(@NonNull Context context,
      @NonNull Model.Options options) throws IOException {
    return new SsdMobilenetV11Metadata1(context, options);
  }

  @NonNull
  public Outputs process(@NonNull TensorImage image) {
    imageHeight = image.getHeight();
    imageWidth = image.getWidth();
    TensorImage processedimage = imageProcessor.process(image);
    Outputs outputs = new Outputs(model);
    model.run(new Object[] {processedimage.getBuffer()}, outputs.getBuffer());
    return outputs;
  }

  public void close() {
    model.close();
  }

  @NonNull
  public Outputs process(@NonNull TensorBuffer image) {
    TensorBuffer processedimage = image;
    Outputs outputs = new Outputs(model);
    model.run(new Object[] {processedimage.getBuffer()}, outputs.getBuffer());
    return outputs;
  }

  public class Outputs {
    private TensorBuffer locations;

    private TensorBuffer classes;

    private TensorBuffer scores;

    private TensorBuffer numberOfDetections;

    private Outputs(Model model) {
      this.locations = TensorBuffer.createFixedSize(model.getOutputTensorShape(0), DataType.FLOAT32);
      this.classes = TensorBuffer.createFixedSize(model.getOutputTensorShape(1), DataType.FLOAT32);
      this.scores = TensorBuffer.createFixedSize(model.getOutputTensorShape(2), DataType.FLOAT32);
      this.numberOfDetections = TensorBuffer.createFixedSize(model.getOutputTensorShape(3), DataType.FLOAT32);
    }

    @NonNull
    private List<RectF> getLocationsAsRectFList() {
      List<RectF> originalBoxes = BoundingBoxUtil.convert(locations, new int[] {1,0,3,2}, 2, BoundingBoxUtil.Type.BOUNDARIES, BoundingBoxUtil.CoordinateType.RATIO, 300, 300);
      List<RectF> processedBoxes = new ArrayList<>();
      for (android.graphics.RectF box : originalBoxes) {
        processedBoxes.add(imageProcessor.inverseTransform(box, imageHeight, imageWidth));
      }
      return processedBoxes;
    }

    @NonNull
    public TensorBuffer getLocationsAsTensorBuffer() {
      return locationsPostProcessor.process(locations);
    }

    @NonNull
    private List<String> getClassesAsStringList() {
      return LabelUtil.mapValueToLabels(classes, labelmap, 0);
    }

    @NonNull
    public TensorBuffer getClassesAsTensorBuffer() {
      return classesPostProcessor.process(classes);
    }

    @NonNull
    public TensorBuffer getScoresAsTensorBuffer() {
      return scoresPostProcessor.process(scores);
    }

    @NonNull
    public TensorBuffer getNumberOfDetectionsAsTensorBuffer() {
      return numberOfDetectionsPostProcessor.process(numberOfDetections);
    }

    @NonNull
    private Map<Integer, Object> getBuffer() {
      Map<Integer, Object> outputs = new HashMap<>();
      outputs.put(0, locations.getBuffer());
      outputs.put(1, classes.getBuffer());
      outputs.put(2, scores.getBuffer());
      outputs.put(3, numberOfDetections.getBuffer());
      return outputs;
    }
  }
}
