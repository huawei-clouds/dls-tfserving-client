package com.huawei.dls.tfservingclient;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import org.tensorflow.serving.*;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.tensorflow.framework.DataType;
import com.google.protobuf.Int64Value;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;

public class ImageClassificationPredictClient extends PredictClient
{
  private static String HEIGHT = "height";
  private static String WIDTH = "width";
  private static String NCHANNELS = "nChannels";
  private static String DATA = "data";

  // Initialization
  public ImageClassificationPredictClient(String[] args)
  {
    // Set default values for some key parameters.
    parser.setDefault(MODEL_NAME, "inception_v3");
    parser.setDefault(INPUT_KEY, "images");
    parser.setDefault(OUTPUT_KEY1, "labels");
    parser.setDefault(OUTPUT_KEY2, "confidences");
    try
    {
      ns = parser.parseArgs(args);
    }
    catch (ArgumentParserException e)
    {
      parser.handleError(e);
      System.exit(1);
    }
  }
  // Get input image which should be fed into models for prediction.
  public Map<String, Object> getInputData(String dataPath)
  {
    // Read an image
    File imageFile = new File(dataPath);
    Map<String, Object> inputDataMap = new HashMap<String, Object>();
    BufferedImage image;
    try {
      image = ImageIO.read(imageFile);
      int height = image.getHeight();
      int width = image.getWidth();
      int nChannels = 3;
      inputDataMap.put(HEIGHT, height);
      inputDataMap.put(WIDTH, width);
      inputDataMap.put(NCHANNELS,nChannels);
      TensorProto.Builder imageTensorBuilder = TensorProto.newBuilder();
      for (int i = 0; i < height; i++)
      {
        for (int j = 0; j < width; j++)
        {
          int pixelVal = image.getRGB(j, i); //pixel value
          int rVal = (pixelVal >> 16) & 0xff; //red
          int gVal = (pixelVal >> 8) & 0xff;  //gren
          int bVal = (pixelVal) & 0xff;  //blue
          imageTensorBuilder.addFloatVal((float)rVal);
          imageTensorBuilder.addFloatVal((float)gVal);
          imageTensorBuilder.addFloatVal((float)bVal);
        }
      }
      inputDataMap.put(DATA, imageTensorBuilder);
    }
    catch (IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return inputDataMap;
  }

  // Make a prediction.
  public void doPredict()
  {
    int numOfImages = 1;
    Map<String, Object> imageData = getInputData(ns.get(DATA_PATH));
    int height = (int)imageData.get(HEIGHT);
    int width = (int)imageData.get(WIDTH);
    int nChannels = (int)imageData.get(NCHANNELS);
    TensorShapeProto.Dim imageDim1 = TensorShapeProto.Dim.newBuilder()
                                                          .setSize(numOfImages).build();
    TensorShapeProto.Dim imageDim2 = TensorShapeProto.Dim.newBuilder()
                                                         .setSize(height).build();
    TensorShapeProto.Dim imageDim3 = TensorShapeProto.Dim.newBuilder()
                                                         .setSize(width).build();
    TensorShapeProto.Dim imageDim4 = TensorShapeProto.Dim.newBuilder()
                                                         .setSize(nChannels).build();
    TensorShapeProto imageShape = TensorShapeProto.newBuilder()
                                                  .addDim(imageDim1)
                                                  .addDim(imageDim2)
                                                  .addDim(imageDim3)
                                                  .addDim(imageDim4).build();
    TensorProto.Builder imageTensorBuilder = (TensorProto.Builder)imageData.get(DATA);
    imageTensorBuilder.setDtype(DataType.DT_FLOAT).setTensorShape(imageShape);
    TensorProto imageTensor = imageTensorBuilder.build();
    // Send the image tensor to a tensorflow-serving server and get the predicted respone.
    // Optionall, the predicted labels as well as the confidences can be printed.
    getOutputs(imageTensor);
  }

}



