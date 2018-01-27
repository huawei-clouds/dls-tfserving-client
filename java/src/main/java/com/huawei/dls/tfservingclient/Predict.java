package com.huawei.dls.tfservingclient;

import java.util.*;

public class Predict
{
  public static String IMAGE_CLASSIFICATION = "image_classification";
  public static String TEXT_CLASSIFICATION = "text_classification";
  public static void main(String[] args)
  {
    String[] sub_args = Arrays.copyOfRange(args, 1, args.length);
    PredictClient predictClient = null;
    if (args[0]!=null)
    {
      if (args[0].equals(IMAGE_CLASSIFICATION))
      {
         predictClient = new ImageClassificationPredictClient(sub_args);
         predictClient.doPredict();
      }
      else if (args[0].equals(TEXT_CLASSIFICATION))
      {
         predictClient = new TextClassificationPredictClient(sub_args);
         predictClient.doPredict();
      }
      else
      {
        System.out.println("Unknown task type.\n"
          + "Please set it as image_classification or text_classification. ");
      }
    }
    else
    {
      System.out.println("Please input task type,"
          + "e.g., image_classification, or text_classification.");
    }
  }
}
