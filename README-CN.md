#预测作业客户端使用指南

预测作业创建成功后，可以下载和运行客户端发起预测请求，具体使用说明如下：

**(注意：请确保运行客户端client的机器是连网的。)**

### 下载

执行如下命令下载预测作业客户端：

```sh
git clone https://github.com/huawei-clouds/dls-tfserving-client.git
```



### 例子

以**图像分类**为例（样例数据为dls-tfserving-client/data目录下的某个图片），Java和Python客户端使用方式为：

#### Java客户端

依次执行如下命令即可实现一次预测：

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

#### Python客户端

##### 环境设置

**Linux**

下列部署我们在Anaconda环境中测试 (Anaconda2-4.2.0-Linux-x86_64.sh)。

1. 安装 python 2.7 / python 3.6

   创建一个名为"my-env-py27"的环境，并安装python 2.7:

   ```
   conda create -n my-env-py27 python=2.7
   source activate my-env-py27
   ```

   或者

   创建一个名为"my-env-py36"的环境，并安装python 3.6:

   ```
   conda create -n my-env-py36 python=3.6
   source activate my-env-py36
   ```

2. 安装 tensorflow

   ```
   pip install tensorflow
   ```

3. 安装其它依赖包

   ```
   pip install image
   ```

4. 把下列路径加入到环境变量 PYTHONPATH 中

   ```
   export PYTHONPATH=PYTHONPATH:xx/dls-tfserving-client/python/predict_client
   ```

   注意: **xx**是指"dls-tfserving-client" 所在的目录。

   **Windows**


1. 安装 python 3.6

   假设已经安装了Anaconda环境, 可以使用以下命令创建一个名为"my-env" 的环境，并安装python 3.6:

   ```
   conda create -n my-env python=3.6
   ```

2. 下载 [tensorflow 1.5.1](https://pypi.python.org/packages/9c/7c/0c37da035ca2348d17f7747d7388d567ab1c53b626a9071b9767c9201272/tensorflow-1.5.1-cp36-cp36m-win_amd64.whl#md5=6fff811cbb3cb2cdc36759115ca17589) 安装包

3. 安装 tensorflow

   ```
   pip install tensorflow-1.5.1-cp36-cp36m-win_amd64.whl
   ```

4. 安装其它依赖包

   ```
   pip install grpcio image
   ```

5. 把下列路径加入到环境变量 PYTHONPATH 中

   "PATHONPATH=xx/dls-tfserving-client/python/predict_client"

   注意: **xx**是指"dls-tfserving-client" 所在的目录。

   ​

直接运行如下命令即可实现一次预测：

```sh
python dls-tfserving-client/python/predict.py \
--task_type="image_classification" \
--host=xx.xx.xx.xx \
--port=xxxx \
--data_path="xx/dls-tfserving-client/data/flowers/flower1.jpg" \
--labels_file_path="xx/dls-tfserving-client/data/flowers/labels.txt" \
--model_name="resnet_v1_50"
```



#### 参数说明（适用于上述所有客户端，根据实际值修改）

**host**：远程预测服务的IP。

**port**：远程预测服务的端口。

**dataPath(data_path)**：输入数据所在的路径。

**labelsFilePath(labels_file_path)**：数据集标签名所在的路径。

**modelName(model_name)**：创建预测作业时输入的模型名称。



### 资源

* [客户端工程](https://github.com/huawei-clouds/dls-tfserving-client) 







