#Usage of Predict Client 

After the inference job (service) is created, you can download, modify and run predict client to send request to inference service. The detailed instructions are:

**(Note: Please assure your computer is connected to the Internet.)**

### Download

Run the following code to down the predict client:

```sh
git clone https://github.com/huawei-clouds/dls-tfserving-client.git
```



### Example

Take **Image Classification** for example（the input data is one image under the folder of dls-tfserving-client/data）, the usages of Java and Python predict client are：

#### Java Predict Client

Execute the following codes successively:

```sh
cd dls-tfserving-client/java
mvn clean install
java -jar target/predict-1.0.0.jar \
image_classification \
--host=xx.xx.xx.xx \
--port=xxxx \
--dataPath="xx/dls-tfserving-client/data/flowers/flower1.jpg" \
--labelsFilePath="xx/dls-tfserving-client/data/flowers/labels.txt" \
--modelName="resnet_v1_50"
```

#### Python Predict Client

##### Pre-requisites

**Linux**

We have tested the following installation under the Anaconda environment (Anaconda2-4.2.0-Linux-x86_64.sh).

1. Install python 2.7 / python 3.6

   Create an environment named "my-env-py27" with python 2.7 installed:

   ```
   conda create -n my-env-py27 python=2.7
   source activate my-env-py27
   ```

   or 

   Create an environment name "my-env-py36" with python 3.6 installed:

   ```
   conda create -n my-env-py36 python=3.6
   source activate my-env-py36
   ```

2. Install tensorflow

   ```
   pip install tensorflow
   ```

3. Install other dependencies

   ```
   pip install image
   ```

4. Add the following directory to the environment variable PYTHONPATH

   ```
   export PYTHONPATH=PYTHONPATH:xx/dls-tfserving-client/python/predict_client
   ```

   Note: **xx** indicates the directory where "dls-tfserving-client" is located.

**Windows**

1. Install python 3.6

   For example, assuming you have installed Anaconda, you can use the following command to create an environment named "my-env" with python 3.6 installed:

   ```
   conda create -n my-env python=3.6
   ```

2. Download the [tensorflow 1.5.1](https://pypi.python.org/packages/9c/7c/0c37da035ca2348d17f7747d7388d567ab1c53b626a9071b9767c9201272/tensorflow-1.5.1-cp36-cp36m-win_amd64.whl#md5=6fff811cbb3cb2cdc36759115ca17589) package

3. Install the tensorflow package

   ```
   pip install tensorflow-1.5.1-cp36-cp36m-win_amd64.whl
   ```

4. Install other dependencies

   ```
   pip install grpcio image
   ```

5. Add the following directory to the environment variable PYTHONPATH.

   "PATHONPATH=xx/dls-tfserving-client/python/predict_client"

   Note: **xx** indicates the directory where "dls-tfserving-client" is located.



Directly execute the following code：

```sh
python dls-tfserving-client/python/predict.py \
--task_type="image_classification" \
--host=xx.xx.xx.xx \
--port=xxxx \
--data_path="xx/dls-tfserving-client/data/flowers/flower1.jpg" \
--labels_file_path="xx/dls-tfserving-client/data/flowers/labels.txt" \
--model_name="resnet_v1_50"
```



#### Parameter Interpretation (The parameters are suitable to both clients above, and should be modified according to practical situations.)

**host**: the IP of the remote inference service.

**port**: the port of the remote inference service.

**dataPath(data_path)** : the url of the input data.

**labelsFilePath(labels_file_path)**: the url of the file of data labels.

**modelName(model_name)**: the model name used in creating the inference service.



## Resources

* [Project for predict client](https://github.com/huawei-clouds/dls-tfserving-client)

