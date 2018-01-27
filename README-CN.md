#预测作业客户端使用指南

预测作业创建成功后，可以下载和运行客户端发起预测请求，具体使用说明如下：

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
cd dls-tfserving-client
mvn clean install
java -jar target/predict-1.0.0.jar image_classification --host=xx.xx.xx.xx --port=xxxx --dataPath="xx/dls-tfserving-client/data/flowers/flower1.jpg" --labelsFilePath="xx/dls-tfserving-client/data/flowers/labels.txt" --modelName="resnet_v1_50"
```

#### Python客户端

直接运行如下命令即可实现一次预测：

```sh
python predict.py --task_type="image_classification" --host=xx.xx.xx.xx --port=xxxx --data_path="xx/dls-tfserving-client/data/flowers/flower1.jpg" --labels_file_path="xx/dls-tfserving-client/data/flowers/labels.txt" --model_name="resnet_v1_50"
```



#### 参数说明（适用于上述所有客户端，根据实际值修改）

**host**：远程预测服务的IP。

**port**：远程预测服务的端口。

**dataPath(data_path)**：输入数据所在的路径。

**labelsFilePath(labels_file_path)**：数据集标签名所在的路径。

**modelName(model_name)**：创建预测作业时输入的模型名称。



### 资源

* [客户端工程](https://github.com/huawei-clouds/dls-tfserving-client) 







