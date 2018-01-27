package com.huawei.dls.tfservingclient;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Formatter;
import org.tensorflow.serving.*;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.tensorflow.framework.DataType;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import com.google.protobuf.Int64Value;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// This is an abstract class of predict client.
// To make a new client for other tasks, e.g., audio classification, 
// just implement a subclass which mainly focuse on data reading and preprocessing.
// For more complication tasks, such as object detection, 
// some methods such as getLabelIdName() should be overrided in the subclass.
public abstract class PredictClient
{
  protected static final Logger LOGGER = Logger.getLogger(PredictClient.class.getName());
  protected static ArgumentParser parser = ArgumentParsers.newFor("PredictClient").build();
  protected static Namespace ns = null;
  protected static String HOST = "host";
  protected static String PORT = "port";
  protected static String DATA_PATH = "dataPath";
  protected static String LABELS_FILE_PATH = "labelsFilePath";
  protected static String MODEL_NAME = "modelName";
  protected static String INPUT_KEY = "inputKey";
  protected static String OUTPUT_KEY1 = "outputKey1";
  protected static String OUTPUT_KEY2 = "outputKey2";
  protected static String MODEL_VERSION_NUM = "modelVersionNum";
  protected static String MODEL_SIGNATURE_NAME = "modelSignatureName";
  protected static int MAX_NUM_OUTPUTS = 5;
  protected static boolean PRINT_RESPONSE = true;

  public abstract Map<String, Object> getInputData(String dataPath);
  public abstract void doPredict();

  public PredictClient()
  {
    this.parser.addArgument("--" + HOST)
        .type(String.class)
        .setDefault("0.0.0.0")
        .help("Please specify the host of the server running tensorflow serving.");
    this.parser.addArgument("--" + PORT)
        .type(Integer.class)
        .setDefault(9029)
        .help("Please specify the port of the server running tensorflow serving");
    this.parser.addArgument("--" + DATA_PATH)
        .type(String.class)
        .help("Please specify the path of the data used for prediction.");
    this.parser.addArgument("--" + LABELS_FILE_PATH)
        .type(String.class)
        .help("Please specify the path of the labels.txt used for parsing the label name");
    this.parser.addArgument("--" + MODEL_NAME)
        .type(String.class)
        .help("Please specify the model name.");
    this.parser.addArgument("--" + INPUT_KEY)
        .type(String.class)
        .help("Please specify the input key, e.g. 'images'.");
    this.parser.addArgument("--" + OUTPUT_KEY1)
        .type(String.class)
        .help("Please specify the first output key, e.g. 'labels'.");
    this.parser.addArgument("--" + OUTPUT_KEY2)
        .type(String.class)
        .setDefault("confidences")
        .help("Please specify the second key, e.g. 'confidences'.");
    this.parser.addArgument("--" + MODEL_VERSION_NUM)
        .type(Integer.class)
        .setDefault(1)
        .help("Please specify the model version number.");
    this.parser.addArgument("--" + MODEL_SIGNATURE_NAME)
        .type(String.class)
        .setDefault("predict_object")
        .help("Please specify the model signature name.");
  }

  // Get the mapping from label ID to label name.
  public Map<Integer, String> getLabelIdName(String labelsFilePath) 
  {
    Map<Integer, String> labelIdNameMap = new HashMap<Integer, String>();
    try
    {
      FileReader reader = new FileReader(labelsFilePath);
      BufferedReader bufferedReader = new BufferedReader(reader);
      StringBuffer stringBuffer = new StringBuffer("");
      String line = null;
      while((line = bufferedReader.readLine())!=null)
      {
        stringBuffer.append(line);
        String[] splitLine = line.split(":");
        labelIdNameMap.put(Integer.parseInt(splitLine[0]), splitLine[1]);
      }
    }
    catch(FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "label file is not found.");
    }
    catch(IOException e)
    {
      LOGGER.log(Level.WARNING, "IO errors occurs when reading label file");
    }
    return labelIdNameMap;
  }

  // Print the predicted response.
  public void printResponse(Map<String, TensorProto> predictResponse)
  {
    List<Integer> outList1 = predictResponse.get(ns.get(OUTPUT_KEY1)).getIntValList();
    List<Float> outList2 = predictResponse.get(ns.get(OUTPUT_KEY2)).getFloatValList();
    Map<Integer, String> resMap = getLabelIdName(ns.get(LABELS_FILE_PATH));
    Formatter formatter = new Formatter(System.out);
    for (int i=0; i < MAX_NUM_OUTPUTS; i++)
    {
      formatter.format("%-20s %-20s %-20s %-20s\n",
                       ns.get(OUTPUT_KEY1) + String.valueOf(i) + ": ",
                       resMap.get(outList1.get(i)),
                       ns.get(OUTPUT_KEY2) + String.valueOf(i) + ": ",
                       outList2.get(i));
    }
  }


  public void getOutputs(TensorProto inputDataTensor)
  {
    int modelVersionNum = ns.get(MODEL_VERSION_NUM);
    Int64Value modelVersion = Int64Value.newBuilder()
                                        .setValue(modelVersionNum).build();
    ModelSpec modelSpec = ModelSpec.newBuilder()
                                   .setName(ns.get(MODEL_NAME))
                                   .setVersion(modelVersion)
                                   .setSignatureName(ns.get(MODEL_SIGNATURE_NAME)).build();
    PredictRequest predictRequest = PredictRequest.newBuilder()
                                                  .setModelSpec(modelSpec)
                                                  .putInputs(ns.get(INPUT_KEY), inputDataTensor)
                                                  .build();
    // Send request and get response.
    ManagedChannel channel = ManagedChannelBuilder.forAddress(ns.get(HOST), ns.get(PORT))
                                                  .usePlaintext(true).build();
    PredictionServiceGrpc.PredictionServiceBlockingStub blockingStub =
        PredictionServiceGrpc.newBlockingStub(channel);
    Map<String, TensorProto> predictResponse = new HashMap<String, TensorProto>();
    try
    {
      predictResponse = blockingStub.predict(predictRequest).getOutputsMap();
    }
    catch (StatusRuntimeException e)
    {
      LOGGER.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
    }
    if (PRINT_RESPONSE)
    {
      printResponse(predictResponse);
    }
  }

}
