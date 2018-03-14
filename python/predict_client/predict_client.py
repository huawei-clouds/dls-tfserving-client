from grpc.beta import implementations
import predict_pb2
import prediction_service_pb2
import model_pb2
from tensorflow.contrib.util import make_tensor_proto 
from numpy import ndarray as numpy_ndarray
from re import match as re_match

import tensorflow as tf
import argparse
import numpy as np

PRINT_RESPONSE = True 
MAX_RESPONSE_TIME = 50

class PredictClient(object):

  def __init__(self, parser):
    self.parser = parser
  
  def get_labels_id_name(self):
    # read the data labels info
    self.labels_info = {}
    with open(self.ns.labels_file_path) as f:
      lines = f.read().splitlines()
      for line in lines:
        (key, val) = line.split(':')
        self.labels_info[int(key)] = val
       
  def print_response(self, response):
    self.get_labels_id_name()
    output1_proto = response.outputs[self.ns.output_key1]      
    output1_values = tf.contrib.util.make_ndarray(output1_proto)[0]
    output2_proto = response.outputs[self.ns.output_key2]
    output2_values = tf.contrib.util.make_ndarray(output2_proto)[0]
    
    for i in range(self.num_outputs):
      print('{output_key1}{i}:{label}, {output_key2}{i}:{confidence}'
          .format(output_key1=self.ns.output_key1, label=self.labels_info[int(output1_values[i])],
            output_key2=self.ns.output_key2, confidence=str(output2_values[i]), i=i))
    
  def get_outputs(self, input_data):
    # Step 0: check the type of input parameters
    if not isinstance(self.ns.host, str):
      print("The type of \"host\" must be str (string)!")
      raise IllegalArgumentException
    
    if not re_match("^[0-9localhost.:/]+$", self.ns.host):
      print("hostport does not match preseted character-set" )
      raise IllegalArgumentException
    
    if not isinstance(self.ns.port, int):
      print("The type of \"port\* must be int!")
      raise IllegalArgumentException

    if not isinstance(self.ns.model_name, str):
      print("the type of \"model_name\" must be str (string)!")
      raise IllegalArgumentException
        
    if not re_match("^[0-9A-Za-z_. \-/]+$", self.ns.model_name):
      print("model_name does not match preseted character-set" )
      raise IllegalArgumentException

    if not isinstance(input_data, dict):
      print("the type of \"input_data\" must be dict!")
      raise IllegalArgumentException
        
    if (not isinstance(MAX_RESPONSE_TIME, int)) and (not isinstance(MAX_RESPONSE_TIME, float)):
      print("the type of \"max_response_time\" must be int or float!")
      raise IllegalArgumentException

    # Setup connection
    channel = implementations.insecure_channel(self.ns.host, self.ns.port)
    stub = prediction_service_pb2.beta_create_PredictionService_stub(channel)
    
    # Initialize the request
    request = predict_pb2.PredictRequest()
    request.model_spec.name = self.ns.model_name
    request.model_spec.signature_name = self.ns.model_signature_name
    #request.model_spec.version = self.ns.model_version_num
    # Set the input variables of the request
    for key, value in input_data.items():
      if not re_match("^[0-9A-Za-z_. \-/]+$", key):
        print("model_name does not match preseted character-set" )
        raise IllegalArgumentException
      if isinstance(value, numpy_ndarray):
        request.inputs[key].CopyFrom(make_tensor_proto(value, shape=list(value.shape)))
      elif isinstance(value, int) or isinstance(value, float):
        request.inputs[key].CopyFrom(make_tensor_proto(value) )
      else:
        request.inputs[key].CopyFrom(make_tensor_proto(value, shape=list(value.shape)))
    
    # Obtain the result of prediction
    response = stub.Predict(request, MAX_RESPONSE_TIME)
    if PRINT_RESPONSE:
      self.print_response(response)

class ImageClassificationPredictClient(PredictClient):
  
  def __init__(self, parser):
    super(ImageClassificationPredictClient, self).__init__(parser)
    self.parser.set_defaults(model_name = 'resnet_v1_50')
    self.parser.set_defaults(input_key = 'images')
    self.parser.set_defaults(output_key1 = 'labels')
    self.parser.set_defaults(output_key2 = 'confidences')
    self.ns = self.parser.parse_args()
    self.num_outputs = 5
  
  def do_predict(self):
    from PIL import Image
    image = Image.open(self.ns.data_path)
    image = image.convert('RGB')
    image = np.asarray(image, dtype = np.float32)
    image = image[np.newaxis, :, :, :]
    input_data = {self.ns.input_key: image}
    self.get_outputs(input_data)

class TextClassificationPredictClient(PredictClient):
  
  def __init__(self, parser):
    super(TextClassificationPredictClient, self).__init__(parser)
    self.parser.add_argument('--num_seqs', default=1)
    self.parser.set_defaults(model_name = 'textcnn')
    self.parser.set_defaults(input_key = 'text')
    self.parser.set_defaults(output_key1 = 'labels')
    self.parser.set_defaults(output_key2 = 'confidences')
    self.ns = self.parser.parse_args()
    self.num_outputs = 1
  def do_predict(self):
    import re
    with open(self.ns.data_path) as f:
      lines = f.read().splitlines()
      data = [re.split(',', line) for line in lines]
      text = [line[:-1] for line in data]
      text = np.asarray(text, dtype=np.int64)
      f.close()
    text = text[:self.ns.num_seqs]
    seq_length = text.shape[1]
    for i in range(self.ns.num_seqs):
      input_text = np.reshape(text[i], (1, seq_length))
      input_data = {self.ns.input_key: input_text}
      self.get_outputs(input_data)



