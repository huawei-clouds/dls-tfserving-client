#Usage of Predict Client 

After the inference job (service) is created, you can download, modify and run predict client to send request to inference service. The detailed instructions are:

### Download

Run the following code to down the predict client:

```sh
git clone https://github.com/huawei-clouds/dls-tfserving-client.git
```



### Example

Take **Image Classification** for example（the input data is one image under the folder of dls-tfserving-client/data），the usages of Java and Python predict client are：

#### Java Predict Client

Execute the following codes successively:

```sh
cd dls-tfserving-client
mvn clean install
java -jar target/predict-1.0.0.jar image_classification --host=xx.xx.xx.xx --port=xxxx --dataPath="xx/dls-tfserving-client/data/flowers/flower1.jpg" --labelsFilePath="xx/dls-tfserving-client/data/flowers/labels.txt" --modelName="resnet_v1_50"
```

#### Python Predict Client

Directly execute the following code：

```sh
python predict.py --task_type="image_classification" --host=xx.xx.xx.xx --port=xxxx --data_path="xx/dls-tfserving-client/data/flowers/flower1.jpg" --labels_file_path="xx/dls-tfserving-client/data/flowers/labels.txt" --model_name="resnet_v1_50"
```



#### Parameter Interpretation (The parameters are suitable to both clients above, and should be modified according to practical situations.)

**host**: the IP of the remote inference service.

**port**: the port of the remote inference service.

**dataPath(data_path)** : the url of the input data.

**labelsFilePath(labels_file_path)**: the url of the file of data labels.

**modelName(model_name)**: the model name used in creating the inference service.



## Resources

* [Project for predict client](https://github.com/huawei-clouds/dls-tfserving-client)

