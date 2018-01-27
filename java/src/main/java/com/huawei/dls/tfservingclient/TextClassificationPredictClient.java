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

public class TextClassificationPredictClient extends PredictClient
{
  private static String SEQ_LEN = "seqLength";
  private static String TEXT = "text";
  private static String LABELS = "labels";
  private static int NUM_SEQS = 10;

  public TextClassificationPredictClient(String[] args)
  {

    parser.setDefault(MODEL_NAME, "textcnn");
    parser.setDefault(INPUT_KEY, "text");
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

  public Map<String, Object> getInputData(String dataPath)
  {
    File textFile = new File(dataPath);
    Map<String, Object> inputDataMap = new HashMap<String, Object>();
    ArrayList<long[]> textBatch = new ArrayList<long[]>();
    ArrayList<Long> labelsBatch = new ArrayList<Long>();
    try
    {
      FileReader fileReader = new FileReader(textFile);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String line;
      int seqLength = 0;
      int row = 0;
      TensorProto.Builder textTensorBuilder = TensorProto.newBuilder();
      TensorProto.Builder labelsTensorBuilder = TensorProto.newBuilder();
      while ((line = bufferedReader.readLine())!=null && row < NUM_SEQS)
      {
        String[] valsLine = line.trim().split(", ");
        long oneLabel = 0;
        seqLength = valsLine.length;
        long[] oneSeq = new long[seqLength - 1];
        for (int i = 0; i < seqLength; i++)
        {
          // the last var is label.
          if (i == (seqLength - 1))
          {
            oneLabel = Long.parseLong(valsLine[i]);
            labelsTensorBuilder.addInt64Val(oneLabel);
            break;
          }
          // get the id and add it to the current row
          oneSeq[i] = Long.parseLong(valsLine[i]);
          textTensorBuilder.addInt64Val(oneSeq[i]);
        }
        row++;
      }
      bufferedReader.close();
      inputDataMap.put(SEQ_LEN, seqLength - 1);
      inputDataMap.put(TEXT, textTensorBuilder);
      inputDataMap.put(LABELS, labelsTensorBuilder);
    }
    catch (IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return inputDataMap;
  }

  public void doPredict()
  {
    Map<String, Object> textData = getInputData(ns.get(DATA_PATH));
    int seqLength = (int)textData.get(SEQ_LEN);
    System.out.println("sequence length is: " + seqLength);
    // generate text query TensorProto
    TensorShapeProto.Dim queryDim1 = TensorShapeProto.Dim.newBuilder()
                                                          .setSize(NUM_SEQS).build();
    TensorShapeProto.Dim queryDim2 = TensorShapeProto.Dim.newBuilder()
                                                         .setSize(seqLength).build();
    TensorShapeProto queryShape = TensorShapeProto.newBuilder()
                                                  .addDim(queryDim1)
                                                  .addDim(queryDim2).build();
    TensorProto.Builder textTensorBuilder = (TensorProto.Builder)textData.get(TEXT);
    textTensorBuilder.setDtype(DataType.DT_INT64).setTensorShape(queryShape);
    TensorProto textTensor = textTensorBuilder.build();
    getOutputs(textTensor);
  }

}



